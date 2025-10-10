import React from 'react';
import {
  View,
  Text,
  FlatList,
  TouchableOpacity,
  StyleSheet,
  Alert,
} from 'react-native';
import { deleteExpense } from '../services/api';

interface Expense {
  id: number;
  valor: number;
  tipo: string;
  data: string;
}

interface ExpenseListProps {
  expenses: Expense[];
  onExpenseDeleted: () => void;
}

const ExpenseList: React.FC<ExpenseListProps> = ({ expenses, onExpenseDeleted }) => {
  const handleDelete = async (id: number) => {
    try {
      await deleteExpense(id);
      onExpenseDeleted();
      Alert.alert('Sucesso', 'Gasto deletado com sucesso!');
    } catch (error) {
      console.error('Erro ao deletar gasto:', error);
      Alert.alert('Erro', 'Não foi possível deletar o gasto');
    }
  };

  const confirmDelete = (id: number) => {
    Alert.alert(
      'Deletar Gasto',
      'Tem certeza que deseja deletar este gasto?',
      [
        { text: 'Cancelar', style: 'cancel' },
        { text: 'Deletar', style: 'destructive', onPress: () => handleDelete(id) },
      ]
    );
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
  };

  const formatType = (type: string) => {
    return type.charAt(0).toUpperCase() + type.slice(1);
  };

  const renderItem = ({ item }: { item: Expense }) => (
    <View style={styles.item}>
      <View style={styles.itemContent}>
        <Text style={styles.type}>{formatType(item.tipo)}</Text>
        <Text style={styles.value}>R$ {item.valor.toFixed(2)}</Text>
        <Text style={styles.date}>{formatDate(item.data)}</Text>
      </View>
      <TouchableOpacity
        style={styles.deleteButton}
        onPress={() => confirmDelete(item.id)}
      >
        <Text style={styles.deleteButtonText}>Deletar</Text>
      </TouchableOpacity>
    </View>
  );

  if (expenses.length === 0) {
    return (
      <View style={styles.emptyContainer}>
        <Text style={styles.emptyText}>Nenhum gasto registrado ainda</Text>
      </View>
    );
  }

  return (
    <FlatList
      data={expenses}
      keyExtractor={(item) => item.id.toString()}
      renderItem={renderItem}
      style={styles.list}
      showsVerticalScrollIndicator={false}
    />
  );
};

const styles = StyleSheet.create({
  list: {
    marginTop: 20,
  },
  item: {
    backgroundColor: '#fff',
    padding: 15,
    marginVertical: 5,
    borderRadius: 8,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.22,
    shadowRadius: 2.22,
  },
  itemContent: {
    flex: 1,
  },
  type: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 4,
  },
  value: {
    fontSize: 18,
    fontWeight: '600',
    color: '#e74c3c',
    marginBottom: 4,
  },
  date: {
    fontSize: 14,
    color: '#666',
  },
  deleteButton: {
    backgroundColor: '#e74c3c',
    paddingHorizontal: 15,
    paddingVertical: 8,
    borderRadius: 6,
  },
  deleteButtonText: {
    color: '#fff',
    fontWeight: 'bold',
    fontSize: 14,
  },
  emptyContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 50,
  },
  emptyText: {
    fontSize: 16,
    color: '#666',
    textAlign: 'center',
  },
});

export default ExpenseList;