import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Alert,
  ActivityIndicator,
} from 'react-native';
import { Picker } from '@react-native-picker/picker';

interface Category {
  id: number;
  name: string;
  description?: string;
}

interface ExpenseFormProps {
  onExpenseAdded: () => void;
}

const ExpenseForm: React.FC<ExpenseFormProps> = ({ onExpenseAdded }) => {
  const [valor, setValor] = useState('');
  const [tipo, setTipo] = useState('alimentacao');
  const [categoryId, setCategoryId] = useState<number | null>(null);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(false);

  const tiposGasto = [
    { label: 'Alimentação', value: 'alimentacao' },
    { label: 'Transporte', value: 'transporte' },
    { label: 'Lazer', value: 'lazer' },
    { label: 'Saúde', value: 'saude' },
    { label: 'Educação', value: 'educacao' },
    { label: 'Casa', value: 'casa' },
    { label: 'Outros', value: 'outros' },
  ];

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      const response = await fetch('http://10.0.2.2:8083/api/categories');
      const data = await response.json();
      setCategories(data);
      if (data.length > 0 && !categoryId) {
        setCategoryId(data[0].id);
      }
    } catch (error) {
      console.error('Erro ao carregar categorias:', error);
      Alert.alert('Erro', 'Não foi possível carregar as categorias. Verifique se o backend está rodando.');
    }
  };

  const handleSubmit = async () => {
    if (!valor || parseFloat(valor) <= 0) {
      Alert.alert('Erro', 'Por favor, insira um valor válido');
      return;
    }

    if (!categoryId) {
      Alert.alert('Erro', 'Por favor, selecione uma categoria');
      return;
    }

    setLoading(true);
    try {
      const expense = {
        description: tipo || 'Gasto',
        amount: parseFloat(valor),
        date: new Date().toISOString().split('T')[0], // YYYY-MM-DD
        categoryId: categoryId,
        userId: 1, // TODO: Implementar autenticação
      };

      const response = await fetch('http://10.0.2.2:8083/api/expenses', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(expense),
      });

      if (response.ok) {
        setValor('');
        setTipo('alimentacao');
        setCategoryId(null);
        Alert.alert('Sucesso', 'Gasto adicionado com sucesso!');
        onExpenseAdded();
      } else {
        const errorData = await response.json();
        console.error('Erro do servidor:', errorData);
        Alert.alert('Erro', 'Erro ao adicionar gasto');
      }
    } catch (error) {
      console.error('Erro ao criar gasto:', error);
      Alert.alert('Erro', 'Não foi possível adicionar o gasto. Verifique se o backend está rodando.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.inputGroup}>
        <Text style={styles.label}>Valor (R$)</Text>
        <TextInput
          style={styles.input}
          value={valor}
          onChangeText={setValor}
          placeholder="0,00"
          keyboardType="numeric"
          editable={!loading}
        />
      </View>

      <View style={styles.inputGroup}>
        <Text style={styles.label}>Categoria</Text>
        <View style={styles.pickerContainer}>
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

      <TouchableOpacity
        style={[styles.button, loading && styles.buttonDisabled]}
        onPress={handleSubmit}
        disabled={loading}
      >
        {loading ? (
          <ActivityIndicator color="#fff" />
        ) : (
          <Text style={styles.buttonText}>Adicionar Gasto</Text>
        )}
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: 10,
  },
  inputGroup: {
    marginBottom: 15,
  },
  label: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 5,
    color: '#333',
  },
  input: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    padding: 12,
    fontSize: 16,
    backgroundColor: '#fff',
  },
  button: {
    backgroundColor: '#007AFF',
    padding: 15,
    borderRadius: 8,
    alignItems: 'center',
    marginTop: 10,
  },
  buttonDisabled: {
    backgroundColor: '#ccc',
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
  pickerContainer: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    backgroundColor: '#fff',
  },
  picker: {
    height: 50,
  },
});

export default ExpenseForm;