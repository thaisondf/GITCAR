package com.gitcar.app.dao;

import com.gitcar.app.models.AgendamentoTestDrive;
import com.gitcar.app.utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgendamentoTestDriveDAO {

    private AgendamentoTestDrive mapearResultSetParaAgendamento(ResultSet rs) throws SQLException {
        return new AgendamentoTestDrive(
                rs.getInt("id_agendamento"),
                rs.getInt("id_carro"),
                rs.getInt("id_funcionario"),
                rs.getString("nome_cliente"),
                rs.getString("contato_cliente"),
                rs.getString("data_hora_test_drive"),
                rs.getString("status")
        );
    }

    public AgendamentoTestDrive adicionarAgendamento(AgendamentoTestDrive agendamento) {
        String sql = "INSERT INTO agendamento_test_drive (id_carro, id_funcionario, nome_cliente, contato_cliente, data_hora_test_drive, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexao = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, agendamento.getIdVeiculo());
            pstmt.setInt(2, agendamento.getIdEmpregado());
            pstmt.setString(3, agendamento.getNomeCliente());
            pstmt.setString(4, agendamento.getContatoCliente());
            pstmt.setString(5, agendamento.getDataHoraTestDrive());
            pstmt.setString(6, agendamento.getStatus() != null ? agendamento.getStatus() : "Agendado");

            int linhasAfetadas = pstmt.executeUpdate();

            if (linhasAfetadas > 0) {
                try (ResultSet chavesGeradas = pstmt.getGeneratedKeys()) {
                    if (chavesGeradas.next()) {
                        agendamento.setIdTestDrive(chavesGeradas.getInt(1));
                        return agendamento;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar agendamento de test drive: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<AgendamentoTestDrive> buscarTodosAgendados() {
        List<AgendamentoTestDrive> agendamentos = new ArrayList<>();
        String sql = "SELECT atd.*, " +
                     "CONCAT(c.marca, ' ', c.modelo, ' (', c.ano, ')') AS infoVeiculo, " +
                     "f.nome AS nomeFuncionario " +
                     "FROM agendamento_test_drive atd " +
                     "LEFT JOIN carros c ON atd.id_carro = c.id " +
                     "LEFT JOIN funcionario f ON atd.id_funcionario = f.id " +
                     "WHERE atd.status = ? " +
                     "ORDER BY atd.data_hora_test_drive";

        try (Connection conexao = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setString(1, "Agendado");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                AgendamentoTestDrive agendamento = mapearResultSetParaAgendamento(rs);
                agendamento.setInfoVeiculo(rs.getString("infoVeiculo"));
                agendamento.setNomeEmpregado(rs.getString("nomeFuncionario"));
                agendamentos.add(agendamento);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar agendamentos de test drives: " + e.getMessage());
            e.printStackTrace();
        }
        return agendamentos;
    }

    public boolean atualizarStatusAgendamento(int idAgendamento, String status) {
        String sql = "UPDATE agendamento_test_drive SET status = ? WHERE id_agendamento = ?";
        try (Connection conexao = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, idAgendamento);
            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status do agendamento de test drive: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}