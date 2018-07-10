import { Moment } from 'moment';

export interface IWord {
  id?: number;
  word?: string;
  meaning?: string;
  dateAdded?: Moment;
}

export const defaultValue: Readonly<IWord> = {};
