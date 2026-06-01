import { useMemo, useState } from 'react';
import { getCharacters } from '../api/charactersApi';
import { createCompletion, deleteCompletion, getCompletionsByDate } from '../api/completionsApi';
import { createWish, deactivateWish, getWishes, updateWish } from '../api/wishesApi';
import { EmptyState } from '../components/common/EmptyState';
import { ErrorState } from '../components/common/ErrorState';
import { LoadingState } from '../components/common/LoadingState';
import { PageShell } from '../components/layout/PageShell';
import { WishForm } from '../components/wishes/WishForm';
import { WishGroup } from '../components/wishes/WishGroup';
import { useAsyncData } from '../hooks/useAsyncData';
import { useTodayDate } from '../hooks/useTodayDate';
import { groupWishesByCharacter } from '../utils/groupBy';
import type { CreateWishRequest, UpdateWishRequest, WishResponse } from '../types/wishes';
import type { WishWithTodayCompletion } from '../types/ui';
import styles from './WishesPage.module.css';

type WishesPageData = {
  characters: Awaited<ReturnType<typeof getCharacters>>;
  wishes: Awaited<ReturnType<typeof getWishes>>;
  completions: Awaited<ReturnType<typeof getCompletionsByDate>>;
};

export function WishesPage() {
  const today = useTodayDate();
  const [busyWishId, setBusyWishId] = useState<string | null>(null);
  const [formBusy, setFormBusy] = useState(false);
  const [editWish, setEditWish] = useState<WishResponse | null>(null);
  const [mutationError, setMutationError] = useState<string | null>(null);

  const { data, loading, error, reload } = useAsyncData<WishesPageData>(
    async () => {
      const [characters, wishes, completions] = await Promise.all([
        getCharacters(),
        getWishes(),
        getCompletionsByDate(today),
      ]);

      return { characters, wishes, completions };
    },
    [today],
  );

  const grouped = useMemo(() => {
    if (!data) {
      return [];
    }

    return groupWishesByCharacter(data.characters, data.wishes, data.completions);
  }, [data]);

  async function refreshData() {
    await reload();
  }

  async function handleCreateOrUpdateWish(payload: CreateWishRequest | UpdateWishRequest) {
    setFormBusy(true);
    setMutationError(null);

    try {
      if (editWish) {
        await updateWish(editWish.id, payload as UpdateWishRequest);
        setEditWish(null);
      } else {
        await createWish(payload as CreateWishRequest);
      }
      await refreshData();
    } catch (caughtError) {
      setMutationError(caughtError instanceof Error ? caughtError.message : 'This wish could not be saved right now.');
    } finally {
      setFormBusy(false);
    }
  }

  async function handleComplete(wish: WishWithTodayCompletion) {
    setBusyWishId(wish.id);
    setMutationError(null);
    try {
      await createCompletion({ wishId: wish.id, date: today, notes: null });
      await refreshData();
    } catch (caughtError) {
      setMutationError(caughtError instanceof Error ? caughtError.message : 'This wish could not be completed right now.');
    } finally {
      setBusyWishId(null);
    }
  }

  async function handleUndo(wish: WishWithTodayCompletion) {
    if (!wish.todayCompletion) return;

    setBusyWishId(wish.id);
    setMutationError(null);
    try {
      await deleteCompletion(wish.todayCompletion.id);
      await refreshData();
    } catch (caughtError) {
      setMutationError(caughtError instanceof Error ? caughtError.message : 'This completion could not be undone right now.');
    } finally {
      setBusyWishId(null);
    }
  }

  async function handleDeactivate(wish: WishWithTodayCompletion) {
    setBusyWishId(wish.id);
    setMutationError(null);
    try {
      await deactivateWish(wish.id);
      if (editWish?.id === wish.id) {
        setEditWish(null);
      }
      await refreshData();
    } catch (caughtError) {
      setMutationError(caughtError instanceof Error ? caughtError.message : 'This wish could not be set to rest right now.');
    } finally {
      setBusyWishId(null);
    }
  }

  return (
    <PageShell title="Wishes and small offerings" subtitle="Feed today, let some wishes rest, and keep the page compassionate.">
      <div className={styles.layout}>
        <aside className={styles.sidebar}>
          <WishForm characters={data?.characters ?? []} initialWish={editWish} busy={formBusy} onSubmit={handleCreateOrUpdateWish} onCancelEdit={() => setEditWish(null)} />
          {mutationError ? <ErrorState message={mutationError} /> : null}
        </aside>

        <section className={styles.content}>
          {loading ? <LoadingState message="Sorting wishes by character..." /> : null}
          {error ? <ErrorState message={error} onRetry={reload} /> : null}

          {!loading && !error && data ? (
            grouped.length > 0 ? (
              grouped.map((group) => (
                <WishGroup
                  key={group.characterId}
                  group={group}
                  busyWishId={busyWishId}
                  onComplete={handleComplete}
                  onUndo={handleUndo}
                  onEdit={(wish) => setEditWish(wish)}
                  onDeactivate={handleDeactivate}
                />
              ))
            ) : (
              <EmptyState title="No wishes yet" message="Add one small act of care to begin the page." />
            )
          ) : null}
        </section>
      </div>
    </PageShell>
  );
}
