import { NavLink } from 'react-router-dom';
import styles from './Header.module.css';

const navItems = [
  { to: '/', label: 'Today' },
  { to: '/wishes', label: 'Wishes' },
  { to: '/week', label: 'Week' },
];

export function Header() {
  return (
    <header className={styles.header}>
      <div className={styles.brandBlock}>
        <p className={styles.kicker}>A warm journal for gentle noticing</p>
        <h1 className={styles.title}>Inner Council</h1>
        <p className={styles.subtitle}>Everyone deserves a little attention.</p>
      </div>

      <nav className={styles.nav} aria-label="Primary navigation">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) => (isActive ? `${styles.link} ${styles.active}` : styles.link)}
          >
            {item.label}
          </NavLink>
        ))}
      </nav>
    </header>
  );
}
