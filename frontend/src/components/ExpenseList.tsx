import React from 'react';
import {
  View,
  Text,
  FlatList,
  TouchableOpacity,
  StyleSheet,
  Alert,
} from 'react-native';
import { Feather } from '@expo/vector-icons';
import { expenseApi, Expense } from '../services/api';

interface ExpenseListProps {
  expenses: Expense[];
  onExpenseDeleted: () => void;
}

const ExpenseList: React.FC<ExpenseListProps> = ({ 
  expenses, 
  onExpenseDeleted,
}) => {
  const handleDelete = async (id: number) => {
    try {
      await expenseApi.delete(id);
      onExpenseDeleted();
      Alert.alert('Sucesso', 'Gasto deletado com sucesso!');
    } catch (error) {
      console.error('Erro ao deletar gasto:', error);
      Alert.alert('Erro', 'Não foi possível deletar o gasto');
    }
  };

  const confirmDelete = (id: number, description: string) => {
    Alert.alert(
      'Deletar Gasto',
      `Tem certeza que deseja deletar "${description}"?`,
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

  const renderItem = ({ item }: { item: Expense }) => {
    const categoryName = (item as any).categoryName || item.category?.name || 'Sem categoria';
    
    return (
      <View style={styles.item}>
        <View style={styles.itemContent}>
          <Text style={styles.description} numberOfLines={1}>
            {item.description}
          </Text>
          <View style={styles.categoryContainer}>
            <Feather name="tag" size={12} color="#33cc5c" />
            <Text style={styles.category}>
              {categoryName}
            </Text>
          </View>
          <Text style={styles.value}>R$ {item.amount.toFixed(2)}</Text>
          <View style={styles.dateContainer}>
            <Feather name="calendar" size={12} color="#6B7280" />
            <Text style={styles.date}>
              {new Date(item.date).toLocaleDateString('pt-BR')}
            </Text>
          </View>
        </View>
        <TouchableOpacity
          style={styles.deleteButton}
          onPress={() => item.id && confirmDelete(item.id, item.description)}
        >
          <Feather name="trash-2" size={18} color="#FFFFFF" />
        </TouchableOpacity>
      </View>
    );
  };

  const renderEmptyComponent = () => (
    <View style={styles.emptyContainer}>
      <Feather name="inbox" size={48} color="#D1D5DB" />
      <Text style={styles.emptyText}>Nenhum gasto registrado ainda</Text>
      <Text style={styles.emptySubtext}>
        Use o formulário acima para adicionar seu primeiro gasto
      </Text>
    </View>
  );

  return (
    <FlatList
      data={expenses}
      keyExtractor={(item) => item.id?.toString() || Math.random().toString()}
      renderItem={renderItem}
      style={styles.list}
      contentContainerStyle={styles.listContent}
      showsVerticalScrollIndicator={false}
      ListEmptyComponent={renderEmptyComponent}
      scrollEnabled={false}
    />
  );
};

const styles = StyleSheet.create({
  list: {
    flex: 1,
  },
  listContent: {
    paddingBottom: 10,
  },
  item: {
    backgroundColor: '#F9FAFB',
    padding: 16,
    marginBottom: 12,
    borderRadius: 8,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#E5E7EB',
  },
  itemContent: {
    flex: 1,
    marginRight: 12,
  },
  description: {
    fontSize: 16,
    fontWeight: '600',
    color: '#111827',
    marginBottom: 6,
  },
  categoryContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 6,
    gap: 4,
  },
  category: {
    fontSize: 13,
    color: '#33cc5c',
    fontWeight: '500',
  },
  value: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#EF4444',
    marginBottom: 6,
  },
  dateContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
  },
  date: {
    fontSize: 13,
    color: '#6B7280',
  },
  deleteButton: {
    backgroundColor: '#EF4444',
    paddingHorizontal: 12,
    paddingVertical: 10,
    borderRadius: 6,
    justifyContent: 'center',
    alignItems: 'center',
  },
  emptyContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 50,
    paddingHorizontal: 20,
  },
  emptyText: {
    fontSize: 16,
    fontWeight: '600',
    color: '#6B7280',
    textAlign: 'center',
    marginTop: 16,
    marginBottom: 8,
  },
  emptySubtext: {
    fontSize: 14,
    color: '#9CA3AF',
    textAlign: 'center',
  },
});

export default ExpenseList;