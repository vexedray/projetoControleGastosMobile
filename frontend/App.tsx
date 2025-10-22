import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Ionicons } from '@expo/vector-icons';
import CategoriesScreen from './src/screens/CategoriesScreen';
import HomeScreen from './src/screens/HomeScreen';

const Tab = createBottomTabNavigator();

export default function App() {
  return (
    <NavigationContainer>
      <Tab.Navigator
        screenOptions={({ route }) => ({
          tabBarIcon: ({ focused, color, size }) => {
            let iconName: keyof typeof Ionicons.glyphMap;

            if (route.name === 'Categorias') {
              iconName = focused ? 'list' : 'list-outline';
            } else if (route.name === 'Gastos') {
              iconName = focused ? 'wallet' : 'wallet-outline';
            } else {
              iconName = 'help';
            }

            return <Ionicons name={iconName} size={size} color={color} />;
          },
          tabBarActiveTintColor: '#007AFF',
          tabBarInactiveTintColor: 'gray',
          headerShown: false,
        })}
      >
        <Tab.Screen 
          name="Categorias" 
          component={CategoriesScreen}
          options={{
            tabBarLabel: 'Categorias',
          }}
        />
        <Tab.Screen 
          name="Gastos" 
          component={HomeScreen}
          options={{
            tabBarLabel: 'Gastos',
          }}
        />
      </Tab.Navigator>
    </NavigationContainer>
  );
}