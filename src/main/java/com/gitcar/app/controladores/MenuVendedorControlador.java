package com.gitcar.app.controladores;

import com.gitcar.app.AplicacaoPrincipal;
import com.gitcar.app.models.Empregado;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;

public class MenuVendedorControlador {

    @FXML private BorderPane painelPrincipal;
    @FXML private MenuItem itemMenuSair;
    @FXML private MenuItem itemMenuInfo;
    @FXML private Button botaoVenderVeiculo;
    @FXML private Button botaoHistoricoVendas;
    @FXML private Button botaoAgendarTestDrive;
    @FXML private Pane areaConteudo;
    @FXML private Label rotuloBarraStatus;

    @FXML
    public void initialize() {
        rotuloBarraStatus.setText("Usuário: " + Empregado.logado.getNome());
    }

    @FXML
    void acaoVenderVeiculo(ActionEvent evento) {
        carregarView("/com/gitcar/view/VenderVeiculoView.fxml");
    }

    @FXML
    void acaoHistoricoVendas(ActionEvent evento) {
        carregarView("/com/gitcar/view/HistoricoVendasView.fxml");
    }

    @FXML
    void acaoAgendarTestDrive(ActionEvent evento) {
        carregarView("/com/gitcar/view/AgendarTestDriveView.fxml");
    }

    @FXML
    void acaoSair(ActionEvent evento) {
        try {
            AplicacaoPrincipal.mostrarTelaLogin();
        } catch (IOException e) {
            mostrarErro("Erro ao fazer logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void acaoSobre(ActionEvent evento) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Sobre GIT CAR");
        alerta.setHeaderText("GIT CAR - Sistema para gerenciamento de concessionária");
        alerta.setContentText("Versão 1.0\nDesenvolvido como projeto acadêmico.\n\nDesenvolvedores: \nEduarda Silva Santos\nGuilherme Fior\nIgor Nogueira Ferreira\nThaison Damaceno Freitas");
        alerta.showAndWait();
    }

    private void carregarView(String caminhoFxml) {
        try {
            URL urlFxml = getClass().getResource(caminhoFxml);
            if (urlFxml == null) {
                mostrarErro("Não foi possível encontrar o arquivo FXML: " + caminhoFxml);
                return;
            }
            FXMLLoader carregador = new FXMLLoader(urlFxml);
            Parent view = carregador.load();

            if (caminhoFxml.equals("/com/gitcar/view/HistoricoVendasView.fxml")) {
                HistoricoVendasControlador controlador = carregador.getController();
                controlador.setIdVendedorLogado(Empregado.logado.getIdEmpregado());
                // O carregamento do histórico é feito dentro do setIdVendedorLogado()
            }

            areaConteudo.getChildren().setAll(view);
        } catch (IOException e) {
            mostrarErro("Erro ao carregar a tela: " + caminhoFxml + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarErro(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Erro");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
