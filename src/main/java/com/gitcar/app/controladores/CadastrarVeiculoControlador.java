package com.gitcar.app.controladores;

import com.gitcar.app.dao.VeiculoDAO;
import com.gitcar.app.models.Veiculo;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

public class CadastrarVeiculoControlador {

    @FXML
    private TextField modeloField;

    @FXML
    private TextField precoField;

    @FXML
    private TextField marcaField;

    @FXML
    private TextField anoField;

    @FXML
    private ComboBox<String> categoriaComboBox;

    @FXML
    private ComboBox<String> combustivelComboBox;

    @FXML
    private TextField corField;

    @FXML
    private TextField kmField;

    @FXML
    private TextField placaField;

    @FXML
    private ComboBox<String> cambioComboBox;

    @FXML
    private Button cadastrarButton;

    private VeiculoDAO veiculoDAO = new VeiculoDAO(); // Instância do DAO

    @FXML
    private void initialize() {
        categoriaComboBox.getItems().addAll("Sedan", "SUV", "Hatch", "Pickup");
        combustivelComboBox.getItems().addAll("Gasolina", "Álcool", "Diesel", "Elétrico", "Híbrido");
        cambioComboBox.getItems().addAll("Manual", "Automático", "CVT");
    }

    @FXML
    private void cadastrarVeiculo(ActionEvent event) {
        String modelo = modeloField.getText().trim();
        String precoStr = precoField.getText().trim();
        String marca = marcaField.getText().trim();
        String anoStr = anoField.getText().trim();
        String categoria = categoriaComboBox.getValue();
        String combustivel = combustivelComboBox.getValue();
        String cor = corField.getText().trim();
        String kmStr = kmField.getText().trim();
        String placa = placaField.getText().trim();
        String cambio = cambioComboBox.getValue();

        if (modelo.isEmpty() || precoStr.isEmpty() || marca.isEmpty() || anoStr.isEmpty() ||
            categoria == null || combustivel == null || cor.isEmpty() || kmStr.isEmpty() ||
            placa.isEmpty() || cambio == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de validação", "Por favor, preencha todos os campos.");
            return;
        }

        double preco;
        int ano;
        int km;

        try {
            preco = Double.parseDouble(precoStr);
            ano = Integer.parseInt(anoStr);
            km = Integer.parseInt(kmStr);
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de formato", "Preço, Ano e KM devem ser números válidos.");
            return;
        }

        // Criar objeto Veiculo com status "Disponível"
        Veiculo veiculo = new Veiculo(
            modelo, marca, ano, km, categoria, preco,
            cor, combustivel, placa, cambio, "Disponível"
        );

        try {
            veiculoDAO.adicionarVeiculo(veiculo); // Salva no banco
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Veículo cadastrado com sucesso!");
            limparCampos();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao cadastrar", "Falha ao cadastrar o veículo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private void limparCampos() {
        modeloField.clear();
        precoField.clear();
        marcaField.clear();
        anoField.clear();
        categoriaComboBox.getSelectionModel().clearSelection();
        combustivelComboBox.getSelectionModel().clearSelection();
        corField.clear();
        kmField.clear();
        placaField.clear();
        cambioComboBox.getSelectionModel().clearSelection();
    }
}
