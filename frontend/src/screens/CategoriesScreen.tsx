import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  FlatList,
  StyleSheet,
  Alert,
  ActivityIndicator,
} from 'react-native';
import { Feather } from '@expo/vector-icons';
import api from '../services/api';

interface Category {
  id: number;
  name: string;
  description?: string;
  color?: string;
  createdAt: string;
  totalExpenses?: number;
}

// Helper para extrair dados HATEOAS
const extractHateoasData = <T,>(response: any): T[] => {
  if (Array.isArray(response)) return response;
  if (response._embedded) {
    const firstKey = Object.keys(response._embedded)[0];
    if (firstKey && Array.isArray(response._embedded[firstKey])) {
      return response._embedded[firstKey].map((item: any) => {
        const { _links, ...data } = item;
        return data;
      });
    }
  }
  if (response.content && Array.isArray(response.content)) return response.content;
  return [];
};

const extractHateoasItem = <T,>(response: any): T => {
  if (!response || typeof response !== 'object') return response;
  const { _links, ...data } = response;
  return data as T;
};

export default function CategoriesScreen() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [selectedColor, setSelectedColor] = useState('#22C55E');
  const [loading, setLoading] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);

  const availableColors = [
    '#22C55E', // Verde
    '#3B82F6', // Azul
    '#F59E0B', // Amarelo
    '#EF4444', // Vermelho
    '#8B5CF6', // Roxo
    '#EC4899', // Rosa
    '#14B8A6', // Turquesa
    '#F97316', // Laranja
  ];

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      const response = await api.get('/categories');
      const categoriesData = extractHateoasData<Category>(response.data);
      setCategories(categoriesData);
    } catch (error) {
      console.error('Erro ao carregar categorias:', error);
      Alert.alert('Erro', 'Não foi possível carregar as categorias');
    }
  };

  const handleSubmit = async () => {
    if (!name.trim()) {
      Alert.alert('Atenção', 'O nome da categoria é obrigatório');
      return;
    }

    setLoading(true);
    try {
      if (editingId) {
        const response = await api.put(`/categories/${editingId}`, {
          name: name.trim(),
          description: description.trim() || undefined,
          color: selectedColor,
        });
        const updatedCategory = extractHateoasItem<Category>(response.data);
        console.log('Categoria atualizada:', updatedCategory);
        Alert.alert('Sucesso', 'Categoria atualizada!');
        setEditingId(null);
      } else {
        const response = await api.post('/categories', {
          name: name.trim(),
          description: description.trim() || undefined,
          color: selectedColor,
        });
        const newCategory = extractHateoasItem<Category>(response.data);
        console.log('Categoria criada:', newCategory);
        Alert.alert('Sucesso', 'Categoria criada!');
      }
      
      setName('');
      setDescription('');
      setSelectedColor('#22C55E');
      fetchCategories();
    } catch (error) {
      console.error('Erro ao salvar categoria:', error);
      Alert.alert('Erro', 'Não foi possível salvar a categoria');
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (category: Category) => {
    console.log('Editando categoria:', category);
    setName(category.name);
    setDescription(category.description || '');
    setSelectedColor(category.color || '#22C55E');
    setEditingId(category.id);
  };

  const handleCancelEdit = () => {
    setName('');
    setDescription('');
    setSelectedColor('#22C55E');
    setEditingId(null);
  };

  const handleDelete = (id: number) => {
    console.log('Tentando excluir categoria:', id);
    Alert.alert(
      'Confirmar exclusão',
      'Deseja realmente excluir esta categoria?',
      [
        { 
          text: 'Cancelar', 
          style: 'cancel' 
        },
        {
          text: 'Excluir',
          style: 'destructive',
          onPress: async () => {
            try {
              console.log('Excluindo categoria ID:', id);
              await api.delete(`/categories/${id}`);
              Alert.alert('Sucesso', 'Categoria excluída!');
              fetchCategories();
            } catch (error) {
              console.error('Erro ao excluir categoria:', error);
              Alert.alert('Erro', 'Não foi possível excluir a categoria');
            }
          },
        },
      ]
    );
  };

  const renderCategoryItem = ({ item }: { item: Category }) => (
    <View style={styles.categoryCard}>
      <View style={styles.categoryContent}>
        <View style={[styles.categoryDot, { backgroundColor: item.color || '#22C55E' }]} />
        <View style={styles.categoryInfo}>
          <Text style={styles.categoryName}>{item.name}</Text>
          {item.description && (
            <Text style={styles.categoryDescription}>{item.description}</Text>
          )}
        </View>
      </View>
      <View style={styles.categoryActions}>
        <TouchableOpacity
          style={styles.actionButton}
          onPress={() => {
            console.log('Botão editar pressionado para:', item.name);
            handleEdit(item);
          }}
          activeOpacity={0.7}
        >
          <Feather name="edit-2" size={20} color="#6B7280" />
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.actionButton}
          onPress={() => {
            console.log('Botão deletar pressionado para:', item.name);
            handleDelete(item.id);
          }}
          activeOpacity={0.7}
        >
          <Feather name="trash-2" size={20} color="#EF4444" />
        </TouchableOpacity>
      </View>
    </View>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Gerenciar Categorias</Text>
        <Text style={styles.subtitle}>Crie e organize suas categorias de gastos</Text>
      </View>

      <View style={styles.form}>
        {editingId && (
          <View style={styles.editingBanner}>
            <Text style={styles.editingText}>Editando categoria</Text>
            <TouchableOpacity onPress={handleCancelEdit}>
              <Feather name="x" size={20} color="#EF4444" />
            </TouchableOpacity>
          </View>
        )}
        <TextInput
          style={styles.input}
          placeholder="Nome da categoria"
          placeholderTextColor="#9CA3AF"
          value={name}
          onChangeText={setName}
        />
        <TextInput
          style={styles.input}
          placeholder="Descrição (opcional)"
          placeholderTextColor="#9CA3AF"
          value={description}
          onChangeText={setDescription}
          multiline
        />
        
        <Text style={styles.colorLabel}>Escolha uma cor:</Text>
        <View style={styles.colorPicker} key={`color-picker-${editingId || 'new'}-${selectedColor}`}>
          {availableColors.map((color) => (
            <TouchableOpacity
              key={color}
              style={[
                styles.colorOption,
                { backgroundColor: color },
                selectedColor === color && styles.colorOptionSelected,
              ]}
              onPress={() => {
                console.log('Cor selecionada:', color);
                setSelectedColor(color);
              }}
              activeOpacity={0.7}
            />
          ))}
        </View>

        <TouchableOpacity
          style={[styles.submitButton, loading && styles.submitButtonDisabled]}
          onPress={handleSubmit}
          disabled={loading}
          activeOpacity={0.8}
        >
          {loading ? (
            <ActivityIndicator color="#FFFFFF" />
          ) : (
            <Text style={styles.submitButtonText}>
              {editingId ? 'Atualizar Categoria' : 'Criar Categoria'}
            </Text>
          )}
        </TouchableOpacity>
      </View>

      <View style={styles.listHeader}>
        <Text style={styles.listTitle}>
          Categorias Existentes ({categories.length})
        </Text>
      </View>

      <FlatList
        data={categories}
        renderItem={renderCategoryItem}
        keyExtractor={(item) => item.id.toString()}
        contentContainerStyle={styles.listContent}
        showsVerticalScrollIndicator={false}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F9FAFB',
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
    marginBottom: 4,
    textAlign: 'center',
  },
  subtitle: {
    fontSize: 14,
    color: '#6B7280',
    textAlign: 'center',
  },
  form: {
    backgroundColor: '#FFFFFF',
    padding: 20,
    marginTop: 16,
    marginHorizontal: 16,
    borderRadius: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.05,
    shadowRadius: 4,
    elevation: 2,
  },
  input: {
    backgroundColor: '#F9FAFB',
    borderWidth: 1,
    borderColor: '#E5E7EB',
    borderRadius: 8,
    padding: 14,
    fontSize: 16,
    color: '#111827',
    marginBottom: 12,
  },
  editingBanner: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: '#FEF3C7',
    padding: 12,
    borderRadius: 8,
    marginBottom: 12,
  },
  editingText: {
    color: '#92400E',
    fontWeight: '600',
  },
  colorLabel: {
    fontSize: 14,
    fontWeight: '600',
    color: '#374151',
    marginBottom: 12,
  },
  colorPicker: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 12,
    marginBottom: 16,
  },
  colorOption: {
    width: 40,
    height: 40,
    borderRadius: 20,
    borderWidth: 2,
    borderColor: 'transparent',
  },
  colorOptionSelected: {
    borderColor: '#111827',
    borderWidth: 3,
  },
  submitButton: {
    backgroundColor: '#3B82F6',
    borderRadius: 8,
    padding: 16,
    alignItems: 'center',
    marginTop: 8,
  },
  submitButtonDisabled: {
    opacity: 0.6,
  },
  submitButtonText: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: '600',
  },
  listHeader: {
    paddingHorizontal: 20,
    paddingTop: 24,
    paddingBottom: 12,
  },
  listTitle: {
    fontSize: 18,
    fontWeight: '700',
    color: '#111827',
  },
  listContent: {
    paddingHorizontal: 16,
    paddingBottom: 20,
  },
  categoryCard: {
    backgroundColor: '#FFFFFF',
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.05,
    shadowRadius: 2,
    elevation: 1,
  },
  categoryContent: {
    flexDirection: 'row',
    alignItems: 'center',
    flex: 1,
  },
  categoryDot: {
    width: 12,
    height: 12,
    borderRadius: 6,
    marginRight: 12,
  },
  categoryInfo: {
    flex: 1,
  },
  categoryName: {
    fontSize: 16,
    fontWeight: '600',
    color: '#111827',
    marginBottom: 4,
  },
  categoryDescription: {
    fontSize: 14,
    color: '#6B7280',
    lineHeight: 18,
  },
  categoryActions: {
    flexDirection: 'row',
    gap: 12,
  },
  actionButton: {
    padding: 8,
  },
});