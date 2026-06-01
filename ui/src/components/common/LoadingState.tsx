import styles from './Feedback.module.css';

export function LoadingState({ message = 'Gathering today\'s pages...' }: { message?: string }) {
  return (
    <div className={styles.stateBox}>
      <p className={styles.stateTitle}>Loading</p>
      <p className={styles.stateMessage}>{message}</p>
    </div>
  );
}
