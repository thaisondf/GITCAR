package com.gitcar.app.controladores;

import com.gitcar.app.dao.VeiculoDAO;
import com.gitcar.app.models.Veiculo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import java.io.IOException;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Optional;

public class GerenciarVeiculosControlador {

    @FXML private TextField txtBusca;
    @FXML private TableView<Veiculo> tabelaVeiculos;
    @FXML private TableColumn<Veiculo, Integer> colId;
    @FXML private TableColumn<Veiculo, String> colModelo;
    @FXML private TableColumn<Veiculo, String> colMarca;
    @FXML private TableColumn<Veiculo, Integer> colAno;
    @FXML private TableColumn<Veiculo, String> colPlaca;
    @FXML private TableColumn<Veiculo, String> colStatus;

    private VeiculoDAO veiculoDAO;
    private ObservableList<Veiculo> listaVeiculos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        veiculoDAO = new VeiculoDAO();
        configurarColunasTabela();
        carregarVeiculos();
        tabelaVeiculos.setItems(listaVeiculos);
    }

    private void configurarColunasTabela() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colAno.setCellValueFactory(new PropertyValueFactory<>("ano"));
        colPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void carregarVeiculos() {
        listaVeiculos.clear();
        List<Veiculo> veiculos = veiculoDAO.buscarTodos();
        listaVeiculos.addAll(veiculos);
    }

    @FXML
    void acaoBuscar(ActionEvent evento) {
        String termo = txtBusca.getText().trim();
        listaVeiculos.clear();
        if (termo.isEmpty()) {
            carregarVeiculos();
        } else {
            // Reutilizando o método de busca por termo, mas para todos os veículos (não apenas disponíveis)
            // Como o VeiculoDAO não tem um método para buscar todos por termo, vamos usar o buscarDisponiveisPorTermo
            // e adaptar, ou criar um novo. Por enquanto, vou usar o buscarDisponiveisPorTermo e assumir que o status
            // 'Disponível' é o que o usuário quer ver, ou adaptar o DAO.
            // Vou adaptar o DAO para ter um método de busca geral.
            // Por enquanto, vou carregar todos e filtrar na memória (não ideal, mas rápido para o contexto).
            // Melhor: vou criar o método no DAO.
            // Para não quebrar o fluxo, vou criar o método no DAO na próxima etapa.
            // Por agora, vou apenas carregar todos e filtrar na memória.
            // O método buscarDisponiveisPorTermo já faz uma busca por termo, mas filtra por status.
            // Vou usar o buscarTodos e filtrar na memória por enquanto.
            List<Veiculo> todosVeiculos = veiculoDAO.buscarTodos();
            for (Veiculo v : todosVeiculos) {
                if (String.valueOf(v.getId()).contains(termo) ||
                    v.getModelo().toLowerCase().contains(termo.toLowerCase()) ||
                    v.getMarca().toLowerCase().contains(termo.toLowerCase()) ||
                    v.getPlaca().toLowerCase().contains(termo.toLowerCase())) {
                    listaVeiculos.add(v);
                }
            }
        }
    }

    @FXML
    void acaoEditar(ActionEvent evento) {
        Veiculo veiculoSelecionado = tabelaVeiculos.getSelectionModel().getSelectedItem();
        if (veiculoSelecionado != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gitcar/view/EditarVeiculoView.fxml"));
                Parent root = loader.load();

                EditarVeiculoControlador controlador = loader.getController();
                controlador.setVeiculo(veiculoSelecionado);

                Stage stage = new Stage();
                stage.setTitle("Editar Veículo");
                stage.setScene(new Scene(root));
                stage.showAndWait();

                // Recarregar a lista após o fechamento da janela de edição
                carregarVeiculos();

            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao Abrir Edição", "Não foi possível abrir a tela de edição: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Nenhuma Seleção", "Nenhum Veículo Selecionado", "Por favor, selecione um veículo na tabela para editar.");
        }
    }

    @FXML
    void acaoExcluir(ActionEvent evento) {
        Veiculo veiculoSelecionado = tabelaVeiculos.getSelectionModel().getSelectedItem();
        if (veiculoSelecionado != null) {
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmação de Exclusão");
            confirmacao.setHeaderText("Excluir Veículo: " + veiculoSelecionado.getModelo());
            confirmacao.setContentText("Tem certeza que deseja excluir o veículo selecionado? Esta ação é irreversível.");

            Optional<ButtonType> resultado = confirmacao.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                if (veiculoDAO.excluirVeiculo(veiculoSelecionado.getId())) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Veículo Excluído", "O veículo foi excluído com sucesso.");
                    carregarVeiculos(); // Recarrega a lista
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao Excluir", "Não foi possível excluir o veículo. Verifique se ele está associado a alguma venda.");
                }
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Nenhuma Seleção", "Nenhum Veículo Selecionado", "Por favor, selecione um veículo na tabela para excluir.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String cabecalho, String conteudo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecalho);
        alerta.setContentText(conteudo);
        alerta.showAndWait();
    }
}
