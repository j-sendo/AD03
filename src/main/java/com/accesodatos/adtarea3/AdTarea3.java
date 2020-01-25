/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.accesodatos.adtarea3;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author José Rosendo
 */
public class AdTarea3 {
    final static Scanner ENTRADA_TECLADO=new Scanner(System.in);
    final static File FICHEIRO=new File("datos.json");
    
    static int opcion;
    
    private static void obtenerTitularesElPais() {
        URL elpais;
        try {
            elpais = new URL("http://ep00.epimg.net/rss/elpais/portada.xml");
            SAXParserFactory spf=SAXParserFactory.newInstance();
            SAXParser sp=spf.newSAXParser();
            ManejadorRss manejador=new ManejadorRss();
            sp.parse(elpais.openStream(), manejador);
                        
        } catch (MalformedURLException | SAXException | ParserConfigurationException ex) {
            Logger.getLogger(AdTarea3.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.err.println("Erro lectura RSS El país, revise a conexión a internet.");
        }
    }
    private static void mostrarMenu(){
        System.out.println("Operacións dispoñibles:");
        System.out.println("1.-Engadir unha tenda");
        System.out.println("2.-Mostrar as tendas");
        System.out.println("3.-Eliminar unha tenda");
        System.out.println("4.-Engadir un producto");
        System.out.println("5.-Mostrar todos os productos");
        System.out.println("6.-Mostrar os productos dunha tenda");
        System.out.println("7.-Engadir un producto a unha tenda");
        System.out.println("8.-Actualizar o stock dun producto nunha tenda");
        System.out.println("9.-Mostrar o stock dun producto dunha tenda");
        System.out.println("10.-Eliminar un producto dunha determinada tenda");
        System.out.println("11.-Eliminar un producto");
        System.out.println("12.-Engadir un cliente");
        System.out.println("13.-Mostrar os clientes");
        System.out.println("14.-Eliminar un cliente");
        System.out.println("15.-Engadir un empregado");
        System.out.println("16.-Engadir un empregado a unha tenda");
        System.out.println("17.-Mostrar lista de empregados.");
        System.out.println("18.-Eliminar un empregado.");
        System.out.println("19.-Eliminar un empregado dunha tenda.");
        System.out.println("20.-Titulares do periódico El País");
        System.out.println("0.-Saír da aplicación.");
        System.out.print("Indique a operación desexada: ");
    }



    private static void continuar(){
        System.out.print("\nPulse Enter para continuar....");
        ENTRADA_TECLADO.nextLine();
        System.out.println("\n");
    }
    public static void main(String[] args) throws IOException, SQLException {
        File fichBd=new File("franquicia.db");
        FranquiciaBd conexionBd=new FranquiciaBd(fichBd);
                
        while(true){
            try {   
            mostrarMenu();
            opcion=Integer.parseInt(ENTRADA_TECLADO.nextLine());
            switch(opcion){
                case 1:
                    conexionBd.insertarTenda();
                break;
                case 2:
                    conexionBd.listadoTendas();
                break;
                case 3:
                    conexionBd.eliminarTenda();                   
                break;
                case 4:
                    conexionBd.insertarProducto();
                break;
                case 5:
                    conexionBd.listadoProductos();

                break;
                case 6:
                    conexionBd.listadoTendas();
                    System.out.print("Introduza o identificador da tenda: "); 
                    int idTenda=Integer.parseInt(ENTRADA_TECLADO.nextLine());
                    conexionBd.productosPorTenda(idTenda);
                break;
                case 7:
                    conexionBd.insertarProductoTenda();
                break;
                case 8:
                    conexionBd.actualizarStockProductoTenda();
                break;
                case 9:
                    conexionBd.mostrarStockProductoTenda();
                break;
                case 10:
                    conexionBd.eliminarProductoTenda();
                break;
                case 11:
                    System.out.print("Introduza o identificador do producto a eliminar: ");     
                    int idProd=Integer.parseInt(ENTRADA_TECLADO.nextLine());
                    if (conexionBd.eliminarProducto(idProd)) System.out.println("Producto eliminado correctamente."); 
                    else System.out.println("Non se eliminou ningún producto. Revise o id introducido.");
                break;
                case 12:
                    conexionBd.engadirCliente();
                break;
                case 13:
                    conexionBd.listaClientes();
                break;
                case 14:
                     conexionBd.listaClientes();
                     System.out.print("Introduza o identificador do cliente a eliminar: ");
                     int idCliente=Integer.parseInt(ENTRADA_TECLADO.nextLine());
                     if (conexionBd.eliminarCliente(idCliente)) System.out.println("Cliente eliminado correctamente.");
                     else System.out.println("Non se eliminou ningún cliente. Comprobe o id introducido.");
                     
                break;   
                case 15:
                    conexionBd.engadirEmpregado();
                    break;
                case 16:
                    conexionBd.insertarEmpregadoTenda();
                    break;
                case 17:
                    conexionBd.listaEmpregados();
                    break;
                case 18:
                    conexionBd.listaEmpregados();
                    System.out.print("Introduza o identificador do empregado a eliminar: ");
                    int idEmp=Integer.parseInt(ENTRADA_TECLADO.nextLine());
                    if (conexionBd.eliminarEmpregado(idEmp)) System.out.println("Empregado eliminado correctamente.");  
                break;
                case 19:
                    conexionBd.eliminarEmpregadoTenda();
                break;
                case 20:
                    obtenerTitularesElPais();
                break;
                case 0:
                    conexionBd.cerrarConexBd();
                    System.exit(0);
                break;
                default:
                    System.out.println("Non existe a opción introducida.");
                break;
                
            }

            } catch (SQLException e){
                StackTraceElement[] st=e.getStackTrace();
                if (e.getMessage().contains("UNIQUE constraint failed: Cliente.email")) System.err.println("\nErro, xa existe un cliente co email indicado.");
                else if (st[5].getMethodName().equals("insertarProductoTenda")) System.err.println("\nErro. O producto correspondente o id introducido non existe ou non se pode engadir a tenda seleccionada.");
                else if (st[5].getMethodName().equals("actualizarStockProductoTenda")) System.err.println("\nErro. O producto correspondente o id introducido non existe na tenda seleccionada.");
                else if (st[5].getMethodName().equals("insertarTenda")) System.err.println("\nErro. Non existe unha provincia co id introducido.");
                else if (st[5].getMethodName().equals("insertarEmpregadoTenda")) System.err.println("\nErro. O empregado xa traballa na tenda seleccionada.");
                //e.printStackTrace();

            }catch (java.lang.NumberFormatException e){
                StackTraceElement[] st=e.getStackTrace();
                if (st[2].getClassName().contains("Integer")) System.err.println("Valor introducido incorrecto, esperábase un número enteiro.");   
                else if ((st[2].getClassName().contains("Double")))  System.err.println("Valor introducido incorrecto, esperábase un número real.");  
                else System.err.println("Valor introducido incorrecto."); 

            }catch (java.lang.IndexOutOfBoundsException e){
                System.err.println("Selección incorrecta.");    
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            } finally {
                continuar();
            }
            


        }
    }
    
}
