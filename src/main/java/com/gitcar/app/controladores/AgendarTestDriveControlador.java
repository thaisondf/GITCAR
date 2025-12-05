package com.gitcar.app.controladores;

import com.gitcar.app.dao.AgendamentoTestDriveDAO;
import com.gitcar.app.dao.VeiculoDAO;
import com.gitcar.app.models.AgendamentoTestDrive;
import com.gitcar.app.models.Veiculo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class AgendarTestDriveControlador {

    @FXML
    private ComboBox<Veiculo> comboBoxVeiculosDisponiveis;
    @FXML
    private TextField campoNomeCliente;
    @FXML
    private TextField campoContatoCliente;
    @FXML
    private DatePicker datePickerDataTestDrive;
    @FXML
    private ComboBox<String> comboBoxHoraTestDrive;
    @FXML
    private Button botaoCancelar;
    @FXML
    private Button botaoAgendar;
    @FXML
    private Label rotuloStatus;
    
    // Colunas da tabela
    @FXML
    private TableView<AgendamentoTestDrive> tabelaAgendamentos;
    @FXML
    private TableColumn<AgendamentoTestDrive, Integer> collVeiculo;
    @FXML
    private TableColumn<AgendamentoTestDrive, Integer> collIdCarro;
    @FXML
    private TableColumn<AgendamentoTestDrive, Integer> collIdFunc;
    @FXML
    private TableColumn<AgendamentoTestDrive, String> collNomeClient;
    @FXML
    private TableColumn<AgendamentoTestDrive, String> collContato;
    @FXML
    private TableColumn<AgendamentoTestDrive, String> collData;
    @FXML
    private TableColumn<AgendamentoTestDrive, String> collHora;

    private VeiculoDAO veiculoDAO;
    private AgendamentoTestDriveDAO agendamentoTestDriveDAO;

    private ObservableList<Veiculo> veiculosDisponiveis = FXCollections.observableArrayList();
    private ObservableList<AgendamentoTestDrive> listaAgendamentos = FXCollections.observableArrayList();

    private int idEmpregadoLogado = 1;

    @FXML
    public void initialize() {
        veiculoDAO = new VeiculoDAO();
        agendamentoTestDriveDAO = new AgendamentoTestDriveDAO();

        rotuloStatus.setText("");
        configurarComboBoxes();
        configurarTabela();
        carregarVeiculosDisponiveis();
        carregarAgendamentos();
        datePickerDataTestDrive.setValue(LocalDate.now());
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

        List<String> horarios = new ArrayList<>();
        LocalTime hora = LocalTime.of(9, 0);
        LocalTime horaFim = LocalTime.of(17, 30);
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("HH:mm");

        while (!hora.isAfter(horaFim)) {
            horarios.add(hora.format(formatador));
            hora = hora.plusMinutes(30);
        }

        comboBoxHoraTestDrive.setItems(FXCollections.observableArrayList(horarios));
    }
    
    private void configurarTabela() {
        // Configurar as colunas da tabela
        collVeiculo.setCellValueFactory(new PropertyValueFactory<>("idTestDrive"));
        collIdCarro.setCellValueFactory(new PropertyValueFactory<>("idVeiculo"));
        collIdFunc.setCellValueFactory(new PropertyValueFactory<>("idEmpregado"));
        collNomeClient.setCellValueFactory(new PropertyValueFactory<>("nomeCliente"));
        collContato.setCellValueFactory(new PropertyValueFactory<>("contatoCliente"));
        
        // Configurar colunas de data e hora usando PropertyValueFactory simples
        // Isso é uma simplificação para evitar problemas de compilação
        collData.setCellValueFactory(new PropertyValueFactory<>("dataHoraTestDrive"));
        collHora.setCellValueFactory(new PropertyValueFactory<>("dataHoraTestDrive"));
        
        // Associar a lista de agendamentos à tabela
        tabelaAgendamentos.setItems(listaAgendamentos);
    }

    private void carregarVeiculosDisponiveis() {
        try {
            List<Veiculo> veiculos = veiculoDAO.buscarPorStatus("Disponível");
            veiculosDisponiveis.setAll(veiculos);
            if (veiculos.isEmpty()) {
                rotuloStatus.setText("nenhum veiculo disponivel pra agendamento.");
            }
        } catch (Exception e) {
            rotuloStatus.setText("erro ao carregar veiculos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void carregarAgendamentos() {
        try {
            List<AgendamentoTestDrive> agendamentos = agendamentoTestDriveDAO.buscarTodosAgendados();
            listaAgendamentos.setAll(agendamentos);
        } catch (Exception e) {
            rotuloStatus.setText("erro ao carregar agendamentos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void acaoAgendar(ActionEvent evento) {
        rotuloStatus.setText("");

        Veiculo veiculoSelecionado = comboBoxVeiculosDisponiveis.getSelectionModel().getSelectedItem();
        String nomeCliente = campoNomeCliente.getText().trim();
        String contatoCliente = campoContatoCliente.getText().trim();
        LocalDate dataTestDrive = datePickerDataTestDrive.getValue();
        String horaSelecionada = comboBoxHoraTestDrive.getSelectionModel().getSelectedItem();

        if (veiculoSelecionado == null) {
            rotuloStatus.setText("erro: selecione um veiculo.");
            return;
        }
        if (nomeCliente.isEmpty()) {
            rotuloStatus.setText("erro: nome do cliente obrigatorio.");
            return;
        }
        if (contatoCliente.isEmpty()) {
            rotuloStatus.setText("erro: contato do cliente obrigatorio.");
            return;
        }
        if (dataTestDrive == null) {
            rotuloStatus.setText("erro: selecione a data.");
            return;
        }
        if (horaSelecionada == null || horaSelecionada.isEmpty()) {
            rotuloStatus.setText("erro: selecione a hora.");
            return;
        }

        LocalTime horaTestDrive;
        try {
            horaTestDrive = LocalTime.parse(horaSelecionada, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            rotuloStatus.setText("erro: formato de hora invalido.");
            return;
        }

        LocalDateTime dataHoraTestDrive = LocalDateTime.of(dataTestDrive, horaTestDrive);

        if (dataHoraTestDrive.isBefore(LocalDateTime.now())) {
            rotuloStatus.setText("erro: data/hora nao pode ser no passado.");
            return;
        }

        String dataHoraString = dataHoraTestDrive.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        AgendamentoTestDrive novoAgendamento = new AgendamentoTestDrive(
                veiculoSelecionado.getId(),
                idEmpregadoLogado,
                nomeCliente,
                contatoCliente,
                dataHoraString
        );

        try {
            AgendamentoTestDrive agendamentoSalvo = agendamentoTestDriveDAO.adicionarAgendamento(novoAgendamento);
            if (agendamentoSalvo != null) {
                rotuloStatus.setText("agendado com sucesso para " + nomeCliente + " (id: " + agendamentoSalvo.getIdTestDrive() + ")");
                limparFormulario();
                veiculoDAO.atualizarStatusVeiculo(veiculoSelecionado.getId(), "Reservado");
                
                // Atualizar a tabela após o agendamento
                carregarAgendamentos();
            } else {
                rotuloStatus.setText("erro ao salvar agendamento no banco.");
            }
        } catch (Exception e) {
            rotuloStatus.setText("erro ao agendar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void acaoCancelar(ActionEvent evento) {
        limparFormulario();
        rotuloStatus.setText("agendamento cancelado.");
    }

    private void limparFormulario() {
        comboBoxVeiculosDisponiveis.getSelectionModel().clearSelection();
        campoNomeCliente.clear();
        campoContatoCliente.clear();
        datePickerDataTestDrive.setValue(LocalDate.now());
        comboBoxHoraTestDrive.getSelectionModel().clearSelection();
        rotuloStatus.setText("");
    }
}