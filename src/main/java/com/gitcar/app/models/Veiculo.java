package com.gitcar.app.models;

public class Veiculo {

    private int id;
    private String modelo;
    private String marca;
    private int ano;
    private int km;
    private String categoria;
    private double preco;
    private String cor;
    private String combustivel;
    private String placa;
    private String cambio;
    private String status;

    public Veiculo(int id, String modelo, String marca, int ano, int km, String categoria, double preco,
                   String cor, String combustivel, String placa, String cambio, String status) {
        this.id = id;
        this.modelo = modelo;
        this.marca = marca;
        this.ano = ano;
        this.km = km;
        this.categoria = categoria;
        this.preco = preco;
        this.cor = cor;
        this.combustivel = combustivel;
        this.placa = placa;
        this.cambio = cambio;
        this.status = status;
    }

    public Veiculo(String modelo, String marca, int ano, int km, String categoria, double preco,
                   String cor, String combustivel, String placa, String cambio, String status) {
        this.modelo = modelo;
        this.marca = marca;
        this.ano = ano;
        this.km = km;
        this.categoria = categoria;
        this.preco = preco;
        this.cor = cor;
        this.combustivel = combustivel;
        this.placa = placa;
        this.cambio = cambio;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getKm() {
        return km;
    }

    public void setKm(int km) {
        this.km = km;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getCombustivel() {
        return combustivel;
    }

    public void setCombustivel(String combustivel) {
        this.combustivel = combustivel;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getCambio() {
        return cambio;
    }

    public void setCambio(String cambio) {
        this.cambio = cambio;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return id + " - " + marca + " " + modelo + " (" + ano + ") - Status: " + status;
    }
}
