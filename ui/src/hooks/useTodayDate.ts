export function useTodayDate(): string {
  return new Date().toISOString().slice(0, 10);
}
