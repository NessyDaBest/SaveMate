package org.example.savemate.model;

public class Cuenta {
    private int idCuenta;
    private String nombre;
    private String banco;
    private double saldoInicial;

    public Cuenta(int idCuenta, String nombre, String banco, double saldoInicial) {
        this.idCuenta = idCuenta;
        this.nombre = nombre;
        this.banco = banco;
        this.saldoInicial = saldoInicial;
    }

    public int getIdCuenta() {
        return idCuenta;
    }

    public String getNombre() {
        return nombre;
    }

    public String getBanco() {
        return banco;
    }

    public double getSaldoInicial() {
        return saldoInicial;
    }
    public void setSaldoInicial(double saldoInicial) {
        this.saldoInicial = saldoInicial;
    }
}