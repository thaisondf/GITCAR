package com.gitcar.app.controladores;

import com.gitcar.app.dao.ClienteDAO;
import com.gitcar.app.models.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdicionarClienteControlador {

    @FXML
    private TextField textFieldNome;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private TextField textFieldTelefone;

    @FXML
    private Button buttonAdicionar;

    @FXML
    private Button buttonEditar;

    @FXML
    private Button buttonApagar;

    @FXML
    private TableView<Cliente> tableViewClientes;

    @FXML
    private TableColumn<Cliente, String> tableColumnNome;

    @FXML
    private TableColumn<Cliente, String> tableColumnEmail;

    @FXML
    private TableColumn<Cliente, String> tableColumnTelefone;

    private ClienteDAO clienteDAO;
    private ObservableList<Cliente> clientes;

    @FXML
    public void initialize() {
        clienteDAO = new ClienteDAO();
        clientes = FXCollections.observableArrayList();

        tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));

        tableViewClientes.setItems(clientes);
        carregarClientes();

        buttonAdicionar.setOnAction(e -> adicionarCliente());
        buttonEditar.setOnAction(e -> editarCliente());
        buttonApagar.setOnAction(e -> apagarCliente());

        // Preenche automaticamente os campos ao selecionar um cliente
        tableViewClientes.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            if (novo != null) {
                preencherCampos(novo);
            }
        });
    }

    private void carregarClientes() {
        clientes.setAll(clienteDAO.buscarTodos());
    }

    private void adicionarCliente() {
        String nome = textFieldNome.getText().trim();
        String email = textFieldEmail.getText().trim();
        String telefone = textFieldTelefone.getText().trim();

        if (nome.isEmpty() || email.isEmpty() || telefone.isEmpty()) {
            mostrarAlerta("Preencha todos os campos.");
            return;
        }

        Cliente novo = new Cliente(0, nome, email, telefone);
        clienteDAO.adicionarCliente(novo);
        carregarClientes();
        limparCampos();
    }

    private void editarCliente() {
        Cliente selecionado = tableViewClientes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Selecione um cliente para editar.");
            return;
        }

        String nome = textFieldNome.getText().trim();
        String email = textFieldEmail.getText().trim();
        String telefone = textFieldTelefone.getText().trim();

        if (nome.isEmpty() || email.isEmpty() || telefone.isEmpty()) {
            mostrarAlerta("Preencha todos os campos.");
            return;
        }

        selecionado.setNome(nome);
        selecionado.setEmail(email);
        selecionado.setTelefone(telefone);

        clienteDAO.atualizarCliente(selecionado);
        carregarClientes();
        limparCampos();
    }

    private void apagarCliente() {
        Cliente selecionado = tableViewClientes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Selecione um cliente para apagar.");
            return;
        }

        clienteDAO.excluirCliente(selecionado.getIdCliente());
        carregarClientes();
        limparCampos();
    }

    private void preencherCampos(Cliente cliente) {
        textFieldNome.setText(cliente.getNome());
        textFieldEmail.setText(cliente.getEmail());
        textFieldTelefone.setText(cliente.getTelefone());
    }

    private void limparCampos() {
        textFieldNome.clear();
        textFieldEmail.clear();
        textFieldTelefone.clear();
    }

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}