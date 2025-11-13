# 📱 Frontend - Controle de Gastos Mobile

Aplicativo React Native para controle de gastos pessoais.

## 🚀 Como Rodar o Projeto

### 1️⃣ Pré-requisitos

- **Node.js** instalado (versão 14 ou superior)
- **Expo CLI** (será instalado automaticamente)
- **Backend rodando** na porta 8083

### 2️⃣ Instalar Dependências

```bash
cd frontend
npm install
```

### 3️⃣ Iniciar o Projeto

```bash
npm start
```

### 4️⃣ Abrir no Dispositivo

Após executar `npm start`, você verá um QR Code. Escolha uma opção:

**Opção A: Emulador Android**
- Pressione `a` no terminal
- Ou use Android Studio

**Opção B: Emulador iOS (apenas Mac)**
- Pressione `i` no terminal
- Ou use Xcode Simulator

**Opção C: Dispositivo Físico**
1. Instale o **Expo Go** no seu celular:
   - [Android - Play Store](https://play.google.com/store/apps/details?id=host.exp.exponent)
   - [iOS - App Store](https://apps.apple.com/app/expo-go/id982107779)

2. Escaneie o QR Code:
   - **Android**: Use o app Expo Go
   - **iOS**: Use a câmera nativa

---

## ⚙️ Configuração Importante

### 📍 Configurar IP do Backend

O projeto está configurado para **emulador Android** (`10.0.2.2:8083`).

**Se estiver usando dispositivo físico ou emulador iOS:**

1. Descubra o IP da sua máquina:
   ```bash
   # Windows
   ipconfig
   
   # Mac/Linux
   ifconfig
   ```

2. Edite o arquivo `src/services/api.ts` (linha 29):
   ```typescript
   const API_BASE_URL = 'http://192.168.1.100:8083/api'; // Coloque seu IP aqui
   ```

3. **Certifique-se de que:**
   - ✅ Backend está rodando (`mvn spring-boot:run`)
   - ✅ MySQL está ativo
   - ✅ Celular está na **mesma rede Wi-Fi** do computador

---

## 📂 Estrutura do Projeto

```
frontend/
├── src/
│   ├── components/
│   │   ├── ExpenseForm.tsx    # Formulário de adicionar gasto
│   │   └── ExpenseList.tsx    # Lista de gastos
│   ├── screens/
│   │   ├── HomeScreen.tsx     # Tela principal (gastos)
│   │   └── CategoriesScreen.tsx # Tela de categorias
│   └── services/
│       └── api.ts             # Integração com backend
├── App.tsx                     # Navegação principal
└── package.json               # Dependências
```

---

## 🎯 Funcionalidades

### ✅ Tela de Gastos (Home)
- [x] Listar todos os gastos
- [x] Adicionar novo gasto
- [x] Deletar gasto (com confirmação)
- [x] Atualizar lista (pull-to-refresh)
- [x] Exibir categoria de cada gasto

### ✅ Tela de Categorias
- [x] Listar todas as categorias
- [x] Criar nova categoria
- [x] Exibir descrição e data de criação
- [x] Atualizar lista (pull-to-refresh)

---

## 🔗 Endpoints Utilizados

O frontend consome os seguintes endpoints do backend:

### Categorias
- `GET /api/categories` - Listar todas
- `POST /api/categories` - Criar nova
- `PUT /api/categories/{id}` - Atualizar
- `DELETE /api/categories/{id}` - Deletar

### Gastos (Expenses)
- `GET /api/expenses` - Listar todos
- `POST /api/expenses` - Criar novo
- `GET /api/expenses/user/{userId}` - Por usuário
- `GET /api/expenses/category/{categoryId}` - Por categoria
- `PUT /api/expenses/{id}` - Atualizar
- `DELETE /api/expenses/{id}` - Deletar

---

## 🐛 Troubleshooting

### ❌ Erro: "Network request failed"
**Solução:**
1. Verifique se o backend está rodando (`http://localhost:8083`)
2. Confira o IP no arquivo `api.ts`
3. Certifique-se de estar na mesma rede Wi-Fi

### ❌ Erro: "Cannot connect to backend"
**Solução:**
1. Teste o backend no navegador: `http://localhost:8083/api/categories`
2. Verifique se o MySQL está ativo
3. Confira as configurações do `application.properties`

### ❌ Expo não abre
**Solução:**
```bash
# Limpar cache
npm start -- --clear

# Reinstalar dependências
rm -rf node_modules
npm install
```

### ❌ Categorias não aparecem
**Solução:**
1. Crie categorias primeiro na tela "Categorias"
2. Verifique se o backend retorna dados em `http://localhost:8083/api/categories`

---

## 📝 Notas Técnicas

### IPs por Tipo de Dispositivo

| Dispositivo | IP do Backend |
|-------------|---------------|
| Emulador Android | `10.0.2.2:8083` |
| Emulador iOS | `localhost:8083` |
| Dispositivo Físico | `192.168.X.X:8083` (IP da máquina) |

### Formato dos Dados

**Categoria:**
```json
{
  "id": 1,
  "name": "Alimentação",
  "description": "Gastos com comida",
  "createdAt": "2025-11-13T00:00:00"
}
```

**Gasto:**
```json
{
  "id": 1,
  "description": "Almoço",
  "amount": 35.50,
  "date": "2025-11-13",
  "categoryId": 1,
  "userId": 1,
  "categoryName": "Alimentação"
}
```

---

## 🔧 Comandos Úteis

```bash
# Iniciar projeto
npm start

# Limpar cache
npm start -- --clear

# Rodar no Android
npm run android

# Rodar no iOS (apenas Mac)
npm run ios

# Verificar compatibilidade
npx expo-doctor

# Atualizar Expo
npm install expo@latest
```

---

## 👨‍💻 Desenvolvimento

Projeto desenvolvido com:
- **React Native** 0.72
- **Expo** 49
- **TypeScript** 5.1
- **React Navigation** 6.x
- **Axios** para requisições HTTP

---

## ✅ Checklist Antes de Testar

- [ ] Backend rodando na porta 8083
- [ ] MySQL ativo e com banco criado
- [ ] Dependências instaladas (`npm install`)
- [ ] IP correto no `api.ts` (se dispositivo físico)
- [ ] Celular na mesma rede Wi-Fi (se dispositivo físico)
- [ ] Pelo menos 1 categoria criada

---

**Pronto para usar!** 🚀

Execute `npm start` e bom teste!
