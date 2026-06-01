import type { ReactNode } from 'react';
import styles from './PageShell.module.css';

interface PageShellProps {
  title: string;
  subtitle: string;
  actions?: ReactNode;
  children: ReactNode;
}

export function PageShell({ title, subtitle, actions, children }: PageShellProps) {
  return (
    <section className={styles.pageShell}>
      <div className={styles.headingRow}>
        <div>
          <h2 className={styles.title}>{title}</h2>
          <p className={styles.subtitle}>{subtitle}</p>
        </div>
        {actions ? <div className={styles.actions}>{actions}</div> : null}
      </div>
      {children}
    </section>
  );
}
