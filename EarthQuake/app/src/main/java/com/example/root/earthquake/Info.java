package com.example.root.earthquake;

/**
 * Created by root on 23/10/16.
 */
public class Info {
    String place,date;
    double mag;
    Info(String a,String b,double c){
        place = a;
        date = b;
        mag = c;
    }
    String getPlace(){return place;}
    String getDate(){return date;}
    double getMag(){return mag;}
}
