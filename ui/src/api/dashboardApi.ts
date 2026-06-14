import { requestJson } from './client';
import type { TodayDashboardResponse, WeeklyDashboardResponse } from '../types/dashboard';

export function getTodayDashboard(): Promise<TodayDashboardResponse> {
  return requestJson('/api/dashboard/today');
}

export function getWeeklyDashboard(endDate?: string): Promise<WeeklyDashboardResponse> {
  return requestJson('/api/dashboard/week', { query: { endDate } });
}
