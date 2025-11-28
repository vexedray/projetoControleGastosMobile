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

interface Category {
  id: number;
  name: string;
  description?: string;
  color?: string;
}

interface ExpenseListProps {
  expenses: Expense[];
  onExpenseDeleted: () => void;
  onExpenseEdit: (expense: Expense) => void;
  categories: Category[];
}

export default function ExpenseList({ 
  expenses, 
  onExpenseDeleted, 
  onExpenseEdit,
  categories 
}: ExpenseListProps) {
  
  const handleDelete = (id: number) => {
    Alert.alert(
      'Confirmar exclusão',
      'Deseja realmente excluir este gasto?',
      [
        {
          text: 'Cancelar',
          style: 'cancel',
        },
        {
          text: 'Excluir',
          style: 'destructive',
          onPress: async () => {
            try {
              await expenseApi.delete(id);
              Alert.alert('Sucesso', 'Gasto excluído com sucesso!');
              onExpenseDeleted();
            } catch (error) {
              console.error('Erro ao excluir gasto:', error);
              Alert.alert('Erro', 'Não foi possível excluir o gasto');
            }
          },
        },
      ]
    );
  };

  const getCategoryById = (categoryId: number) => {
    return categories.find(cat => cat.id === categoryId);
  };

  const formatCurrency = (value: number) => {
    return value.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    });
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
  };

  const renderExpenseItem = ({ item }: { item: Expense }) => {
    const category = getCategoryById(item.categoryId || 0);
    
    return (
      <View style={styles.expenseCard}>
        <View style={styles.expenseHeader}>
          <View style={styles.expenseMainInfo}>
            <View style={styles.categoryBadge}>
              <View 
                style={[
                  styles.categoryDot, 
                  { backgroundColor: category?.color || '#22C55E' }
                ]} 
              />
              <Text style={styles.categoryText}>
                {category?.name || 'Sem categoria'}
              </Text>
            </View>
            <Text style={styles.expenseDescription}>{item.description}</Text>
          </View>
          <View style={styles.expenseAmountContainer}>
            <Text style={styles.expenseAmount}>
              {formatCurrency(item.amount)}
            </Text>
            <Text style={styles.expenseDate}>
              {formatDate(item.date)}
            </Text>
          </View>
        </View>
        
        <View style={styles.expenseActions}>
          <TouchableOpacity
            style={styles.actionButton}
            onPress={() => onExpenseEdit(item)}
            activeOpacity={0.7}
          >
            <Feather name="edit-2" size={18} color="#6B7280" />
            <Text style={styles.actionButtonText}>Editar</Text>
          </TouchableOpacity>
          
          <TouchableOpacity
            style={[styles.actionButton, styles.deleteButton]}
            onPress={() => item.id && handleDelete(item.id)}
            activeOpacity={0.7}
          >
            <Feather name="trash-2" size={18} color="#EF4444" />
            <Text style={[styles.actionButtonText, styles.deleteButtonText]}>
              Excluir
            </Text>
          </TouchableOpacity>
        </View>
      </View>
    );
  };

  if (expenses.length === 0) {
    return (
      <View style={styles.emptyState}>
        <Feather name="inbox" size={48} color="#9CA3AF" />
        <Text style={styles.emptyText}>Nenhum gasto cadastrado</Text>
        <Text style={styles.emptySubtext}>
          Adicione seu primeiro gasto usando o formulário acima
        </Text>
      </View>
    );
  }

  return (
    <FlatList
      data={expenses}
      renderItem={renderExpenseItem}
      keyExtractor={(item) => (item.id || 0).toString()}
      contentContainerStyle={styles.listContent}
      scrollEnabled={false}
    />
  );
}

const styles = StyleSheet.create({
  listContent: {
    paddingBottom: 20,
  },
  expenseCard: {
    backgroundColor: '#F9FAFB',
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: '#E5E7EB',
  },
  expenseHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 12,
  },
  expenseMainInfo: {
    flex: 1,
    marginRight: 16,
  },
  categoryBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 8,
  },
  categoryDot: {
    width: 10,
    height: 10,
    borderRadius: 5,
    marginRight: 8,
  },
  categoryText: {
    fontSize: 12,
    color: '#6B7280',
    fontWeight: '600',
    textTransform: 'uppercase',
    letterSpacing: 0.5,
  },
  expenseDescription: {
    fontSize: 16,
    fontWeight: '600',
    color: '#111827',
    marginBottom: 4,
  },
  expenseAmountContainer: {
    alignItems: 'flex-end',
  },
  expenseAmount: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#EF4444',
    marginBottom: 4,
  },
  expenseDate: {
    fontSize: 12,
    color: '#6B7280',
  },
  expenseActions: {
    flexDirection: 'row',
    gap: 8,
    paddingTop: 12,
    borderTopWidth: 1,
    borderTopColor: '#E5E7EB',
  },
  actionButton: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#FFFFFF',
    paddingVertical: 10,
    paddingHorizontal: 16,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#E5E7EB',
    gap: 6,
  },
  actionButtonText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#6B7280',
  },
  deleteButton: {
    borderColor: '#FEE2E2',
    backgroundColor: '#FEF2F2',
  },
  deleteButtonText: {
    color: '#EF4444',
  },
  emptyState: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 60,
    paddingHorizontal: 20,
  },
  emptyText: {
    fontSize: 16,
    fontWeight: '600',
    color: '#6B7280',
    marginTop: 16,
    marginBottom: 8,
  },
  emptySubtext: {
    fontSize: 14,
    color: '#9CA3AF',
    textAlign: 'center',
  },
});