import { useState } from 'react';
import { getHealth } from './api';

export function ConnectionStatus() {
  const [pending, setPending] = useState(false);
  const [message, setMessage] = useState('서비스 연결을 확인할 수 있습니다.');

  async function checkConnection() {
    setPending(true);
    try {
      const result = await getHealth();
      if (result.status !== 'UP' || result.database !== 'UP') {
        throw new Error('서비스가 준비되지 않았습니다.');
      }
      setMessage(`${result.applicationName} 연결이 확인되었습니다.`);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : '연결을 확인하지 못했습니다.');
    } finally {
      setPending(false);
    }
  }

  return (
    <section className="flex flex-col items-start gap-4" aria-label="연결 상태">
      <button
        className="rounded-lg bg-stone-900 px-5 py-3 text-sm text-white disabled:opacity-50"
        disabled={pending}
        onClick={() => void checkConnection()}
        type="button"
      >
        {pending ? '확인 중…' : '연결 확인'}
      </button>
      <p className="text-sm text-stone-600" role="status" aria-live="polite">
        {message}
      </p>
    </section>
  );
}
