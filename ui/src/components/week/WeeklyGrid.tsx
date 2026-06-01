import { Fragment } from 'react';
import { formatDateLabel } from '../../utils/date';
import type { WeeklyDashboardResponse } from '../../types/dashboard';
import styles from './WeeklyGrid.module.css';

interface WeeklyGridProps {
  data: WeeklyDashboardResponse;
}

export function WeeklyGrid({ data }: WeeklyGridProps) {
  return (
    <div className={styles.scrollFrame}>
      <div className={styles.grid}>
        <div className={styles.corner}>Character</div>
        {data.days.map((day) => (
          <div key={day.date} className={styles.dayHeader}>
            {formatDateLabel(day.date)}
          </div>
        ))}

        {data.characters.map((character) => (
          <Fragment key={character.characterId}>
            <div key={`${character.characterId}-name`} className={styles.nameCell}>
              <span className={styles.nameSwatch} style={{ backgroundColor: character.color }} />
              <div>
                <strong>{character.name}</strong>
                <span>{character.status.toLowerCase()}</span>
              </div>
            </div>

            {data.days.map((day) => {
              const entry = day.characters.find((dayCharacter) => dayCharacter.characterId === character.characterId);

              return (
                <div
                  key={`${character.characterId}-${day.date}`}
                  className={styles.dayCell}
                  style={{ backgroundColor: `${character.color}18`, borderColor: `${character.color}30` }}
                >
                  <strong>{entry?.score ?? 0}</strong>
                  <span>{entry?.completedWishCount ?? 0} wishes</span>
                </div>
              );
            })}
          </Fragment>
        ))}
      </div>
    </div>
  );
}
