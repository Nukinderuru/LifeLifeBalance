const DATE_FORMATTER = new Intl.DateTimeFormat('en', {
  month: 'short',
  day: 'numeric',
});

const FULL_DATE_FORMATTER = new Intl.DateTimeFormat('en', {
  weekday: 'long',
  month: 'long',
  day: 'numeric',
});

export function formatDateLabel(date: string): string {
  return DATE_FORMATTER.format(new Date(date));
}

export function formatFullDateLabel(date: string): string {
  return FULL_DATE_FORMATTER.format(new Date(date));
}

export function formatInputDate(date: Date): string {
  return date.toISOString().slice(0, 10);
}
