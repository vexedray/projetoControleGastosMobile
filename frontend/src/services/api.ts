import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

// ==================== TIPOS ====================

export interface User {
  id?: number;
  name: string;
  email: string;
  password?: string;
  createdAt?: string;
}

export interface Category {
  id?: number;
  name: string;
  description?: string;
  color?: string;
  icon?: string;
  createdAt?: string;
}

export interface Expense {
  id?: number;
  description: string;
  amount: number;
  date: string;
  categoryId?: number;  // Opcional, pois não vem na resposta
  userId?: number;      // Opcional, pois não vem na resposta
  category?: Category;  // Objeto completo vem do backend
  user?: User;          // Objeto completo vem do backend
}

// ==================== CONFIGURAÇÃO ====================

// Para emulador Android use: 10.0.2.2
// Para emulador iOS use: localhost
// Para dispositivo físico use o IP da sua máquina (ex: 192.168.1.100)
const API_BASE_URL = 'http://10.0.2.2:8083/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para adicionar token JWT automaticamente
api.interceptors.request.use(
  async (config) => {
    const token = await AsyncStorage.getItem('@expense_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    console.log('Request:', config.method?.toUpperCase(), config.url);
    console.log('Token presente:', !!token);
    return config;
  },
  (error) => {
    console.error('Request Error:', error);
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => {
    console.log('Response:', response.status, response.config.url);
    return response;
  },
  async (error) => {
    // Se receber 401/403, remove token e usuário do storage
    if (error.response?.status === 401 || error.response?.status === 403) {
      await AsyncStorage.removeItem('@expense_token');
      await AsyncStorage.removeItem('@expense_user');
    }
    console.error('Response Error:', error.response?.status, error.message);
    return Promise.reject(error);
  }
);

// ==================== USER API ====================

export const userApi = {
  getAll: async (): Promise<User[]> => {
    const response = await api.get('/users');
    return response.data;
  },

  getById: async (id: number): Promise<User> => {
    const response = await api.get(`/users/${id}`);
    return response.data;
  },

  getByEmail: async (email: string): Promise<User> => {
    const response = await api.get(`/users/email/${email}`);
    return response.data;
  },

  checkEmail: async (email: string): Promise<{ available: boolean }> => {
    const response = await api.get(`/users/check-email/${email}`);
    return response.data;
  },

  create: async (user: Omit<User, 'id' | 'createdAt'>): Promise<User> => {
    const response = await api.post('/users', user);
    return response.data;
  },

  update: async (id: number, user: Partial<User>): Promise<User> => {
    const response = await api.put(`/users/${id}`, user);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/users/${id}`);
  },
};

// ==================== CATEGORY API ====================

export const categoryApi = {
  getAll: async (): Promise<Category[]> => {
    const response = await api.get('/categories');
    return response.data;
  },

  getById: async (id: number): Promise<Category> => {
    const response = await api.get(`/categories/${id}`);
    return response.data;
  },

  create: async (category: Omit<Category, 'id' | 'createdAt'>): Promise<Category> => {
    const response = await api.post('/categories', category);
    return response.data;
  },

  update: async (id: number, category: Partial<Category>): Promise<Category> => {
    const response = await api.put(`/categories/${id}`, category);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/categories/${id}`);
  },
};

// ==================== EXPENSE API ====================

export const expenseApi = {
  getAll: async (): Promise<Expense[]> => {
    const response = await api.get('/expenses');
    return response.data;
  },

  getById: async (id: number): Promise<Expense> => {
    const response = await api.get(`/expenses/${id}`);
    return response.data;
  },

  getByUser: async (userId: number): Promise<Expense[]> => {
    const response = await api.get(`/expenses/user/${userId}`);
    return response.data;
  },

  getByCategory: async (categoryId: number): Promise<Expense[]> => {
    const response = await api.get(`/expenses/category/${categoryId}`);
    return response.data;
  },

  create: async (expense: Omit<Expense, 'id'>): Promise<Expense> => {
    const response = await api.post('/expenses', expense);
    return response.data;
  },

  update: async (id: number, expense: Partial<Expense>): Promise<Expense> => {
    const response = await api.put(`/expenses/${id}`, expense);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/expenses/${id}`);
  },
};

// ==================== EXPORTS LEGADOS (compatibilidade) ====================

export const getAllCategories = categoryApi.getAll;
export const createCategory = categoryApi.create;
export const getAllExpenses = expenseApi.getAll;
export const createExpense = expenseApi.create;
export const deleteExpense = expenseApi.delete;

export default api;