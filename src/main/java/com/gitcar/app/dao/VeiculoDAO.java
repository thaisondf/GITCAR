package com.gitcar.app.dao;

import com.gitcar.app.models.Veiculo;
import com.gitcar.app.utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO {

    private Veiculo mapearResultSetParaVeiculo(ResultSet rs) throws SQLException {
        return new Veiculo(
            rs.getInt("id"),
            rs.getString("modelo"),
            rs.getString("marca"),
            rs.getInt("ano"),
            rs.getInt("km"),
            rs.getString("categoria"),
            rs.getDouble("preco"),
            rs.getString("cor"),
            rs.getString("combustivel"),
            rs.getString("placa"),
            rs.getString("cambio"),
            rs.getString("status")
        );
    }

    public Veiculo buscarPorId(int id) {
        String sql = "SELECT * FROM carros WHERE id = ?";
        try (Connection conexao = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetParaVeiculo(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar veículo por id: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Veiculo> buscarTodos() {
        List<Veiculo> veiculos = new ArrayList<>();
        String sql = "SELECT * FROM carros ORDER BY marca, modelo";
        try (Connection conexao = DatabaseUtil.getConnection();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                veiculos.add(mapearResultSetParaVeiculo(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os veículos: " + e.getMessage());
            e.printStackTrace();
        }
        return veiculos;
    }

    public List<Veiculo> buscarPorMarca(String marca) {
        List<Veiculo> veiculos = new ArrayList<>();
        String sql = "SELECT * FROM carros WHERE marca = ? ORDER BY modelo";
        try (Connection conexao = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setString(1, marca);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    veiculos.add(mapearResultSetParaVeiculo(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar veículos por marca: " + e.getMessage());
            e.printStackTrace();
        }
        return veiculos;
    }

    public List<Veiculo> buscarDisponiveisPorTermo(String termo) {
        List<Veiculo> veiculos = new ArrayList<>();
        String sql = "SELECT * FROM carros WHERE status = 'Disponível' AND " +
                     "(CAST(id AS CHAR) LIKE ? OR modelo LIKE ? OR marca LIKE ? OR placa LIKE ?) " +
                     "ORDER BY marca, modelo";

        try (Connection conexao = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            String filtro = "%" + termo + "%";
            pstmt.setString(1, filtro);
            pstmt.setString(2, filtro);
            pstmt.setString(3, filtro);
            pstmt.setString(4, filtro);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    veiculos.add(mapearResultSetParaVeiculo(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar veículos disponíveis por termo: " + e.getMessage());
            e.printStackTrace();
        }
        return veiculos;
    }

    public Veiculo adicionarVeiculo(Veiculo veiculo) {
        String sql = "INSERT INTO carros (modelo, preco, marca, ano, categoria, combustivel, cor, km, placa, cambio, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexao = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, veiculo.getModelo());
            pstmt.setDouble(2, veiculo.getPreco());
            pstmt.setString(3, veiculo.getMarca());
            pstmt.setInt(4, veiculo.getAno());
            pstmt.setString(5, veiculo.getCategoria());
            pstmt.setString(6, veiculo.getCombustivel());
            pstmt.setString(7, veiculo.getCor());
            pstmt.setInt(8, veiculo.getKm());
            pstmt.setString(9, veiculo.getPlaca());
            pstmt.setString(10, veiculo.getCambio());
            pstmt.setString(11, veiculo.getStatus());

            int linhasAfetadas = pstmt.executeUpdate();

            if (linhasAfetadas > 0) {
                try (ResultSet chavesGeradas = pstmt.getGeneratedKeys()) {
                    if (chavesGeradas.next()) {
                        veiculo.setId(chavesGeradas.getInt(1));
                        return veiculo;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar veículo: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean atualizarVeiculo(Veiculo veiculo) {
        String sql = "UPDATE carros SET modelo = ?, preco = ?, marca = ?, ano = ?, categoria = ?, combustivel = ?, cor = ?, km = ?, placa = ?, cambio = ?, status = ? WHERE id = ?";
        try (Connection conexao = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setString(1, veiculo.getModelo());
            pstmt.setDouble(2, veiculo.getPreco());
            pstmt.setString(3, veiculo.getMarca());
            pstmt.setInt(4, veiculo.getAno());
            pstmt.setString(5, veiculo.getCategoria());
            pstmt.setString(6, veiculo.getCombustivel());
            pstmt.setString(7, veiculo.getCor());
            pstmt.setInt(8, veiculo.getKm());
            pstmt.setString(9, veiculo.getPlaca());
            pstmt.setString(10, veiculo.getCambio());
            pstmt.setString(11, veiculo.getStatus());
            pstmt.setInt(12, veiculo.getId());

            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar veículo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizarStatusVeiculo(int id, String status) {
        String sql = "UPDATE carros SET status = ? WHERE id = ?";
        try (Connection conexao = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, id);

            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status do veículo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Veiculo> buscarPorStatus(String status) {
        List<Veiculo> veiculos = new ArrayList<>();
        String sql = "SELECT * FROM carros WHERE status = ? ORDER BY marca, modelo";
        try (Connection conexao = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    veiculos.add(mapearResultSetParaVeiculo(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar veículos por status: " + e.getMessage());
            e.printStackTrace();
        }
        return veiculos;
    }

    public boolean excluirVeiculo(int id) {
        String sql = "DELETE FROM carros WHERE id = ?";
        try (Connection conexao = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao excluir veículo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
