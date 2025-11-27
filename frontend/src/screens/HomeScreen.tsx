import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  SafeAreaView,
  ScrollView,
  Alert,
  ActivityIndicator,
  Platform,
  RefreshControl,
} from 'react-native';
import { Picker } from '@react-native-picker/picker';
import DateTimePicker from '@react-native-community/datetimepicker';
import { Feather } from '@expo/vector-icons';
import { useFocusEffect } from '@react-navigation/native';
import ExpenseList from '../components/ExpenseList';
import { expenseApi, Expense } from '../services/api';
import api from '../services/api';
import { useAuth } from '../contexts/AuthContext';

interface Category {
  id: number;
  name: string;
  description?: string;
  color?: string;
}

// Helper para extrair dados HATEOAS
const extractHateoasData = <T,>(response: any): T[] => {
  const cleanItem = (obj: any): any => {
    if (Array.isArray(obj)) return obj.map(cleanItem);
    if (obj && typeof obj === 'object') {
      const { _links, ...rest } = obj;
      const cleaned: any = {};
      for (const key in rest) {
        cleaned[key] = cleanItem(rest[key]);
      }
      return cleaned;
    }
    return obj;
  };

  if (Array.isArray(response)) return response.map(cleanItem);
  if (response._embedded) {
    const firstKey = Object.keys(response._embedded)[0];
    if (firstKey && Array.isArray(response._embedded[firstKey])) {
      return response._embedded[firstKey].map(cleanItem);
    }
  }
  if (response.content && Array.isArray(response.content)) return response.content.map(cleanItem);
  return [];
};

const extractHateoasItem = <T,>(response: any): T => {
  const cleanItem = (obj: any): any => {
    if (Array.isArray(obj)) return obj.map(cleanItem);
    if (obj && typeof obj === 'object') {
      const { _links, ...rest } = obj;
      const cleaned: any = {};
      for (const key in rest) {
        cleaned[key] = cleanItem(rest[key]);
      }
      return cleaned;
    }
    return obj;
  };
  return cleanItem(response);
};

export default function HomeScreen({ navigation }: any) {
  const { user } = useAuth();
  const [expenses, setExpenses] = useState<Expense[]>([]);
  const [refreshing, setRefreshing] = useState(false);
  
  // Form states
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [categoryId, setCategoryId] = useState<number | null>(null);
  const [date, setDate] = useState(new Date());
  const [showDatePicker, setShowDatePicker] = useState(false);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(false);
  const [loadingCategories, setLoadingCategories] = useState(true);

  // Recarrega dados sempre que a tela receber foco
  useFocusEffect(
    React.useCallback(() => {
      fetchExpenses();
      loadCategories();
    }, [])
  );

  const fetchExpenses = async () => {
    try {
      const data = await expenseApi.getAll();
      console.log('Expenses carregadas:', data);
      console.log('Primeira expense:', data[0]);
      setExpenses(data);
    } catch (error) {
      console.error('Erro ao buscar gastos:', error);
    }
  };

  const loadCategories = async () => {
    try {
      setLoadingCategories(true);
      const response = await api.get('/categories');
      const categoriesData = extractHateoasData<Category>(response.data);
      setCategories(categoriesData);
      
      if (categoriesData.length > 0 && !categoryId) {
        setCategoryId(categoriesData[0].id);
      }
    } catch (error) {
      console.error('Erro ao carregar categorias:', error);
      Alert.alert('Erro', 'Não foi possível carregar as categorias');
    } finally {
      setLoadingCategories(false);
    }
  };

  const onRefresh = async () => {
    setRefreshing(true);
    await fetchExpenses();
    await loadCategories();
    setRefreshing(false);
  };

  const handleDateChange = (event: any, selectedDate?: Date) => {
    setShowDatePicker(Platform.OS === 'ios');
    if (selectedDate) {
      setDate(selectedDate);
    }
  };

  const formatDate = (date: Date): string => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  const handleSubmit = async () => {
    if (!amount || parseFloat(amount) <= 0) {
      Alert.alert('Erro', 'Por favor, insira um valor válido');
      return;
    }

    if (!description.trim()) {
      Alert.alert('Erro', 'Por favor, insira uma descrição');
      return;
    }

    if (!categoryId) {
      Alert.alert('Erro', 'Por favor, selecione uma categoria');
      return;
    }

    if (!user) {
      Alert.alert('Erro', 'Usuário não autenticado');
      return;
    }

    setLoading(true);
    try {
      const expenseData = {
        amount: parseFloat(amount),
        description: description.trim(),
        categoryId: categoryId,
        userId: user.id,
        date: formatDate(date),
      };

    const response = await api.post('/expenses', expenseData);
    const createdExpense = extractHateoasItem(response.data);
    console.log('Expense criado:', createdExpense);
    
    Alert.alert('Sucesso', 'Gasto cadastrado com sucesso!');      // Limpar formulário
      setAmount('');
      setDescription('');
      setDate(new Date());
      setCategoryId(categories.length > 0 ? categories[0].id : null);
      
      // Atualizar lista
      await fetchExpenses();
    } catch (error: any) {
      console.error('Erro ao criar gasto:', error);
      const errorMessage = error.response?.data?.message || 
                          error.response?.data?.error || 
                          'Não foi possível cadastrar o gasto';
      Alert.alert('Erro', errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView 
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
        }
        showsVerticalScrollIndicator={false}
      >
        {/* Formulário de Adicionar Gasto */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Adicionar Novo Gasto</Text>
          
          {loadingCategories ? (
            <ActivityIndicator size="large" color="#33cc5c" />
          ) : (
            <>
              <View style={styles.inputGroup}>
                <Text style={styles.label}>Valor (R$) *</Text>
                <View style={styles.inputContainer}>
                  <Feather name="dollar-sign" size={20} color="#6B7280" style={styles.inputIcon} />
                  <TextInput
                    style={styles.input}
                    placeholder="0.00"
                    keyboardType="decimal-pad"
                    value={amount}
                    onChangeText={setAmount}
                    editable={!loading}
                  />
                </View>
              </View>

              <View style={styles.inputGroup}>
                <Text style={styles.label}>Descrição *</Text>
                <View style={styles.inputContainer}>
                  <Feather name="file-text" size={20} color="#6B7280" style={styles.inputIcon} />
                  <TextInput
                    style={styles.input}
                    placeholder="Ex: Almoço, Conta de luz..."
                    value={description}
                    onChangeText={setDescription}
                    editable={!loading}
                  />
                </View>
              </View>

              <View style={styles.inputGroup}>
                <View style={styles.labelRow}>
                  <Text style={styles.label}>Categoria *</Text>
                  <TouchableOpacity
                    onPress={() => navigation?.navigate('Categories')}
                    style={styles.addCategoryButton}
                  >
                    <Feather name="plus" size={16} color="#2cb350ff" />
                    <Text style={styles.addCategoryText}>Nova Categoria</Text>
                  </TouchableOpacity>
                </View>
                <View style={styles.pickerContainer}>
                  <Feather name="tag" size={20} color="#6B7280" style={styles.pickerIcon} />
                  <Picker
                    selectedValue={categoryId}
                    onValueChange={(itemValue) => setCategoryId(itemValue)}
                    style={styles.picker}
                    enabled={!loading}
                  >
                    <Picker.Item label="Selecione uma categoria" value={null} />
                    {categories.map((category) => (
                      <Picker.Item
                        key={category.id}
                        label={category.name}
                        value={category.id}
                      />
                    ))}
                  </Picker>
                </View>
              </View>

              <View style={styles.inputGroup}>
                <Text style={styles.label}>Data *</Text>
                <TouchableOpacity
                  style={styles.dateButton}
                  onPress={() => setShowDatePicker(true)}
                >
                  <Feather name="calendar" size={20} color="#6B7280" />
                  <Text style={styles.dateButtonText}>
                    {date.toLocaleDateString('pt-BR')}
                  </Text>
                </TouchableOpacity>
              </View>

              {showDatePicker && (
                <DateTimePicker
                  value={date}
                  mode="date"
                  display="default"
                  onChange={handleDateChange}
                />
              )}

              <TouchableOpacity
                style={[styles.submitButton, loading && styles.submitButtonDisabled]}
                onPress={handleSubmit}
                disabled={loading}
              >
                {loading ? (
                  <ActivityIndicator color="#FFFFFF" />
                ) : (
                  <>
                    <Text style={styles.submitButtonText}>Cadastrar Gasto</Text>
                    <Feather name="check" size={20} color="#FFFFFF" />
                  </>
                )}
              </TouchableOpacity>

              {categories.length === 0 && (
                <TouchableOpacity
                  style={styles.warningButton}
                  onPress={() => navigation?.navigate('Categories')}
                >
                  <Feather name="alert-circle" size={16} color="#EF4444" />
                  <Text style={styles.warningText}>
                    Nenhuma categoria encontrada. Cadastre uma categoria primeiro.
                  </Text>
                </TouchableOpacity>
              )}
            </>
          )}
        </View>

        {/* Lista de Gastos */}
        <View style={styles.listSection}>
          <Text style={styles.sectionTitle}>
            Meus Gastos ({expenses.length})
          </Text>
          <ExpenseList 
            expenses={expenses}
            onExpenseDeleted={fetchExpenses}
            categories={categories}
          />
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F9FAFB',
  },
  scrollView: {
    flex: 1,
  },
  scrollContent: {
    paddingBottom: 20,
  },
  section: {
    backgroundColor: '#FFFFFF',
    marginHorizontal: 16,
    marginTop: 16,
    marginBottom: 16,
    padding: 20,
    borderRadius: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.05,
    shadowRadius: 4,
    elevation: 2,
  },
  listSection: {
    backgroundColor: '#FFFFFF',
    marginHorizontal: 16,
    marginBottom: 16,
    padding: 20,
    paddingBottom: 0,
    borderRadius: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.05,
    shadowRadius: 4,
    elevation: 2,
    minHeight: 200,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 16,
    color: '#1d8037ff',
  },
  inputGroup: {
    marginBottom: 16,
  },
  labelRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  label: {
    fontSize: 14,
    fontWeight: '600',
    color: '#374151',
  },
  addCategoryButton: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
  },
  addCategoryText: {
    fontSize: 14,
    color: '#2cb350ff',
    fontWeight: '600',
  },
  inputContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#F9FAFB',
    borderWidth: 1,
    borderColor: '#E5E7EB',
    borderRadius: 8,
  },
  inputIcon: {
    marginLeft: 12,
  },
  input: {
    flex: 1,
    padding: 14,
    fontSize: 16,
    color: '#111827',
  },
  pickerContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#F9FAFB',
    borderWidth: 1,
    borderColor: '#E5E7EB',
    borderRadius: 8,
    overflow: 'hidden',
  },
  pickerIcon: {
    marginLeft: 12,
  },
  picker: {
    flex: 1,
    height: 50,
  },
  dateButton: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#F9FAFB',
    borderWidth: 1,
    borderColor: '#E5E7EB',
    borderRadius: 8,
    padding: 14,
  },
  dateButtonText: {
    fontSize: 16,
    color: '#111827',
    marginLeft: 12,
  },
  submitButton: {
    flexDirection: 'row',
    backgroundColor: '#33cc5c',
    padding: 16,
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: 8,
    gap: 8,
  },
  submitButtonDisabled: {
    backgroundColor: '#9CA3AF',
    opacity: 0.6,
  },
  submitButtonText: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: '600',
  },
  warningButton: {
    flexDirection: 'row',
    marginTop: 16,
    padding: 12,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#FEE2E2',
    borderRadius: 8,
    gap: 8,
  },
  warningText: {
    color: '#991B1B',
    fontSize: 14,
    textAlign: 'center',
    flex: 1,
  },
});