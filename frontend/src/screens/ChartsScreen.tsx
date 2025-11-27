import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  Dimensions,
  ActivityIndicator,
} from 'react-native';
import { PieChart, BarChart } from 'react-native-chart-kit';
import { useFocusEffect } from '@react-navigation/native';
import api from '../services/api';

const screenWidth = Dimensions.get('window').width;

interface Category {
  id: number;
  name: string;
  description: string;
  color: string;
  icon: string;
}

interface CategoryExpense {
  category: string;
  total: number;
  color: string;
  icon: string;
}

// Helper para extrair dados HATEOAS
const extractHateoasData = <T,>(response: any): T[] => {
  console.log('Extracting HATEOAS data:', JSON.stringify(response, null, 2));
  
  if (Array.isArray(response)) {
    return response;
  }
  
  if (response._embedded) {
    const keys = Object.keys(response._embedded);
    if (keys.length > 0 && Array.isArray(response._embedded[keys[0]])) {
      return response._embedded[keys[0]];
    }
  }
  
  if (response.content && Array.isArray(response.content)) {
    return response.content;
  }
  
  return [];
};

export default function ChartsScreen() {
  const [loading, setLoading] = useState(true);
  const [categoryData, setCategoryData] = useState<CategoryExpense[]>([]);
  const [totalExpenses, setTotalExpenses] = useState(0);

  useFocusEffect(
    React.useCallback(() => {
      fetchExpenseData();
    }, [])
  );

  const fetchExpenseData = async () => {
    try {
      setLoading(true);
      console.log('=== FETCHING CHART DATA ===');
      
      // Busca categorias para obter as cores
      const categoriesResponse = await api.get('/categories');
      console.log('Categories Response:', JSON.stringify(categoriesResponse.data, null, 2));
      
      const categories: Category[] = extractHateoasData<Category>(categoriesResponse.data);
      console.log('Extracted Categories:', categories);
      
      // Cria um mapa de categorias por ID e por nome
      const categoryByIdMap = new Map<number, Category>();
      const categoryByNameMap = new Map<string, Category>();
      
      categories.forEach((cat) => {
        categoryByIdMap.set(cat.id, cat);
        categoryByNameMap.set(cat.name, cat);
      });

      // Busca despesas
      const expensesResponse = await api.get('/expenses');
      console.log('Expenses Response:', JSON.stringify(expensesResponse.data, null, 2));
      
      const expenses = extractHateoasData<any>(expensesResponse.data);
      console.log('Extracted Expenses:', expenses);
      console.log('Number of expenses:', expenses.length);

      if (expenses.length === 0) {
        console.log('No expenses found');
        setCategoryData([]);
        setTotalExpenses(0);
        setLoading(false);
        return;
      }

      // Agrupa gastos por categoria
      const categoryExpenseMap = new Map<string, number>();
      
      expenses.forEach((expense: any) => {
        const categoryName = expense.categoryName || 'Sem Categoria';
        const amount = parseFloat(expense.amount) || 0;
        
        console.log(`Processing expense: ${categoryName} - R$ ${amount}`);
        
        if (categoryExpenseMap.has(categoryName)) {
          categoryExpenseMap.set(categoryName, categoryExpenseMap.get(categoryName)! + amount);
        } else {
          categoryExpenseMap.set(categoryName, amount);
        }
      });

      console.log('Category Expense Map:', Array.from(categoryExpenseMap.entries()));

      // Cores padrão caso a categoria não tenha cor definida
      const defaultColors = ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'];
      let colorIndex = 0;

      // Converte para array e usa as cores do banco
      const data: CategoryExpense[] = Array.from(categoryExpenseMap.entries()).map(
        ([categoryName, total]) => {
          const category = categoryByNameMap.get(categoryName);
          let color = defaultColors[colorIndex++ % defaultColors.length];
          
          if (category?.color) {
            color = category.color.startsWith('#') ? category.color : `#${category.color}`;
          }
          
          const item = {
            category: categoryName,
            total,
            color,
            icon: category?.icon || '💰',
          };
          
          console.log('Created category data:', item);
          return item;
        }
      );

      console.log('Final category data:', data);
      console.log('Number of categories:', data.length);

      const total = data.reduce((sum, item) => sum + item.total, 0);
      console.log('Total expenses:', total);

      setCategoryData(data);
      setTotalExpenses(total);
    } catch (error) {
      console.error('Erro ao buscar dados:', error);
      if (error instanceof Error) {
        console.error('Error message:', error.message);
        console.error('Error stack:', error.stack);
      }
    } finally {
      setLoading(false);
    }
  };

  const pieChartData = categoryData.map((item) => ({
    name: item.category,
    amount: item.total,
    color: item.color,
    legendFontColor: '#000000ff',
    legendFontSize: 12,
  }));

  const barChartData = {
    labels: categoryData.map((item) => item.category.substring(0, 8)),
    datasets: [
      {
        data: categoryData.length > 0 ? categoryData.map((item) => item.total) : [0],
      },
    ],
  };

  console.log('Rendering with:', {
    loading,
    categoryDataLength: categoryData.length,
    totalExpenses,
  });

  if (loading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#33cc5c" />
        <Text style={{ marginTop: 16, color: '#6B7280' }}>Carregando dados...</Text>
      </View>
    );
  }

  return (
    <ScrollView style={styles.container}>
      {categoryData.length > 0 ? (
        <>
          <View style={styles.totalCard}>
            <Text style={styles.totalLabel}>Total de Gastos</Text>
            <Text style={styles.totalValue}>R$ {totalExpenses.toFixed(2)}</Text>
          </View>

          <View style={styles.chartContainer}>
            <Text style={styles.chartTitle}>Gastos por Categoria</Text>
            <PieChart
              data={pieChartData}
              width={screenWidth - 32}
              height={220}
              chartConfig={{
                color: (opacity = 1) => `rgba(0, 0, 0, ${opacity})`,
              }}
              accessor="amount"
              backgroundColor="transparent"
              paddingLeft="15"
              absolute
            />
          </View>

          <View style={styles.chartContainer}>
            <Text style={styles.chartTitle}>Comparativo de Categorias</Text>
            <BarChart
              data={barChartData}
              width={screenWidth - 64}
              height={220}
              yAxisLabel="R$ "
              yAxisSuffix=""
              chartConfig={{
                backgroundColor: '#ffffff',
                backgroundGradientFrom: '#ffffff',
                backgroundGradientTo: '#ffffff',
                decimalPlaces: 2,
                color: (opacity = 1) => `rgba(29, 128, 55, ${opacity})`,
                labelColor: (opacity = 1) => `rgba(0, 0, 0, ${opacity})`,
                style: {
                  borderRadius: 16,
                },
                propsForBackgroundLines: {
                  strokeDasharray: '',
                  stroke: '#E5E7EB',
                },
                barPercentage: 0.7,
                propsForLabels: {
                  fontSize: 10,
                },
              }}
              style={styles.chart}
              fromZero
              showValuesOnTopOfBars
            />
          </View>

          <View style={styles.legendContainer}>
            <Text style={styles.chartTitle}>Detalhes por Categoria</Text>
            {categoryData.map((item, index) => (
              <View key={index} style={styles.legendItem}>
                <View style={[styles.colorBox, { backgroundColor: item.color }]} />
                <Text style={styles.iconText}>{item.icon}</Text>
                <View style={styles.legendTextContainer}>
                  <Text style={styles.legendCategory}>{item.category}</Text>
                  <Text style={styles.legendAmount}>
                    R$ {item.total.toFixed(2)} ({((item.total / totalExpenses) * 100).toFixed(1)}%)
                  </Text>
                </View>
              </View>
            ))}
          </View>
        </>
      ) : (
        <View style={styles.emptyContainer}>
          <Text style={styles.emptyText}>📊</Text>
          <Text style={styles.emptyTitle}>Nenhum gasto registrado</Text>
          <Text style={styles.emptyDescription}>
            Adicione gastos na tela inicial para visualizar os gráficos
          </Text>
        </View>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F9FAFB',
  },
  centerContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F9FAFB',
  },
  totalCard: {
    backgroundColor: '#33cc5c',
    margin: 16,
    padding: 24,
    borderRadius: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  totalLabel: {
    fontSize: 14,
    color: '#ffffffff',
    marginBottom: 8,
    fontWeight: '600',
  },
  totalValue: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#ffffffff',
  },
  chartContainer: {
    backgroundColor: '#FFFFFF',
    marginHorizontal: 16,
    marginBottom: 16,
    padding: 16,
    borderRadius: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.05,
    shadowRadius: 4,
    elevation: 2,
  },
  chartTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 16,
    color: '#1d8037ff',
  },
  chart: {
    marginVertical: 8,
    borderRadius: 16,
  },
  legendContainer: {
    backgroundColor: '#FFFFFF',
    marginHorizontal: 16,
    marginBottom: 16,
    padding: 16,
    borderRadius: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.05,
    shadowRadius: 4,
    elevation: 2,
  },
  legendItem: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 16,
    paddingBottom: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#F3F4F6',
  },
  colorBox: {
    width: 16,
    height: 16,
    borderRadius: 4,
    marginRight: 12,
  },
  iconText: {
    fontSize: 20,
    marginRight: 12,
  },
  legendTextContainer: {
    flex: 1,
  },
  legendCategory: {
    fontSize: 14,
    fontWeight: '600',
    color: '#111827',
    marginBottom: 4,
  },
  legendAmount: {
    fontSize: 13,
    color: '#6B7280',
  },
  emptyContainer: {
    padding: 40,
    alignItems: 'center',
    marginTop: 60,
  },
  emptyText: {
    fontSize: 64,
    marginBottom: 16,
  },
  emptyTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#111827',
    marginBottom: 8,
  },
  emptyDescription: {
    fontSize: 14,
    color: '#6B7280',
    textAlign: 'center',
  },
});