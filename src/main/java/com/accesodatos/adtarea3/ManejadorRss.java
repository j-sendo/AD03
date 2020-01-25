/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.accesodatos.adtarea3;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author José Rosendo
 */
public class ManejadorRss extends DefaultHandler{

    @Override
    public void endDocument() throws SAXException {
        System.out.println("--------------------------------------------------------------------\n");
    }

    @Override
    public void startDocument() throws SAXException {
        System.out.println("\n-------------------------Titulares el País-------------------------");
    }
    public boolean titulo=false;
    public boolean item=false;
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
       if (item&&titulo) {
           System.out.println(new String(ch, start, length));
       }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        titulo=false;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("title")) titulo=true;
        else if (qName.equals("item")) item=true;
    }
    
}
