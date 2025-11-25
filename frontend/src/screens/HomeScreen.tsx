import React, { useState, useEffect } from 'react';
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

  useEffect(() => {
    fetchExpenses();
    loadCategories();
  }, []);

  // Recarrega categorias sempre que a tela receber foco
  useFocusEffect(
    React.useCallback(() => {
      loadCategories();
    }, [])
  );

  const fetchExpenses = async () => {
    try {
      const data = await expenseApi.getAll();
      setExpenses(data);
    } catch (error) {
      console.error('Erro ao buscar gastos:', error);
    }
  };

  const loadCategories = async () => {
    try {
      setLoadingCategories(true);
      const response = await api.get('/categories');
      setCategories(response.data);
      
      if (response.data.length > 0 && !categoryId) {
        setCategoryId(response.data[0].id);
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

      await api.post('/expenses', expenseData);
      
      Alert.alert('Sucesso', 'Gasto cadastrado com sucesso!');
      
      // Limpar formulário
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
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
        }
      >
        <View style={styles.header}>
          <Feather name="dollar-sign" size={32} color="#3B82F6" />
          <Text style={styles.title}>Controle de Gastos</Text>
        </View>

        {/* Formulário de Adicionar Gasto */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Adicionar Novo Gasto</Text>
          
          {loadingCategories ? (
            <ActivityIndicator size="large" color="#3B82F6" />
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
                    <Feather name="plus" size={16} color="#3B82F6" />
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
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>
            Meus Gastos ({expenses.length})
          </Text>
          <ExpenseList 
            expenses={expenses} 
            onExpenseDeleted={fetchExpenses}
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
  header: {
    backgroundColor: '#FFFFFF',
    paddingHorizontal: 20,
    paddingTop: 20,
    paddingBottom: 20,
    alignItems: 'center',
    borderBottomWidth: 1,
    borderBottomColor: '#E5E7EB',
    marginBottom: 16,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#111827',
    marginTop: 12,
  },
  section: {
    backgroundColor: '#FFFFFF',
    marginHorizontal: 16,
    marginBottom: 16,
    padding: 20,
    borderRadius: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.05,
    shadowRadius: 4,
    elevation: 2,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 16,
    color: '#111827',
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
    color: '#3B82F6',
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
    backgroundColor: '#3B82F6',
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