import { CharacterStatusBadge } from '../characters/CharacterStatusBadge';
import { PaperCard } from '../common/PaperCard';
import { formatDateLabel } from '../../utils/date';
import type { WeeklyCharacterSummaryResponse } from '../../types/dashboard';
import styles from './WeeklyCharacterSummaryCard.module.css';

export function WeeklyCharacterSummaryCard({ summary }: { summary: WeeklyCharacterSummaryResponse }) {
  return (
    <PaperCard accentColor={summary.color} className={styles.card}>
      <div className={styles.heading}>
        <div>
          <h3 className={styles.name}>{summary.name}</h3>
        </div>
        <CharacterStatusBadge status={summary.status} color={summary.color} />
      </div>

      <dl className={styles.stats}>
        <div>
          <dt>Total score</dt>
          <dd>{summary.totalScore}</dd>
        </div>
        <div>
          <dt>Average day</dt>
          <dd>{summary.averageDailyScore.toFixed(1)}</dd>
        </div>
        <div>
          <dt>Fed wishes</dt>
          <dd>{summary.completedWishCount}</dd>
        </div>
        <div>
          <dt>Hungry days</dt>
          <dd>{summary.hungryDaysCount}</dd>
        </div>
      </dl>

      <p className={styles.bestDay}>
        Best day: <strong>{formatDateLabel(summary.bestDay.date)}</strong> with {summary.bestDay.score} points.
      </p>
    </PaperCard>
  );
}
