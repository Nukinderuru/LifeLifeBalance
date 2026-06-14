import { CharacterCard } from '../components/characters/CharacterCard';
import { EmptyState } from '../components/common/EmptyState';
import { ErrorState } from '../components/common/ErrorState';
import { LoadingState } from '../components/common/LoadingState';
import { PageShell } from '../components/layout/PageShell';
import { getTodayDashboard } from '../api/dashboardApi';
import { useAsyncData } from '../hooks/useAsyncData';
import styles from './TodayDashboardPage.module.css';

export function TodayDashboardPage() {
  const { data, loading, error, reload } = useAsyncData(getTodayDashboard, []);

  return (
    <PageShell title="Who is hungry today?" subtitle="Each small gesture still counts.">
      {loading ? <LoadingState message="Gathering the five council cards..." /> : null}
      {error ? <ErrorState message={error} onRetry={reload} /> : null}

      {!loading && !error && data ? (
        data.characters.length > 0 ? (
          <div className={styles.grid}>
            {data.characters.map((character) => (
              <CharacterCard key={character.id} character={character} />
            ))}
          </div>
        ) : (
          <EmptyState title="The page is quiet today" message="No character cards appeared yet." />
        )
      ) : null}
    </PageShell>
  );
}
