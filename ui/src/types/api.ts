export type Uuid = string;
export type IsoDate = string;
export type IsoInstant = string;

export type CharacterCode = 'MAYA' | 'ELINA' | 'TORA' | 'DANA' | 'NAOMI';
export type WishCategory = 'DAILY' | 'WEEKLY' | 'BIG';
export type CharacterStatus = 'STARVING' | 'HUNGRY' | 'CONTENT' | 'HAPPY' | 'FLOURISHING';

export interface ApiErrorResponse {
  message: string;
}
