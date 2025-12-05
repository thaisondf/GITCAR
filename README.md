
## Pré-requisitos
Para compilar e executar o projeto, você precisará ter instalado:

*   **JDK (Java Development Kit)** versão 11 ou superior.
*   **Apache Maven**.
*   Um servidor de banco de dados **MySQL** configurado (db_agencia) (ou optar por usar SQLite).

## Configuração do Banco de Dados
O projeto utiliza a classe `com.gitcar.app.utils.DatabaseUtil` para gerenciar a conexão com o banco de dados.

1.  **MySQL:** Certifique-se de que seu servidor MySQL esteja em execução e crie o esquema de banco de dados necessário. As credenciais de conexão devem ser configuradas na classe `com.gitcar.app.utils.DatabaseUtil`.
2.  **SQLite (Opcional):** Se optar por SQLite, o banco de dados será um arquivo local.

*É altamente recomendável revisar e atualizar as credenciais de banco de dados na classe `DatabaseUtil` antes da primeira execução.*

## Funcionalidades Principais
Com base nos arquivos FXML e controladores, o sistema parece incluir as seguintes funcionalidades:

*   **Login e Autenticação** (`LoginView.fxml` )
*   **Menus de Navegação** (`MenuGerenteView.fxml`, `MenuVendedorView.fxml`)
*   **Gestão de Veículos** (Cadastro, Edição e Gerenciamento)
*   **Gestão de Clientes** (Adicionar Cliente)
*   **Gestão de Empregados**
*   **Agendamento de Test Drive**
*   **Registro de Vendas** (`VenderVeiculoView.fxml`, `HistoricoVendasView.fxml`)
*   **Relatórios de Vendas** (Geração de PDF)
