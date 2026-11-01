import { getJson } from '@/shared/api/client';
import type { HealthResponse } from './types';

export function getHealth(): Promise<HealthResponse> {
  return getJson<HealthResponse>('/api/health');
}
