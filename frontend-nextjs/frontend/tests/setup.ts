import '@testing-library/jest-dom';
import { server } from './mocks/server';

// Polyfill fetch/Response for MSW/node using cross-fetch
import fetch from 'cross-fetch';
if (!globalThis.fetch) globalThis.fetch = fetch;

// Polyfill Response, Request, Headers from undici
try {
  const undici = require('undici');
  if (!globalThis.Response) globalThis.Response = undici.Response;
  if (!globalThis.Request) globalThis.Request = undici.Request;
  if (!globalThis.Headers) globalThis.Headers = undici.Headers;
} catch (e) {
  // undici 可能未安装，忽略
}

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());
