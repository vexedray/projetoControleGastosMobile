# Documentação HATEOAS - API de Controle de Gastos

## Visão Geral

Esta API implementa o padrão HATEOAS (Hypermedia As The Engine Of Application State) em todos os endpoints, fornecendo links de navegação dinâmicos que permitem aos clientes descobrir e interagir com os recursos da API de forma mais intuitiva.

## O que é HATEOAS?

HATEOAS é um constraint da arquitetura REST que permite que as aplicações cliente naveguem pela API dinamicamente através dos links fornecidos nas respostas, sem necessidade de conhecimento prévio da estrutura da API.

## Estrutura de Resposta HATEOAS

Todas as respostas da API agora incluem um objeto `_links` contendo links hipermídia relacionados ao recurso:

```json
{
  "id": 1,
  "name": "João Silva",
  "email": "joao@example.com",
  "_links": {
    "self": {
      "href": "http://localhost:8083/api/users/1"
    },
    "users": {
      "href": "http://localhost:8083/api/users"
    },
    "expenses": {
      "href": "http://localhost:8083/api/expenses/user/1"
    }
  }
}
```

## Endpoints e Links HATEOAS

### 1. Autenticação (`/api/auth`)

#### POST `/api/auth/login`
**Descrição:** Realiza login e retorna token JWT com links para recursos relacionados.

**Resposta de Sucesso:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "joao@example.com",
  "name": "João Silva",
  "_links": {
    "user": {
      "href": "http://localhost:8083/api/users/1"
    },
    "users": {
      "href": "http://localhost:8083/api/users"
    },
    "expenses": {
      "href": "http://localhost:8083/api/expenses/user/1"
    },
    "categories": {
      "href": "http://localhost:8083/api/categories"
    }
  }
}
```

**Links Disponíveis:**
- `user`: Link para os dados completos do usuário autenticado
- `users`: Link para listar todos os usuários
- `expenses`: Link para as despesas do usuário autenticado
- `categories`: Link para listar todas as categorias disponíveis

#### POST `/api/auth/register`
**Descrição:** Registra novo usuário com links para recursos relacionados.

**Resposta de Sucesso:**
```json
{
  "userId": 2,
  "email": "maria@example.com",
  "name": "Maria Santos",
  "_links": {
    "user": {
      "href": "http://localhost:8083/api/users/2"
    },
    "login": {
      "href": "http://localhost:8083/api/auth/login"
    },
    "users": {
      "href": "http://localhost:8083/api/users"
    }
  }
}
```

**Links Disponíveis:**
- `user`: Link para os dados do usuário recém-criado
- `login`: Link para realizar login
- `users`: Link para listar todos os usuários

---

### 2. Usuários (`/api/users`)

#### GET `/api/users`
**Descrição:** Lista todos os usuários com links HATEOAS.

**Resposta:**
```json
{
  "_embedded": {
    "userModelList": [
      {
        "id": 1,
        "name": "João Silva",
        "email": "joao@example.com",
        "_links": {
          "self": {
            "href": "http://localhost:8083/api/users/1"
          },
          "users": {
            "href": "http://localhost:8083/api/users"
          },
          "user-by-email": {
            "href": "http://localhost:8083/api/users/email/joao@example.com"
          },
          "update": {
            "href": "http://localhost:8083/api/users/1"
          },
          "delete": {
            "href": "http://localhost:8083/api/users/1"
          },
          "expenses": {
            "href": "http://localhost:8083/api/expenses/user/1"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8083/api/users"
    }
  }
}
```

**Links Disponíveis (por usuário):**
- `self`: Link para o próprio recurso do usuário
- `users`: Link para a coleção de usuários
- `user-by-email`: Link para buscar o usuário por email
- `update`: Link para atualizar o usuário (PUT)
- `delete`: Link para deletar o usuário (DELETE)
- `expenses`: Link para as despesas do usuário

#### GET `/api/users/{id}`
**Descrição:** Busca usuário por ID com links relacionados.

**Resposta:**
```json
{
  "id": 1,
  "name": "João Silva",
  "email": "joao@example.com",
  "_links": {
    "self": {
      "href": "http://localhost:8083/api/users/1"
    },
    "users": {
      "href": "http://localhost:8083/api/users"
    },
    "user-by-email": {
      "href": "http://localhost:8083/api/users/email/joao@example.com"
    },
    "update": {
      "href": "http://localhost:8083/api/users/1"
    },
    "delete": {
      "href": "http://localhost:8083/api/users/1"
    },
    "expenses": {
      "href": "http://localhost:8083/api/expenses/user/1"
    }
  }
}
```

#### GET `/api/users/email/{email}`
**Descrição:** Busca usuário por email com os mesmos links do GET por ID.

#### POST `/api/users`
**Descrição:** Cria novo usuário e retorna com links HATEOAS.

#### PUT `/api/users/{id}`
**Descrição:** Atualiza usuário e retorna dados atualizados com links HATEOAS.

#### DELETE `/api/users/{id}`
**Descrição:** Remove usuário (retorna 204 No Content).

---

### 3. Categorias (`/api/categories`)

#### GET `/api/categories`
**Descrição:** Lista todas as categorias com links HATEOAS.

**Resposta:**
```json
{
  "_embedded": {
    "categoryModelList": [
      {
        "id": 1,
        "name": "Alimentação",
        "description": "Gastos com alimentação",
        "color": "#FF5733",
        "icon": "utensils",
        "_links": {
          "self": {
            "href": "http://localhost:8083/api/categories/1"
          },
          "categories": {
            "href": "http://localhost:8083/api/categories"
          },
          "update": {
            "href": "http://localhost:8083/api/categories/1"
          },
          "delete": {
            "href": "http://localhost:8083/api/categories/1"
          },
          "expenses": {
            "href": "http://localhost:8083/api/expenses/category/1"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8083/api/categories"
    }
  }
}
```

**Links Disponíveis (por categoria):**
- `self`: Link para o próprio recurso da categoria
- `categories`: Link para a coleção de categorias
- `update`: Link para atualizar a categoria (PUT)
- `delete`: Link para deletar a categoria (DELETE)
- `expenses`: Link para as despesas desta categoria

#### GET `/api/categories/{id}`
**Descrição:** Busca categoria por ID com links relacionados.

#### POST `/api/categories`
**Descrição:** Cria nova categoria e retorna com links HATEOAS.

#### PUT `/api/categories/{id}`
**Descrição:** Atualiza categoria e retorna dados atualizados com links HATEOAS.

#### DELETE `/api/categories/{id}`
**Descrição:** Remove categoria (retorna 204 No Content).

---

### 4. Despesas (`/api/expenses`)

#### GET `/api/expenses`
**Descrição:** Lista todas as despesas com links HATEOAS.

**Resposta:**
```json
{
  "_embedded": {
    "expenseModelList": [
      {
        "id": 1,
        "amount": 150.50,
        "description": "Almoço no restaurante",
        "date": "2025-11-24",
        "userId": 1,
        "userName": "João Silva",
        "categoryId": 1,
        "categoryName": "Alimentação",
        "_links": {
          "self": {
            "href": "http://localhost:8083/api/expenses/1"
          },
          "expenses": {
            "href": "http://localhost:8083/api/expenses"
          },
          "user": {
            "href": "http://localhost:8083/api/users/1"
          },
          "category": {
            "href": "http://localhost:8083/api/categories/1"
          },
          "user-expenses": {
            "href": "http://localhost:8083/api/expenses/user/1"
          },
          "category-expenses": {
            "href": "http://localhost:8083/api/expenses/category/1"
          },
          "update": {
            "href": "http://localhost:8083/api/expenses/1"
          },
          "delete": {
            "href": "http://localhost:8083/api/expenses/1"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8083/api/expenses"
    }
  }
}
```

**Links Disponíveis (por despesa):**
- `self`: Link para o próprio recurso da despesa
- `expenses`: Link para a coleção de despesas
- `user`: Link para o usuário que criou a despesa
- `category`: Link para a categoria da despesa
- `user-expenses`: Link para todas as despesas do mesmo usuário
- `category-expenses`: Link para todas as despesas da mesma categoria
- `update`: Link para atualizar a despesa (PUT)
- `delete`: Link para deletar a despesa (DELETE)

#### GET `/api/expenses/{id}`
**Descrição:** Busca despesa por ID com links relacionados.

#### GET `/api/expenses/user/{userId}`
**Descrição:** Lista despesas de um usuário específico.

**Resposta:**
```json
{
  "_embedded": {
    "expenseModelList": [...]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8083/api/expenses/user/1"
    },
    "user": {
      "href": "http://localhost:8083/api/users/1"
    },
    "all-expenses": {
      "href": "http://localhost:8083/api/expenses"
    }
  }
}
```

**Links Disponíveis (na coleção):**
- `self`: Link para a própria coleção
- `user`: Link para os dados do usuário
- `all-expenses`: Link para todas as despesas

#### GET `/api/expenses/category/{categoryId}`
**Descrição:** Lista despesas de uma categoria específica.

**Resposta:**
```json
{
  "_embedded": {
    "expenseModelList": [...]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8083/api/expenses/category/1"
    },
    "category": {
      "href": "http://localhost:8083/api/categories/1"
    },
    "all-expenses": {
      "href": "http://localhost:8083/api/expenses"
    }
  }
}
```

**Links Disponíveis (na coleção):**
- `self`: Link para a própria coleção
- `category`: Link para os dados da categoria
- `all-expenses`: Link para todas as despesas

#### POST `/api/expenses`
**Descrição:** Cria nova despesa e retorna com links HATEOAS.

#### PUT `/api/expenses/{id}`
**Descrição:** Atualiza despesa e retorna dados atualizados com links HATEOAS.

#### DELETE `/api/expenses/{id}`
**Descrição:** Remove despesa (retorna 204 No Content).

---

## Benefícios da Implementação HATEOAS

1. **Descoberta Automática**: Clientes podem descobrir ações disponíveis através dos links fornecidos
2. **Desacoplamento**: Mudanças na estrutura da API não quebram os clientes que seguem os links
3. **Navegabilidade**: Navegação intuitiva entre recursos relacionados
4. **Documentação Viva**: Os links servem como documentação em tempo real das ações disponíveis
5. **RESTful Compliance**: Atende ao nível 3 do Richardson Maturity Model

## Estrutura de Classes

### Models HATEOAS
- `UserModel`: Modelo de usuário com suporte HATEOAS
- `CategoryModel`: Modelo de categoria com suporte HATEOAS
- `ExpenseModel`: Modelo de despesa com suporte HATEOAS
- `LoginResponseModel`: Modelo de resposta de login com suporte HATEOAS

### Assemblers
- `UserModelAssembler`: Constrói links HATEOAS para usuários
- `CategoryModelAssembler`: Constrói links HATEOAS para categorias
- `ExpenseModelAssembler`: Constrói links HATEOAS para despesas

## Como Usar os Links HATEOAS

### Exemplo em JavaScript/TypeScript:

```javascript
// 1. Fazer login
const loginResponse = await fetch('http://localhost:8083/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email: 'joao@example.com', password: 'senha123' })
});

const loginData = await loginResponse.json();

// 2. Usar o link para buscar despesas do usuário
const expensesUrl = loginData._links.expenses.href;
const expensesResponse = await fetch(expensesUrl, {
  headers: { 'Authorization': `Bearer ${loginData.token}` }
});

const expensesData = await expensesResponse.json();

// 3. Navegar para uma despesa específica
const firstExpense = expensesData._embedded.expenseModelList[0];
const expenseUrl = firstExpense._links.self.href;

// 4. Buscar o usuário da despesa
const userUrl = firstExpense._links.user.href;
const userResponse = await fetch(userUrl, {
  headers: { 'Authorization': `Bearer ${loginData.token}` }
});
```

## Dependências Adicionadas

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-hateoas</artifactId>
</dependency>
```

## Testando a API com HATEOAS

### Usando cURL:

```bash
# Login
curl -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"joao@example.com","password":"senha123"}'

# Buscar usuário por ID (seguindo o link retornado no login)
curl -X GET http://localhost:8083/api/users/1 \
  -H "Authorization: Bearer SEU_TOKEN_JWT"

# Listar todas as categorias
curl -X GET http://localhost:8083/api/categories \
  -H "Authorization: Bearer SEU_TOKEN_JWT"
```

## Considerações de Segurança

- Todos os endpoints (exceto login e register) requerem autenticação JWT
- Os links HATEOAS não substituem a necessidade de autorização adequada
- O token deve ser enviado no header `Authorization: Bearer {token}`

## Versionamento

Esta implementação HATEOAS foi adicionada mantendo compatibilidade com clientes existentes. Os campos de dados originais permanecem inalterados, apenas foram adicionados os links `_links`.

---

**Versão:** 1.0  
**Data:** 24 de Novembro de 2025  
**Autor:** Sistema de Controle de Gastos
