import { useCallback, useEffect, useState } from 'react';
import type { DependencyList, Dispatch, SetStateAction } from 'react';

interface AsyncState<T> {
  data: T | null;
  loading: boolean;
  error: string | null;
  reload: () => Promise<void>;
  setData: Dispatch<SetStateAction<T | null>>;
}

export function useAsyncData<T>(load: () => Promise<T>, dependencies: DependencyList = []): AsyncState<T> {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const reload = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const result = await load();
      setData(result);
    } catch (caughtError) {
      const message = caughtError instanceof Error ? caughtError.message : 'Something gentle failed to load.';
      setError(message);
    } finally {
      setLoading(false);
    }
  }, dependencies);

  useEffect(() => {
    void reload();
  }, [reload]);

  return { data, loading, error, reload, setData };
}
