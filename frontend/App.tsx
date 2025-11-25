import React, { useEffect } from 'react';
import { AppState } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

import { AuthProvider } from './src/contexts/AuthContext';
import AppNavigator from './src/navigation/AppNavigator';

export default function App() {
  useEffect(() => {
    // Limpa o armazenamento quando o app é iniciado
    const clearStorageOnStart = async () => {
      await AsyncStorage.removeItem('@expense_token');
      await AsyncStorage.removeItem('@expense_user');
    };
    
    clearStorageOnStart();

    // Limpa quando o app vai para segundo plano
    const subscription = AppState.addEventListener('change', (nextAppState) => {
      if (nextAppState === 'background' || nextAppState === 'inactive') {
        clearStorageOnStart();
      }
    });

    return () => {
      subscription.remove();
    };
  }, []);

  return (
    <AuthProvider>
      <AppNavigator />
    </AuthProvider>
  );
}