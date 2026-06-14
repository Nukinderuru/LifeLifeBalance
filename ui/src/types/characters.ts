import type { CharacterStatus, IsoDate, IsoInstant, Uuid } from './api';
import type { CharacterCode } from './api';
import type { WishResponse } from './wishes';

export interface CharacterResponse {
  id: Uuid;
  code: CharacterCode;
  name: string;
  color: string;
  description: string;
  createdAt: IsoInstant;
}

export interface CompletedWishResponse {
  completionId: Uuid;
  wishId: Uuid;
  title: string;
  description: string | null;
  points: number;
  completedDate: IsoDate;
  notes: string | null;
}

export interface CharacterSummaryResponse {
  character: CharacterResponse;
  dailyScore: number;
  weeklyScore: number;
  status: CharacterStatus;
  completedWishes: CompletedWishResponse[];
  missingWishes: WishResponse[];
}
