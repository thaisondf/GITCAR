package com.gitcar.app.controladores;

import com.gitcar.app.models.InteresseCliente; 
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class GerenciarInteressesControlador { 

    //<editor-fold desc="Campos FXML">
    @FXML private TextField campoBuscaInteresse; 
    @FXML private Button botaoBuscarInteresse; 
    @FXML private TableView<InteresseCliente> tabelaInteresses; 
    @FXML private TableColumn<InteresseCliente, Integer> colIdInteresse; 
    @FXML private TableColumn<InteresseCliente, String> colNomeCliente; 
    @FXML private TableColumn<InteresseCliente, String> colInfoContato; 
    @FXML private TableColumn<InteresseCliente, String> colVeiculoInteresse; 
    @FXML private TableColumn<InteresseCliente, String> colObservacoes; 
    @FXML private TextField campoIdInteresse; 
    @FXML private TextField campoNomeClienteForm; 
    @FXML private TextField campoInfoContatoForm; 
    @FXML private TextField campoVeiculoInteresseForm; 
    @FXML private TextArea areaObservacoes; 
    @FXML private Button botaoAdicionarInteresse; 
    @FXML private Button botaoSalvarInteresse; 
    @FXML private Button botaoLimparFormInteresse; 
    @FXML private Button botaoExcluirInteresse; 
    @FXML private Label rotuloStatusForm; 
    @FXML private Label rotuloStatusGeral; 
    

  
    private ObservableList<InteresseCliente> dadosInteresses = FXCollections.observableArrayList();
    
    private FilteredList<InteresseCliente> interessesFiltrados;
    
    private InteresseCliente interesseSelecionado = null;
  
    private static final AtomicInteger contadorId = new AtomicInteger(0);

    @FXML
    public void initialize() {
        // configuracoes iniciais da tela apos carregamento do fxml
        rotuloStatusGeral.setText("Dados gerenciados em memoria (temporario).");
        rotuloStatusForm.setText("");
        configurarColunasTabela(); 
        carregarInteressesIniciaisPlaceholder(); 

        // cria um filtro inicial sem restricoes para envolver a lista observavel
        interessesFiltrados = new FilteredList<>(dadosInteresses, p -> true);

        // associa a lista filtrada a tabela para exibicao
        tabelaInteresses.setItems(interessesFiltrados);

       
        tabelaInteresses.getSelectionModel().selectedItemProperty().addListener(
                (observavel, valorAntigo, valorNovo) -> preencherFormulario(valorNovo));

        // limpa e desabilita formulario inicialmente
        limparFormulario();
        desabilitarFormulario(true);
    }

    private void configurarColunasTabela() {
        // associa os atributos do objeto InteresseCliente as colunas da tabela pelo nome dos atributos
        colIdInteresse.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNomeCliente.setCellValueFactory(new PropertyValueFactory<>("nomeCliente"));
        colInfoContato.setCellValueFactory(new PropertyValueFactory<>("infoContato"));
        colVeiculoInteresse.setCellValueFactory(new PropertyValueFactory<>("interesseVeiculo"));
        colObservacoes.setCellValueFactory(new PropertyValueFactory<>("observacoes"));
    }

    @FXML
    void acaoBuscarInteresse(ActionEvent evento) {
        // captura o texto digitado para filtro e converte para minusculas 
        String termoBusca = campoBuscaInteresse.getText().toLowerCase();

        // aplica o filtro para atualizar os itens exibidos
        interessesFiltrados.setPredicate(interesse -> {
            if (termoBusca == null || termoBusca.isEmpty()) {
                return true; // sem filtro, exibe todos
            }
            // verifica se o nome do cliente ou o veiculo de interesse contem o termo digitado
            return interesse.getNomeCliente().toLowerCase().contains(termoBusca) ||
                   interesse.getInteresseVeiculo().toLowerCase().contains(termoBusca);
        });
       //informa quantos interesses estao sendo exibidos
        rotuloStatusGeral.setText("Exibindo " + interessesFiltrados.size() + " interesses.");
    }

    private void preencherFormulario(InteresseCliente interesse) {
        // preenche os campos do formulario com os dados do interesse selecionado
        interesseSelecionado = interesse;
        if (interesse != null) {
            // habilita os campos para edicao
            desabilitarFormulario(false);
            campoIdInteresse.setText(String.valueOf(interesse.getId()));
            campoNomeClienteForm.setText(interesse.getNomeCliente());
            campoInfoContatoForm.setText(interesse.getInfoContato());
            campoVeiculoInteresseForm.setText(interesse.getInteresseVeiculo());
            areaObservacoes.setText(interesse.getObservacoes());
            rotuloStatusForm.setText("");
            botaoSalvarInteresse.setText("Salvar Alteracoes");
        } else {
            limparFormulario();
            desabilitarFormulario(true);
        }
    }
    @FXML
    void acaoAdicionarInteresse(ActionEvent evento) {
        limparFormulario();
        desabilitarFormulario(false);
        interesseSelecionado = null; // indica que e um novo interesse
        campoIdInteresse.setText("(Novo)");
        rotuloStatusForm.setText("Preencha os dados para adicionar novo interesse.");
        botaoSalvarInteresse.setText("Adicionar");
        campoNomeClienteForm.requestFocus(); // focado para facilitar digitacao
    }

    @FXML
    void acaoSalvarInteresse(ActionEvent evento) {
        // salvaou atualiza um existente 
        rotuloStatusForm.setText("");
        String nomeCliente = campoNomeClienteForm.getText().trim();
        String infoContato = campoInfoContatoForm.getText().trim();
        String veiculoInteresse = campoVeiculoInteresseForm.getText().trim();
        String observacoes = areaObservacoes.getText().trim();

        // valida campos obrigatorios
        if (nomeCliente.isEmpty() || veiculoInteresse.isEmpty()) {
            rotuloStatusForm.setText("Erro: Nome do Cliente e Veiculo de Interesse sao obrigatorios.");
            return;
        }

        if (interesseSelecionado == null) { // adiciona novo interesse
            int novoId = contadorId.incrementAndGet();
            InteresseCliente novoInteresse = new InteresseCliente(novoId, nomeCliente, infoContato, veiculoInteresse, observacoes);
            dadosInteresses.add(novoInteresse);
            rotuloStatusForm.setText("Interesse adicionado com sucesso (ID: " + novoId + ").");
            System.out.println("Adicionando Interesse (em memoria): " + nomeCliente);

        } else { // atualiza interesse 
            interesseSelecionado.setNomeCliente(nomeCliente);
            interesseSelecionado.setInfoContato(infoContato);
            interesseSelecionado.setInteresseVeiculo(veiculoInteresse);
            interesseSelecionado.setObservacoes(observacoes);
            rotuloStatusForm.setText("Interesse atualizado com sucesso.");
            System.out.println("Atualizando Interesse (em memoria) ID: " + interesseSelecionado.getId());
            tabelaInteresses.refresh(); // atualiza exibicao da tabela
        }
       
        limparFormulario();
        desabilitarFormulario(true);
    }

    @FXML
    void acaoLimparFormInteresse(ActionEvent evento) {
     
        limparFormulario();
        desabilitarFormulario(true);
        tabelaInteresses.getSelectionModel().clearSelection();
    }

    @FXML
    void acaoExcluirInteresse(ActionEvent evento) {
      
        if (interesseSelecionado == null) {
            rotuloStatusForm.setText("Selecione um interesse na tabela para excluir.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusao");
        confirmacao.setHeaderText("Excluir interesse de " + interesseSelecionado.getNomeCliente() + "?");
        confirmacao.setContentText("Esta acao nao pode ser desfeita (dados em memoria).");

        confirmacao.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.OK) {
                dadosInteresses.remove(interesseSelecionado);
                rotuloStatusForm.setText("Interesse excluido.");
                System.out.println("Excluindo Interesse (em memoria) ID: " + interesseSelecionado.getId());
                limparFormulario();
                desabilitarFormulario(true);
            }
        });
    }

    private void limparFormulario() {
        
        interesseSelecionado = null;
        campoIdInteresse.clear();
        campoNomeClienteForm.clear();
        campoInfoContatoForm.clear();
        campoVeiculoInteresseForm.clear();
        areaObservacoes.clear();
        rotuloStatusForm.setText("");
        botaoSalvarInteresse.setText("Salvar");
    }

    private void desabilitarFormulario(boolean desabilitar) {
       
        campoNomeClienteForm.setDisable(desabilitar);
        campoInfoContatoForm.setDisable(desabilitar);
        campoVeiculoInteresseForm.setDisable(desabilitar);
        areaObservacoes.setDisable(desabilitar);
        botaoSalvarInteresse.setDisable(desabilitar);
        botaoLimparFormInteresse.setDisable(desabilitar);
        // botao excluir so habilita se formulario habilitado e interesse selecionado nao for nulo
        botaoExcluirInteresse.setDisable(desabilitar || interesseSelecionado == null);
    }

  

    private void carregarInteressesIniciaisPlaceholder() {
        
        dadosInteresses.clear();
        dadosInteresses.addAll(
                new InteresseCliente(contadorId.incrementAndGet(), "Ana Beatriz", "ana.b@email.com", "SUV Compacto", "Prefere cor branca ou prata"),
                new InteresseCliente(contadorId.incrementAndGet(), "Marcos Paulo", "11 98888-7777", "Pickup Media", "Necessita para trabalho, diesel")
        );
        rotuloStatusGeral.setText("Carregado " + dadosInteresses.size() + " interesses (placeholder).");
    }
    
}
