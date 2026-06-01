import type { CharacterStatus } from '../types/api';
import type { StatusLabel } from '../types/ui';

export const STATUS_LABELS: StatusLabel = {
  STARVING: 'very hungry',
  HUNGRY: 'needs attention',
  CONTENT: 'content',
  HAPPY: 'happy',
  FLOURISHING: 'flourishing',
};

export function getStatusCopy(status: CharacterStatus): string {
  return STATUS_LABELS[status];
}
