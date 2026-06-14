import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import type { CharacterResponse } from '../../types/characters';
import type { WishCategory } from '../../types/api';
import type { CreateWishRequest, UpdateWishRequest, WishResponse } from '../../types/wishes';
import styles from './WishForm.module.css';

type WishDraft = {
  characterId: string;
  title: string;
  description: string;
  points: string;
  category: WishCategory;
  active: boolean;
};

interface WishFormProps {
  characters: CharacterResponse[];
  initialWish?: WishResponse | null;
  busy: boolean;
  preferredCharacterId?: string;
  onPreferredCharacterChange?: (characterId: string) => void;
  onSubmit: (payload: CreateWishRequest | UpdateWishRequest) => Promise<void>;
  onCancelEdit?: () => void;
}

const defaultDraft = (characterId = ''): WishDraft => ({
  characterId,
  title: '',
  description: '',
  points: '5',
  category: 'DAILY',
  active: true,
});

export function WishForm({ characters, initialWish, busy, preferredCharacterId, onPreferredCharacterChange, onSubmit, onCancelEdit }: WishFormProps) {
  const [draft, setDraft] = useState<WishDraft>(defaultDraft(preferredCharacterId ?? characters[0]?.id ?? ''));
  const isEditing = Boolean(initialWish);

  useEffect(() => {
    if (initialWish) {
      setDraft({
        characterId: initialWish.characterId,
        title: initialWish.title,
        description: initialWish.description ?? '',
        points: String(initialWish.points),
        category: initialWish.category,
        active: initialWish.active,
      });
      return;
    }

    const fallbackCharacterId = preferredCharacterId && characters.some((character) => character.id === preferredCharacterId)
      ? preferredCharacterId
      : (characters[0]?.id ?? '');
    setDraft(defaultDraft(fallbackCharacterId));
  }, [characters, initialWish, preferredCharacterId]);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    await onSubmit({
      characterId: draft.characterId,
      title: draft.title.trim(),
      description: draft.description.trim() || null,
      points: Number(draft.points),
      category: draft.category,
      active: draft.active,
    });

    if (!isEditing) {
      const fallbackCharacterId = preferredCharacterId && characters.some((character) => character.id === preferredCharacterId)
        ? preferredCharacterId
        : (characters[0]?.id ?? '');
      setDraft(defaultDraft(fallbackCharacterId));
    }
  }

  return (
    <form className={styles.form} onSubmit={handleSubmit}>
      <div className={styles.headingRow}>
        <div>
          <h3 className={styles.title}>{isEditing ? 'Tend this wish' : 'Add a new wish'}</h3>
          <p className={styles.subtitle}>Small moments count too.</p>
        </div>
        {isEditing && onCancelEdit ? (
          <button className={styles.ghostButton} type="button" onClick={onCancelEdit}>
            Close edit
          </button>
        ) : null}
      </div>

      <div className={styles.grid}>
        <label>
          Character
          <select
            value={draft.characterId}
            onChange={(event) => {
              const characterId = event.target.value;
              setDraft((current) => ({ ...current, characterId }));
              if (!isEditing) {
                onPreferredCharacterChange?.(characterId);
              }
            }}
          >
            {characters.map((character) => (
              <option key={character.id} value={character.id}>
                {character.name}
              </option>
            ))}
          </select>
        </label>

        <label>
          Title
          <input value={draft.title} onChange={(event) => setDraft((current) => ({ ...current, title: event.target.value }))} required />
        </label>

        <label>
          Points
          <input
            min="1"
            type="number"
            value={draft.points}
            onChange={(event) => setDraft((current) => ({ ...current, points: event.target.value }))}
            required
          />
        </label>

        <label>
          Category
          <select value={draft.category} onChange={(event) => setDraft((current) => ({ ...current, category: event.target.value as WishCategory }))}>
            <option value="DAILY">Daily</option>
            <option value="WEEKLY">Weekly</option>
            <option value="BIG">Big</option>
          </select>
        </label>

        <label className={styles.descriptionField}>
          Description
          <textarea value={draft.description} onChange={(event) => setDraft((current) => ({ ...current, description: event.target.value }))} rows={4} />
        </label>

        {isEditing ? (
          <label className={styles.checkboxField}>
            <input type="checkbox" checked={draft.active} onChange={(event) => setDraft((current) => ({ ...current, active: event.target.checked }))} />
            Keep this wish active
          </label>
        ) : null}
      </div>

      <button className={styles.submitButton} disabled={busy} type="submit">
        {busy ? 'Saving gently...' : isEditing ? 'Save wish' : 'Create wish'}
      </button>
    </form>
  );
}
