import { getWeeklyDashboard } from '../api/dashboardApi';
import { EmptyState } from '../components/common/EmptyState';
import { ErrorState } from '../components/common/ErrorState';
import { LoadingState } from '../components/common/LoadingState';
import { PageShell } from '../components/layout/PageShell';
import { WeeklyCharacterSummaryCard } from '../components/week/WeeklyCharacterSummaryCard';
import { WeeklyGrid } from '../components/week/WeeklyGrid';
import { useAsyncData } from '../hooks/useAsyncData';
import { useTodayDate } from '../hooks/useTodayDate';
import styles from './WeeklyOverviewPage.module.css';

export function WeeklyOverviewPage() {
  const today = useTodayDate();
  const { data, loading, error, reload } = useAsyncData(() => getWeeklyDashboard(today), [today]);

  return (
    <PageShell
      title="Who was nourished this week?"
      subtitle="Who received care, and who might welcome a little more warmth next week?"
    >
      {loading ? <LoadingState message="Collecting the week into one page..." /> : null}
      {error ? <ErrorState message={error} onRetry={reload} /> : null}

      {!loading && !error && data ? (
        <>
          <div className={styles.cardGrid}>
            {data.characters.map((summary) => (
              <WeeklyCharacterSummaryCard key={summary.characterId} summary={summary} />
            ))}
          </div>

          {data.days.length > 0 ? (
            <section className={styles.gridSection}>
              <h3 className={styles.gridTitle}>Seven gentle days</h3>
              <p className={styles.gridCopy}>A small calendar of who was fed, without pressure and without punishment.</p>
              <WeeklyGrid data={data} />
            </section>
          ) : (
            <EmptyState title="No weekly entries yet" message="The week will start to bloom here as wishes are completed." />
          )}
        </>
      ) : null}
    </PageShell>
  );
}
