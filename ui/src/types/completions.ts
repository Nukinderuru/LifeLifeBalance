import type { CharacterCode, IsoDate, IsoInstant, Uuid, WishCategory } from './api';

export interface CreateCompletionRequest {
  wishId: Uuid;
  date: IsoDate;
  notes: string | null;
}

export interface CompletionResponse {
  id: Uuid;
  wishId: Uuid;
  characterId: Uuid;
  code: CharacterCode;
  characterName: string;
  characterColor: string;
  wishTitle: string;
  wishCategory: WishCategory;
  points: number;
  completedDate: IsoDate;
  notes: string | null;
  createdAt: IsoInstant;
}
