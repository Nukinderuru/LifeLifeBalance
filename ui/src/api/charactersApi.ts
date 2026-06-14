import { requestJson } from './client';
import type { CharacterResponse, CharacterSummaryResponse } from '../types/characters';

export function getCharacters(): Promise<CharacterResponse[]> {
  return requestJson('/api/characters');
}

export function getCharacterSummary(id: string, date: string): Promise<CharacterSummaryResponse> {
  return requestJson(`/api/characters/${id}/summary`, { query: { date } });
}
