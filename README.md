# 🚗 Sistema de Oficina - Frontend Integrado

Este é o frontend integrado para o sistema de oficina, conectado com a API da oficina-ac3.

## 🚀 Funcionalidades

### 👥 **Proprietários**

- ✅ Cadastrar novos proprietários
- ✅ Listar proprietários com paginação
- ✅ Editar dados de proprietários existentes
- ✅ Excluir proprietários
- ✅ Visualizar veículos associados a cada proprietário

### 🚗 **Veículos**

- ✅ Cadastrar novos veículos
- ✅ Listar veículos com paginação
- ✅ Editar dados de veículos existentes
- ✅ Excluir veículos
- ✅ Associar veículos a proprietários
- ✅ Visualizar informações do proprietário

### 🔧 **Revisões**

- ✅ Cadastrar novas revisões
- ✅ Listar revisões com paginação
- ✅ Editar revisões existentes
- ✅ Excluir revisões
- ✅ Selecionar múltiplos tipos de revisão
- ✅ Cálculo automático de valor total
- ✅ Marcar revisões como concluídas
- ✅ Associar revisões a veículos

## 🛠️ **Tipos de Revisão Disponíveis**

1. **Troca de Óleo** - R$ 120,00
2. **Alinhamento e Balanceamento** - R$ 150,00
3. **Troca de Pneu** - R$ 250,00
4. **Revisão Completa** - R$ 400,00
5. **Troca de Filtro de Ar** - R$ 50,00
6. **Troca de Filtro de Óleo** - R$ 60,00
7. **Troca de Bateria** - R$ 350,00
8. **Troca de Velas de Ignição** - R$ 80,00
9. **Troca de Limpador de Parabrisa** - R$ 30,00
10. **Inspeção Técnica** - R$ 100,00

## 🔧 **Configuração**

### Pré-requisitos

- Backend da oficina-ac3 rodando na porta 8080
- Navegador web moderno

### Como usar

1. Certifique-se de que o backend está rodando
2. Abra o arquivo `index.html` no navegador
3. Navegue pelas abas para gerenciar proprietários, veículos e revisões

## 📡 **APIs Utilizadas**

- **Proprietários**: `http://localhost:8080/proprietario`
- **Veículos**: `http://localhost:8080/veiculo`
- **Revisões**: `http://localhost:8080/revisao`

## 🎨 **Características da Interface**

- **Design Responsivo**: Interface adaptável a diferentes tamanhos de tela
- **Navegação por Abas**: Organização clara das funcionalidades
- **Paginação**: Listagem eficiente de dados
- **Validação**: Validação de formulários no frontend
- **Feedback Visual**: Mensagens de sucesso e erro
- **Interface Intuitiva**: Fácil de usar e navegar

## 🔄 **Fluxo de Trabalho**

1. **Cadastre Proprietários** primeiro
2. **Cadastre Veículos** associando-os aos proprietários
3. **Cadastre Revisões** para os veículos existentes
4. **Gerencie** todos os dados através das abas correspondentes

## 🐛 **Solução de Problemas**

### Erro de Conexão

- Verifique se o backend está rodando na porta 8080
- Verifique se não há bloqueios de CORS

### Erro de Chave Primária Duplicada

- Execute o script `fix_sequences.sql` no banco de dados
- Ou reinicie o banco de dados com o script `init.sql` atualizado

### Dados não aparecem

- Verifique se há dados no banco de dados
- Verifique o console do navegador para erros JavaScript

## 📝 **Notas Técnicas**

- O frontend utiliza JavaScript vanilla (sem frameworks)
- As requisições são feitas usando Fetch API
- O design utiliza CSS Grid e Flexbox
- A paginação é implementada no frontend
- Os tipos de revisão são hardcoded no frontend

## 🚀 **Próximas Melhorias**

- [ ] Busca e filtros avançados
- [ ] Relatórios e estatísticas
- [ ] Upload de imagens
- [ ] Notificações em tempo real
- [ ] Exportação de dados
- [ ] Temas personalizáveis
