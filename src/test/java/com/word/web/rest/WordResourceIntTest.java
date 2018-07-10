package com.word.web.rest;

import com.word.RimbombaApp;

import com.word.domain.Word;
import com.word.repository.WordRepository;
import com.word.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;


import static com.word.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the WordResource REST controller.
 *
 * @see WordResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RimbombaApp.class)
public class WordResourceIntTest {

    private static final String DEFAULT_WORD = "AAAAAAAAAA";
    private static final String UPDATED_WORD = "BBBBBBBBBB";

    private static final String DEFAULT_MEANING = "AAAAAAAAAA";
    private static final String UPDATED_MEANING = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_ADDED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_ADDED = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private WordRepository wordRepository;


    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restWordMockMvc;

    private Word word;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final WordResource wordResource = new WordResource(wordRepository);
        this.restWordMockMvc = MockMvcBuilders.standaloneSetup(wordResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Word createEntity(EntityManager em) {
        Word word = new Word()
            .word(DEFAULT_WORD)
            .meaning(DEFAULT_MEANING)
            .dateAdded(DEFAULT_DATE_ADDED);
        return word;
    }

    @Before
    public void initTest() {
        word = createEntity(em);
    }

    @Test
    @Transactional
    public void createWord() throws Exception {
        int databaseSizeBeforeCreate = wordRepository.findAll().size();

        // Create the Word
        restWordMockMvc.perform(post("/api/words")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(word)))
            .andExpect(status().isCreated());

        // Validate the Word in the database
        List<Word> wordList = wordRepository.findAll();
        assertThat(wordList).hasSize(databaseSizeBeforeCreate + 1);
        Word testWord = wordList.get(wordList.size() - 1);
        assertThat(testWord.getWord()).isEqualTo(DEFAULT_WORD);
        assertThat(testWord.getMeaning()).isEqualTo(DEFAULT_MEANING);
        assertThat(testWord.getDateAdded()).isEqualTo(DEFAULT_DATE_ADDED);
    }

    @Test
    @Transactional
    public void createWordWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = wordRepository.findAll().size();

        // Create the Word with an existing ID
        word.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restWordMockMvc.perform(post("/api/words")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(word)))
            .andExpect(status().isBadRequest());

        // Validate the Word in the database
        List<Word> wordList = wordRepository.findAll();
        assertThat(wordList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkWordIsRequired() throws Exception {
        int databaseSizeBeforeTest = wordRepository.findAll().size();
        // set the field null
        word.setWord(null);

        // Create the Word, which fails.

        restWordMockMvc.perform(post("/api/words")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(word)))
            .andExpect(status().isBadRequest());

        List<Word> wordList = wordRepository.findAll();
        assertThat(wordList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDateAddedIsRequired() throws Exception {
        int databaseSizeBeforeTest = wordRepository.findAll().size();
        // set the field null
        word.setDateAdded(null);

        // Create the Word, which fails.

        restWordMockMvc.perform(post("/api/words")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(word)))
            .andExpect(status().isBadRequest());

        List<Word> wordList = wordRepository.findAll();
        assertThat(wordList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWords() throws Exception {
        // Initialize the database
        wordRepository.saveAndFlush(word);

        // Get all the wordList
        restWordMockMvc.perform(get("/api/words?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(word.getId().intValue())))
            .andExpect(jsonPath("$.[*].word").value(hasItem(DEFAULT_WORD.toString())))
            .andExpect(jsonPath("$.[*].meaning").value(hasItem(DEFAULT_MEANING.toString())))
            .andExpect(jsonPath("$.[*].dateAdded").value(hasItem(DEFAULT_DATE_ADDED.toString())));
    }
    

    @Test
    @Transactional
    public void getWord() throws Exception {
        // Initialize the database
        wordRepository.saveAndFlush(word);

        // Get the word
        restWordMockMvc.perform(get("/api/words/{id}", word.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(word.getId().intValue()))
            .andExpect(jsonPath("$.word").value(DEFAULT_WORD.toString()))
            .andExpect(jsonPath("$.meaning").value(DEFAULT_MEANING.toString()))
            .andExpect(jsonPath("$.dateAdded").value(DEFAULT_DATE_ADDED.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingWord() throws Exception {
        // Get the word
        restWordMockMvc.perform(get("/api/words/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWord() throws Exception {
        // Initialize the database
        wordRepository.saveAndFlush(word);

        int databaseSizeBeforeUpdate = wordRepository.findAll().size();

        // Update the word
        Word updatedWord = wordRepository.findById(word.getId()).get();
        // Disconnect from session so that the updates on updatedWord are not directly saved in db
        em.detach(updatedWord);
        updatedWord
            .word(UPDATED_WORD)
            .meaning(UPDATED_MEANING)
            .dateAdded(UPDATED_DATE_ADDED);

        restWordMockMvc.perform(put("/api/words")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedWord)))
            .andExpect(status().isOk());

        // Validate the Word in the database
        List<Word> wordList = wordRepository.findAll();
        assertThat(wordList).hasSize(databaseSizeBeforeUpdate);
        Word testWord = wordList.get(wordList.size() - 1);
        assertThat(testWord.getWord()).isEqualTo(UPDATED_WORD);
        assertThat(testWord.getMeaning()).isEqualTo(UPDATED_MEANING);
        assertThat(testWord.getDateAdded()).isEqualTo(UPDATED_DATE_ADDED);
    }

    @Test
    @Transactional
    public void updateNonExistingWord() throws Exception {
        int databaseSizeBeforeUpdate = wordRepository.findAll().size();

        // Create the Word

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restWordMockMvc.perform(put("/api/words")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(word)))
            .andExpect(status().isBadRequest());

        // Validate the Word in the database
        List<Word> wordList = wordRepository.findAll();
        assertThat(wordList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteWord() throws Exception {
        // Initialize the database
        wordRepository.saveAndFlush(word);

        int databaseSizeBeforeDelete = wordRepository.findAll().size();

        // Get the word
        restWordMockMvc.perform(delete("/api/words/{id}", word.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Word> wordList = wordRepository.findAll();
        assertThat(wordList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Word.class);
        Word word1 = new Word();
        word1.setId(1L);
        Word word2 = new Word();
        word2.setId(word1.getId());
        assertThat(word1).isEqualTo(word2);
        word2.setId(2L);
        assertThat(word1).isNotEqualTo(word2);
        word1.setId(null);
        assertThat(word1).isNotEqualTo(word2);
    }
}
