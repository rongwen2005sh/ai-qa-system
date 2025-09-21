import { http, HttpResponse } from 'msw';

const API_BASE_URL = 'http://localhost:8080';

const mockUser = {
  id: '1',
  username: 'testuser',
  email: 'test@example.com',
};

export const handlers = [
  // 登录接口
  http.post(`${API_BASE_URL}/api/auth/login`, async ({ request }) => {
    const body = await request.json();
    
    if (body.username === 'testuser' && body.password === 'password123') {
      return HttpResponse.json({
        token: 'mock-jwt-token',
        user: mockUser,
      });
    }
    
    return new HttpResponse('Invalid credentials', { status: 401 });
  }),

  // 注册接口
  http.post(`${API_BASE_URL}/api/auth/register`, async ({ request }) => {
    const body = await request.json();
    
    if (body.username && body.email && body.password) {
      return HttpResponse.json({
        token: 'mock-jwt-token',
        user: {
          ...mockUser,
          username: body.username,
          email: body.email,
        },
      });
    }
    
    return new HttpResponse('Invalid registration data', { status: 400 });
  }),

  // 获取用户信息接口
  http.get(`${API_BASE_URL}/api/auth/me`, ({ request }) => {
    const authHeader = request.headers.get('Authorization');
    
    if (authHeader === 'Bearer mock-jwt-token') {
      return HttpResponse.json(mockUser);
    }
    
    return new HttpResponse('Unauthorized', { status: 401 });
  }),
];
