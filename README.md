# Sistema de Controle de Gastos

Sistema completo de controle de gastos pessoais com backend em Spring Boot e frontend em React Native.

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
- **Victory Native** (gráficos)
- **Axios** (HTTP client)

## 📋 Pré-requisitos

### Backend
- Java 17 ou superior
- Maven 3.6+
- MySQL Server 8.0+

### Frontend
- Node.js 16+
- npm ou yarn
- Expo CLI

## 🛠️ Instalação e Configuração

### 1. Backend (Spring Boot)

```bash
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

**Configuração do Banco de Dados:**
1. Instale o MySQL Server
2. Crie um usuário root sem senha ou configure no `application.properties`
3. O banco `sistema_gastos` será criado automaticamente

### 2. Frontend (React Native)

```bash
# Navegar para o diretório do frontend
cd frontend

# Instalar dependências
npm install --legacy-peer-deps

# Executar o projeto
npm start
```

## 📱 Funcionalidades

### Backend - API REST

#### Endpoints disponíveis:

- **POST /gastos** - Criar novo gasto
- **GET /gastos** - Listar todos os gastos
- **GET /gastos/por-tipo** - Retornar gastos agrupados por tipo (para gráfico)
- **DELETE /gastos/{id}** - Deletar um gasto

#### Modelo de dados:
```json
{
  "id": 1,
  "valor": 25.50,
  "tipo": "alimentacao",
  "data": "2024-10-01T12:30:00"
}
```

### Frontend - App Mobile

#### Funcionalidades:
- ✅ Formulário para adicionar gastos (valor, tipo, data)
- ✅ Lista de gastos com opção de excluir
- ✅ Gráfico de pizza mostrando distribuição por tipo
- ✅ Interface responsiva e intuitiva

#### Tipos de gastos suportados:
- Alimentação
- Transporte
- Lazer
- Saúde
- Educação
- Outros


## 📄 Licença

Este projeto está sob a licença MIT.
