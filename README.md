# 💰 Sistema de Controle de Gastos

---

Sistema completo de controle de gastos pessoais com backend em **Spring Boot** e frontend em **React Native**.

---

## 📋 Sobre o Projeto

Aplicativo mobile desenvolvido para controle financeiro pessoal, permitindo aos usuários registrar, categorizar e visualizar seus gastos de forma simples e intuitiva.

### 🎯 Objetivo Principal

Desenvolver uma solução completa que auxilie pessoas a terem maior controle sobre suas finanças pessoais, acompanhar seus gastos mensais e tomar decisões financeiras mais conscientes.

---

## ✨ Funcionalidades

### 📌 Funcionalidades Principais

- ✅ **Cadastro de Despesas**: Adicionar gastos com descrição, valor, categoria e data
- ✅ **Listagem de Gastos**: Visualizar histórico completo de despesas registradas
- ✅ **Gerenciamento de Usuários**: Sistema de cadastro e autenticação JWT
- ✅ **Categorização**: Organização de gastos por categorias personalizáveis
- ✅ **Exclusão de Despesas**: Remover registros indesejados
- ✅ Gráficos de gastos por categoria
  
### 🔮 Funcionalidades Futuras (Opcionais)

- 🔄 Editar despesas existentes
- 🎯 Definir metas de gastos
- 🔔 Notificações e alertas
- 📄 Exportar dados para PDF/Excel
- 🎨 Categorias personalizadas
- 📄 Filtros**: Busca por data, categoria e valor
- 🌙 Modo escuro

---

## 🚀 Tecnologias Utilizadas

### Backend
- **Java 17+**
- **Spring Boot 3.1.0**
- **Spring Data JPA**
- **MySQL 8.0**
- **Maven 3.9.4**

### Frontend
- **React Native 0.72.6**
- **Expo 49.0.15**
- **TypeScript**
- **Axios** (HTTP client)

## 📋 Pré-requisitos

### Backend
- Java 17 ou superior
- Maven 3.6+
- MySQL Server 8.0+

### Frontend
- Node.js 16+
- npm
- Expo CLI



### 🔧 Configuração do Backend

```
# Navegar para o diretório do backend
cd backend

# Configurar variáveis de ambiente do Maven (Windows)
$env:MAVEN_HOME = "C:\Users\rayssa_almeida\apache-maven-3.9.4"
$env:PATH = "$env:MAVEN_HOME\bin;$env:PATH"

# Instalar dependências
mvn clean install

# Executar o projeto
mvn spring-boot:run
```

2. **Configuração do banco de dados**

```
-Instale o MySQL Server
-Crie um usuário root sem senha ou configure no application.properties
-O banco sistema_gastos será criado automaticamente
```

3. **Configure o arquivo `application.properties`**
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/controle_gastos
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT Configuration
jwt.secret=sua_chave_secreta_aqui
jwt.expiration=86400000
```

O backend estará rodando em `http://localhost:8083`

### 📱 Configuração do Frontend

1. **Navegue até a pasta frontend**
```bash
cd ../frontend
```

2. **Instale as dependências**
```bash

npm axios 
npm install --legacy-peer-deps
   
3. **Configure a URL da API**

Edite o arquivo `src/services/api.ts`:
```typescript
const api = axios.create({
  baseURL: 'http://localhost:8083/api', // ou seu IP local
});
```

4. **Execute o projeto**
```bash
npx expo start
```

5. **Abra no seu dispositivo**
- Escaneie o QR Code com o app **Expo Go** (Android/iOS)
- Ou pressione `a` para Android, `i` para iOS

---

## 🗂️ Estrutura do Projeto

### Backend (Spring Boot)

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── expense/
│   │   │           ├── ExpenseApplication.java         # Classe principal da aplicação
│   │   │           ├── assembler/                      # Montadores de modelos HATEOAS
│   │   │           ├── config/                         # Configurações (ex: CORS)
│   │   │           ├── controller/                     # Controllers REST
│   │   │           ├── dto/                            # Objetos de transferência de dados
│   │   │           ├── mapper/                         # Mapeamento entre entidades e DTOs
│   │   │           ├── model/                          # Entidades JPA
│   │   │           ├── repository/                     # Repositórios JPA
│   │   │           ├── security/                       # Segurança e autenticação JWT
│   │   │           └── service/                        # Regras de negócio
│   ├── resources/
│   │   ├── application.properties                      # Configurações da aplicação
│   │   └── db/
│   │       └── migration/                              # Scripts de migração do banco
│   │           ├── V1__create_user_table.sql
│   │           ├── V2__create_categories_table.sql
│   │           ├── V3__create_expense_table.sql
│   │           └── V4__insert_test_data.sql
├── pom.xml                                             # Gerenciador de dependências Maven
└── HATEOAS_DOCUMENTATION.md                            # Documentação HATEOAS
```

### Frontend (React Native)

```
frontend/
├── src/
│   ├── components/          # Componentes reutilizáveis
│   │   ├── ExpenseForm.tsx
│   │   └── ExpenseList.tsx
│   ├── screens/             # Telas do app
│   │   ├── AddExpenseScreen.tsx
│   │   ├── HomeScreen.tsx
│   │   └── LoginScreen.tsx
│   ├── services/            # Serviços e API
│   │   └── api.ts
│   └── App.tsx
├── package.json
└── README.md
```

---


## 📊 Modelo de Dados

### 📐 Diagrama de Classes (UML)

![Diagrama sem nome (2)](https://github.com/user-attachments/assets/7a26365b-62dc-4165-934a-5eadb4e21fa2)



### 🗄️ Modelo Entidade-Relacionamento (ER)
![e97aae50-7cdd-4bbe-b123-799c2bfa1f0e](https://github.com/user-attachments/assets/9fedb195-cda1-4e22-a855-0f16e60109e7)




---

## 📖 Documentação

### 📚 Swagger/OpenAPI

Acesse a documentação interativa da API em:
```
http://localhost:8083/swagger-ui.html
```


---

## 🎯 Público-Alvo

- 💼 Pessoas que desejam ter maior controle sobre suas finanças pessoais
- 📊 Usuários que buscam acompanhar seus gastos mensais
- 💡 Indivíduos interessados em tomar decisões financeiras mais conscientes

---

