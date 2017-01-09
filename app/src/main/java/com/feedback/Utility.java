package com.feedback;

import android.view.View;
import android.widget.TextView;

/**
 * Created by abdul on 8/4/16.
 */
public class Utility {

    public static String URL = "http://52.38.79.165:8080/Tap-A-Meal/webresources/";

    public static boolean isNotNull(String txt){
        return txt!=null && txt.trim().length()>0 ? true: false;
    }

    public static void textViewHide(TextView _input){
        _input.setVisibility(View.GONE);
    }
    public static void textViewShow(TextView _input){
        _input.setVisibility(View.VISIBLE);
    }


}
