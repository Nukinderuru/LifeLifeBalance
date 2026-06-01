import { Outlet } from 'react-router-dom';
import { Header } from './Header';
import styles from './AppLayout.module.css';

export function AppLayout() {
  return (
    <div className={styles.page}>
      <div className={styles.texture} />
      <Header />
      <main className={styles.main}>
        <Outlet />
      </main>
    </div>
  );
}
