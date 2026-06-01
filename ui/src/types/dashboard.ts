import type { CharacterCode, CharacterStatus, IsoDate, Uuid } from './api';

export interface TodayCharacterDashboardResponse {
  id: Uuid;
  code: CharacterCode;
  name: string;
  color: string;
  dailyScore: number;
  weeklyScore: number;
  status: CharacterStatus;
  completedWishCount: number;
}

export interface TodayDashboardResponse {
  date: IsoDate;
  characters: TodayCharacterDashboardResponse[];
}

export interface DailyCharacterAggregateResponse {
  characterId: Uuid;
  code: CharacterCode;
  name: string;
  color: string;
  score: number;
  status: CharacterStatus;
  completedWishCount: number;
}

export interface DailyDashboardEntryResponse {
  date: IsoDate;
  characters: DailyCharacterAggregateResponse[];
}

export interface BestDayResponse {
  date: IsoDate;
  score: number;
}

export interface WeeklyCharacterSummaryResponse {
  characterId: Uuid;
  code: CharacterCode;
  name: string;
  color: string;
  totalScore: number;
  averageDailyScore: number;
  status: CharacterStatus;
  completedWishCount: number;
  hungryDaysCount: number;
  bestDay: BestDayResponse;
}

export interface WeeklyDashboardResponse {
  startDate: IsoDate;
  endDate: IsoDate;
  days: DailyDashboardEntryResponse[];
  characters: WeeklyCharacterSummaryResponse[];
}
