import type { CSSProperties } from 'react';
import { WishCard } from './WishCard';
import type { WishWithTodayCompletion, WishesByCharacterGroup } from '../../types/ui';
import styles from './WishGroup.module.css';

interface WishGroupProps {
  group: WishesByCharacterGroup;
  expanded: boolean;
  onToggle: () => void;
  busyWishId: string | null;
  onComplete: (wish: WishWithTodayCompletion) => Promise<void>;
  onUndo: (wish: WishWithTodayCompletion) => Promise<void>;
  onEdit: (wish: WishWithTodayCompletion) => void;
  onDeactivate: (wish: WishWithTodayCompletion) => Promise<void>;
}

export function WishGroup({ group, expanded, onToggle, busyWishId, onComplete, onUndo, onEdit, onDeactivate }: WishGroupProps) {
  const groupStyle = { '--group-color': group.color } as CSSProperties;

  return (
    <section className={styles.group}>
      <button className={styles.toggleButton} style={groupStyle} type="button" onClick={onToggle} aria-expanded={expanded}>
        <div>
          <h3 className={styles.title}>{group.characterName}</h3>
          <p className={styles.caption}>{expanded ? 'Offer one small thing, or simply notice what is resting.' : 'Open this page to see their wishes.'}</p>
        </div>
        <span className={styles.summary}>{group.wishes.length} wishes · {expanded ? 'hide' : 'show'}</span>
      </button>

      {expanded ? (
        group.wishes.length === 0 ? (
          <div className={styles.emptyBox}>No wishes here yet. You can add one gentle idea below.</div>
        ) : (
          <div className={styles.grid}>
            {group.wishes.map((wish) => (
              <WishCard
                key={wish.id}
                wish={wish}
                color={group.color}
                busy={busyWishId === wish.id}
                onComplete={onComplete}
                onUndo={onUndo}
                onEdit={onEdit}
                onDeactivate={onDeactivate}
              />
            ))}
          </div>
        )
      ) : null}
    </section>
  );
}
