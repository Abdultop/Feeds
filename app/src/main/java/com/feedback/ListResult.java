package com.feedback;

/**
 * Created by abdul on 29/7/16.
 */
public class ListResult {

    String date ="";
    String item = "";
    int rating = 0;

    public ListResult(String date, String item, int rating){
        this.date = date;
        this.item = item;
        this.rating = rating;

    }

    public void setDate(String date){
        this.date = date;
    }

    public String getDate(){
        return date;
    }

    public void setItem(String item){
        this.item = item;
    }

    public  String getItem(){
        return item;
    }

    public void setRating(int rating){
        this.rating = rating;
    }

    public int getRating(){
        return rating;
    }


}
