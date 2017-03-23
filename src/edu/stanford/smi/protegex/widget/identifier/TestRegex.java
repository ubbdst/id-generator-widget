/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.stanford.smi.protegex.widget.identifier;

import java.util.regex.Pattern;

/**
 *
 * @author hru066
 */
public class TestRegex {
    
    public static void main(String[] args)
    {
        String s = "http://xmlns.com/foaf/0.1/#Person";
        String v = s.replaceAll("^.+?([^/#]+)$", "$1"); 
        
        boolean b = Pattern.matches("", "1998");
        System.out.print(v);
        //"^.+?
    }
    
}
