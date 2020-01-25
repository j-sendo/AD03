/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.accesodatos.adtarea3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author José Rosendo
 */
public class Provincias {
    ArrayList<Provincia> provincias;
    void insertarBd(Connection conexion){
        try {
            
            Statement sentenciaCreac=conexion.createStatement();
            sentenciaCreac.execute("CREATE TABLE IF NOT EXISTS Provincia(" +
                        "   id INT PRIMARY KEY NOT NULL," +
                        "   nome TEXT NOT NULL"+");");
            
            PreparedStatement sentenciaInsercion=conexion.prepareStatement("INSERT INTO Provincia VALUES (?,?);");
                    for (int i=0;i<provincias.size();i++){
                        Provincia tmp=provincias.get(i);
                        sentenciaInsercion.setInt(1, tmp.getId());
                        sentenciaInsercion.setString(2, tmp.getNome());
                        sentenciaInsercion.executeUpdate();
                    }
                    
        } catch (SQLException ex) {
            Logger.getLogger(Provincias.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
