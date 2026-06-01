import { requestJson } from './client';
import type { CompletionResponse, CreateCompletionRequest } from '../types/completions';

export function getCompletionsByDate(date: string): Promise<CompletionResponse[]> {
  return requestJson('/api/completions', { query: { date } });
}

export function createCompletion(payload: CreateCompletionRequest): Promise<CompletionResponse> {
  return requestJson('/api/completions', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export function deleteCompletion(id: string): Promise<void> {
  return requestJson(`/api/completions/${id}`, { method: 'DELETE' });
}
