import styles from './Feedback.module.css';

interface ErrorStateProps {
  message: string;
  onRetry?: () => void;
}

export function ErrorState({ message, onRetry }: ErrorStateProps) {
  return (
    <div className={styles.stateBox}>
      <p className={styles.stateTitle}>A page slipped loose</p>
      <p className={styles.stateMessage}>{message}</p>
      {onRetry ? (
        <button className={styles.actionButton} type="button" onClick={() => void onRetry()}>
          Try again gently
        </button>
      ) : null}
    </div>
  );
}
