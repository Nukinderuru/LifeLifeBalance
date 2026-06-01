import { SectionTitle } from '../common/SectionTitle';
import { WishCard } from './WishCard';
import type { WishWithTodayCompletion, WishesByCharacterGroup } from '../../types/ui';
import styles from './WishGroup.module.css';

interface WishGroupProps {
  group: WishesByCharacterGroup;
  busyWishId: string | null;
  onComplete: (wish: WishWithTodayCompletion) => Promise<void>;
  onUndo: (wish: WishWithTodayCompletion) => Promise<void>;
  onEdit: (wish: WishWithTodayCompletion) => void;
  onDeactivate: (wish: WishWithTodayCompletion) => Promise<void>;
}

export function WishGroup({ group, busyWishId, onComplete, onUndo, onEdit, onDeactivate }: WishGroupProps) {
  return (
    <section className={styles.group}>
      <SectionTitle title={group.characterName} caption="Offer one small thing, or simply notice what is resting." />

      {group.wishes.length === 0 ? (
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
      )}
    </section>
  );
}
