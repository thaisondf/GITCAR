package com.gitcar.app.models;

public class InteresseCliente {

    private int id;
    private String nomeCliente;
    private String infoContato;
    private String interesseVeiculo;
    private String observacoes;

    public InteresseCliente(int id, String nomeCliente, String infoContato, String interesseVeiculo, String observacoes) {
        this.id = id;
        this.nomeCliente = nomeCliente;
        this.infoContato = infoContato;
        this.interesseVeiculo = interesseVeiculo;
        this.observacoes = observacoes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getInfoContato() {
        return infoContato;
    }

    public void setInfoContato(String infoContato) {
        this.infoContato = infoContato;
    }

    public String getInteresseVeiculo() {
        return interesseVeiculo;
    }

    public void setInteresseVeiculo(String interesseVeiculo) {
        this.interesseVeiculo = interesseVeiculo;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    @Override
    public String toString() {
        return "Interesse [id=" + id + ", nomeCliente=" + nomeCliente + ", interesseVeiculo=" + interesseVeiculo + "]";
    }
}
