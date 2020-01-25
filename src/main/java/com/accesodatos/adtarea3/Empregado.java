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
public class Empregado {
    int id;
    String nome,apelidos;

    public Empregado(int id, String nome, String apelidos) {
        this.id = id;
        this.nome = nome;
        this.apelidos = apelidos;
    }

    public Empregado(String nome, String apelidos) throws Exception {
        if (nome.isEmpty()||apelidos.isEmpty()) throw new Exception("Erro. Non se introduciron os datos requeridos.");
        this.nome = nome;
        this.apelidos = apelidos;
    }
    public boolean insertarBd(Connection conex) throws SQLException{
        boolean resultado;
        PreparedStatement tmpPrep=conex.prepareStatement("INSERT INTO Empregado (nome,apelidos) VALUES (?,?);");
        tmpPrep.setString(1, nome);
        tmpPrep.setString(2, apelidos);
        if (tmpPrep.executeUpdate()!=0) resultado=true;
        else resultado=false;
        tmpPrep.close();
        return resultado;
    }

    public boolean eliminarBd(Connection conex) throws SQLException{
        boolean resultado;
        PreparedStatement tmpPrep=conex.prepareStatement("DELETE FROM Empregado WHERE id==?;");
        tmpPrep.setInt(1, id);
        if (tmpPrep.executeUpdate()!=0) resultado=true;
        else resultado=false;
        tmpPrep.close();
        return resultado;
    }
    @Override
    public String toString() {
        return String.format("%-3s %-14s %-28s\n",id,nome,apelidos);
    }
    
}
