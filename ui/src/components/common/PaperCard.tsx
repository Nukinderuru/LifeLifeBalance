import type { CSSProperties, ReactNode } from 'react';
import styles from './PaperCard.module.css';

interface PaperCardProps {
  children: ReactNode;
  className?: string;
  accentColor?: string;
  onClick?: () => void;
}

export function PaperCard({ children, className, accentColor, onClick }: PaperCardProps) {
  const style = accentColor ? ({ '--card-accent': accentColor } as CSSProperties) : undefined;

  return (
    <section
      className={className ? `${styles.card} ${className}` : styles.card}
      style={style}
      onClick={onClick}
      role={onClick ? 'button' : undefined}
      tabIndex={onClick ? 0 : undefined}
      onKeyDown={
        onClick
          ? (event) => {
              if (event.key === 'Enter' || event.key === ' ') {
                event.preventDefault();
                onClick();
              }
            }
          : undefined
      }
    >
      {children}
    </section>
  );
}
