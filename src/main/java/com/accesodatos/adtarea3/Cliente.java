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
public class Cliente {
    int id;
    String nome,apelidos,email;
    public Cliente (String nome, String apelidos, String email) throws Exception{
        if (nome.isEmpty()||email.isEmpty()) throw new Exception("Erro. Non se introduciron os datos requeridos.");
        if (!email.matches("[\\p{Alnum}-_+.]+@([\\p{Alnum}-]+\\.)+\\p{Alpha}+")) throw new Exception("Erro. Formato email incorrecto.");
        this.nome=nome;
        this.apelidos=apelidos;
        this.email=email;
    }
    public Cliente (int id, String nome, String apelidos, String email) {
        this.id=id;
        this.nome=nome;
        this.apelidos=apelidos;
        this.email=email;
    }
    @Override
    public String toString(){
        return String.format("%-3s %-14s %-28s %-35s\n",id,nome,apelidos,email);
    }
    public boolean insertarBd(Connection conex) throws SQLException{
        boolean resultado;
        PreparedStatement tmpPrep;
        tmpPrep=conex.prepareStatement("INSERT INTO Cliente (nome,apelidos,email) VALUES (?,?,?);");
        tmpPrep.setString(1, nome);
        tmpPrep.setString(2, apelidos);
        tmpPrep.setString(3, email);
        resultado=tmpPrep.executeUpdate()!=0;
        tmpPrep.close();
        return resultado;
    }
}

