import { useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getCharacterSummary } from '../api/charactersApi';
import { CharacterPortrait } from '../components/characters/CharacterPortrait';
import { CharacterStatusBadge } from '../components/characters/CharacterStatusBadge';
import { EmptyState } from '../components/common/EmptyState';
import { ErrorState } from '../components/common/ErrorState';
import { LoadingState } from '../components/common/LoadingState';
import { PaperCard } from '../components/common/PaperCard';
import { PageShell } from '../components/layout/PageShell';
import { useAsyncData } from '../hooks/useAsyncData';
import { useTodayDate } from '../hooks/useTodayDate';
import { formatFullDateLabel } from '../utils/date';
import styles from './CharacterDetailPage.module.css';

export function CharacterDetailPage() {
  const { id } = useParams<{ id: string }>();
  const today = useTodayDate();
  const [selectedDate, setSelectedDate] = useState(today);

  const loader = useMemo(() => {
    if (!id) {
      return async () => {
        throw new Error('Character id is missing.');
      };
    }

    return () => getCharacterSummary(id, selectedDate);
  }, [id, selectedDate]);

  const { data, loading, error, reload } = useAsyncData(loader, [loader]);

  return (
    <PageShell
      title={data ? `${data.character.name}'s page` : 'Character page'}
      subtitle="A cozy profile page for what was fed, and what still longs for care."
      actions={
        <label className={styles.datePickerLabel}>
          Date
          <input type="date" value={selectedDate} onChange={(event) => setSelectedDate(event.target.value)} />
        </label>
      }
    >
      {loading ? <LoadingState message="Turning to the right journal page..." /> : null}
      {error ? <ErrorState message={error} onRetry={reload} /> : null}

      {!loading && !error && data ? (
        <div className={styles.layout}>
          <PaperCard accentColor={data.character.color} className={styles.profileCard}>
            <CharacterPortrait code={data.character.code} color={data.character.color} />
            <div className={styles.profileHeader}>
              <div>
                <h3 className={styles.name}>{data.character.name}</h3>
                <p className={styles.description}>{data.character.description}</p>
              </div>
              <CharacterStatusBadge status={data.status} color={data.character.color} />
            </div>

            <div className={styles.statRow}>
              <div>
                <span>Daily score</span>
                <strong>{data.dailyScore}</strong>
              </div>
              <div>
                <span>Weekly score</span>
                <strong>{data.weeklyScore}</strong>
              </div>
            </div>

            <p className={styles.dateCaption}>{formatFullDateLabel(selectedDate)}</p>
          </PaperCard>

          <div className={styles.columns}>
            <PaperCard accentColor={data.character.color}>
              <h3 className={styles.sectionTitle}>Completed wishes</h3>
              {data.completedWishes.length === 0 ? (
                <EmptyState title="Nothing logged for this day yet" message="That is only information, not failure." />
              ) : (
                <ul className={styles.list}>
                  {data.completedWishes.map((wish) => (
                    <li key={wish.completionId}>
                      <strong>{wish.title}</strong>
                      <span>{wish.points} points</span>
                      {wish.notes ? <p>{wish.notes}</p> : null}
                    </li>
                  ))}
                </ul>
              )}
            </PaperCard>

            <PaperCard accentColor={data.character.color}>
              <h3 className={styles.sectionTitle}>Still waiting gently</h3>
              {data.missingWishes.length === 0 ? (
                <EmptyState title="All active wishes were fed" message="A soft, full page for this day." />
              ) : (
                <ul className={styles.list}>
                  {data.missingWishes.map((wish) => (
                    <li key={wish.id}>
                      <strong>{wish.title}</strong>
                      <span>
                        {wish.points} points · {wish.category.toLowerCase()}
                      </span>
                      {wish.description ? <p>{wish.description}</p> : null}
                    </li>
                  ))}
                </ul>
              )}
            </PaperCard>
          </div>
        </div>
      ) : null}
    </PageShell>
  );
}
