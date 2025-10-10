import React, { useState } from 'react';
import { View, Text, TextInput, Button, StyleSheet } from 'react-native';
import { createExpense } from '../services/api';

const AddExpenseScreen = ({ navigation }) => {
    const [value, setValue] = useState('');
    const [type, setType] = useState('');
    const [date, setDate] = useState(new Date());

    const handleSubmit = async () => {
        if (!value || !type) {
            alert('Please fill in all fields');
            return;
        }

        const expenseData = {
            value: parseFloat(value),
            type,
            date: date.toISOString(),
        };

        try {
            await createExpense(expenseData);
            navigation.goBack();
        } catch (error) {
            alert('Error creating expense');
        }
    };

    return (
        <View style={styles.container}>
            <Text style={styles.title}>Add Expense</Text>
            <TextInput
                style={styles.input}
                placeholder="Value"
                keyboardType="numeric"
                value={value}
                onChangeText={setValue}
            />
            <TextInput
                style={styles.input}
                placeholder="Type"
                value={type}
                onChangeText={setType}
            />
            <Button title="Add Expense" onPress={handleSubmit} />
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        padding: 20,
        justifyContent: 'center',
    },
    title: {
        fontSize: 24,
        marginBottom: 20,
    },
    input: {
        height: 40,
        borderColor: 'gray',
        borderWidth: 1,
        marginBottom: 20,
        paddingHorizontal: 10,
    },
});

export default AddExpenseScreen;