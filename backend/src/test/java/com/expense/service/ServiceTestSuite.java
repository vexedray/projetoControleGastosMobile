package com.expense.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

/**
 * Suite de testes para todos os Services
 * Execute com: mvn test -Dtest="*ServiceTest"
 */
public class ServiceTestSuite {
    
    @Test
    @DisplayName("Informações sobre execução dos testes de Service")
    void serviceTestInfo() {
        System.out.println("=== TESTES DE SERVICES ===");
        System.out.println("Para executar todos os testes de service:");
        System.out.println("mvn test -Dtest=\"*ServiceTest\"");
        System.out.println("");
        System.out.println("Testes disponíveis:");
        System.out.println("- CategoryServiceTest");
        System.out.println("- UserServiceTest"); 
        System.out.println("- ExpenseServiceTest");
    }
}