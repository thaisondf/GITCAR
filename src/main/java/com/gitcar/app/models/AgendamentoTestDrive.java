package com.gitcar.app.models;

public class AgendamentoTestDrive {

    private int idTestDrive;
    private int idVeiculo;
    private int idEmpregado;
    private String nomeCliente;
    private String contatoCliente;
    private String dataHoraTestDrive;
    private String status;
    private String infoVeiculo;
    private String nomeEmpregado;

    public AgendamentoTestDrive(int idVeiculo, int idEmpregado, String nomeCliente, String contatoCliente, String dataHoraTestDrive) {
        this.idVeiculo = idVeiculo;
        this.idEmpregado = idEmpregado;
        this.nomeCliente = nomeCliente;
        this.contatoCliente = contatoCliente;
        this.dataHoraTestDrive = dataHoraTestDrive;
        this.status = "Agendado";
    }
    public AgendamentoTestDrive(int idTestDrive, int idVeiculo, int idEmpregado, String nomeCliente, String contatoCliente, String dataHoraTestDrive, String status) {
        this.idTestDrive = idTestDrive;
        this.idVeiculo = idVeiculo;
        this.idEmpregado = idEmpregado;
        this.nomeCliente = nomeCliente;
        this.contatoCliente = contatoCliente;
        this.dataHoraTestDrive = dataHoraTestDrive;
        this.status = status;
    }
    public int getIdTestDrive() {
        return idTestDrive;
    }

    public void setIdTestDrive(int idTestDrive) {
        this.idTestDrive = idTestDrive;
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

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getContatoCliente() {
        return contatoCliente;
    }

    public void setContatoCliente(String contatoCliente) {
        this.contatoCliente = contatoCliente;
    }

    public String getDataHoraTestDrive() {
        return dataHoraTestDrive;
    }

    public void setDataHoraTestDrive(String dataHoraTestDrive) {
        this.dataHoraTestDrive = dataHoraTestDrive;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfoVeiculo() {
        return infoVeiculo;
    }

    public void setInfoVeiculo(String infoVeiculo) {
        this.infoVeiculo = infoVeiculo;
    }

    public String getNomeEmpregado() {
        return nomeEmpregado;
    }

    public void setNomeEmpregado(String nomeEmpregado) {
        this.nomeEmpregado = nomeEmpregado;
    }
}