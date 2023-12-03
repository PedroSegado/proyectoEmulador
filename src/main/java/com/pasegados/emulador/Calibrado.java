package com.pasegados.emulador;

/**
 *
 * @author paseg
 */
public class Calibrado {
    
    private String nombre;
    private int duración;
    private int pagAna;
    private int pagAnaMen;
    private int pagCali;
    private int pagCaliMen;
    private double coefCuad;
    private double coefLin;
    private double termInd;

    public Calibrado(String nombre, int duración, int pagAna, int pagAnaMen, int pagCali, int pagCaliMen, double coefCuad, double coefLin, double termInd) {
        this.nombre = nombre;
        this.duración = duración;
        this.pagAna = pagAna;
        this.pagAnaMen = pagAnaMen;
        this.pagCali = pagCali;
        this.pagCaliMen = pagCaliMen;
        this.coefCuad = coefCuad;
        this.coefLin = coefLin;
        this.termInd = termInd;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDuración() {
        return duración;
    }

    public void setDuración(int duración) {
        this.duración = duración;
    }

    public int getPagAna() {
        return pagAna;
    }

    public void setPagAna(int pagAna) {
        this.pagAna = pagAna;
    }

    public int getPagAnaMen() {
        return pagAnaMen;
    }

    public void setPagAnaMen(int pagAnaMen) {
        this.pagAnaMen = pagAnaMen;
    }

    public int getPagCali() {
        return pagCali;
    }

    public void setPagCali(int pagCali) {
        this.pagCali = pagCali;
    }

    public int getPagCaliMen() {
        return pagCaliMen;
    }

    public void setPagCaliMen(int pagCaliMen) {
        this.pagCaliMen = pagCaliMen;
    }

    public double getCoefCuad() {
        return coefCuad;
    }

    public void setCoefCuad(double coefCuad) {
        this.coefCuad = coefCuad;
    }

    public double getCoefLin() {
        return coefLin;
    }

    public void setCoefLin(double coefLin) {
        this.coefLin = coefLin;
    }

    public double getTermInd() {
        return termInd;
    }

    public void setTermInd(double termInd) {
        this.termInd = termInd;
    }
    
    
    
}
