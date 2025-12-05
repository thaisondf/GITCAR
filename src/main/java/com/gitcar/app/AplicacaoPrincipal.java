package com.gitcar.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.awt.Frame;

import java.io.IOException;
import java.net.URL;
import javafx.scene.image.Image;
import javax.swing.JFrame;

public class AplicacaoPrincipal extends Application {

    private static Stage palcoPrincipal;
    private static final double LARGURA_PADRAO = 1480;
    private static final double ALTURA_PADRAO = 900;

    @Override
    public void start(Stage palco) throws IOException {
        palcoPrincipal = palco;
        palcoPrincipal.setTitle("GIT CAR");

        // ✅ Ícone da aplicação (barra de tarefas e janela)
        palcoPrincipal.getIcons().add(
            new Image(getClass().getResourceAsStream("/com/gitcar/images/car.png"))
        );

        mostrarTelaLogin();
        palcoPrincipal.show();
    }

    public static void mostrarTelaLogin() throws IOException {
        carregarCena("/com/gitcar/view/LoginView.fxml");
    }

    public static void mostrarMenuVendedor() throws IOException {
        carregarCena("/com/gitcar/view/MenuVendedorView.fxml");
        palcoPrincipal.setWidth(LARGURA_PADRAO);
        palcoPrincipal.setHeight(ALTURA_PADRAO);
        palcoPrincipal.centerOnScreen();
    }

    public static void mostrarMenuGerente() throws IOException {
        carregarCena("/com/gitcar/view/MenuGerenteView.fxml");
        palcoPrincipal.setWidth(LARGURA_PADRAO);
        palcoPrincipal.setHeight(ALTURA_PADRAO);
        palcoPrincipal.centerOnScreen();
    }

    private static void carregarCena(String caminhoFxml) throws IOException {
        URL urlFxml = AplicacaoPrincipal.class.getResource(caminhoFxml);
        if (urlFxml == null) {
            System.err.println("Não foi possível carregar o arquivo FXML: " + caminhoFxml);
            throw new IOException("Não foi possível encontrar o recurso FXML: " + caminhoFxml);
        }
        FXMLLoader carregador = new FXMLLoader(urlFxml);
        Parent raiz = carregador.load();

        Scene cena = palcoPrincipal.getScene();
        if (cena == null) {
            cena = new Scene(raiz);
            palcoPrincipal.setScene(cena);
        } else {
            palcoPrincipal.getScene().setRoot(raiz);
        }

        if (caminhoFxml.endsWith("LoginView.fxml")) {
            palcoPrincipal.sizeToScene();
            palcoPrincipal.centerOnScreen();
        }
    }

    public static Stage getPalcoPrincipal() {
        return palcoPrincipal;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
