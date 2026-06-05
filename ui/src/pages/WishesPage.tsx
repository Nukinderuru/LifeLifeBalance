import { useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
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
import type { WishCategory } from '../types/api';
import type { CreateWishRequest, UpdateWishRequest, WishResponse } from '../types/wishes';
import type { WishWithTodayCompletion } from '../types/ui';
import styles from './WishesPage.module.css';

type WishesPageData = {
  characters: Awaited<ReturnType<typeof getCharacters>>;
  wishes: Awaited<ReturnType<typeof getWishes>>;
  completions: Awaited<ReturnType<typeof getCompletionsByDate>>;
};

type WishFilter = 'ALL' | WishCategory;

const wishFilters: Array<{ value: WishFilter; label: string }> = [
  { value: 'ALL', label: 'All wishes' },
  { value: 'DAILY', label: 'Daily' },
  { value: 'WEEKLY', label: 'Weekly' },
  { value: 'BIG', label: 'Big' },
];

const CREATE_WISH_CHARACTER_STORAGE_KEY = 'inner-council.create-wish-character-id';

function parseWishFilter(rawValue: string | null): WishFilter {
  return rawValue === 'DAILY' || rawValue === 'WEEKLY' || rawValue === 'BIG' ? rawValue : 'ALL';
}

export function WishesPage() {
  const today = useTodayDate();
  const [searchParams, setSearchParams] = useSearchParams();
  const [busyWishId, setBusyWishId] = useState<string | null>(null);
  const [formBusy, setFormBusy] = useState(false);
  const [editWish, setEditWish] = useState<WishResponse | null>(null);
  const [mutationError, setMutationError] = useState<string | null>(null);
  const [preferredCharacterId, setPreferredCharacterId] = useState<string>(() => {
    if (typeof window === 'undefined') {
      return '';
    }

    return window.localStorage.getItem(CREATE_WISH_CHARACTER_STORAGE_KEY) ?? '';
  });

  const activeFilter = parseWishFilter(searchParams.get('category'));

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

    return groupWishesByCharacter(data.characters, data.wishes, data.completions)
      .map((group) => ({
        ...group,
        wishes: activeFilter === 'ALL' ? group.wishes : group.wishes.filter((wish) => wish.category === activeFilter),
      }))
      .filter((group) => group.wishes.length > 0);
  }, [activeFilter, data]);

  async function refreshData() {
    await reload();
  }

  function handleFilterChange(filter: WishFilter) {
    const nextParams = new URLSearchParams(searchParams);

    if (filter === 'ALL') {
      nextParams.delete('category');
    } else {
      nextParams.set('category', filter);
    }

    setSearchParams(nextParams, { replace: true });
  }

  function handlePreferredCharacterChange(characterId: string) {
    setPreferredCharacterId(characterId);
    if (typeof window !== 'undefined') {
      window.localStorage.setItem(CREATE_WISH_CHARACTER_STORAGE_KEY, characterId);
    }
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
          <WishForm
            characters={data?.characters ?? []}
            initialWish={editWish}
            busy={formBusy}
            preferredCharacterId={preferredCharacterId}
            onPreferredCharacterChange={handlePreferredCharacterChange}
            onSubmit={handleCreateOrUpdateWish}
            onCancelEdit={() => setEditWish(null)}
          />
          {mutationError ? <ErrorState message={mutationError} /> : null}
        </aside>

        <section className={styles.content}>
          {loading ? <LoadingState message="Sorting wishes by character..." /> : null}
          {error ? <ErrorState message={error} onRetry={reload} /> : null}

          {!loading && !error && data ? (
            <>
              <div className={styles.filterBar}>
                <p className={styles.filterCopy}>Choose a gentler slice of the page.</p>
                <div className={styles.filterChips}>
                  {wishFilters.map((filter) => (
                    <button
                      key={filter.value}
                      type="button"
                      className={activeFilter === filter.value ? `${styles.filterChip} ${styles.filterChipActive}` : styles.filterChip}
                      onClick={() => handleFilterChange(filter.value)}
                    >
                      {filter.label}
                    </button>
                  ))}
                </div>
              </div>

              {grouped.length > 0 ? (
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
                <EmptyState
                  title={activeFilter === 'ALL' ? 'No wishes yet' : `No ${activeFilter.toLowerCase()} wishes yet`}
                  message={activeFilter === 'ALL' ? 'Add one small act of care to begin the page.' : 'There is nothing in this category right now. You can choose another view or add one below.'}
                />
              )}
            </>
          ) : null}
        </section>
      </div>
    </PageShell>
  );
}
