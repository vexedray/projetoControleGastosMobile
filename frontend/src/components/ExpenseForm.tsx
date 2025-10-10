import React, { useState } from 'react';
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
import { createExpense } from '../services/api';

interface ExpenseFormProps {
  onExpenseAdded: () => void;
}

const ExpenseForm: React.FC<ExpenseFormProps> = ({ onExpenseAdded }) => {
  const [valor, setValor] = useState('');
  const [tipo, setTipo] = useState('alimentacao');
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

  const handleSubmit = async () => {
    if (!valor || parseFloat(valor) <= 0) {
      Alert.alert('Erro', 'Por favor, insira um valor válido');
      return;
    }

    setLoading(true);
    try {
      const expenseData = {
        valor: parseFloat(valor),
        tipo,
        data: new Date().toISOString(),
      };

      await createExpense(expenseData);
      setValor('');
      setTipo('alimentacao');
      Alert.alert('Sucesso', 'Gasto adicionado com sucesso!');
      onExpenseAdded();
    } catch (error) {
      console.error('Erro ao criar gasto:', error);
      Alert.alert('Erro', 'Não foi possível adicionar o gasto');
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
        <Text style={styles.label}>Tipo de Gasto</Text>
        <TextInput
          style={styles.input}
          value={tipo}
          onChangeText={setTipo}
          placeholder="Tipo do gasto"
          editable={!loading}
        />
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
});

export default ExpenseForm;