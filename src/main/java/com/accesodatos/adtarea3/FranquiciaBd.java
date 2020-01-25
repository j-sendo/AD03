/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.accesodatos.adtarea3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

/**
 *
 * @author José Rosendo
 */
public class FranquiciaBd {
    final static Scanner ENTRADA_TECLADO=new Scanner(System.in);
        final static String[] SENTENCIAS_CREACION={
        "CREATE TABLE Cliente(\n" +
        "	id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
        "	nome TEXT NOT NULL,\n" +
        "	apelidos TEXT NOT NULL,\n" +
        "	email TEXT UNIQUE\n" +
        ");",
        "CREATE TABLE Tenda (\n" +
        "	id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
        "	nome TEXT NOT NULL,\n" +
        "	cidade TEXT NOT NULL,\n" +
        "	id_prov INTEGER NOT NULL REFERENCES Provincia (id)\n" +
        ");",
        "CREATE TABLE Producto (\n" +
        "	id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
        "	nome TEXT NOT NULL,\n" +
        "	descripcion TEXT,\n" +
        "	prezo REAL NOT NULL\n" +
        ");",
                "CREATE TABLE ProductosTenda (\n" +
        "	id_Tenda INTEGER NOT NULL REFERENCES Tenda (id) ON DELETE CASCADE,\n" +
        "	id_Producto INTEGER NOT NULL REFERENCES Producto(id) ON DELETE CASCADE,\n" +
        "	stock INTEGER NOT NULL,\n" +
        "	PRIMARY KEY (id_Tenda,id_Producto)\n" +
        ");",
                "CREATE TABLE Empregado (\n" +
        "	id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
        "	nome TEXT NOT NULL,\n" +
        "	apelidos TEXT NOT NULL\n" +
        ");",
                "CREATE TABLE Traballa (\n" +
        "	id_Tenda INTEGER NOT NULL REFERENCES Tenda(id) ON DELETE CASCADE,\n" +
        "	id_Empre INTEGER NOT NULL REFERENCES Empregado(id) ON DELETE CASCADE,\n" +
        "	horas INTEGER NOT NULL,\n" +
        "	PRIMARY KEY (id_Tenda,id_Empre)\n" +
        ");"     
    };
        
    Connection conexionBd;
    final static Properties propiedadesConex=new Properties();
    static {
        propiedadesConex.setProperty("foreign_keys", "true");
    }    
    public FranquiciaBd (File fichBd) throws SQLException, FileNotFoundException{
        if (!fichBd.exists()){               
                conexionBd=DriverManager.getConnection("jdbc:sqlite:"+fichBd,propiedadesConex);
                Gson GSON=new GsonBuilder().setPrettyPrinting().create(); 
                InputStreamReader iSR=new InputStreamReader(new FileInputStream(new File("provincias.json")),Charset.forName("UTF-8"));
                Provincias listaProv=GSON.fromJson(iSR, Provincias.class);
                listaProv.insertarBd(conexionBd);
                Statement tmpSt=conexionBd.createStatement();
                for (String sentTmp : SENTENCIAS_CREACION) {
                    tmpSt.execute(sentTmp);
                }  
                tmpSt.close();
            } else {
                conexionBd=DriverManager.getConnection("jdbc:sqlite:"+fichBd,propiedadesConex);
            }
    }
    
    public void cerrarConexBd() throws SQLException{
        conexionBd.close();
    }
    
    private Provincia getProvincia(int id) throws SQLException{
        PreparedStatement tmpPrep=conexionBd.prepareStatement("SELECT * FROM Provincia WHERE id==?");
        tmpPrep.setInt(1, id);
        ResultSet rs=tmpPrep.executeQuery();
        if (rs.next()) {
            return new Provincia(rs.getInt(1),rs.getString(2));          
        } else return null;      
    }
    
    private void listaProvincias() throws SQLException{
        Statement tmpSta=conexionBd.createStatement();
        ResultSet tmpRes=tmpSta.executeQuery("SELECT * FROM Provincia ORDER BY id;");
        System.out.printf("%-3s %-18s\n","ID","Provincia");
        while(tmpRes.next()){
            System.out.printf("%-3s %-18s\n",tmpRes.getInt(1),tmpRes.getString(2));
        }
        System.out.println();
        tmpRes.close();
        tmpSta.close();
    }
    
    
    public void insertarTenda() throws SQLException, Exception{
        Tenda tenda;
       
        System.out.print("Nome da tenda: ");
        String nome=ENTRADA_TECLADO.nextLine();

        System.out.print("Cidade: ");
        String cidade=ENTRADA_TECLADO.nextLine();

        listaProvincias();
        System.out.print("Id da provincia: ");
        int idProv=Integer.parseInt(ENTRADA_TECLADO.nextLine());
        
        tenda=new Tenda(nome, cidade, getProvincia(idProv));
        if (tenda.insertarBd(conexionBd)) System.out.println("Tenda engadida correctamente.");
        
    }
    
    private Tenda getTenda(int idTenda) throws SQLException, Exception{
        Tenda tenda;
        ResultSet tmpT=conexionBd.createStatement().executeQuery("SELECT * FROM Tenda WHERE id=="+idTenda);
        if (tmpT.next()) {
            tenda=new Tenda(tmpT.getInt(1),tmpT.getString(2),tmpT.getString(3),getProvincia(tmpT.getInt(4)));
        }
        else throw new Exception("Non existe unha tenda con ese ID.");
        tmpT.close();
        return tenda;
    }
    
    public ArrayList<Tenda> getTendas() throws SQLException{
        Statement tmpSta=conexionBd.createStatement();
        ResultSet tmpRes=tmpSta.executeQuery("SELECT * FROM Tenda;");
        Tenda tmp;
        ArrayList<Tenda> tendas=new ArrayList<>();
        while (tmpRes.next()){
            tmp=new Tenda(tmpRes.getInt(1),tmpRes.getString(2),tmpRes.getString(3),getProvincia(tmpRes.getInt(4)));
            tendas.add(tmp);
        }
        tmpSta.close();
        tmpRes.close();
        return tendas;
    }
    
    public void listadoTendas() throws SQLException, Exception{
        Statement tmpSta=conexionBd.createStatement();
        ArrayList<Tenda> tendas=getTendas();
        if (tendas.isEmpty()) throw new Exception("Non existen tendas.");
        System.out.println("Listado de tendas: ");
        System.out.printf("%-3s %-10s %-18s %-18s\n","ID","Nome","Cidade","Provincia");
        for (Tenda t:tendas){
            System.out.print(t);
        }
    }
    
    public void eliminarTenda() throws SQLException, Exception{
        System.out.print("Introduza o identificador da tenda a eliminar: ");                                   
        Tenda tendaTmp=getTenda(Integer.parseInt(ENTRADA_TECLADO.nextLine()));
        if (tendaTmp.eliminarBd(conexionBd)) System.out.println("Tenda eliminada correctamente.");
        else System.out.println("Non se eliminou ningunha tenda.");
    }
    
    public void insertarProducto() throws SQLException, Exception{
        
        System.out.print("Nome do producto: ");
        String nome=ENTRADA_TECLADO.nextLine();

        System.out.print("Descripción: ");
        String descr=ENTRADA_TECLADO.nextLine();
        System.out.print("Prezo: ");
        String prezoString=ENTRADA_TECLADO.nextLine();
        prezoString=prezoString.replace(",", ".");
        double prezo=Double.parseDouble(prezoString);

        Producto tmpProd=new Producto(nome,descr,prezo);
        if (tmpProd.insertarBd(conexionBd)) System.out.println("Producto engadido correctamente.");

    }
    public ArrayList<Producto> getProductos() throws SQLException{
        Statement tmpSta=conexionBd.createStatement();
        ResultSet tmpRes=tmpSta.executeQuery("SELECT * FROM Producto;");
        Producto tmp;
        ArrayList<Producto> productos=new ArrayList<>();
        while (tmpRes.next()){
            tmp=new Producto(tmpRes.getInt(1),tmpRes.getString(2),tmpRes.getString(3),tmpRes.getDouble(4));
            productos.add(tmp);
        }
        tmpSta.close();
        tmpRes.close();
        return productos;
    }
    public void listadoProductos() throws SQLException, Exception{
        ArrayList<Producto> productos=getProductos();
        if (productos.isEmpty()) throw new Exception("Non existen productos na base de datos.");
        System.out.println("Listado de productos: ");
        System.out.printf("%-3s %-14s %-40s %8s\n","ID","Nome","Descripción","Prezo");
        for (Producto p: productos){
            System.out.print(p);
        }
    }
    
    public void productosPorTenda(int idTenda) throws SQLException, Exception{
        comprobarTenda(idTenda);
        PreparedStatement tmpPrep=conexionBd.prepareStatement("SELECT Producto.id,Producto.nome,Producto.prezo,ProductosTenda.stock FROM Producto,ProductosTenda WHERE id_Tenda==? AND ProductosTenda.id_Producto==Producto.id;");
        tmpPrep.setInt(1, idTenda);
        ResultSet tmpRes=tmpPrep.executeQuery();
        System.out.printf("%-4s %-14s %-8s %-4s\n","ID","Nome","Prezo","Stock");
        while (tmpRes.next()){
            System.out.printf("%-4s %-14s %6.2f€ %5s\n",tmpRes.getInt(1),tmpRes.getString(2),tmpRes.getDouble(3),tmpRes.getInt(4));
        }
        tmpPrep.close();
    }
    private void comprobarTenda(int idTenda) throws SQLException, Exception{
        ResultSet tmpT=conexionBd.createStatement().executeQuery("SELECT id FROM Tenda WHERE id=="+idTenda);
        if (!tmpT.next()) throw new Exception("A tenda indicada non existe.");
    }
    public void insertarProductoTenda() throws SQLException, Exception{
        int idTmp;
        PreparedStatement tmpPrep=conexionBd.prepareStatement("INSERT INTO ProductosTenda (id_Tenda,id_Producto,stock) VALUES (?,?,?);");
        PreparedStatement tmpPrep2=conexionBd.prepareStatement("SELECT Producto.id,Producto.nome FROM Producto WHERE\n" +
                        " id NOT IN (SELECT ProductosTenda.id_Producto FROM ProductosTenda WHERE id_Tenda==?);");
                    
        listadoTendas();
        System.out.print("Introduza o identificador da tenda: "); 
        idTmp=Integer.parseInt(ENTRADA_TECLADO.nextLine());
        comprobarTenda(idTmp);
        
        tmpPrep2.setInt(1,idTmp);
        tmpPrep.setInt(1,idTmp);
                    
        ResultSet tmpRes=tmpPrep2.executeQuery();   
            if (!tmpRes.next()) {
                        System.out.println("Non existen productos que se podan engadir a tenda indicada.");
            } else {
                System.out.println("Productos que se poden engadir: ");
                do {
                    System.out.printf("%-4s %-14ss\n",tmpRes.getInt(1),tmpRes.getString(2));                
                } while (tmpRes.next());
                        
                System.out.print("Identificador do producto a engadir: ");                        
                tmpPrep.setInt(2, Integer.parseInt(ENTRADA_TECLADO.nextLine()));
                System.out.print("Stock dese producto na tenda: "); 
                int stock=Integer.parseInt(ENTRADA_TECLADO.nextLine());
                if (stock<0) throw new Exception("O stock non pode ser negativo.");
                tmpPrep.setInt(3, stock);
                    
                if (tmpPrep.executeUpdate()!=0) System.out.println("Producto engadido na tenda correctamente.");
            }
                    
            tmpPrep.close();
            tmpPrep2.close();
    }
    public void actualizarStockProductoTenda() throws SQLException, Exception{
        PreparedStatement tmpPrep,tmpPrep2;
        tmpPrep=conexionBd.prepareStatement("SELECT Producto.id,Producto.nome,ProductosTenda.stock FROM Producto,ProductosTenda WHERE id_Tenda==? AND ProductosTenda.id_Producto==Producto.id;");
        tmpPrep2=conexionBd.prepareStatement("UPDATE ProductosTenda SET stock=? WHERE id_Producto==?;");
        listadoTendas();
        System.out.print("Introduza o identificador da tenda: "); 
        tmpPrep.setInt(1, Integer.parseInt(ENTRADA_TECLADO.nextLine()));
        ResultSet tmpRes=tmpPrep.executeQuery();

        if (!tmpRes.next()) {
            System.out.println("A tenda indicada non dispón de productos ou non existe unha tenda co ID introducido.");
        } else {
            System.out.printf("%-4s %-14s %-4s\n","ID","Nome","Stock");
            do {
                System.out.printf("%-4s %-14s %-4s\n",tmpRes.getInt(1),tmpRes.getString(2),tmpRes.getInt(3));               
            } while (tmpRes.next());

            System.out.print("Identificador do producto: ");     
            tmpPrep2.setInt(2, Integer.parseInt(ENTRADA_TECLADO.nextLine()));
            System.out.print("Novo valor de stock: ");
            int stock=Integer.parseInt(ENTRADA_TECLADO.nextLine());
            if (stock<0) throw new Exception("O stock non pode ser negativo.");
            tmpPrep2.setInt(1, stock);
            if (tmpPrep2.executeUpdate()!=0) System.out.println("Stock do producto actualizado.");
            else System.out.println("Non se actualizou ningún valor, id de producto inexistente na tenda.");


        }
        tmpPrep.close();   
        tmpPrep2.close(); 
    }
    
    public void mostrarStockProductoTenda() throws SQLException, Exception{
        PreparedStatement tmpPrep,tmpPrep2;
        ResultSet tmpRes;
        tmpPrep=conexionBd.prepareStatement("SELECT Producto.id,Producto.nome,ProductosTenda.stock FROM Producto,ProductosTenda WHERE id_Tenda==? AND ProductosTenda.id_Producto==Producto.id;");
        tmpPrep2=conexionBd.prepareStatement("SELECT stock FROM ProductosTenda WHERE id_Producto==?;");
        listadoTendas();
        System.out.print("Introduza o identificador da tenda: "); 
        tmpPrep.setInt(1, Integer.parseInt(ENTRADA_TECLADO.nextLine()));
        tmpRes=tmpPrep.executeQuery();

        if (!tmpRes.next()) {
            System.out.println("A tenda indicada non dispón de productos ou non existe unha tenda co ID introducido.");
        } else {
            System.out.println("Productos: ");
            do {
                System.out.printf("%-4s %-14s\n",tmpRes.getInt(1),tmpRes.getString(2));                
            } while (tmpRes.next());

            System.out.print("Identificador do producto: ");     
            tmpPrep2.setInt(1, Integer.parseInt(ENTRADA_TECLADO.nextLine()));
            tmpRes=tmpPrep2.executeQuery();

                if (!tmpRes.next()) {
                    System.out.println("Identificador de producto incorrecto, non existe na tenda indicada.");
                } else {
                    System.out.printf("Stock dese producto na tenda: %2s\n",tmpRes.getInt(1)); 
                }

        }
        tmpPrep.close();   
        tmpPrep2.close();  
    }
    public void eliminarProductoTenda() throws SQLException, Exception{
        PreparedStatement tmpPrep,tmpPrep2;
        ResultSet tmpRes;
        int idTmp;
        tmpPrep=conexionBd.prepareStatement("DELETE FROM ProductosTenda WHERE ProductosTenda.id_Producto==? AND ProductosTenda.id_Tenda==?;");
        tmpPrep2=conexionBd.prepareStatement("SELECT Tenda.id,Tenda.nome FROM Tenda,ProductosTenda WHERE ProductosTenda.id_Tenda==Tenda.id AND ProductosTenda.id_Producto==?;");
        listadoProductos();
        System.out.print("Introduza o identificador do producto a eliminar: "); 
        idTmp=Integer.parseInt(ENTRADA_TECLADO.nextLine());
        tmpPrep2.setInt(1,idTmp);
        tmpPrep.setInt(1,idTmp);

        tmpRes=tmpPrep2.executeQuery();

        if (!tmpRes.next()) {
            System.out.println("O producto non está asociado a ningunha tenda ou non existe.");
        } else {
            System.out.println("Tendas que teñen ese producto: ");
            do {
                System.out.printf("%-4s %-14ss\n",tmpRes.getInt(1),tmpRes.getString(2));                
            } while (tmpRes.next());

            System.out.print("Identificador da tenda na que quere eliminar o producto: ");  
            int idTenda=Integer.parseInt(ENTRADA_TECLADO.nextLine());
            comprobarTenda(idTenda);
            tmpPrep.setInt(2, idTenda);            
            if (tmpPrep.executeUpdate()!=0) System.out.println("Producto eliminado da tenda correctamente.");
            else System.out.println("Non se eliminou o producto, non existe na tenda seleccionada.");
        }

        tmpPrep.close();
        tmpPrep2.close();  
    }
    public boolean eliminarProducto(int idProd) throws SQLException{                                 
        PreparedStatement tmpPrep=conexionBd.prepareStatement("DELETE FROM Producto WHERE id==?;");
        tmpPrep.setInt(1,idProd);
        boolean resultado;
        if (tmpPrep.executeUpdate()!=0) resultado=true;
        else resultado=false;
        tmpPrep.close(); 
        return resultado;
    }

    public void engadirCliente() throws SQLException, Exception {

        System.out.print("Nome do cliente: ");
        String nome=ENTRADA_TECLADO.nextLine();

        System.out.print("Apelidos do cliente: ");
        String apelidos=ENTRADA_TECLADO.nextLine();

        System.out.print("email do cliente: ");
        String email=ENTRADA_TECLADO.nextLine();

        Cliente clienteTmp=new Cliente(nome,apelidos,email);
        
        if (clienteTmp.insertarBd(conexionBd)) System.out.println("Cliente engadido correctamente.");

    }

    public void listaClientes() throws SQLException, Exception {
        ArrayList<Cliente> clientes=getClientes();
        System.out.println("Listado de clientes: ");
        if (clientes.isEmpty()) throw new Exception("Non existen clientes na base de datos.");
        System.out.printf("%-3s %-14s %-28s %-35s\n","ID","Nome","Apelidos","e-mail");
        for (Cliente cliente:clientes){
            System.out.print(cliente);
        }
    }
    
    boolean eliminarCliente(int idCliente) throws SQLException {
        boolean resultado;
        PreparedStatement tmpPrep=conexionBd.prepareStatement("DELETE FROM Cliente WHERE id==?;");
        tmpPrep.setInt(1, idCliente);
        resultado = tmpPrep.executeUpdate()!=0;
        tmpPrep.close();
        return resultado;
    }
    
    public ArrayList<Cliente> getClientes() throws SQLException{
        Statement tmpSta=conexionBd.createStatement();
        ResultSet tmpRes=tmpSta.executeQuery("SELECT id,nome,apelidos,email FROM Cliente;");
        Cliente tmp;
        ArrayList<Cliente> clientes=new ArrayList<>();
        while (tmpRes.next()){
            tmp=new Cliente(tmpRes.getInt(1),tmpRes.getString(2),tmpRes.getString(3),tmpRes.getString(4));
            clientes.add(tmp);
        }
        return clientes;
    }
    void engadirEmpregado() throws SQLException, Exception{
        Empregado empregado;
        System.out.print("Nome do empregado: ");
        String nome=ENTRADA_TECLADO.nextLine();
        System.out.print("Apelidos do empregado: ");
        String apelido=ENTRADA_TECLADO.nextLine();
        empregado=new Empregado(nome,apelido);
        if (empregado.insertarBd(conexionBd)) System.out.println("Empregado engadido correctamente.");
    }
    public ArrayList<Empregado> getEmpregados() throws SQLException{
        Statement tmpSta=conexionBd.createStatement();
        ResultSet tmpRes=tmpSta.executeQuery("SELECT * FROM Empregado;");
        Empregado tmp;
        ArrayList<Empregado> empregados=new ArrayList<>();
        while (tmpRes.next()){
            tmp=new Empregado(tmpRes.getInt(1),tmpRes.getString(2),tmpRes.getString(3));
            empregados.add(tmp);
        }
        tmpSta.close();
        tmpRes.close();
        return empregados;
    }
    public ArrayList<Empregado> getEmpregadosTenda(int id_Tenda) throws SQLException, Exception{
        comprobarTenda(id_Tenda);
        PreparedStatement tmpPre=conexionBd.prepareStatement("SELECT * FROM Empregado WHERE id IN (SELECT id_Empre FROM Traballa WHERE id_Tenda==?);");
        tmpPre.setInt(1, id_Tenda);
        Empregado tmp;
        ArrayList<Empregado> empregados=new ArrayList<>();
        ResultSet tmpRes=tmpPre.executeQuery();
        while (tmpRes.next()){
            tmp=new Empregado(tmpRes.getInt(1),tmpRes.getString(2),tmpRes.getString(3));
            empregados.add(tmp);
        }
        tmpRes.close();
        tmpPre.close();
        return empregados;
    }
    public void listaEmpregados() throws SQLException, Exception {
        ArrayList<Empregado> empregados=getEmpregados();
        if (empregados.isEmpty()) throw new Exception("Non existen empregados na base de datos.");
        System.out.println("Lista de empregados: ");
        System.out.printf("%-3s %-14s %-28s\n","ID","Nome","Apelidos");
        for (Empregado e:empregados){
            System.out.print(e);
        }
    }
    boolean eliminarEmpregado(int idEmp) throws SQLException, Exception {
        boolean resultado;
        Empregado empregado=getEmpregado(idEmp); 
        resultado = empregado.eliminarBd(conexionBd);
        return resultado;
    }

    private void comprobarEmpregado(int idEmp) throws SQLException, Exception{
        ResultSet tmpT=conexionBd.createStatement().executeQuery("SELECT id FROM Empregado WHERE id=="+idEmp);
        if (!tmpT.next()) throw new Exception("Non existe un empregado co ID introducido.");
        tmpT.close();
    }
    private Empregado getEmpregado(int idEmp) throws SQLException, Exception{
        Empregado empregado;
        ResultSet tmpT=conexionBd.createStatement().executeQuery("SELECT * FROM Empregado WHERE id=="+idEmp);
        if (tmpT.next()) {
            empregado=new Empregado(tmpT.getInt(1),tmpT.getString(2),tmpT.getString(3));
        }
        else throw new Exception("Non existe un empregado co ID introducido.");
        tmpT.close();
        return empregado;
    }
    
    void insertarEmpregadoTenda() throws SQLException, Exception {
        PreparedStatement insertEmpregadoTenda=conexionBd.prepareStatement("INSERT INTO Traballa VALUES(?,?,?)");
        PreparedStatement tmpPrep=conexionBd.prepareStatement("SELECT Tenda.id,Tenda.nome FROM Tenda WHERE Tenda.id NOT IN (SELECT id_Tenda FROM Traballa WHERE id_Empre==?);");
        listaEmpregados();
        
        System.out.print("ID do empregado a incluir como traballador nunha tenda: ");
        int idEmp=Integer.parseInt(ENTRADA_TECLADO.nextLine());     
        comprobarEmpregado(idEmp);
        insertEmpregadoTenda.setInt(2, idEmp);
        tmpPrep.setInt(1, idEmp);
        
        ResultSet tmpRes=tmpPrep.executeQuery();
        if (!tmpRes.next()) {
            System.out.println("Non existen tendas nas que poder incluir o empregado.");
        } else {
            System.out.println("Tendas nas que se pode incluir o traballador: ");
            do {
                System.out.printf("%-4s %-14s\n",tmpRes.getInt(1),tmpRes.getString(2));                
            } while (tmpRes.next());
            tmpRes.close();
            tmpPrep.close();
            System.out.print("Identificador da tenda na que quere incluir o traballador: ");  
            int idTenda=Integer.parseInt(ENTRADA_TECLADO.nextLine());
            comprobarTenda(idTenda);
            insertEmpregadoTenda.setInt(1, idTenda);  
            
            System.out.print("Número de horas de traballo semanais: "); 
            int horas=Integer.parseInt(ENTRADA_TECLADO.nextLine());
            if (horas<=0) throw new Exception("Erro. O número de horas debe ser un enteiro positivo.");
            insertEmpregadoTenda.setInt(3, horas);
            
            if (insertEmpregadoTenda.executeUpdate()!=0) System.out.println("Empregado incluido na tenda correctamente");
            insertEmpregadoTenda.close();  
        }      
    }

    void eliminarEmpregadoTenda() throws SQLException, Exception {
        PreparedStatement tmpPrep;
        tmpPrep=conexionBd.prepareStatement("DELETE FROM Traballa WHERE id_Tenda==? AND id_Empre==?;");
        listadoTendas();
        System.out.print("Introduza o identificador da tenda: "); 
        int id_Tenda=Integer.parseInt(ENTRADA_TECLADO.nextLine());
        ArrayList<Empregado> empregados=getEmpregadosTenda(id_Tenda);
        
        
        if(empregados.isEmpty()) System.out.println("A tenda indicada non ten ningún empregado asignado.");     
        else {
            tmpPrep.setInt(1, id_Tenda);
            System.out.println("Lista de empregados asignados a tenda: ");
            System.out.printf("%-3s %-14s %-28s\n","ID","Nome","Apelidos");
            for (Empregado e:empregados){
                System.out.print(e);
            }           
            System.out.print("Identificador do empregado a eliminar da tenda: ");     
            tmpPrep.setInt(2, Integer.parseInt(ENTRADA_TECLADO.nextLine()));

            if (tmpPrep.executeUpdate()!=0) System.out.println("Empregado eliminado da tenda indicada.");
            else System.out.println("Non existe o empregado co id indicado na tenda");
        }
        tmpPrep.close();   
    }
  
}
