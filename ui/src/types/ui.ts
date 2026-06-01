import type { CharacterCode, CharacterStatus, Uuid } from './api';
import type { CompletionResponse } from './completions';
import type { WishResponse } from './wishes';

export interface CharacterThemeMeta {
  code: CharacterCode;
  mood: string;
  accentGlow: string;
  prompt: string;
}

export interface WishWithTodayCompletion extends WishResponse {
  todayCompletion: CompletionResponse | null;
}

export interface WishesByCharacterGroup {
  characterId: Uuid;
  characterName: string;
  characterCode: CharacterCode;
  color: string;
  wishes: WishWithTodayCompletion[];
}

export type StatusLabel = Record<CharacterStatus, string>;
