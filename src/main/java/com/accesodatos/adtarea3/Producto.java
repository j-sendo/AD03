/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.accesodatos.adtarea3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author José Rosendo
 */
public class Producto {
    int id;
    String nome, descripcion;
    double prezo;

    public Producto(String nome, String descripcion, double prezo) throws Exception {
        if (nome.isEmpty()) throw new Exception("Erro. O campo nome é obrigatorio.");
        else if (prezo<=0) throw new Exception("Erro. O prezo debe ser unha cantidade positiva.");
        this.nome = nome;
        this.descripcion = descripcion;
        this.prezo = prezo;
    }

    public Producto(int id, String nome, String descripcion, double prezo) {
        this.id = id;
        this.nome = nome;
        this.descripcion = descripcion;
        this.prezo = prezo;
    }
    
    public boolean insertarBd(Connection conex) throws SQLException{
        PreparedStatement tmpPrep=conex.prepareStatement("INSERT INTO Producto (nome,descripcion,prezo) VALUES (?,?,?);");
        boolean resultado;
        tmpPrep.setString(1, nome);
        tmpPrep.setString(2, descripcion);
        tmpPrep.setDouble(3, prezo);
        if (tmpPrep.executeUpdate()!=0) resultado=true;
        else resultado=false;
        tmpPrep.close();
        return resultado;
    }

    @Override
    public String toString() {
        return String.format("%-3s %-14s %-40s %6.2f€\n",id,nome,descripcion,prezo);
    }
    
}
