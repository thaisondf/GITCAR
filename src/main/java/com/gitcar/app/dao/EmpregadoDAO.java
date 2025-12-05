package com.gitcar.app.dao;

import com.gitcar.app.models.Empregado;
import com.gitcar.app.utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpregadoDAO {

    private Empregado mapearResultSetParaEmpregado(ResultSet rs) throws SQLException {
        return new Empregado(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("senha"),
                rs.getString("cargo"),
                rs.getString("status")
        );
    }

    public Empregado buscarPorEmail(String email) {
        String sql = "SELECT * FROM funcionario WHERE email = ?";
        try (Connection conexao = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearResultSetParaEmpregado(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar funcionário por email: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Empregado autenticar(String email, String senha) {
    String sql = "SELECT * FROM funcionario WHERE email = ? AND senha = ?";
    try (Connection conexao = DatabaseUtil.getConnection();
         PreparedStatement pstmt = conexao.prepareStatement(sql)) {

        pstmt.setString(1, email);
        pstmt.setString(2, senha);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return mapearResultSetParaEmpregado(rs);
        }
    } catch (SQLException e) {
        System.err.println("Erro ao autenticar funcionário: " + e.getMessage());
        e.printStackTrace();
    }
    return null;
    }


    public Empregado adicionarEmpregado(Empregado empregado) {
        String sql = "INSERT INTO funcionario(nome, email, senha, cargo, status) VALUES(?,?,?,?,?)";

        try (Connection conexao = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, empregado.getNome());
            pstmt.setString(2, empregado.getEmail());
            pstmt.setString(3, empregado.getSenha());
            pstmt.setString(4, empregado.getCargo());
            pstmt.setString(5, empregado.getStatus());

            int linhasAfetadas = pstmt.executeUpdate();

            if (linhasAfetadas > 0) {
                try (ResultSet chavesGeradas = pstmt.getGeneratedKeys()) {
                    if (chavesGeradas.next()) {
                        empregado.setIdEmpregado(chavesGeradas.getInt(1));
                        return empregado;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar funcionário: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean atualizarEmpregado(Empregado empregado) {
        String sql = "UPDATE funcionario SET nome = ?, email = ?, senha = ?, cargo = ?, status = ? WHERE id = ?";

        try (Connection conexao = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setString(1, empregado.getNome());
            pstmt.setString(2, empregado.getEmail());
            pstmt.setString(3, empregado.getSenha());
            pstmt.setString(4, empregado.getCargo());
            pstmt.setString(5, empregado.getStatus());
            pstmt.setInt(6, empregado.getIdEmpregado());

            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar funcionário: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizarStatusEmpregado(int idEmpregado, String status) {
        String sql = "UPDATE funcionario SET status = ? WHERE id = ?";
        try (Connection conexao = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, idEmpregado);
            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Empregado> buscarTodos() {
        List<Empregado> empregados = new ArrayList<>();
        String sql = "SELECT * FROM funcionario ORDER BY nome";
        try (Connection conexao = DatabaseUtil.getConnection();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                empregados.add(mapearResultSetParaEmpregado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os funcionários: " + e.getMessage());
            e.printStackTrace();
        }
        return empregados;
    }
}
