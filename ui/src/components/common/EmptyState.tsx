import styles from './Feedback.module.css';

interface EmptyStateProps {
  title: string;
  message: string;
}

export function EmptyState({ title, message }: EmptyStateProps) {
  return (
    <div className={styles.stateBox}>
      <p className={styles.stateTitle}>{title}</p>
      <p className={styles.stateMessage}>{message}</p>
    </div>
  );
}
