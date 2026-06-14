import type { CharacterResponse } from '../types/characters';
import type { CompletionResponse } from '../types/completions';
import type { WishResponse } from '../types/wishes';
import type { WishesByCharacterGroup, WishWithTodayCompletion } from '../types/ui';

export function groupWishesByCharacter(
  characters: CharacterResponse[],
  wishes: WishResponse[],
  completions: CompletionResponse[],
): WishesByCharacterGroup[] {
  const completionsByWishId = new Map<string, CompletionResponse>(
    completions.map((completion) => [completion.wishId, completion]),
  );

  return characters.map((character) => {
    const characterWishes: WishWithTodayCompletion[] = wishes
      .filter((wish) => wish.characterId === character.id)
      .map((wish) => ({
        ...wish,
        todayCompletion: completionsByWishId.get(wish.id) ?? null,
      }));

    return {
      characterId: character.id,
      characterName: character.name,
      characterCode: character.code,
      color: character.color,
      wishes: characterWishes,
    };
  });
}
