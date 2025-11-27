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
  const { logout, user } = useAuth();
  const userName = user?.name?.split(' ')[0] || 'Usuário';

  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        tabBarIcon: ({ color, size }) => {
          let iconName: keyof typeof Feather.glyphMap = 'home';

          if (route.name === 'Início') {
            iconName = 'home';
          } else if (route.name === 'Categories') {
            iconName = 'tag';
            } else if (route.name === 'Gráficos') {
              iconName = 'bar-chart-2';
          }

          return <Feather name={iconName} size={size} color={color} />;
        },
        tabBarActiveTintColor: '#1d8037',
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
          color: '#2cb350ff',
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
        options={{ 
          title: `Olá, ${userName}`,
          tabBarLabel: 'Início'
        }}
      />
      <Tab.Screen 
        name="Categories" 
        component={CategoriesScreen}
        options={{ 
          title: `Olá, ${userName}`,
          tabBarLabel: 'Categorias'
        }}
      />
      <Tab.Screen 
        name="Gráficos" 
        component={ChartsScreen}
        options={{ 
          title: `Olá, ${userName}`,
          tabBarLabel: 'Gráficos'
        }}
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
        <ActivityIndicator size="large" color="#33cc5c" />
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
