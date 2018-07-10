import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './word.reducer';
import { IWord } from 'app/shared/model/word.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IWordDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: number }> {}

export class WordDetail extends React.Component<IWordDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { wordEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            Word [<b>{wordEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="word">Word</span>
            </dt>
            <dd>{wordEntity.word}</dd>
            <dt>
              <span id="meaning">Meaning</span>
            </dt>
            <dd>{wordEntity.meaning}</dd>
          </dl>
          <Button tag={Link} to="/entity/word" replace color="info">
            <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
          </Button>&nbsp;
          <Button tag={Link} to={`/entity/word/${wordEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
          </Button>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = ({ word }: IRootState) => ({
  wordEntity: word.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(WordDetail);
