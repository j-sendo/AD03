/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.accesodatos.adtarea3;

/**
 *
 * @author José Rosendo
 */
public class Provincia {
    int id;

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
    String nome;

    @Override
    public String toString() {
        return String.format("%-3s %-18s\n",id,nome);
    }

    public Provincia(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }
}
