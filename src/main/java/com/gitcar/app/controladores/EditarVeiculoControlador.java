package com.gitcar.app.controladores;

import com.gitcar.app.dao.VeiculoDAO;
import com.gitcar.app.models.Veiculo;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class EditarVeiculoControlador {

    @FXML private Label idLabel;
    @FXML private TextField modeloField;
    @FXML private TextField precoField;
    @FXML private TextField marcaField;
    @FXML private TextField anoField;
    @FXML private ComboBox<String> categoriaComboBox;
    @FXML private ComboBox<String> combustivelComboBox;
    @FXML private TextField corField;
    @FXML private TextField kmField;
    @FXML private TextField placaField;
    @FXML private ComboBox<String> cambioComboBox;
    @FXML private ComboBox<String> statusComboBox;

    private VeiculoDAO veiculoDAO;
    private Veiculo veiculoParaEdicao;

    @FXML
    public void initialize() {
        veiculoDAO = new VeiculoDAO();
        // Inicializar ComboBoxes com valores
        categoriaComboBox.setItems(FXCollections.observableArrayList("Sedan", "Hatch", "SUV", "Picape", "Esportivo"));
        combustivelComboBox.setItems(FXCollections.observableArrayList("Gasolina", "Etanol", "Flex", "Diesel", "Elétrico"));
        cambioComboBox.setItems(FXCollections.observableArrayList("Manual", "Automático"));
        statusComboBox.setItems(FXCollections.observableArrayList("Disponível", "Vendido", "Reservado", "Manutenção"));
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculoParaEdicao = veiculo;
        preencherCampos();
    }

    private void preencherCampos() {
        if (veiculoParaEdicao != null) {
            idLabel.setText(String.valueOf(veiculoParaEdicao.getId()));
            modeloField.setText(veiculoParaEdicao.getModelo());
            precoField.setText(String.valueOf(veiculoParaEdicao.getPreco()));
            marcaField.setText(veiculoParaEdicao.getMarca());
            anoField.setText(String.valueOf(veiculoParaEdicao.getAno()));
            categoriaComboBox.getSelectionModel().select(veiculoParaEdicao.getCategoria());
            combustivelComboBox.getSelectionModel().select(veiculoParaEdicao.getCombustivel());
            corField.setText(veiculoParaEdicao.getCor());
            kmField.setText(String.valueOf(veiculoParaEdicao.getKm()));
            placaField.setText(veiculoParaEdicao.getPlaca());
            cambioComboBox.getSelectionModel().select(veiculoParaEdicao.getCambio());
            statusComboBox.getSelectionModel().select(veiculoParaEdicao.getStatus());
        }
    }

    @FXML
    void salvarEdicao(ActionEvent evento) {
        if (veiculoParaEdicao == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro de Edição", "Nenhum veículo selecionado para edição.");
            return;
        }

        try {
            // Validação básica (pode ser expandida)
            if (modeloField.getText().isEmpty() || precoField.getText().isEmpty() || marcaField.getText().isEmpty() ||
                anoField.getText().isEmpty() || categoriaComboBox.getValue() == null || combustivelComboBox.getValue() == null ||
                corField.getText().isEmpty() || kmField.getText().isEmpty() || placaField.getText().isEmpty() ||
                cambioComboBox.getValue() == null || statusComboBox.getValue() == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campos Vazios", "Preencha todos os campos", "Todos os campos são obrigatórios.");
                return;
            }

            // Atualizar o objeto Veiculo
            veiculoParaEdicao.setModelo(modeloField.getText());
            veiculoParaEdicao.setPreco(Double.parseDouble(precoField.getText()));
            veiculoParaEdicao.setMarca(marcaField.getText());
            veiculoParaEdicao.setAno(Integer.parseInt(anoField.getText()));
            veiculoParaEdicao.setCategoria(categoriaComboBox.getValue());
            veiculoParaEdicao.setCombustivel(combustivelComboBox.getValue());
            veiculoParaEdicao.setCor(corField.getText());
            veiculoParaEdicao.setKm(Integer.parseInt(kmField.getText()));
            veiculoParaEdicao.setPlaca(placaField.getText());
            veiculoParaEdicao.setCambio(cambioComboBox.getValue());
            veiculoParaEdicao.setStatus(statusComboBox.getValue());

            if (veiculoDAO.atualizarVeiculo(veiculoParaEdicao)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Edição Salva", "O veículo foi atualizado com sucesso.");
                // Fechar a janela de edição ou recarregar a tela de gerenciamento
                // Como não temos acesso direto ao Stage, vamos apenas mostrar o alerta.
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao Salvar", "Não foi possível atualizar o veículo no banco de dados.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Formato", "Formato Inválido", "Preço, Ano e KM devem ser números válidos.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro Inesperado", "Ocorreu um erro ao salvar a edição: " + e.getMessage());
            e.printStackTrace();
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
