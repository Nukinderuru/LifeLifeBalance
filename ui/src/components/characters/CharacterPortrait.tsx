import { CHARACTER_META } from '../../utils/characterMeta';
import { characterImages } from '../../images/characterImages';
import type { CharacterCode } from '../../types/api';
import styles from './CharacterPortrait.module.css';

interface CharacterPortraitProps {
  code: CharacterCode;
  color: string;
}

export function CharacterPortrait({ code, color }: CharacterPortraitProps) {
  const meta = CHARACTER_META[code];
  const image = characterImages[code];

  return (
    <div className={styles.portrait} style={{ background: `radial-gradient(circle at top, ${meta.accentGlow}, rgba(255,255,255,0))` }}>
      <div className={styles.orbit} style={{ borderColor: `${color}50` }} />
      <div className={styles.frame} style={{ backgroundColor: `${color}18`, borderColor: `${color}55` }}>
        <img className={styles.image} src={image} alt={`${code.toLowerCase()} portrait`} />
      </div>
      <p className={styles.caption}>{meta.mood}</p>
    </div>
  );
}
