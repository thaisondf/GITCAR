package com.gitcar.app.controladores;

import com.gitcar.app.dao.EmpregadoDAO;
import com.gitcar.app.dao.VendaDAO;
import com.gitcar.app.dao.VeiculoDAO;
import com.gitcar.app.models.Empregado;
import com.gitcar.app.models.Venda;
import com.gitcar.app.models.Veiculo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class RelatorioVendasControlador {

    
    @FXML private DatePicker datePickerDataInicio;
    @FXML private DatePicker datePickerDataFim;
    @FXML private ComboBox<Empregado> comboBoxFiltroVendedor;
    @FXML private ComboBox<String> comboBoxFiltroStatusVeiculo;
    @FXML private TextField campoFiltroModeloMarca;
    @FXML private Button botaoAplicarFiltros;
    @FXML private Button botaoLimparFiltros;
    @FXML private Button botaoExportarRelatorio;

    @FXML private TableView<Venda> tabelaVeiculosVendidos;
    @FXML private TableColumn<Venda, Integer> colIdVeiculoVendido;
    @FXML private TableColumn<Venda, String> colModeloVendido;
    @FXML private TableColumn<Venda, String> colMarcaVendida;
   
    @FXML private TableColumn<Venda, Double> colValorVendido;
    @FXML private TableColumn<Venda, String> colDataVenda;
    @FXML private TableColumn<Venda, String> colVendedorVenda;
    @FXML private TableColumn<Venda, String> colClienteVenda;
    @FXML private TableColumn<Venda, String> colPagamentoVenda;

    @FXML private TableView<Veiculo> tabelaVeiculosDisponiveis;
    @FXML private TableColumn<Veiculo, Integer> colIdVeiculoDisp;
    @FXML private TableColumn<Veiculo, String> colModeloDisp;
    @FXML private TableColumn<Veiculo, String> colMarcaDisp;
    @FXML private TableColumn<Veiculo, Integer> colAnoDisp;
    @FXML private TableColumn<Veiculo, Double> colPrecoDisp;
    @FXML private TableColumn<Veiculo, String> colStatusDisp;
    @FXML private TableColumn<Veiculo, Integer> colKmDisp;
    @FXML private TableColumn<Veiculo, String> colCategoriaDisp;

    @FXML private Label rotuloTotalVendido;
    @FXML private Label rotuloValorTotalVendas;
    @FXML private Label rotuloTotalVeiculosExibidos;
    @FXML private Label rotuloMelhoresVendedores;
    @FXML private Label rotuloModeloMaisVendido;
    @FXML private Label rotuloStatus;
    //</editor-fold>

    private VendaDAO vendaDAO;
    private VeiculoDAO veiculoDAO;
    private EmpregadoDAO empregadoDAO;

    private ObservableList<Venda> dadosVendas = FXCollections.observableArrayList();
    private ObservableList<Veiculo> dadosVeiculos = FXCollections.observableArrayList();
    private ObservableList<Empregado> opcoesEmpregados = FXCollections.observableArrayList();

    private final NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @FXML
    public void initialize() {
        vendaDAO = new VendaDAO();
        veiculoDAO = new VeiculoDAO();
        empregadoDAO = new EmpregadoDAO();

        rotuloStatus.setText("");
        configurarColunasTabela();
        configurarComboBoxesFiltro();

        tabelaVeiculosVendidos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabelaVeiculosDisponiveis.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        carregarEmpregadosParaFiltro();
        aplicarFiltros();
    }

    private void configurarColunasTabela() {
        // configuracao das colunas da tabela vendas
        colIdVeiculoVendido.setCellValueFactory(new PropertyValueFactory<>("idVeiculo"));
        colModeloVendido.setCellValueFactory(new PropertyValueFactory<>("modeloVeiculo"));
        colMarcaVendida.setCellValueFactory(new PropertyValueFactory<>("marcaVeiculo"));
       
        colValorVendido.setCellValueFactory(new PropertyValueFactory<>("valorVenda"));
        colDataVenda.setCellValueFactory(new PropertyValueFactory<>("dataVenda"));
        colVendedorVenda.setCellValueFactory(new PropertyValueFactory<>("nomeEmpregado"));
        colClienteVenda.setCellValueFactory(new PropertyValueFactory<>("nomeCliente"));
        colPagamentoVenda.setCellValueFactory(new PropertyValueFactory<>("metodoPagamento"));
        configurarColunaMoeda(colValorVendido);

        // configuracao das colunas da tabela veiculos disponiveis
        colIdVeiculoDisp.setCellValueFactory(new PropertyValueFactory<>("id"));
        colModeloDisp.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colMarcaDisp.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colAnoDisp.setCellValueFactory(new PropertyValueFactory<>("ano"));
        colPrecoDisp.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colStatusDisp.setCellValueFactory(new PropertyValueFactory<>("status"));
        colKmDisp.setCellValueFactory(new PropertyValueFactory<>("km"));
        colCategoriaDisp.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        configurarColunaMoeda(colPrecoDisp);

        tabelaVeiculosVendidos.setItems(dadosVendas);
        tabelaVeiculosDisponiveis.setItems(dadosVeiculos);
    }

    private <T> void configurarColunaMoeda(TableColumn<T, Double> coluna) {
        coluna.setCellFactory(tc -> new TableCell<T, Double>() {
            @Override
            protected void updateItem(Double preco, boolean empty) {
                super.updateItem(preco, empty);
                if (empty || preco == null) {
                    setText(null);
                } else {
                    setText(formatadorMoeda.format(preco));
                }
            }
        });
    }

    private void configurarComboBoxesFiltro() {
        comboBoxFiltroVendedor.setConverter(new StringConverter<Empregado>() {
            @Override
            public String toString(Empregado e) {
                return e == null ? "Todos Vendedores" : e.getNome();
            }
            @Override
            public Empregado fromString(String s) {
                return null;
            }
        });
        comboBoxFiltroVendedor.setItems(opcoesEmpregados);

        comboBoxFiltroStatusVeiculo.setItems(FXCollections.observableArrayList("Todos", "Disponível", "Vendido", "Reservado"));
        comboBoxFiltroStatusVeiculo.getSelectionModel().select("Todos");
    }

    private void carregarEmpregadosParaFiltro() {
        try {
            List<Empregado> empregados = empregadoDAO.buscarTodos();
            opcoesEmpregados.clear();
            opcoesEmpregados.add(null); // representa "Todos"
            opcoesEmpregados.addAll(empregados);
            comboBoxFiltroVendedor.getSelectionModel().selectFirst();
        } catch (Exception e) {
            rotuloStatus.setText("Erro ao carregar vendedores: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void acaoAplicarFiltros(ActionEvent evento) {
        aplicarFiltros();
    }

    @FXML
    void acaoLimparFiltros(ActionEvent evento) {
        datePickerDataInicio.setValue(null);
        datePickerDataFim.setValue(null);
        comboBoxFiltroVendedor.getSelectionModel().selectFirst();
        comboBoxFiltroStatusVeiculo.getSelectionModel().selectFirst();
        campoFiltroModeloMarca.clear();
        aplicarFiltros();
        rotuloStatus.setText("Filtros limpos. Exibindo todos os dados.");
    }

    @FXML
    void acaoExportarRelatorio(ActionEvent evento) {
        LocalDate dataInicio = datePickerDataInicio.getValue();
        LocalDate dataFim = datePickerDataFim.getValue();
        
        if (dataInicio == null || dataFim == null) {
            rotuloStatus.setText("Por favor, selecione o período (data início e data fim) para gerar o relatório.");
            return;
        }
        
        if (dataInicio.isAfter(dataFim)) {
            rotuloStatus.setText("A data de início deve ser anterior à data de fim.");
            return;
        }
        
        try {
            rotuloStatus.setText("Gerando relatório PDF...");
            
            com.gitcar.app.utils.RelatorioVendasPDF geradorPDF = new com.gitcar.app.utils.RelatorioVendasPDF();
            java.io.File arquivoPDF = geradorPDF.gerarRelatorioPDF(dataInicio, dataFim);
            
            // Abrir o arquivo PDF gerado
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(arquivoPDF);
                rotuloStatus.setText("Relatório PDF gerado com sucesso: " + arquivoPDF.getName());
            } else {
                rotuloStatus.setText("Relatório PDF gerado em: " + arquivoPDF.getAbsolutePath());
            }
            
        } catch (Exception e) {
            rotuloStatus.setText("Erro ao gerar relatório PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void aplicarFiltros() {
        LocalDate dataInicio = datePickerDataInicio.getValue();
        LocalDate dataFim = datePickerDataFim.getValue();
        Empregado vendedorSelecionado = comboBoxFiltroVendedor.getValue();
        Integer idVendedor = vendedorSelecionado != null ? vendedorSelecionado.getIdEmpregado() : null;
        String statusVeiculoSelecionado = comboBoxFiltroStatusVeiculo.getValue();
        String termoModeloMarca = campoFiltroModeloMarca.getText().trim();

        rotuloStatus.setText("Carregando dados...");

        try {
            List<Venda> vendasFiltradas = vendaDAO.buscarVendasPorCriterio(dataInicio, dataFim, idVendedor, termoModeloMarca);
            dadosVendas.setAll(vendasFiltradas);

            List<Veiculo> todosVeiculosBD = veiculoDAO.buscarTodos();
            List<Veiculo> veiculosFiltrados = todosVeiculosBD.stream()
                    .filter(veiculo -> "Todos".equals(statusVeiculoSelecionado) || veiculo.getStatus().equalsIgnoreCase(statusVeiculoSelecionado))
                    .filter(veiculo -> termoModeloMarca.isEmpty()
                            || veiculo.getModelo().toLowerCase().contains(termoModeloMarca.toLowerCase())
                            || veiculo.getMarca().toLowerCase().contains(termoModeloMarca.toLowerCase()))
                    .collect(Collectors.toList());
            dadosVeiculos.setAll(veiculosFiltrados);

            atualizarEstatisticas(dadosVendas, dadosVeiculos);
            rotuloStatus.setText("Relatorio atualizado. " + dadosVendas.size() + " vendas, " + dadosVeiculos.size() + " veiculos exibidos.");

        } catch (Exception e) {
            rotuloStatus.setText("Erro ao aplicar filtros e carregar dados: " + e.getMessage());
            e.printStackTrace();
            dadosVendas.clear();
            dadosVeiculos.clear();
            atualizarEstatisticas(dadosVendas, dadosVeiculos);
        }
    }

    private void atualizarEstatisticas(ObservableList<Venda> vendasAtuais, ObservableList<Veiculo> veiculosAtuais) {
        rotuloTotalVendido.setText("Total vendas exibidas: " + vendasAtuais.size());

        double valorTotal = vendasAtuais.stream().mapToDouble(Venda::getValorVenda).sum();
        rotuloValorTotalVendas.setText("Valor total: " + formatadorMoeda.format(valorTotal));
        rotuloTotalVeiculosExibidos.setText("Total veiculos exibidos: " + veiculosAtuais.size());

        if (!vendasAtuais.isEmpty()) {
            Map<String, Long> vendasPorEmpregado = vendasAtuais.stream()
                    .filter(v -> v.getNomeEmpregado() != null)
                    .collect(Collectors.groupingBy(Venda::getNomeEmpregado, Collectors.counting()));

            String textoMelhoresVendedores = vendasPorEmpregado.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(3)
                    .map(entry -> entry.getKey() + " (" + entry.getValue() + ")")
                    .collect(Collectors.joining("\n"));
            rotuloMelhoresVendedores.setText(textoMelhoresVendedores.isEmpty() ? "N/D" : textoMelhoresVendedores);

            Map<String, Long> vendasPorModelo = vendasAtuais.stream()
                    .filter(v -> v.getModeloVeiculo() != null)
                    .collect(Collectors.groupingBy(v -> v.getMarcaVeiculo() + " " + v.getModeloVeiculo(), Collectors.counting()));

            String textoModeloMaisVendido = vendasPorModelo.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(1)
                    .map(entry -> entry.getKey() + " (" + entry.getValue() + ")")
                    .findFirst().orElse("N/D");
            rotuloModeloMaisVendido.setText(textoModeloMaisVendido);

        } else {
            rotuloMelhoresVendedores.setText("N/D");
            rotuloModeloMaisVendido.setText("N/D");
        }
    }
}