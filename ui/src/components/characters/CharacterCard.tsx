import { useNavigate } from 'react-router-dom';
import { PaperCard } from '../common/PaperCard';
import { CharacterPortrait } from './CharacterPortrait';
import { CharacterStatusBadge } from './CharacterStatusBadge';
import type { TodayCharacterDashboardResponse } from '../../types/dashboard';
import styles from './CharacterCard.module.css';

interface CharacterCardProps {
  character: TodayCharacterDashboardResponse;
}

export function CharacterCard({ character }: CharacterCardProps) {
  const navigate = useNavigate();

  return (
    <PaperCard accentColor={character.color} className={styles.card} onClick={() => navigate(`/characters/${character.id}`)}>
      <CharacterPortrait code={character.code} color={character.color} />

      <div className={styles.headingRow}>
        <div>
          <h3 className={styles.name}>{character.name}</h3>
        </div>
        <CharacterStatusBadge status={character.status} color={character.color} />
      </div>

      <dl className={styles.stats}>
        <div>
          <dt>Today</dt>
          <dd>{character.dailyScore}</dd>
        </div>
        <div>
          <dt>This week</dt>
          <dd>{character.weeklyScore}</dd>
        </div>
        <div>
          <dt>Fed wishes</dt>
          <dd>{character.completedWishCount}</dd>
        </div>
      </dl>
    </PaperCard>
  );
}
