import DanaImage from './Dana.png';
import ElinaImage from './Elina.png';
import MayaImage from './Maya.png';
import NaomiImage from './Naomi.png';
import ToraImage from './Tora.png';
import type { CharacterCode } from '../types/api';

export const characterImages: Record<CharacterCode, string> = {
  MAYA: MayaImage,
  ELINA: ElinaImage,
  TORA: ToraImage,
  DANA: DanaImage,
  NAOMI: NaomiImage,
};
