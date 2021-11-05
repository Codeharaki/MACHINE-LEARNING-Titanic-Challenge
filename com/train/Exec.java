/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.train;
 
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Youness Haraki
 */
public class Exec {
    public static void main(String[] args) {
        
        DAO dao_Passengers =new DAO();
        List<Passenger> passengers_train = new ArrayList<>();
        List<Passenger> passengers_test = new ArrayList<>();
        
        //Set of passengers having no null values on needed features
        //------ Train data
        passengers_train=dao_Passengers.Get_train();
        //System.out.println(passengers_train); //Using toString() to print passengers- train set
        
        //------ Test data
        passengers_test=dao_Passengers.Get_test();
       // System.out.println(passengers_test); //Using toString() to print passengers- test set
        
        //**Decision tree
        DecisionTree D_tree= new DecisionTree();
        D_tree.classify_val();
    }
}
