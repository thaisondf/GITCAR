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

public class MenuGerenteControlador {

    @FXML private BorderPane painelPrincipal;
    @FXML private MenuItem itemMenuSair;
    @FXML private MenuItem itemMenuInfo;
    @FXML private Button botaoRelatorioVendas;
    @FXML private Button botaoGerenciarEmpregados;
    @FXML private Button botaoCadastrarVeiculo;
    @FXML private Button botaoGerenciarVeiculos;
    @FXML private Button botaoGerenciarInteresses;
    @FXML private Pane areaConteudo;
    @FXML private Label rotuloBarraStatus;

    @FXML
    public void initialize() {
        rotuloBarraStatus.setText("Usuário: " + Empregado.logado.getNome());
    }

    @FXML
    void acaoRelatorioVendas(ActionEvent evento) {
        carregarView("/com/gitcar/view/RelatorioVendasView.fxml");
    }

    @FXML
    void acaoGerenciarEmpregados(ActionEvent evento) {
        carregarView("/com/gitcar/view/GerenciarEmpregadosView.fxml");
    }

    @FXML
    void acaoCadastrarVeiculo(ActionEvent evento) {
        carregarView("/com/gitcar/view/CadastrarVeiculoView.fxml");
    }

    @FXML
    void acaoGerenciarVeiculos(ActionEvent evento) {
        carregarView("/com/gitcar/view/GerenciarVeiculosView.fxml");
    }

    @FXML
    void acaoGerenciarInteresses(ActionEvent evento) {
        carregarView("/com/gitcar/view/GerenciarInteressesView.fxml");
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
                mostrarErro("Arquivo FXML não encontrado: " + caminhoFxml);
                return;
            }
            FXMLLoader carregador = new FXMLLoader(urlFxml);
            Parent view = carregador.load();

            if (caminhoFxml.equals("/com/gitcar/view/RelatorioVendasView.fxml")) {
                RelatorioVendasControlador controlador = carregador.getController();
                // O gerente deve ver todas as vendas, mas se o controlador tiver a mesma lógica do vendedor,
                // pode ser necessário adaptar. Vou assumir que o RelatorioVendasControlador é para o gerente
                // e não precisa do ID do vendedor, ou que o ID do gerente é usado para filtrar.
                // Como o problema é no HistoricoVendasView, vou focar no MenuVendedorControlador.
                // O Gerente não tem um botão para HistoricoVendasView, apenas RelatorioVendasView.
                // Se o usuário logado for um Gerente, o problema não deveria ocorrer, a menos que ele
                // esteja acessando a tela de vendedor.
            }

            areaConteudo.getChildren().setAll(view);
        } catch (IOException e) {
            mostrarErro("Erro ao carregar: " + caminhoFxml + "\n" + e.getMessage());
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
