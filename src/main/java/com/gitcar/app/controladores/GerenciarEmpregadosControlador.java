package com.gitcar.app.controladores;

import com.gitcar.app.dao.EmpregadoDAO; // referencia dao pra banco
import com.gitcar.app.models.Empregado; // modelo empregado
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Optional;

public class GerenciarEmpregadosControlador { 

    // campos do fxml que ligam com a interface visual, tipo campos texto, botoes, tabelas
    @FXML private TextField campoBuscaEmpregado; 
    @FXML private ComboBox<String> comboBoxFiltroStatus; 
    @FXML private Button botaoBuscar; 
    @FXML private TableView<Empregado> tabelaEmpregados; 
    @FXML private TableColumn<Empregado, Integer> colIdEmpregado; 
    @FXML private TableColumn<Empregado, String> colNome; 
    @FXML private TableColumn<Empregado, String> colEmail; 
    @FXML private TableColumn<Empregado, String> colCargo; 
    @FXML private TableColumn<Empregado, String> colStatus; 
    @FXML private TextField campoIdEmpregado; 
    @FXML private TextField campoNome; 
    @FXML private TextField campoEmailForm; 
    @FXML private PasswordField campoSenha; 
    @FXML private ComboBox<String> comboBoxCargo; 
    @FXML private ComboBox<String> comboBoxStatus; 
    @FXML private Button botaoAdicionar; 
    @FXML private Button botaoSalvar; 
    @FXML private Button botaoLimparForm;
    @FXML private Button botaoAtivarDesativar; 
    @FXML private Label rotuloStatusForm; 
    @FXML private Label rotuloStatus; 

    // dao pra acessar o banco de dados de empregados
    private EmpregadoDAO empregadoDAO;

    // lista que guarda todos os empregados em memoria (obs: observable pra atualizar interface)
    private ObservableList<Empregado> todosEmpregados = FXCollections.observableArrayList(); 
    // lista filtrada que mostra so os empregados que batem com o filtro da busca e status
    private FilteredList<Empregado> empregadosFiltrados; 
    // empregado selecionado na tabela pra editar ou ativar/desativar
    private Empregado empregadoSelecionado = null; 

    @FXML
    public void initialize() {
    this.empregadoDAO = new EmpregadoDAO();

    rotuloStatus.setText("");
    rotuloStatusForm.setText("");
    configurarColunasTabela();
    configurarComboBoxes();

    // Inicializa a lista base
    todosEmpregados = FXCollections.observableArrayList();

    // Cria lista filtrada e liga à tabela
    empregadosFiltrados = new FilteredList<>(todosEmpregados, p -> true);
    tabelaEmpregados.setItems(empregadosFiltrados);

    // Agora sim pode carregar os dados
    carregarEmpregados();

    // Listener de seleção
    tabelaEmpregados.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> preencherFormulario(novo));

    limparFormulario();
    desabilitarFormulario(true);
}


    private void configurarColunasTabela() {
        // define quais atributos do objeto empregado vao aparecer em cada coluna da tabela
        colIdEmpregado.setCellValueFactory(new PropertyValueFactory<>("idEmpregado")); 
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome")); 
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colCargo.setCellValueFactory(new PropertyValueFactory<>("cargo")); 
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void configurarComboBoxes() {
        // define os valores fixos para filtros e combos (pode mudar se vier do bd)
        comboBoxFiltroStatus.setItems(FXCollections.observableArrayList("Todos", "Ativo", "Inativo")); 
        comboBoxFiltroStatus.getSelectionModel().select("Todos");

        comboBoxCargo.setItems(FXCollections.observableArrayList("Vendedor", "Gerente")); 
        comboBoxStatus.setItems(FXCollections.observableArrayList("Ativo", "Inativo")); 
    }

    private void carregarEmpregados() {
        // busca todos empregados do banco e atualiza lista
        try {
            List<Empregado> empregados = empregadoDAO.buscarTodos();
            todosEmpregados.setAll(empregados);
            rotuloStatus.setText("Carregado " + todosEmpregados.size() + " funcionarios.");
            aplicarPredicadoFiltro(); // aplica filtro na lista apos carregar
        } catch (Exception e) {
            rotuloStatus.setText("Erro ao carregar funcionarios: " + e.getMessage());
            System.out.println("Erro ao carregar funcionarios: " + e.getMessage());
            e.printStackTrace();
            todosEmpregados.clear();
        }
    }

    @FXML
    void acaoBuscar(ActionEvent evento) {
        // filtro é "colocado" ao clicar no buscar
        aplicarPredicadoFiltro();
    }

    private void aplicarPredicadoFiltro() {
        // pega texto da busca e status selecionado
        String termoBusca = campoBuscaEmpregado.getText() == null ? "" : campoBuscaEmpregado.getText().toLowerCase(); 
        String filtroStatus = comboBoxFiltroStatus.getValue(); 

        // filtra a lista conforme termo e status
        empregadosFiltrados.setPredicate(empregado -> { 
            boolean matchStatus = ("Todos".equals(filtroStatus) || empregado.getStatus().equalsIgnoreCase(filtroStatus)); 

            if (termoBusca.isEmpty()) {
                return matchStatus;
            }

            boolean matchTermo = empregado.getNome().toLowerCase().contains(termoBusca) ||
                                 empregado.getEmail().toLowerCase().contains(termoBusca);

            return matchTermo && matchStatus;
        });
        rotuloStatus.setText("Exibindo " + empregadosFiltrados.size() + " funcionarios.");
    }

    private void preencherFormulario(Empregado empregado) {
        // preenche o formulario com os dados do empregado selecionado ou limpa se nulo
        empregadoSelecionado = empregado;
        if (empregado != null) {
            desabilitarFormulario(false);
            campoIdEmpregado.setText(String.valueOf(empregado.getIdEmpregado()));
            campoNome.setText(empregado.getNome());
            campoEmailForm.setText(empregado.getEmail());
            campoSenha.clear(); // nao mostra senha, so limpa
            campoSenha.setPromptText("(deixe em branco para nao alterar)");
            comboBoxCargo.setValue(empregado.getCargo());
            comboBoxStatus.setValue(empregado.getStatus());
            botaoAtivarDesativar.setText("Ativo".equals(empregado.getStatus()) ? "Desativar" : "Ativar");
            rotuloStatusForm.setText("");
            botaoSalvar.setText("Salvar Alteracoes");
        } else {
            limparFormulario();
            desabilitarFormulario(true);
        }
    }

    @FXML
    void acaoAdicionar(ActionEvent evento) {
        // prepara formulario para adicionar novo empregado
        limparFormulario();
        desabilitarFormulario(false);
        empregadoSelecionado = null; // indica que vai adicionar novo
        campoIdEmpregado.setText("(novo)");
        comboBoxStatus.setValue("Ativo"); // status padrao
        campoSenha.setPromptText("(senha obrigatoria)");
        rotuloStatusForm.setText("Preencha os dados para adicionar novo funcionario.");
        botaoSalvar.setText("Adicionar");
        campoNome.requestFocus();
    }

    @FXML
    void acaoSalvar(ActionEvent evento) {
        // salva novo empregado ou atualiza existente com validacoes simples
        rotuloStatusForm.setText("");
        String nome = campoNome.getText().trim();
        String email = campoEmailForm.getText().trim();
        String senha = campoSenha.getText();
        String cargo = comboBoxCargo.getValue();
        String status = comboBoxStatus.getValue();

        if (nome.isEmpty() || email.isEmpty() || cargo == null || status == null) {
            rotuloStatusForm.setText("erro: nome, email, cargo e status sao obrigatorios.");
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            rotuloStatusForm.setText("erro: formato de email invalido.");
            return;
        }

        try {
        if (empregadoSelecionado == null) { // adiciona novo
            if (senha.isEmpty()) {
                rotuloStatusForm.setText("erro: senha obrigatoria para novo funcionario.");
                return;
            }
            // sem hash, usa senha texto simples
            Empregado novoEmpregado = new Empregado(nome, email, senha, cargo);
            novoEmpregado.setStatus(status);

            Empregado empregadoSalvo = empregadoDAO.adicionarEmpregado(novoEmpregado);
            if (empregadoSalvo != null) {
                rotuloStatusForm.setText("funcionario adicionado com sucesso (id: " + empregadoSalvo.getIdEmpregado() + ").");
                carregarEmpregados();
                limparFormulario();
                desabilitarFormulario(true);
            } else {
                rotuloStatusForm.setText("erro ao adicionar funcionario (verifique se o email ja existe).");
            }

        } else { // atualiza existente
            empregadoSelecionado.setNome(nome);
            empregadoSelecionado.setEmail(email);
            empregadoSelecionado.setCargo(cargo);
            empregadoSelecionado.setStatus(status);
            boolean senhaAlterada = false;
            if (!senha.isEmpty()) {
                // sem hash, direto o texto
                empregadoSelecionado.setSenha(senha);
                senhaAlterada = true;
            }
            boolean sucesso = empregadoDAO.atualizarEmpregado(empregadoSelecionado);
            if (sucesso) {
                rotuloStatusForm.setText("funcionario atualizado com sucesso." + (senhaAlterada ? " (senha alterada)" : ""));
                carregarEmpregados();
                limparFormulario();
                desabilitarFormulario(true);
            } else {
                rotuloStatusForm.setText("erro ao atualizar funcionario.");
            }
        }

        } catch (Exception e) {
            rotuloStatusForm.setText("erro ao salvar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void acaoLimparForm(ActionEvent evento) {
        // limpa formulario e desabilita
        limparFormulario();
        desabilitarFormulario(true);
        tabelaEmpregados.getSelectionModel().clearSelection();
    }

    @FXML
    void acaoAtivarDesativar(ActionEvent evento) {
        // ativa ou desativa funcionario apos confirmacao
        if (empregadoSelecionado == null) {
            rotuloStatusForm.setText("selecione um funcionario na tabela.");
            return;
        }

        String statusAtual = empregadoSelecionado.getStatus();
        String novoStatus = "Ativo".equalsIgnoreCase(statusAtual) ? "Inativo" : "Ativo";

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar alteracao de status");
        alerta.setHeaderText(null);
        alerta.setContentText("Tem certeza que deseja " + (novoStatus.equals("Ativo") ? "ativar" : "desativar") + " este funcionario?");

        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            empregadoSelecionado.setStatus(novoStatus);
            boolean sucesso = empregadoDAO.atualizarEmpregado(empregadoSelecionado);
            if (sucesso) {
                rotuloStatusForm.setText("status alterado para " + novoStatus + " com sucesso.");
                carregarEmpregados();
                limparFormulario();
                desabilitarFormulario(true);
            } else {
                rotuloStatusForm.setText("erro ao alterar status.");
            }
        }
    }
//oi
    private void limparFormulario() {
        // limpa todos os campos do formulario
        campoIdEmpregado.clear();
        campoNome.clear();
        campoEmailForm.clear();
        campoSenha.clear();
        campoSenha.setPromptText("");
        comboBoxCargo.getSelectionModel().clearSelection();
        comboBoxStatus.getSelectionModel().clearSelection();
        rotuloStatusForm.setText("");
        botaoAtivarDesativar.setText("Ativar/Desativar");
    }

    private void desabilitarFormulario(boolean desabilitar) {
        // habilita ou desabilita todos os campos e botoes do formulario (menos botao adicionar e buscar)
        campoNome.setDisable(desabilitar);
        campoEmailForm.setDisable(desabilitar);
        campoSenha.setDisable(desabilitar);
        comboBoxCargo.setDisable(desabilitar);
        comboBoxStatus.setDisable(desabilitar);
        botaoSalvar.setDisable(desabilitar);
        botaoLimparForm.setDisable(desabilitar);
        botaoAtivarDesativar.setDisable(desabilitar);
    }
}
