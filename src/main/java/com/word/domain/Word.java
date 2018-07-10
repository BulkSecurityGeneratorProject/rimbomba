package com.word.domain;


import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Word.
 */
@Entity
@Table(name = "word")
public class Word implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "word", nullable = false)
    private String word;

    @Column(name = "meaning")
    private String meaning;

    @NotNull
    @Column(name = "date_added", nullable = false)
    private LocalDate dateAdded;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public Word word(String word) {
        this.word = word;
        return this;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public Word meaning(String meaning) {
        this.meaning = meaning;
        return this;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public LocalDate getDateAdded() {
        return dateAdded;
    }

    public Word dateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
        return this;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Word word = (Word) o;
        if (word.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), word.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Word{" +
            "id=" + getId() +
            ", word='" + getWord() + "'" +
            ", meaning='" + getMeaning() + "'" +
            ", dateAdded='" + getDateAdded() + "'" +
            "}";
    }
}
