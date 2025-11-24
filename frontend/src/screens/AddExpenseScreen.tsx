import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Alert,
  ScrollView,
  Platform,
  ActivityIndicator,
} from 'react-native';
import { Picker } from '@react-native-picker/picker';
import DateTimePicker from '@react-native-community/datetimepicker';
import { Feather } from '@expo/vector-icons';
import api from '../services/api';
import AsyncStorage from '@react-native-async-storage/async-storage';

interface Category {
  id: number;
  name: string;
  description?: string;
}

const AddExpenseScreen = ({ navigation }: any) => {
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [categoryId, setCategoryId] = useState<number | null>(null);
  const [date, setDate] = useState(new Date());
  const [showDatePicker, setShowDatePicker] = useState(false);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(false);
  const [loadingCategories, setLoadingCategories] = useState(true);

  useEffect(() => {
    loadCategories();
  }, []);

  const loadCategories = async () => {
    try {
      setLoadingCategories(true);
      console.log('Carregando categorias...');
      const response = await api.get('/categories');
      console.log('Categorias carregadas:', response.data);
      setCategories(response.data);
      
      // Seleciona a primeira categoria automaticamente se existir
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
    // Validações
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

    try {
      setLoading(true);
      
      // Buscar userId do AsyncStorage
      const userJson = await AsyncStorage.getItem('@expense_user');
      let userId = 1; // Valor padrão
      
      if (userJson) {
        const user = JSON.parse(userJson);
        userId = user.id || 1;
      }
      
      const expenseData = {
        amount: parseFloat(amount),
        description: description.trim(),
        categoryId: categoryId,
        userId: userId,
        date: formatDate(date),
      };

      console.log('Dados do gasto a serem enviados:', expenseData);

      const response = await api.post('/expenses', expenseData);
      
      console.log('Gasto criado com sucesso:', response.data);

      Alert.alert(
        'Sucesso',
        'Gasto cadastrado com sucesso!',
        [
          {
            text: 'OK',
            onPress: () => navigation.goBack(),
          },
        ]
      );

      // Limpar formulário
      setAmount('');
      setDescription('');
      setDate(new Date());
      setCategoryId(categories.length > 0 ? categories[0].id : null);

    } catch (error: any) {
      console.error('Erro ao criar gasto:', error);
      console.error('Resposta do erro:', error.response?.data);
      
      const errorMessage = error.response?.data?.message || 
                          error.response?.data?.error || 
                          'Não foi possível cadastrar o gasto';
      
      Alert.alert('Erro', errorMessage);
    } finally {
      setLoading(false);
    }
  };

  if (loadingCategories) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#3B82F6" />
        <Text style={styles.loadingText}>Carregando categorias...</Text>
      </View>
    );
  }

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Feather name="dollar-sign" size={32} color="#3B82F6" />
        <Text style={styles.title}>Novo Gasto</Text>
        <Text style={styles.subtitle}>Registre um novo gasto</Text>
      </View>

      <View style={styles.form}>
        <Text style={styles.label}>Valor *</Text>
        <View style={styles.inputContainer}>
          <Feather name="dollar-sign" size={20} color="#6B7280" style={styles.inputIcon} />
          <TextInput
            style={styles.input}
            placeholder="0.00"
            keyboardType="decimal-pad"
            value={amount}
            onChangeText={setAmount}
          />
        </View>

        <Text style={styles.label}>Descrição *</Text>
        <View style={styles.inputContainer}>
          <Feather name="file-text" size={20} color="#6B7280" style={styles.inputIcon} />
          <TextInput
            style={styles.input}
            placeholder="Ex: Almoço"
            value={description}
            onChangeText={setDescription}
          />
        </View>

        <Text style={styles.label}>Categoria *</Text>
        <View style={styles.pickerContainer}>
          <Feather name="tag" size={20} color="#6B7280" style={styles.pickerIcon} />
          <Picker
            selectedValue={categoryId}
            onValueChange={(itemValue) => setCategoryId(itemValue)}
            style={styles.picker}
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

        {showDatePicker && (
          <DateTimePicker
            value={date}
            mode="date"
            display="default"
            onChange={handleDateChange}
          />
        )}

        <TouchableOpacity
          style={[styles.button, loading && styles.buttonDisabled]}
          onPress={handleSubmit}
          disabled={loading}
        >
          {loading ? (
            <ActivityIndicator color="#FFFFFF" />
          ) : (
            <>
              <Text style={styles.buttonText}>Cadastrar Gasto</Text>
              <Feather name="check" size={20} color="#FFFFFF" />
            </>
          )}
        </TouchableOpacity>

        {categories.length === 0 && (
          <TouchableOpacity
            style={styles.linkButton}
            onPress={() => navigation.navigate('Categories')}
          >
            <Feather name="alert-circle" size={16} color="#EF4444" />
            <Text style={styles.linkButtonText}>
              Nenhuma categoria encontrada. Cadastre uma categoria primeiro.
            </Text>
          </TouchableOpacity>
        )}
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F9FAFB',
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F9FAFB',
  },
  loadingText: {
    marginTop: 12,
    fontSize: 16,
    color: '#6B7280',
  },
  header: {
    backgroundColor: '#FFFFFF',
    paddingHorizontal: 20,
    paddingTop: 20,
    paddingBottom: 20,
    alignItems: 'center',
    borderBottomWidth: 1,
    borderBottomColor: '#E5E7EB',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#111827',
    marginTop: 12,
    marginBottom: 4,
  },
  subtitle: {
    fontSize: 14,
    color: '#6B7280',
  },
  form: {
    backgroundColor: '#FFFFFF',
    margin: 16,
    padding: 20,
    borderRadius: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.05,
    shadowRadius: 4,
    elevation: 2,
  },
  label: {
    fontSize: 14,
    fontWeight: '600',
    marginBottom: 8,
    color: '#374151',
  },
  inputContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#F9FAFB',
    borderWidth: 1,
    borderColor: '#E5E7EB',
    borderRadius: 8,
    marginBottom: 16,
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
    marginBottom: 16,
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
    marginBottom: 16,
  },
  dateButtonText: {
    fontSize: 16,
    color: '#111827',
    marginLeft: 12,
  },
  button: {
    flexDirection: 'row',
    backgroundColor: '#3B82F6',
    padding: 16,
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: 8,
    gap: 8,
  },
  buttonDisabled: {
    backgroundColor: '#9CA3AF',
    opacity: 0.6,
  },
  buttonText: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: '600',
  },
  linkButton: {
    flexDirection: 'row',
    marginTop: 16,
    padding: 12,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#FEE2E2',
    borderRadius: 8,
    gap: 8,
  },
  linkButtonText: {
    color: '#991B1B',
    fontSize: 14,
    textAlign: 'center',
    flex: 1,
  },
});

export default AddExpenseScreen;