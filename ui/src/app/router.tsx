import { createBrowserRouter } from 'react-router-dom';
import { AppLayout } from '../components/layout/AppLayout';
import { CharacterDetailPage } from '../pages/CharacterDetailPage';
import { TodayDashboardPage } from '../pages/TodayDashboardPage';
import { WeeklyOverviewPage } from '../pages/WeeklyOverviewPage';
import { WishesPage } from '../pages/WishesPage';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <AppLayout />,
    children: [
      { index: true, element: <TodayDashboardPage /> },
      { path: 'wishes', element: <WishesPage /> },
      { path: 'characters/:id', element: <CharacterDetailPage /> },
      { path: 'week', element: <WeeklyOverviewPage /> },
    ],
  },
]);
