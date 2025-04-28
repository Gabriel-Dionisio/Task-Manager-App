# Task Manager App

Este repositório contém o código-fonte do aplicativo **Task Manager**, desenvolvido para gerenciar tarefas de forma eficiente. O aplicativo permite que os usuários criem, editem, excluam e visualizem tarefas com diferentes níveis de prioridade e status (pendente ou concluída).

## Funcionalidades

- **Gerenciamento de Tarefas**: Adicione, edite e exclua tarefas.
- **Exibição de Tarefas Pendentes**: Visualize todas as tarefas pendentes.
- **Exibição de Tarefas Concluídas**: Visualize as tarefas já concluídas.
- **Prioridade das Tarefas**: Atribua diferentes níveis de prioridade (baixa, média ou alta) às tarefas.
- **Interface de Usuário Intuitiva**: A interface permite adicionar e editar tarefas de forma simples com campos de texto e seletores de data e hora.
- **Armazenamento Local**: As tarefas são armazenadas localmente utilizando o banco de dados SQLite.
- **Room**: Usado para facilitar a interação com o banco de dados SQLite.
- **RecyclerView**: Exibição das tarefas em listas.
- **AlertDialog**: Interface para a criação e edição de tarefas.
- **SimpleDateFormat**: Para formatação e parsing de datas.

## Tecnologias Utilizadas

- **Kotlin**: Linguagem principal utilizada no desenvolvimento.
- **Android SDK**: Usado para a criação do aplicativo Android.
- **SQLite**: Banco de dados local para armazenar as tarefas.
- **Room**: Biblioteca para facilitar a interação com o SQLite.
- **RecyclerView**: Exibição das tarefas em listas.
- **AlertDialog**: Interface para a criação e edição de tarefas.
- **SimpleDateFormat**: Para formatação e parsing de datas.

## Instruções de Compilação/Execução

### Pré-requisitos

- **Android Studio** instalado em sua máquina.
- **JDK 8 ou superior**.
- **Repositório clonado** para seu ambiente local.

### Passos para rodar o aplicativo

1. **Clone o repositório**:
   ```bash
   git clone https://github.com/Gabriel-Dionisio/Task-Manager-App.git

## 2. Sincronize o Gradle
- No Android Studio, clique em **Sync Now** para garantir que todas as dependências do Gradle sejam baixadas e que o projeto esteja pronto para ser compilado.

## 3. Execute o Projeto
- Conecte um dispositivo Android ou use o **emulador**.
- Clique no ícone de **execução** no Android Studio para compilar e executar o aplicativo no seu dispositivo.

## 4. Compilação do APK (opcional)
- Para gerar o **APK de produção**, vá até o menu **Build > Build APK(s)** no Android Studio.

# Estrutura do Projeto

## com.example.taskmanager.ui
- **PendingItemsFragment**: Exibe as tarefas pendentes.
- **CompletedItemsFragment**: Exibe as tarefas concluídas.

## com.example.taskmanager.model
- **Task**: Classe que representa uma tarefa, com atributos como nome, descrição, data/hora, nível de prioridade e status de conclusão.

## com.example.taskmanager
- **TaskAdapter**: Adaptador usado para exibir as tarefas em um **RecyclerView**.
- **TaskDialog**: Diálogo para criar ou editar tarefas.
- **MainActivity**: A atividade principal que gerencia os fragments e interações com o usuário.

## com.example.taskmanager.database
- **App**: Classe que configura o banco de dados e a interação com a camada de persistência.
- **TaskDao**: Interface do **DAO** (Data Access Object) para as operações no banco de dados.

# Funcionalidade do Aplicativo

- **Adicionar Tarefa**: O usuário pode adicionar uma nova tarefa através de um formulário onde especifica o nome, descrição, data/hora e prioridade.
- **Editar Tarefa**: O usuário pode editar tarefas existentes, com os campos sendo preenchidos automaticamente com os valores atuais.
- **Excluir Tarefa**: O usuário pode excluir uma tarefa com um simples clique longo (**long click**) sobre a tarefa na lista.
- **Filtrar Tarefas**: O aplicativo permite filtrar as tarefas por status, dividindo-as entre pendentes e concluídas.
- **Visualização de Tarefas**: O aplicativo exibe as tarefas em uma lista organizada, utilizando um **RecyclerView** para mostrar as tarefas pendentes e concluídas.

# Banco de Dados

O aplicativo utiliza o **Room** para gerenciar o banco de dados local. A camada de persistência é configurada da seguinte maneira:

- **Task Entity**: A entidade **Task** representa uma tarefa no banco de dados, com campos como `id`, `taskName`, `description`, `dateHour`, `priorityLevel`, e `done`.
- **DAO**: A interface **TaskDao** define os métodos para acessar o banco de dados, como:
  - `insert`: Para inserir uma nova tarefa.
  - `updateTask`: Para atualizar uma tarefa existente.
  - `getAll`: Para obter todas as tarefas.
  - `updateTaskDoneStatus`: Para atualizar o status de conclusão de uma tarefa.
- **Database**: A classe **App** configura o banco de dados utilizando o **Room**.

