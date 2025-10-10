import axios from 'axios';

interface Expense {
  id: number;
  valor: number;
  tipo: string;
  data: string;
}

interface ExpenseFormData {
  valor: number;
  tipo: string;
  data: string;
}

const API_URL = 'http://10.0.2.2:8083/gastos';

export const createExpense = async (expense: ExpenseFormData): Promise<Expense> => {
    try {
        const response = await axios.post(API_URL, expense);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const getAllExpenses = async (): Promise<Expense[]> => {
    try {
        const response = await axios.get(API_URL);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const getExpensesByType = async (type: string): Promise<Expense[]> => {
    try {
        const response = await axios.get(`${API_URL}/por-tipo`, { params: { type } });
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const deleteExpense = async (id: number): Promise<void> => {
    try {
        await axios.delete(`${API_URL}/${id}`);
    } catch (error) {
        throw error;
    }
};