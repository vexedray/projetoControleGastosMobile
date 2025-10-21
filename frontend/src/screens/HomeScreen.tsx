import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  SafeAreaView,
} from 'react-native';
import ExpenseForm from '../components/ExpenseForm';
import ExpenseList from '../components/ExpenseList';
import { getAllExpenses } from '../services/api';

interface Expense {
  id: number;
  valor: number;
  tipo: string;
  data: string;
}

export default function HomeScreen() {
  const [expenses, setExpenses] = useState<Expense[]>([]);
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => {
    fetchExpenses();
  }, []);

  const fetchExpenses = async () => {
    try {
      const data = await getAllExpenses();
      setExpenses(data);
    } catch (error) {
      console.error('Erro ao buscar gastos:', error);
    }
  };

  const onRefresh = async () => {
    setRefreshing(true);
    await fetchExpenses();
    setRefreshing(false);
  };

  const handleExpenseAdded = () => {
    fetchExpenses();
  };

  const renderHeader = () => (
    <View>
      <Text style={styles.title}>Controle de Gastos</Text>
      
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Adicionar Gasto</Text>
        <ExpenseForm onExpenseAdded={handleExpenseAdded} />
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Lista de Gastos</Text>
      </View>
    </View>
  );

  return (
    <SafeAreaView style={styles.container}>
      <ExpenseList 
        expenses={expenses} 
        onExpenseDeleted={fetchExpenses}
        refreshing={refreshing}
        onRefresh={onRefresh}
        ListHeaderComponent={renderHeader}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  scrollView: {
    flex: 1,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    textAlign: 'center',
    marginVertical: 20,
    color: '#333',
  },
  section: {
    backgroundColor: '#fff',
    margin: 15,
    padding: 15,
    borderRadius: 10,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 3.84,
    elevation: 5,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 15,
    color: '#333',
  },
});