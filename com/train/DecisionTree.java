/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.train;
//import weka.classifiers.trees.Id3; 

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList; 
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48; 
import weka.core.Attribute; 
import weka.core.Instances; 
import weka.core.converters.ArffLoader; 
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.NumericToBinary;

/**
 *
 * @author youne
 */
public class DecisionTree { 
    private Instances train_Set;
    private Instances test_Set;
    private Classifier classifier;
    private double[] IdPassenger;
    public DecisionTree() {
        train_Set=null;
        test_Set=null;
        IdPassenger=null;
    }
    public void classify_val() {
        this.classify();
        try {
            System.out.println(this.evaluate());
        } catch (Exception ex) {
            System.out.println("Error while evaluating the model");
            System.out.println(ex.getMessage());
        }
        try {
            this.generate_result();
        } catch (IOException ex) {
            System.out.println("Error while generating result file");
            System.out.println(ex.getMessage());
        }
    }
    public void classify(){
        try { 
            this.classifier(DAO.getTRAIN_FILE_NAME()); 
            this.classifier(DAO.getTEST_FILE_NAME());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } 
    }
    public void classifier(String FILE_NAME) throws Exception{
        ArffLoader trainLoader = new ArffLoader();
        trainLoader.setSource(new File(DAO.getRESOURCES_PATH()+FILE_NAME.replace("csv", "arff")));
        int[] feat_r = new int[]{1,4,8,9,11,12};
        if (FILE_NAME.equals("test.csv"))
            feat_r = new int[]{1,3,7,8,10,11};
        Instances train_Set = trainLoader.getDataSet();
        int count_r=1;
        this.IdPassenger=train_Set.attributeToDoubleArray(0); 
        for (int value_r : feat_r){
            train_Set.deleteAttributeAt(value_r-count_r); 
            count_r++;
        }
        //Transforming the Gender column into binary
        String[] nominal_to_bin = new String[2];
        nominal_to_bin[0] = "-R";
        nominal_to_bin[1] = "3";
        if (FILE_NAME.equals("test.csv"))
                nominal_to_bin[1] = "2";
        NominalToBinary convertSex = new NominalToBinary();
        convertSex.setOptions(nominal_to_bin);
        convertSex.setInputFormat(train_Set);
        train_Set = Filter.useFilter(train_Set,convertSex);
        
        //Transforming the attribute's values to discrete format
        String[] opt_factors = new String[5]; 
        opt_factors[0] = "-R"; //Specifies column to Discretize
        opt_factors[1] = "6"; //column 6
        if (FILE_NAME.equals("test.csv"))
                opt_factors[1] = "5";
        opt_factors[2] = "-B"; //Specifiy the maximum number of bins
        opt_factors[3] = "10"; // 10 bins by default
        // when Discretizing, use equal-frequency instead of equal-width
        opt_factors[4] = "-F"; // 
        Discretize discretize = new Discretize();
        discretize.setOptions(opt_factors);
        discretize.setInputFormat(train_Set);
        train_Set = Filter.useFilter(train_Set,discretize);
        
        //Transforming the target attribute of the train set to discrete format
        String[] opt = new String[]{"-R","1,3"};
        NumericToBinary convertSurvived = new NumericToBinary();
        convertSurvived.setOptions(opt);
        convertSurvived.setInputFormat(train_Set);
        train_Set = Filter.useFilter(train_Set,convertSurvived);
        if (FILE_NAME.equals("train.csv"))
            this.train_Set=train_Set;
        else
            this.test_Set=train_Set;
    }
    public String evaluate() throws Exception{
        String eval_res="";
        //Setting the index and the target variable
        train_Set.setClassIndex(train_Set.numAttributes()-1);
        Attribute trainAttribute = train_Set.attribute(0);
        train_Set.setClass(trainAttribute);
        // from last
        Classifier id3Classifier = new J48();
        id3Classifier.buildClassifier(train_Set);
        this.classifier=id3Classifier;
        Evaluation eval = new Evaluation(train_Set);
	eval.evaluateModel(id3Classifier, train_Set); 
        eval_res=("----------Evaluation of trained model : \n\r");
        eval_res+=("Precision (P): "+String.format("%.2f", eval.precision(1)))+"\n";
        eval_res+=("Recall (R): "+String.format("%.2f",eval.recall(1)))+"\n";
        eval_res+=("Accuracy (ACC): "+String.format("%.2f",eval.pctCorrect()))+"\n";
        eval_res+=("Root-mean-square error (RMSE): "+eval.rootMeanSquaredError())+"\n";    
        eval_res+=(eval.toMatrixString())+"\n";
        return eval_res;
    }
    public void generate_result() throws IOException{
              //Creating the Target attribute to be predicted
        Attribute newAttribute = new Attribute("Survives");

        //Setting the position of the new target as the train set
        test_Set.insertAttributeAt(newAttribute,0);
        //Setting index
        test_Set.setClassIndex(test_Set.numAttributes()-1);
        Attribute testAttribute = test_Set.attribute(1);
        test_Set.setClass(testAttribute);
        //Creating the Prediction and saving it in an Array List
        ArrayList<Double> Predicted = new ArrayList<>(); 
        for( int i=0; i < test_Set.numInstances(); i++){
            double Survive = 0; 
            try {
                Survive = classifier.classifyInstance(test_Set.instance(i));
            } catch (Exception ex) {
                System.out.println("Can't get the instance: "+i+" of test_set");
                System.out.println(ex.getMessage());
            }
            Predicted.add(Survive);
        }
        //Wrinting the Array List as a unique CSV Column 
        BufferedWriter br = new BufferedWriter(new FileWriter(DAO.getRESOURCES_PATH()+"results.csv"));
        StringBuilder sb = new StringBuilder();
        // Append the values from array
        int id=0;
        sb.append("PassengerId,"+ "Survived\n\r");
        for (Double element : Predicted) {
            sb.append((int)(IdPassenger[id]));
            sb.append(",");
            sb.append((int)Math.round(element));
            sb.append("\n");
            id++;
        }
        br.write(sb.toString());
        br.close();
        
        System.out.println("-----Done predicting");

    }
    
}
