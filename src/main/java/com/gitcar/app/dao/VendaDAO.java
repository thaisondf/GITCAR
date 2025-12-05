package com.gitcar.app.dao;

import com.gitcar.app.models.Venda;
import com.gitcar.app.utils.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO {

    private Venda mapearResultSetParaVenda(ResultSet rs) throws SQLException {
        return new Venda(
                rs.getInt("id_venda"),
                rs.getInt("id_veiculo"),
                rs.getInt("id_funcionario"),
                rs.getInt("id_cliente"),
                rs.getString("data_venda"),
                rs.getDouble("valor_venda"),
                rs.getString("metodo_pagamento")
        );
    }

    public Venda adicionarVenda(Venda venda) {
        String sql = "INSERT INTO vendas(id_veiculo, id_funcionario, id_cliente, valor_venda, metodo_pagamento, data_venda) VALUES (?, ?, ?, ?, ?, ?)";

        String dataVenda = (venda.getDataVenda() != null) ? venda.getDataVenda()
                : java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (Connection conexao = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, venda.getIdVeiculo());
            pstmt.setInt(2, venda.getIdEmpregado());
            pstmt.setInt(3, venda.getIdCliente());
            pstmt.setDouble(4, venda.getValorVenda());
            pstmt.setString(5, venda.getMetodoPagamento());
            pstmt.setString(6, dataVenda);

            int linhasAfetadas = pstmt.executeUpdate();

            if (linhasAfetadas > 0) {
                try (ResultSet chavesGeradas = pstmt.getGeneratedKeys()) {
                    if (chavesGeradas.next()) {
                        venda.setIdVenda(chavesGeradas.getInt(1));
                        venda.setDataVenda(dataVenda);
                        return venda;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar venda: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Venda> buscarVendasPorVendedor(int idEmpregado, LocalDate dataInicio, LocalDate dataFim) {
        List<Venda> vendas = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT v.*, c.modelo AS modeloVeiculo, c.marca AS marcaVeiculo, cli.nome AS nomeCliente " +
                "FROM vendas v " +
                "JOIN carros c ON v.id_veiculo = c.id " +
                "JOIN clientes cli ON v.id_cliente = cli.id " +
                "WHERE v.id_funcionario = ?"
        );

        List<Object> parametros = new ArrayList<>();
        parametros.add(idEmpregado);

        if (dataInicio != null) {
            sql.append(" AND DATE(v.data_venda) >= ?");
            parametros.add(dataInicio.toString());
        }
        if (dataFim != null) {
            sql.append(" AND DATE(v.data_venda) <= ?");
            parametros.add(dataFim.toString());
        }

        sql.append(" ORDER BY v.data_venda DESC");

        try (Connection conexao = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conexao.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                pstmt.setObject(i + 1, parametros.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Venda venda = mapearResultSetParaVenda(rs);
                venda.setModeloVeiculo(rs.getString("modeloVeiculo"));
                venda.setMarcaVeiculo(rs.getString("marcaVeiculo"));
                venda.setNomeCliente(rs.getString("nomeCliente"));
                vendas.add(venda);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar vendas por vendedor: " + e.getMessage());
            e.printStackTrace();
        }

        return vendas;
    }

    public List<Venda> buscarVendasPorCriterio(LocalDate dataInicio, LocalDate dataFim, Integer idEmpregado, String modeloMarcaVeiculo) {
        List<Venda> vendas = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT v.*, c.modelo AS modeloVeiculo, c.marca AS marcaVeiculo, f.nome AS nomeEmpregado, cli.nome AS nomeCliente " +
                "FROM vendas v " +
                "JOIN carros c ON v.id_veiculo = c.id " +
                "JOIN funcionario f ON v.id_funcionario = f.id " +
                "JOIN clientes cli ON v.id_cliente = cli.id " +
                "WHERE 1=1"
        );

        List<Object> parametros = new ArrayList<>();

        if (dataInicio != null) {
            sql.append(" AND DATE(v.data_venda) >= ?");
            parametros.add(dataInicio.toString());
        }
        if (dataFim != null) {
            sql.append(" AND DATE(v.data_venda) <= ?");
            parametros.add(dataFim.toString());
        }
        if (idEmpregado != null) {
            sql.append(" AND v.id_funcionario = ?");
            parametros.add(idEmpregado);
        }
        if (modeloMarcaVeiculo != null && !modeloMarcaVeiculo.isEmpty()) {
            sql.append(" AND (c.modelo LIKE ? OR c.marca LIKE ?)");
            String termoLike = "%" + modeloMarcaVeiculo + "%";
            parametros.add(termoLike);
            parametros.add(termoLike);
        }

        sql.append(" ORDER BY v.data_venda DESC");

        try (Connection conexao = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conexao.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                pstmt.setObject(i + 1, parametros.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Venda venda = mapearResultSetParaVenda(rs);
                venda.setModeloVeiculo(rs.getString("modeloVeiculo"));
                venda.setMarcaVeiculo(rs.getString("marcaVeiculo"));
                venda.setNomeEmpregado(rs.getString("nomeEmpregado"));
                venda.setNomeCliente(rs.getString("nomeCliente"));
                vendas.add(venda);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar vendas por critério: " + e.getMessage());
            e.printStackTrace();
        }

        return vendas;
    }
}
