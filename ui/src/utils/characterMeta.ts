import type { CharacterCode } from '../types/api';
import type { CharacterThemeMeta } from '../types/ui';

export const CHARACTER_META: Record<CharacterCode, CharacterThemeMeta> = {
  MAYA: {
    code: 'MAYA',
    mood: 'tea steam and leaf shadows',
    accentGlow: 'rgba(107, 142, 107, 0.18)',
    prompt: 'Nature, silence, and a gentle place to land.',
  },
  ELINA: {
    code: 'ELINA',
    mood: 'ink notes and twilight study',
    accentGlow: 'rgba(74, 93, 143, 0.18)',
    prompt: 'Curiosity, patterns, and careful thought.',
  },
  TORA: {
    code: 'TORA',
    mood: 'warm sun and lively footsteps',
    accentGlow: 'rgba(201, 122, 64, 0.18)',
    prompt: 'Movement, strength, and spirited energy.',
  },
  DANA: {
    code: 'DANA',
    mood: 'golden chatter and city light',
    accentGlow: 'rgba(212, 176, 76, 0.18)',
    prompt: 'Connection, delight, and shared moments.',
  },
  NAOMI: {
    code: 'NAOMI',
    mood: 'rose silk and candlelight',
    accentGlow: 'rgba(200, 138, 160, 0.18)',
    prompt: 'Beauty, tenderness, and expressive comfort.',
  },
};
