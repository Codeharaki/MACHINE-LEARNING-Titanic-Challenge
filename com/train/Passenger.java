/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.train;

/**
 *
 * @author youne
 */
public class Passenger {
    private int PassengerId;
    private int Survived;
    private int Pclass;
    private String Name;
    private String gender;
    private float Age;
    private final int SibSp;
    private int Parch;
    private String Ticket;
    private float Fare;
    private String Cabin;
    private String Embarked;

    public Passenger(int PassengerId, int Survived, String Name, String gender, float Age, int SibSp, int Parch, String Ticket, float Fare, String Cabin, String Embarked) {
        this.PassengerId = PassengerId;
        this.Survived = Survived;
        this.Name = Name;
        this.gender = gender;
        this.Age = Age;
        this.SibSp = SibSp;
        this.Parch = Parch;
        this.Ticket = Ticket;
        this.Fare = Fare;
        this.Cabin = Cabin;
        this.Embarked = Embarked;
    }
    public Passenger(int PassengerId, int Survived, int Pclass, String Name, String gender, float Age, int SibSp, int Parch, String Ticket, float Fare, String Cabin, String Embarked) {
        this( PassengerId, Survived,Name,  gender,  Age,  SibSp,  Parch,  Ticket,  Fare,  Cabin,  Embarked);
        this.Pclass = Pclass;
    }
    public int getPassengerId() {
        return PassengerId;
    }

    public int isSurvived() {
        return Survived;
    }

    public int getPclass() {
        return Pclass;
    }

    public String getName() {
        return Name;
    }

    public String getgender() {
        return gender;
    }

    public float getAge() {
        return Age;
    }

    public int getSibSp() {
        return SibSp;
    }

    public int getParch() {
        return Parch;
    }

    public String getTicket() {
        return Ticket;
    }

    public float getFare() {
        return Fare;
    }

    public String getCabin() {
        return Cabin;
    }

    public String getEmbarked() {
        return Embarked;
    }

    @Override
    public String toString() {
        String info_a="\nid: "+PassengerId+" |Survived: "+(Survived==0?"YES":"NO")+" |class: "+Pclass+" |Name:"+Name+" |Gender:"+ gender;
        String info_b=" |Age: "+Age+" |Siblings: "+SibSp+" |Parents: "+Parch+" |Ticket: "+Ticket+" |Fare: "+Fare+" |Cabin: "+Cabin+" |Embarked: "+Embarked;
        return info_a+info_b;
    }

    

}
