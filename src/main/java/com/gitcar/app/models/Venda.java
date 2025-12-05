package com.gitcar.app.models;

public class Venda {
    private int idVenda;
    private int idVeiculo;
    private int idEmpregado;
    private int idCliente;
    private String dataVenda;
    private double valorVenda;
    private String metodoPagamento;

    private String modeloVeiculo;
    private String marcaVeiculo;
    private String nomeEmpregado;
    private String nomeCliente;

    public Venda(int idVeiculo, int idEmpregado, int idCliente, double valorVenda, String metodoPagamento) {
        this.idVeiculo = idVeiculo;
        this.idEmpregado = idEmpregado;
        this.idCliente = idCliente;
        this.valorVenda = valorVenda;
        this.metodoPagamento = metodoPagamento;
    }

    public Venda(int idVenda, int idVeiculo, int idEmpregado, int idCliente, String dataVenda, double valorVenda, String metodoPagamento) {
        this.idVenda = idVenda;
        this.idVeiculo = idVeiculo;
        this.idEmpregado = idEmpregado;
        this.idCliente = idCliente;
        this.dataVenda = dataVenda;
        this.valorVenda = valorVenda;
        this.metodoPagamento = metodoPagamento;
    }

    public int getIdVenda() {
        return idVenda;
    }

    public void setIdVenda(int idVenda) {
        this.idVenda = idVenda;
    }

    public int getIdVeiculo() {
        return idVeiculo;
    }

    public void setIdVeiculo(int idVeiculo) {
        this.idVeiculo = idVeiculo;
    }

    public int getIdEmpregado() {
        return idEmpregado;
    }

    public void setIdEmpregado(int idEmpregado) {
        this.idEmpregado = idEmpregado;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(String dataVenda) {
        this.dataVenda = dataVenda;
    }

    public double getValorVenda() {
        return valorVenda;
    }

    public void setValorVenda(double valorVenda) {
        this.valorVenda = valorVenda;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getModeloVeiculo() {
        return modeloVeiculo;
    }

    public void setModeloVeiculo(String modeloVeiculo) {
        this.modeloVeiculo = modeloVeiculo;
    }

    public String getMarcaVeiculo() {
        return marcaVeiculo;
    }

    public void setMarcaVeiculo(String marcaVeiculo) {
        this.marcaVeiculo = marcaVeiculo;
    }

    public String getNomeEmpregado() {
        return nomeEmpregado;
    }

    public void setNomeEmpregado(String nomeEmpregado) {
        this.nomeEmpregado = nomeEmpregado;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }
}
