import axios from 'axios';

interface Category {
  id: number;
  nome: string;
  descricao: string;
  createdAt?: string;
}

interface Expense {
  id: number;
  valor: number;
  data: string;
  category?: Category;
}

interface ExpenseFormData {
  valor: number;
  data: string;
  category: { id: number };
}

const API_BASE_URL = 'http://10.0.2.2:8083';
const EXPENSES_API_URL = `${API_BASE_URL}/gastos`;
const CATEGORIES_API_URL = `${API_BASE_URL}/api/categories`;

// ===== EXPENSES API =====
export const createExpense = async (expense: ExpenseFormData): Promise<Expense> => {
    try {
        const response = await axios.post(EXPENSES_API_URL, expense);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const getAllExpenses = async (): Promise<Expense[]> => {
    try {
        const response = await axios.get(EXPENSES_API_URL);
        return response.data;
    } catch (error) {
        throw error;
    }
};

// Remova getExpensesByType se não usa mais "tipo" no backend
export const deleteExpense = async (id: number): Promise<void> => {
    try {
        await axios.delete(`${EXPENSES_API_URL}/${id}`);
    } catch (error) {
        throw error;
    }
};

// ===== CATEGORIES API =====
export const getAllCategories = async (): Promise<Category[]> => {
    try {
        const response = await axios.get(CATEGORIES_API_URL);
        return response.data;
    } catch (error) {
        console.error('Erro ao buscar categorias:', error);
        throw error;
    }
};

export const createCategory = async (category: { nome: string; descricao: string }): Promise<Category> => {
    try {
        const response = await axios.post(CATEGORIES_API_URL, category);
        return response.data;
    } catch (error) {
        console.error('Erro ao criar categoria:', error);
        throw error;
    }
};