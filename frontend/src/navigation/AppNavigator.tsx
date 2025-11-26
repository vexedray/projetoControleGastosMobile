import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { Feather } from '@expo/vector-icons';
import { ActivityIndicator, View, StyleSheet, TouchableOpacity } from 'react-native';

import HomeScreen from '../screens/HomeScreen';
import CategoriesScreen from '../screens/CategoriesScreen';
import LoginScreen from '../screens/LoginScreen';
import RegisterScreen from '../screens/RegisterScreen';
import { useAuth } from '../contexts/AuthContext';
import ChartsScreen from '../screens/ChartsScreen';

const Tab = createBottomTabNavigator();
const Stack = createNativeStackNavigator();

function MainTabNavigator() {
  const { logout } = useAuth();

  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        tabBarIcon: ({ color, size }) => {
          let iconName: keyof typeof Feather.glyphMap = 'home';

          if (route.name === 'Início') {
            iconName = 'home';
          } else if (route.name === 'Categories') {
            iconName = 'tag';
          }

          return <Feather name={iconName} size={size} color={color} />;
        },
        tabBarActiveTintColor: '#3B82F6',
        tabBarInactiveTintColor: '#9CA3AF',
        tabBarStyle: {
          backgroundColor: '#FFFFFF',
          borderTopWidth: 1,
          borderTopColor: '#E5E7EB',
          height: 60,
          paddingBottom: 8,
          paddingTop: 8,
        },
        headerStyle: {
          backgroundColor: '#FFFFFF',
          elevation: 0,
          shadowOpacity: 0,
          borderBottomWidth: 1,
          borderBottomColor: '#E5E7EB',
        },
        headerTitleStyle: {
          fontWeight: 'bold',
          fontSize: 18,
        },
        headerRight: () => (
          <TouchableOpacity
            style={styles.logoutButton}
            onPress={logout}
            activeOpacity={0.7}
          >
            <Feather name="log-out" size={20} color="#EF4444" />
          </TouchableOpacity>
        ),
      })}
    >
      <Tab.Screen 
        name="Início" 
        component={HomeScreen}
        options={{ title: 'Controle de Gastos' }}
      />
      <Tab.Screen 
        name="Categories" 
        component={CategoriesScreen}
        options={{ title: 'Categorias' }}
      />
      <Tab.Screen 
        name="Gráficos" 
        component={ChartsScreen}
        options={{ title: 'Análise de Gastos' }}
      />
    </Tab.Navigator>
    
  );
}

function AuthStackNavigator() {
  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      <Stack.Screen name="Login" component={LoginScreen} />
      <Stack.Screen name="Register" component={RegisterScreen} />
    </Stack.Navigator>
  );
}

export default function AppNavigator() {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#3B82F6" />
      </View>
    );
  }

  return (
    <NavigationContainer>
      {user ? <MainTabNavigator /> : <AuthStackNavigator />}
    </NavigationContainer>
  );
}

const styles = StyleSheet.create({
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F9FAFB',
  },
  logoutButton: {
    marginRight: 16,
    padding: 8,
  },
});
