import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import api from '../services/api';

interface User {
  id: number;
  name: string;
  email: string;
}

interface AuthContextData {
  user: User | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextData>({} as AuthContextData);

interface AuthProviderProps {
  children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadStoredUser();
  }, []);

  const loadStoredUser = async () => {
    try {
      const storedUser = await AsyncStorage.getItem('@expense_user');
      const storedToken = await AsyncStorage.getItem('@expense_token');
      
      if (storedUser && storedToken) {
        setUser(JSON.parse(storedUser));
        api.defaults.headers.common['Authorization'] = `Bearer ${storedToken}`;
      }
    } catch (error) {
      console.error('Erro ao carregar usuário:', error);
    } finally {
      setLoading(false);
    }
  };

  const login = async (email: string, password: string) => {
    try {
      const response = await api.post('/auth/login', { email, password });
      
      const { token, userId, email: userEmail, name } = response.data;
      
      if (!token || !userEmail || !name) {
        throw new Error('Dados de autenticação inválidos na resposta');
      }
      
      const userData = {
        id: userId || 0, 
        email: userEmail,
        name: name
      };
      
      await AsyncStorage.setItem('@expense_token', token);
      await AsyncStorage.setItem('@expense_user', JSON.stringify(userData));
      
      console.log('Token salvo com sucesso:', token.substring(0, 20) + '...');
      console.log('User ID:', userId);
      
      setUser(userData);
    } catch (error) {
      console.error('Erro ao fazer login:', error);
      throw error;
    }
  };


  const logout = async () => {
    try {
      await AsyncStorage.removeItem('@expense_user');
      await AsyncStorage.removeItem('@expense_token');
      delete api.defaults.headers.common['Authorization'];
      setUser(null);
    } catch (error) {
      console.error('Erro ao fazer logout:', error);
      throw error;
    }
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de um AuthProvider');
  }
  return context;
}
