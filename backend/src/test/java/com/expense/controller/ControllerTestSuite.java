package com.expense.controller;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Suite de testes para executar todos os testes de Controllers
 */
@Suite
@SuiteDisplayName("Controller Tests Suite")
@SelectClasses({
    UserControllerTest.class,
    CategoryControllerTest.class,
    ExpenseControllerTest.class
})
public class ControllerTestSuite {
    // Esta classe permanece vazia, serve apenas como holder para a configuração da suite
}
