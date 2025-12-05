package com.gitcar.app.controladores;

import com.gitcar.app.dao.VendaDAO;
import com.gitcar.app.models.Venda;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class HistoricoVendasControlador {

    @FXML private DatePicker datePickerDataInicio;
    @FXML private DatePicker datePickerDataFim;
    @FXML private Button botaoFiltrar;
    @FXML private Button botaoLimparFiltro;
    @FXML private TableView<Venda> tabelaVendas;
    @FXML private TableColumn<Venda, Integer> colIdVenda;
    @FXML private TableColumn<Venda, Integer> colIdVeiculo;
    @FXML private TableColumn<Venda, String> colModeloVeiculo;
    @FXML private TableColumn<Venda, String> colMarcaVeiculo;
    @FXML private TableColumn<Venda, Double> colValorVenda;
    @FXML private TableColumn<Venda, String> colDataVenda;
    @FXML private TableColumn<Venda, String> colNomeCliente;
    @FXML private TableColumn<Venda, String> colMetodoPagamento;
    @FXML private Label rotuloStatus;

    private VendaDAO vendaDAO;
    private ObservableList<Venda> historicoVendas = FXCollections.observableArrayList();
    private int idVendedorLogado;

    @FXML
        public void setIdVendedorLogado(int idVendedorLogado) {
        this.idVendedorLogado = idVendedorLogado;
        carregarHistoricoVendas(null, null); // Chama o carregamento após o ID ser definido
    }

    @FXML
    public void initialize() {
        vendaDAO = new VendaDAO();
        rotuloStatus.setText("");
        configurarColunasTabela();
        tabelaVendas.setItems(historicoVendas);

        // A verificação de idVendedorLogado e o carregamento inicial foram movidos para setIdVendedorLogado()
        // O initialize() apenas configura a tabela.
    }

    private void configurarColunasTabela() {
        colIdVenda.setCellValueFactory(new PropertyValueFactory<>("idVenda"));
        colIdVeiculo.setCellValueFactory(new PropertyValueFactory<>("idVeiculo"));
        colModeloVeiculo.setCellValueFactory(new PropertyValueFactory<>("modeloVeiculo"));
        colMarcaVeiculo.setCellValueFactory(new PropertyValueFactory<>("marcaVeiculo"));
        colDataVenda.setCellValueFactory(new PropertyValueFactory<>("dataVenda"));
        colNomeCliente.setCellValueFactory(new PropertyValueFactory<>("nomeCliente"));
        colMetodoPagamento.setCellValueFactory(new PropertyValueFactory<>("metodoPagamento"));

        NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        colValorVenda.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getValorVenda()).asObject());
        colValorVenda.setCellFactory(coluna -> new TableCell<Venda, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : formatadorMoeda.format(item));
            }
        });
    }

    @FXML
    void acaoFiltrar(ActionEvent evento) {
        LocalDate dataInicio = datePickerDataInicio.getValue();
        LocalDate dataFim = datePickerDataFim.getValue();

        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            rotuloStatus.setText("erro: data de inicio nao pode ser posterior a data de fim.");
            return;
        }

        rotuloStatus.setText("");
        carregarHistoricoVendas(dataInicio, dataFim);
    }

    @FXML
    void acaoLimparFiltro(ActionEvent evento) {
        datePickerDataInicio.setValue(null);
        datePickerDataFim.setValue(null);
        rotuloStatus.setText("");
        carregarHistoricoVendas(null, null);
    }

    private void carregarHistoricoVendas(LocalDate dataInicio, LocalDate dataFim) {
        if (idVendedorLogado <= 0) {
            rotuloStatus.setText("erro: vendedor nao identificado.");
            return;
        }

        historicoVendas.clear();

        try {
            List<Venda> vendas = vendaDAO.buscarVendasPorVendedor(idVendedorLogado, dataInicio, dataFim);
            historicoVendas.setAll(vendas);

            rotuloStatus.setText(vendas.isEmpty() ?
                "nenhuma venda encontrada para o periodo selecionado." :
                "exibindo " + vendas.size() + " vendas.");
        } catch (Exception e) {
            rotuloStatus.setText("erro ao carregar historico: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
