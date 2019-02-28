/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.reporting;

/**
 *
 * @author cmeehan
 */
public class ErrorReport {
    
    public ErrorReport(){
        
    }
    
    public ErrorReport(String message){
        System.err.println(message);
    }
    
    public ErrorReport(String message, StackTraceElement[] stackTrace){
        System.err.println(message);
        System.err.println(stackTrace);
    }
}
