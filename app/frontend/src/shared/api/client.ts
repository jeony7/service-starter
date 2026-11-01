export async function getJson<T>(path: string): Promise<T> {
  const response = await fetch(path, { headers: { Accept: 'application/json' } });
  if (!response.ok) throw new Error(`요청을 처리하지 못했습니다. (${response.status})`);
  return (await response.json()) as T;
}
