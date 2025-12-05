package com.gitcar.app.controladores;

import com.gitcar.app.AplicacaoPrincipal;
import com.gitcar.app.dao.EmpregadoDAO;
import com.gitcar.app.models.Empregado;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class LoginControlador {

    @FXML private TextField campoEmail;
    @FXML private PasswordField campoSenha;
    @FXML private TextField campoSenhaVisivel;
    @FXML private Button caixaMostrarSenha;
    @FXML private ImageView openeye;
    @FXML private ImageView closeeye;
    @FXML private Button botaoEntrar;
    @FXML private Label rotuloStatus;

    private final EmpregadoDAO empregadoDAO = new EmpregadoDAO();

    // Controle do estado da senha visível 
    private boolean senhaVisivel = false;

    @FXML
    public void initialize() {
        // Senha começa oculta
        campoSenhaVisivel.setVisible(false);
        campoSenhaVisivel.setManaged(false);

        campoSenha.setVisible(true);
        campoSenha.setManaged(true);

        // Ícones openeye oculto, closeeye visível (senha oculta)
        openeye.setVisible(false);
        closeeye.setVisible(true);

        // Sincroniza o texto dos campos para manter os valores iguais
        campoSenhaVisivel.textProperty().bindBidirectional(campoSenha.textProperty());

        rotuloStatus.setText("");
    }

    @FXML
    void acaoBotaoEntrar(ActionEvent evento) {
        String email = campoEmail.getText().trim();
        String senha = senhaVisivel ? campoSenhaVisivel.getText() : campoSenha.getText();

        rotuloStatus.setText("");

        if (email.isEmpty() || senha.isEmpty()) {
            rotuloStatus.setText("Email e senha são obrigatórios.");
            return;
        }

        Empregado empregado = empregadoDAO.autenticar(email, senha);

        if (empregado != null) {
            if (!"ativo".equalsIgnoreCase(empregado.getStatus())) {
                rotuloStatus.setText("Usuário inativo.");
                return;
            }

            // Define o empregado logado 
            Empregado.logado = empregado;

            try {
                switch (empregado.getCargo().toLowerCase()) {
                    case "gerente":
                        AplicacaoPrincipal.mostrarMenuGerente();
                        break;
                    case "vendedor":
                        AplicacaoPrincipal.mostrarMenuVendedor();
                        break;
                    default:
                        rotuloStatus.setText("Cargo de usuário desconhecido: " + empregado.getCargo());
                        break;
                }
            } catch (IOException e) {
                rotuloStatus.setText("Erro ao carregar o menu.");
                e.printStackTrace();
            }
        } else {
            rotuloStatus.setText("Email ou senha inválidos.");
        }
    }

    @FXML
    void alternarMostrarSenha(ActionEvent evento) {
        senhaVisivel = !senhaVisivel;

        campoSenhaVisivel.setVisible(senhaVisivel);
        campoSenhaVisivel.setManaged(senhaVisivel);

        campoSenha.setVisible(!senhaVisivel);
        campoSenha.setManaged(!senhaVisivel);

        openeye.setVisible(senhaVisivel);
        closeeye.setVisible(!senhaVisivel);
    }
}