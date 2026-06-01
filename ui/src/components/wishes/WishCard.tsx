import { CharacterStatusBadge } from '../characters/CharacterStatusBadge';
import { PaperCard } from '../common/PaperCard';
import type { WishCategory } from '../../types/api';
import type { WishWithTodayCompletion } from '../../types/ui';
import styles from './WishCard.module.css';

interface WishCardProps {
  wish: WishWithTodayCompletion;
  color: string;
  busy: boolean;
  onComplete: (wish: WishWithTodayCompletion) => Promise<void>;
  onUndo: (wish: WishWithTodayCompletion) => Promise<void>;
  onEdit: (wish: WishWithTodayCompletion) => void;
  onDeactivate: (wish: WishWithTodayCompletion) => Promise<void>;
}

const categoryCopy: Record<WishCategory, string> = {
  DAILY: 'Daily',
  WEEKLY: 'Weekly',
  BIG: 'Big',
};

export function WishCard({ wish, color, busy, onComplete, onUndo, onEdit, onDeactivate }: WishCardProps) {
  const isCompleted = Boolean(wish.todayCompletion);

  return (
    <PaperCard accentColor={color} className={styles.card}>
      <div className={styles.header}>
        <div>
          <h4 className={styles.title}>{wish.title}</h4>
          <p className={styles.meta}>
            {wish.points} points · {categoryCopy[wish.category]}
          </p>
        </div>
        {wish.active ? (
          isCompleted ? (
            <CharacterStatusBadge status="CONTENT" color={color} />
          ) : null
        ) : (
          <span className={styles.resting}>Resting wish</span>
        )}
      </div>

      {wish.description ? <p className={styles.description}>{wish.description}</p> : null}

      <div className={styles.actions}>
        {wish.active ? (
          isCompleted ? (
            <>
              <button className={styles.primaryButton} disabled={busy} type="button" onClick={() => void onUndo(wish)}>
                Already fed · Undo
              </button>
              <button className={styles.secondaryButton} disabled={busy} type="button" onClick={() => onEdit(wish)}>
                Edit
              </button>
            </>
          ) : (
            <>
              <button className={styles.primaryButton} disabled={busy} type="button" onClick={() => void onComplete(wish)}>
                Feed today
              </button>
              <button className={styles.secondaryButton} disabled={busy} type="button" onClick={() => onEdit(wish)}>
                Edit
              </button>
              <button className={styles.ghostButton} disabled={busy} type="button" onClick={() => void onDeactivate(wish)}>
                Deactivate
              </button>
            </>
          )
        ) : (
          <button className={styles.secondaryButton} disabled={busy} type="button" onClick={() => onEdit(wish)}>
            Edit
          </button>
        )}
      </div>
    </PaperCard>
  );
}
