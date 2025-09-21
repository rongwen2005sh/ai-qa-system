import { authAPI } from '@/lib/auth-api';
import type { LoginRequest, RegisterRequest } from '@/types/auth';

describe('AuthAPI', () => {
  describe('login', () => {
    it('成功登录时应返回用户信息和令牌', async () => {
      const credentials: LoginRequest = {
        username: 'testuser',
        password: 'password123',
      };

      const response = await authAPI.login(credentials);

      expect(response).toEqual({
        token: 'mock-jwt-token',
        user: {
          id: '1',
          username: 'testuser',
          email: 'test@example.com',
        },
      });
    });

    it('登录失败时应抛出错误', async () => {
      const credentials: LoginRequest = {
        username: 'wronguser',
        password: 'wrongpass',
      };

      await expect(authAPI.login(credentials)).rejects.toThrow('Invalid credentials');
    });
  });

  describe('register', () => {
    it('成功注册时应返回用户信息和令牌', async () => {
      const userData: RegisterRequest = {
        username: 'newuser',
        email: 'newuser@example.com',
        password: 'password123',
      };

      const response = await authAPI.register(userData);

      expect(response).toEqual({
        token: 'mock-jwt-token',
        user: {
          id: '1',
          username: 'newuser',
          email: 'newuser@example.com',
        },
      });
    });

    it('注册数据无效时应抛出错误', async () => {
      const userData: RegisterRequest = {
        username: '',
        email: '',
        password: '',
      };

      await expect(authAPI.register(userData)).rejects.toThrow('Invalid registration data');
    });
  });

  describe('getCurrentUser', () => {
    it('使用有效令牌时应返回用户信息', async () => {
      const response = await authAPI.getCurrentUser('mock-jwt-token');

      expect(response).toEqual({
        id: '1',
        username: 'testuser',
        email: 'test@example.com',
      });
    });

    it('使用无效令牌时应抛出错误', async () => {
      await expect(authAPI.getCurrentUser('invalid-token')).rejects.toThrow('Unauthorized');
    });
  });
});
