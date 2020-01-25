/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.accesodatos.adtarea3;

import static com.accesodatos.adtarea3.FranquiciaBd.ENTRADA_TECLADO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 *
 * @author José Rosendo
 */
public class Tenda {
    String nome,cidade;
    int id;
    Provincia prov;
    //ArrayList<Producto> productos=new ArrayList<>();
    //LinkedHashSet<Producto> productos=new LinkedHashSet<>();
    //ArrayList<Empregado> empregados=new ArrayList<>();
    public Tenda (String nome, String cidade, Provincia prov) throws Exception{
        if (nome.isEmpty()||cidade.isEmpty()) throw new Exception("Erro. Nome e cidade son campos obrigatorios.");
        else if (prov==null) throw new Exception("Erro. Non existe a provincia.");
        this.nome=nome;
        this.cidade=cidade;
        this.prov=prov;
    }
    public Tenda (int id,String nome, String cidade, Provincia prov) {
        this.id=id;
        this.nome=nome;
        this.cidade=cidade;
        this.prov=prov;
    }
    public boolean insertarBd(Connection conex) throws SQLException{
        boolean resultado;
        PreparedStatement tmpPrep=conex.prepareStatement("INSERT INTO Tenda (nome,cidade,id_prov) VALUES (?,?,?);");
        tmpPrep.setString(1, nome);
        tmpPrep.setString(2, cidade);
        tmpPrep.setInt(3, prov.id);
        if (tmpPrep.executeUpdate()!=0) resultado=true;
        else resultado=false;
        tmpPrep.close();
        return resultado;
    }
    public boolean eliminarBd(Connection conex) throws SQLException{
        boolean resultado;
        PreparedStatement tmpPrep=conex.prepareStatement("DELETE FROM Tenda WHERE id==?;");
        tmpPrep.setInt(1, id);
        if (tmpPrep.executeUpdate()!=0) resultado=true;
        else resultado=false;
        tmpPrep.close();
        return resultado;
    }
    @Override
    public boolean equals(Object a){
        if (this.id==((Tenda)a).id) return true;
        else return false;
        
    }

    @Override
    public String toString(){
        return String.format("%-3s %-10s %-18s %-18s\n",id,nome,cidade,prov.nome);
    }
    /**
    public boolean engadirProducto(Producto producto){
        return productos.add(producto);
    }
    public boolean eliminarProducto(Producto producto){
        return productos.remove(producto);
    }
    public boolean engadirEmpregado(Empregado empregado){
        return empregados.add(empregado);
    }
    public boolean eliminarEmpregado(Empregado empregado){
        return empregados.remove(empregado);
    }
    public void mostrarEmpregados(){
        for (int i=0;i<empregados.size();i++){
            System.out.println(i+" - "+empregados.get(i));
        }
    }
    public void mostrarProductos(){
        /*for (int i=0;i<productos.size();i++){
            System.out.println(productos.get(i));
        }
        for (Producto a:productos){
            System.out.println(a);
        }
    }*/
}
