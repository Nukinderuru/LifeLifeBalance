import { getStatusCopy } from '../../utils/status';
import type { CharacterStatus } from '../../types/api';
import styles from './CharacterStatusBadge.module.css';

interface CharacterStatusBadgeProps {
  status: CharacterStatus;
  color: string;
}

export function CharacterStatusBadge({ status, color }: CharacterStatusBadgeProps) {
  return (
    <span className={styles.badge} style={{ borderColor: `${color}55`, color, backgroundColor: `${color}12` }}>
      {getStatusCopy(status)}
    </span>
  );
}
