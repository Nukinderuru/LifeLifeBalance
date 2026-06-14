import styles from './SectionTitle.module.css';

interface SectionTitleProps {
  title: string;
  caption?: string;
}

export function SectionTitle({ title, caption }: SectionTitleProps) {
  return (
    <div className={styles.sectionTitle}>
      <h3>{title}</h3>
      {caption ? <p>{caption}</p> : null}
    </div>
  );
}
