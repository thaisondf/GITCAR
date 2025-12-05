package com.gitcar.app.controladores;

import com.gitcar.app.dao.ClienteDAO;
import com.gitcar.app.dao.VendaDAO;
import com.gitcar.app.dao.VeiculoDAO;
import com.gitcar.app.models.Cliente;
import com.gitcar.app.models.Empregado;
import com.gitcar.app.models.Venda;
import com.gitcar.app.models.Veiculo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

    public class VenderVeiculoControlador {

        @FXML
        private TextField campoBuscaVeiculo;

        @FXML
        private Button botaoBuscarVeiculo;

        @FXML
        private ComboBox<Veiculo> comboBoxVeiculosDisponiveis;

        @FXML
        private TextField campoModelo;

        @FXML
        private TextField campoMarca;

        @FXML
        private TextField campoAno;

        @FXML
        private TextField campoPreco;

      
        @FXML
        private TextField campoPlaca;

        @FXML
        private ComboBox<Cliente> comboBoxCliente;

        @FXML
        private Button botaoAdicionarNovoCliente;

        @FXML
        private TextField campoValorVenda;

        @FXML
        private ComboBox<String> comboBoxMetodoPagamento;

        @FXML
        private Label labelParcelas;

        @FXML
        private TextField campoParcelas;

        @FXML
        private Label labelValorParcela;

        @FXML
        private TextField campoValorParcela;

        @FXML
        private DatePicker datePickerDataVenda;

        @FXML
        private Button botaoCancelar;

        @FXML
        private Button botaoConfirmarVenda;

        @FXML
        private Label rotuloStatus;

        private VeiculoDAO veiculoDAO;
        private ClienteDAO clienteDAO;
        private VendaDAO vendaDAO;

        private ObservableList<Veiculo> veiculosDisponiveis = FXCollections.observableArrayList();
        private ObservableList<Cliente> clientes = FXCollections.observableArrayList();

        private Veiculo veiculoSelecionado = null;

        private int idEmpregadoLogado = 1;

        private final NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        @FXML
        public void initialize() {
            veiculoDAO = new VeiculoDAO();
            clienteDAO = new ClienteDAO();
            vendaDAO = new VendaDAO();

            rotuloStatus.setText("");
            configurarComboBoxes();
            datePickerDataVenda.setValue(LocalDate.now());

            carregarClientes();
            desabilitarDetalhesVenda(true);

           
            campoParcelas.textProperty().addListener((observable, oldValue, newValue) -> {
                calcularValorParcela();
            });

            campoValorVenda.textProperty().addListener((observable, oldValue, newValue) -> {
                calcularValorParcela();
            });

           
            if (Empregado.logado == null) {
                rotuloStatus.setText("Aviso: Nenhum empregado logado. As vendas serão registradas com ID padrão.");
            }
        }

        private void configurarComboBoxes() {
            comboBoxVeiculosDisponiveis.setConverter(new StringConverter<Veiculo>() {
                @Override
                public String toString(Veiculo veiculo) {
                    return veiculo == null ? null : veiculo.getId() + " - " + veiculo.getMarca() + " " + veiculo.getModelo() + " (" + veiculo.getAno() + ")";
                }

                @Override
                public Veiculo fromString(String string) {
                    return null; 
                }
            });
            comboBoxVeiculosDisponiveis.setItems(veiculosDisponiveis);

            comboBoxCliente.setConverter(new StringConverter<Cliente>() {
                @Override
                public String toString(Cliente cliente) {
                    return cliente == null ? null : cliente.getIdCliente() + " - " + cliente.getNome();
                }

                @Override
                public Cliente fromString(String string) {
                    return null; 
                }
            });
            comboBoxCliente.setItems(clientes);

            comboBoxMetodoPagamento.setItems(FXCollections.observableArrayList("Dinheiro", "Cartão Débito", "Cartão Crédito"));
        }

        private void carregarClientes() {
            try {
                List<Cliente> listaClientes = clienteDAO.buscarTodos();
                clientes.setAll(listaClientes);
            } catch (Exception e) {
                rotuloStatus.setText("Erro ao carregar clientes: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @FXML
        void acaoSelecaoFormaPagamento(ActionEvent evento) {
            String formaPagamento = comboBoxMetodoPagamento.getSelectionModel().getSelectedItem();
            if (formaPagamento != null && formaPagamento.equals("Cartão Crédito")) {
                labelParcelas.setVisible(true);
                campoParcelas.setVisible(true);
                labelValorParcela.setVisible(true);
                campoValorParcela.setVisible(true);

                // Inicializa com 1 parcela por padrão
                if (campoParcelas.getText().isEmpty()) {
                    campoParcelas.setText("1");
                }
                calcularValorParcela();
            } else {
                labelParcelas.setVisible(false);
                campoParcelas.setVisible(false);
                labelValorParcela.setVisible(false);
                campoValorParcela.setVisible(false);
            }
        }

        private void calcularValorParcela() {
            if (!campoParcelas.isVisible() || campoParcelas.getText().isEmpty() || campoValorVenda.getText().isEmpty()) {
                campoValorParcela.setText("");
                return;
            }

            try {
                int numeroParcelas = Integer.parseInt(campoParcelas.getText());
                double valorTotal = Double.parseDouble(campoValorVenda.getText().replace(",", "."));

                if (numeroParcelas <= 0) {
                    campoValorParcela.setText("Inválido");
                    return;
                }

                double valorParcela = valorTotal / numeroParcelas;
                campoValorParcela.setText(String.format(Locale.US, "%.2f", valorParcela));
            } catch (NumberFormatException e) {
                campoValorParcela.setText("Inválido");
            }
        }

        @FXML
        void acaoBuscarVeiculo(ActionEvent evento) {
            String termoBusca = campoBuscaVeiculo.getText().trim();
            rotuloStatus.setText("");
            veiculosDisponiveis.clear();
            limparDetalhesVeiculo();
            desabilitarDetalhesVenda(true);

            if (termoBusca.isEmpty()) {
                rotuloStatus.setText("Digite um termo para buscar (ID, Modelo, Marca, Placa).");
                return;
            }

            try {
                List<Veiculo> veiculosEncontrados = veiculoDAO.buscarDisponiveisPorTermo(termoBusca);
                if (veiculosEncontrados.isEmpty()) {
                    rotuloStatus.setText("Nenhum veículo disponível encontrado para '" + termoBusca + "'.");
                } else {
                    veiculosDisponiveis.setAll(veiculosEncontrados);
                    rotuloStatus.setText(veiculosEncontrados.size() + " veículo(s) encontrado(s).");
                }
            } catch (Exception e) {
                rotuloStatus.setText("Erro ao buscar veículos: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @FXML
        void acaoSelecaoVeiculo(ActionEvent evento) {
            veiculoSelecionado = comboBoxVeiculosDisponiveis.getSelectionModel().getSelectedItem();
            if (veiculoSelecionado != null) {
                campoModelo.setText(veiculoSelecionado.getModelo());
                campoMarca.setText(veiculoSelecionado.getMarca());
                campoAno.setText(String.valueOf(veiculoSelecionado.getAno()));
                campoPreco.setText(formatadorMoeda.format(veiculoSelecionado.getPreco()));
                campoPlaca.setText(veiculoSelecionado.getPlaca());
                // Valor da venda inicia com preço do veículo
                campoValorVenda.setText(String.format(Locale.US, "%.2f", veiculoSelecionado.getPreco()));
                desabilitarDetalhesVenda(false);
                rotuloStatus.setText("");
            } else {
                limparDetalhesVeiculo();
                desabilitarDetalhesVenda(true);
            }
        }


        @FXML
        void acaoAdicionarNovoCliente(ActionEvent evento) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gitcar/view/AdicionarClienteView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Adicionar Novo Cliente");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Bloqueia a janela principal até fechar essa
            stage.showAndWait();

            // Após fechar a janela, recarrega os clientes
            carregarClientes();

        } catch (Exception e) {
            rotuloStatus.setText("Erro ao abrir a janela de adicionar cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }


        @FXML
        void acaoConfirmarVenda(ActionEvent evento) {
            rotuloStatus.setText("");

            if (veiculoSelecionado == null) {
                rotuloStatus.setText("Erro: Nenhum veículo selecionado.");
                return;
            }

            Cliente clienteSelecionado = comboBoxCliente.getSelectionModel().getSelectedItem();
            if (clienteSelecionado == null) {
                rotuloStatus.setText("Erro: Nenhum cliente selecionado.");
                return;
            }

            String metodoPagamento = comboBoxMetodoPagamento.getSelectionModel().getSelectedItem();
            if (metodoPagamento == null || metodoPagamento.isEmpty()) {
                rotuloStatus.setText("Erro: Selecione a forma de pagamento.");
                return;
            }

            // Validar parcelas para pagamento com cartão de crédito
            if (metodoPagamento.equals("Cartão Crédito")) {
                if (campoParcelas.getText().isEmpty()) {
                    rotuloStatus.setText("Erro: Informe o número de parcelas para pagamento com cartão de crédito.");
                    return;
                }

                try {
                    int numeroParcelas = Integer.parseInt(campoParcelas.getText());
                    if (numeroParcelas <= 0) {
                        rotuloStatus.setText("Erro: O número de parcelas deve ser maior que zero.");
                        return;
                    }

                    // Adicionar informação de parcelas ao método de pagamento
                    metodoPagamento = metodoPagamento + " (" + numeroParcelas + "x)";
                } catch (NumberFormatException e) {
                    rotuloStatus.setText("Erro: Número de parcelas inválido.");
                    return;
                }
            }

            LocalDate dataVenda = datePickerDataVenda.getValue();
            if (dataVenda == null) {
                rotuloStatus.setText("Erro: Selecione a data da venda.");
                return;
            }

            double valorVenda;
            try {
                valorVenda = Double.parseDouble(campoValorVenda.getText().replace(",", "."));
            } catch (NumberFormatException e) {
                rotuloStatus.setText("Erro: Valor da venda inválido.");
                return;
            }

            Venda novaVenda = new Venda(
                    veiculoSelecionado.getId(),
                    Empregado.logado != null ? Empregado.logado.getIdEmpregado() : 1, // Usa o ID do empregado logado
                    clienteSelecionado.getIdCliente(),
                    valorVenda,
                    metodoPagamento
            );

            String dataHoraVendaString = LocalDateTime.of(dataVenda, LocalDateTime.now().toLocalTime())
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            novaVenda.setDataVenda(dataHoraVendaString);

            try {
                Venda vendaAdicionada = vendaDAO.adicionarVenda(novaVenda);
                if (vendaAdicionada != null) {
                    boolean statusAtualizado = veiculoDAO.atualizarStatusVeiculo(veiculoSelecionado.getId(), "Vendido");
                    if (statusAtualizado) {
                        String nomeVendedor = Empregado.logado != null ? Empregado.logado.getNome() : "Vendedor não identificado";
                        rotuloStatus.setText("Venda registrada com sucesso! Veículo ID: " + veiculoSelecionado.getId() + 
                                           " vendido por " + nomeVendedor + ".");
                        veiculosDisponiveis.remove(veiculoSelecionado);
                        limparFormulario();
                        veiculoSelecionado = null;
                        desabilitarDetalhesVenda(true);
                    } else {
                        rotuloStatus.setText("Erro: Venda registrada (ID: " + vendaAdicionada.getIdVenda() + "), mas falha ao atualizar status do veículo.");
                    }
                } else {
                    rotuloStatus.setText("Erro ao registrar a venda no banco de dados.");
                }
            } catch (Exception e) {
                rotuloStatus.setText("Erro no banco de dados ao confirmar venda: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @FXML
        void acaoCancelar(ActionEvent evento) {
            limparFormulario();
            rotuloStatus.setText("Operação cancelada.");
        }

        private void limparFormulario() {
            campoBuscaVeiculo.clear();
            comboBoxVeiculosDisponiveis.getSelectionModel().clearSelection();
            veiculosDisponiveis.clear();
            limparDetalhesVeiculo();
            comboBoxCliente.getSelectionModel().clearSelection();
            campoValorVenda.clear();
            comboBoxMetodoPagamento.getSelectionModel().clearSelection();
            campoParcelas.clear();
            campoValorParcela.clear();
            labelParcelas.setVisible(false);
            campoParcelas.setVisible(false);
            labelValorParcela.setVisible(false);
            campoValorParcela.setVisible(false);
            datePickerDataVenda.setValue(LocalDate.now());
            rotuloStatus.setText("");
            veiculoSelecionado = null;
            desabilitarDetalhesVenda(true);
        }

        private void limparDetalhesVeiculo() {
            campoModelo.clear();
            campoMarca.clear();
            campoAno.clear();
            campoPreco.clear();
            campoPlaca.clear();
        }

        private void desabilitarDetalhesVenda(boolean desabilitar) {
            comboBoxCliente.setDisable(desabilitar);
            botaoAdicionarNovoCliente.setDisable(desabilitar);
            campoValorVenda.setDisable(desabilitar);
            comboBoxMetodoPagamento.setDisable(desabilitar);
            datePickerDataVenda.setDisable(desabilitar);
            botaoConfirmarVenda.setDisable(desabilitar);

            
            campoParcelas.setDisable(desabilitar);

            campoModelo.setDisable(desabilitar);
            campoMarca.setDisable(desabilitar);
            campoAno.setDisable(desabilitar);
            campoPreco.setDisable(desabilitar);
            campoPlaca.setDisable(desabilitar);
        }
}