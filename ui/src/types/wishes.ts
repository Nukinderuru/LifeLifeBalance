import type { IsoInstant, Uuid, WishCategory } from './api';

export interface WishResponse {
  id: Uuid;
  characterId: Uuid;
  title: string;
  description: string | null;
  points: number;
  category: WishCategory;
  active: boolean;
  createdAt: IsoInstant;
  updatedAt: IsoInstant;
}

export interface CreateWishRequest {
  characterId: Uuid;
  title: string;
  description: string | null;
  points: number;
  category: WishCategory;
  active?: boolean;
}

export interface UpdateWishRequest {
  characterId: Uuid;
  title: string;
  description: string | null;
  points: number;
  category: WishCategory;
  active: boolean;
}

export interface WishFilters {
  characterId?: Uuid;
  category?: WishCategory;
  active?: boolean;
}
