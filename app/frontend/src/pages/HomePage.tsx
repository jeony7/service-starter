import { ConnectionStatus } from '@/features/health/ConnectionStatus';

export function HomePage() {
  return (
    <main className="mx-auto flex min-h-screen max-w-xl flex-col justify-center gap-8 px-6 py-12">
      <header>
        <p className="mb-3 text-sm tracking-widest text-stone-500">APPLICATION</p>
        <h1 className="text-4xl font-medium tracking-tight">Service</h1>
        <p className="mt-4 text-stone-600">여기에서 서비스를 시작하세요.</p>
      </header>
      <ConnectionStatus />
      {import.meta.env.DEV && (
        <a className="w-fit text-sm underline underline-offset-4" href="/h2-console/">
          개발용 DB 콘솔
        </a>
      )}
    </main>
  );
}
