import { requestJson } from './client';
import type { CreateWishRequest, UpdateWishRequest, WishFilters, WishResponse } from '../types/wishes';

export function getWishes(filters: WishFilters = {}): Promise<WishResponse[]> {
  return requestJson('/api/wishes', {
    query: {
      characterId: filters.characterId,
      category: filters.category,
      active: filters.active,
    },
  });
}

export function createWish(payload: CreateWishRequest): Promise<WishResponse> {
  return requestJson('/api/wishes', {
    method: 'POST',
    body: JSON.stringify({ ...payload, active: payload.active ?? true }),
  });
}

export function updateWish(id: string, payload: UpdateWishRequest): Promise<WishResponse> {
  return requestJson(`/api/wishes/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  });
}

export function deactivateWish(id: string): Promise<void> {
  return requestJson(`/api/wishes/${id}`, { method: 'DELETE' });
}
