/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.train;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner; 
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.*;
/**
 *
 * @author Youness Haraki
 */
public class DAO {
    private final static String RESOURCES_PATH = "src/main/java/resources/";
    private final static String TRAIN_FILE_NAME= "train.csv";
    private final static String TEST_FILE_NAME= "test.csv"; 
    
    private final ArrayList<String> null_lines;
    private Instances data;
    public DAO(){ 
        null_lines= new ArrayList<>();
    }
    public List<Passenger> Get_train(){
        try {
            return findPassengers(TRAIN_FILE_NAME);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }
    public List<Passenger> Get_test(){
        try {
            return findPassengers(TEST_FILE_NAME);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return null;
        } 
    }
    public List<Passenger> findPassengers(String FILE_NAME) throws FileNotFoundException {
        try {
            Extract_Convert_to_arf(FILE_NAME);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        List< List<String> > data= find_data(FILE_NAME);
        List<Passenger> passengers = new ArrayList<>();
        data.stream().map(oneData -> { 
            Passenger passenger=null;
            int Survived=-1;
            int index=0;
            int PassengerId = Integer.parseInt(oneData.get(index));
            if (FILE_NAME.equals("train.csv"))
                Survived=Integer.parseInt(oneData.get(++index));
            int Pclass=Integer.parseInt(oneData.get(++index));
            String Name=oneData.get(++index);
            String gender=oneData.get(++index);
            float Age=Float.parseFloat(oneData.get(++index));
            int SibSp=Integer.parseInt(oneData.get(++index));
            int Parch=Integer.parseInt(oneData.get(++index));
            String Ticket=oneData.get(++index);
            float Fare=Float.parseFloat(oneData.get(++index));
            String Cabin=oneData.get(++index);
            String Embarked=oneData.get(++index);
            if (FILE_NAME.equals("train.csv"))    
                passenger = new Passenger(PassengerId, Survived, Pclass, Name, gender, Age, SibSp, Parch, Ticket, Fare, Cabin, Embarked);
            else
                passenger = new Passenger(PassengerId, Pclass, Name, gender, Age, SibSp, Parch, Ticket, Fare, Cabin, Embarked);
            return passenger;
        }).forEachOrdered(passenger -> {
            passengers.add(passenger);
        });
        return passengers;
    } 

    public static String getRESOURCES_PATH() {
        return RESOURCES_PATH;
    }

    public static String getTRAIN_FILE_NAME() {
        return TRAIN_FILE_NAME;
    }

    public static String getTEST_FILE_NAME() {
        return TEST_FILE_NAME;
    }
    
    public void Extract_Convert_to_arf(String FILE_NAME) throws Exception{
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File(RESOURCES_PATH+FILE_NAME));
            data = loader.getDataSet();
            // We want to keep all the rows in test set, in order to submit the results for scoring
            if (FILE_NAME.equals("test.csv")){
                String[] opt = new String[]{"-R","5"};
                ReplaceMissingValues replace_val = new ReplaceMissingValues();
                replace_val.setOptions(opt);
                replace_val.setInputFormat(data);
                data = Filter.useFilter(data,replace_val); 
            }
            // In the training data we remove the rows containing null values in important attributes
            if (FILE_NAME.equals("train.csv"))
            {
                data.deleteWithMissing(data.attribute("Survived")); 
                //class
                data.deleteWithMissing(data.attribute("Pclass")); 
                //gender
                //Age
                data.deleteWithMissing(data.attribute("Age"));
                //Fare
                data.deleteWithMissing(data.attribute("Fare"));
                data.deleteWithMissing(data.attribute("Sex"));  
                //Siblings
                data.deleteWithMissing(data.attribute("SibSp")); 
                //Paents-Children
                data.deleteWithMissing(data.attribute("Parch")); 
                //Cabin need to be ignored- in this attribute too many rows have null values

                //data.deleteWithMissing(data.attribute("Cabin")); 
                //Embarked
                data.deleteWithMissing(data.attribute("Embarked"));
            }
            // We save in arff format- in order to use classification methods with our data
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data); 
            saver.setFile(new File(RESOURCES_PATH+FILE_NAME.replace("csv", "arff")));
            saver.writeBatch();
            System.out.println("File:"+FILE_NAME+" successfully converted!"); 
            data.setClassIndex(1);
    }

    public Instances getData() {
        return data;
    }
    
    public List< List<String> > find_data(String FILE_NAME){
        List< List<String> > data = new ArrayList<>();
        List<String> myList = new ArrayList<>();
        try {
        File myObj = new File(RESOURCES_PATH+FILE_NAME.replace("csv", "arff"));
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
             String line = myReader.nextLine();
             if(line.startsWith("@"))
                 continue;
             if (line.length() == 0) {
                continue;
            } 
             myList.add(line);
        }
         myReader.close(); 
        } catch (FileNotFoundException ex) {
            System.out.println("Can't read .arff file!");
            System.out.println(ex.getMessage());
        }
        
        for (String s:myList){
            myList = new ArrayList<>(Arrays.asList((s.substring(0, s.length())).split(","))); 
            data.add(myList);
        }
        return data;
    }
}



















