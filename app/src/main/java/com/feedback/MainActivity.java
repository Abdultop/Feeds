package com.feedback;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.apache.http.entity.StringEntityHC4;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    EditText name;
    EditText mobile;
    TextView none;
    ListView listView;
    FloatingActionButton fab;
    ListCustomAdapter adapter;
    ArrayList<ListResult> itemList = new ArrayList<ListResult>();
    ArrayList<String> tempList = new ArrayList<String>();
    private DBHelper mydb ;
    Context context;
    private CoordinatorLayout coordinatorLayout;
    RelativeLayout listHeader;

    ProgressDialog prgDialog;
    StringEntityHC4 input;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mydb = new DBHelper(this);
        context = this;
        name = (EditText)findViewById(R.id.name);
        mobile = (EditText)findViewById(R.id.mobile);
        none = (TextView)findViewById(R.id.none);
        listView=(ListView)findViewById(R.id.listView);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinatorlayout);
        listHeader = (RelativeLayout)findViewById(R.id.listHeader);
           prgDialog = new ProgressDialog(context);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        Menu();
        tempList = mydb.getAllMenuItemName();
        if(tempList.size()==0){
            none.setVisibility(View.VISIBLE);
            listHeader.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
        }else {
            none.setVisibility(View.GONE);
            listHeader.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
            for(int i=0;i<tempList.size();i++){
                String[] a= tempList.get(i).split("\\$");
                itemList.add(new ListResult(a[0],a[1],0));
            }
        }
        adapter = new ListCustomAdapter(this,itemList);
        listView.setAdapter(adapter);

        name.setFilters(new InputFilter[] {
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if(cs.equals("")){ // for backspace
                            return cs;
                        }
                        if(cs.toString().matches("[a-zA-Z ]+")){
                            return cs;
                        }
                        return "";
                    }
                }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                name.setError(null);
            }
        });

        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mobile.setError(null);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utility.isNotNull(name.getText().toString())){
                    if(Utility.isNotNull(mobile.getText().toString())){
                        if (mobile.getText().toString().length()==10){
                            for (int i=0;i<itemList.size();i++){
                                ListResult result = (ListResult) adapter.getItem(i);
                                String date = result.date;
                                String items = result.item;
                                String rating = String.valueOf(result.rating);
                                if(mydb.insertFeedback(mobile.getText().toString(),items,rating)){
                                    itemList.get(i).setRating(0);
                                    Log.d("Out","Date:"+date+"Item:"+items+"rating:"+rating + " - " + itemList.get(i).getRating());
                                    adapter = new ListCustomAdapter(context,itemList);
                                    adapter.notifyDataSetChanged();
                                    listView.setAdapter(adapter);
                                }else {
                                    Toast.makeText(getApplicationContext(), "Unexpected Error while send Feedback", Toast.LENGTH_LONG).show();
                                }
                            }
                            if(mydb.insertUsers(name.getText().toString(),mobile.getText().toString())){
                                name.setText("");
                                mobile.setText("");
                            }else {
                                Toast.makeText(getApplicationContext(), "Error while send Feedback", Toast.LENGTH_LONG).show();
                            }
                        }else {
                            Toast.makeText(getApplicationContext(), "Mobile Number must have 10 digits", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        mobile.setError("Fill this Field");
                    }
                } else {
                    name.setError("Fill this Field");
                }
            }
        });


    }

    private void Menu() {
      // Show Progress Dialog
      //  prgDialog.show();
        RequestParams params = new RequestParams();
        params.put("type","9840421086");
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Utility.URL+"hotpotFeedback/Items", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Hide Progress Dialog
         //       prgDialog.dismiss();
                try {
                    Log.d("Menu","Updating......");
                    // JSON Object
                    JSONObject obj = new JSONObject(new String(responseBody));
                    JSONArray result = obj.getJSONArray("results");
                    Log.d("resultLength", String.valueOf(result.length()));
                    if(result!=null && result.length()>0){
                        if(mydb.clearTable("item")){ //MR 16-6-15 @Rahman For clear Menu table before inserting Item
                            for(int i = 0;i<result.length();i++){
                                JSONObject jsonResultObject = result.getJSONObject(i);
                                String itemName = jsonResultObject.getString("name").toString();
                                String date = jsonResultObject.getString("price").toString();
                                mydb.insertMenuItem(date,itemName);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Hide Progress Dialog
           //     prgDialog.dismiss();
                Log.d("Menu","Failure......");
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), Constants.ErrorFour100, Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), Constants.ErrorFive100, Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getApplicationContext(), Constants.netConnection, Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sync) {
            if(isNetworkConnected(this)){
             //   ItemMenu();
                tempList.clear();
                itemList.clear();
                tempList = mydb.getAllMenuItemName();
                if(tempList.size()==0){
                    none.setVisibility(View.VISIBLE);
                    listHeader.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);
                }else {
                    none.setVisibility(View.GONE);
                    listHeader.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.VISIBLE);
                    for(int i=0;i<tempList.size();i++){
                        String[] a= tempList.get(i).split("\\$");
                        itemList.add(new ListResult(a[0],a[1],0));
                    }
                }
                adapter = new ListCustomAdapter(this,itemList);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);

                SendtoDb();
            }else {
                Snackbar snackbar;
                snackbar = Snackbar.make(coordinatorLayout,  "No Internet connection", Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
                // textView.setGravity(Gravity.CENTER_HORIZONTAL);
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                snackbar.show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void SendtoDb(){
        ArrayList<String> userList = new ArrayList<String>();
        ArrayList<String> feedbackList = new ArrayList<String>();
        userList= mydb.getAllUsers();
        feedbackList = mydb.getAllFeedback();
        Log.d("ListSize", String.valueOf(userList.size()+"--"+feedbackList.size()));
        if(userList.size()>0 && feedbackList.size()>0){

            JSONObject jsonObject = new JSONObject();
            JSONObject jObj = new JSONObject();
            JSONArray totalArr = new JSONArray();
            JSONArray finalArr = new JSONArray();
            try {
                for (int i=0; i < userList.size(); i++){
                    String[] arr= userList.get(i).split("\\$");
                    Log.d("Test",arr[0]+"-->"+arr[1]);
                    jsonObject.put("name", arr[0]);
                    jsonObject.put("mobile",arr[1]);
                    totalArr.put(jsonObject);
                    jsonObject = new JSONObject();
                }
                jObj.put("Users", totalArr);
                totalArr = new JSONArray();
                jsonObject = new JSONObject();
                for (int i =0; i< feedbackList.size(); i++){
                    String[] arr= feedbackList.get(i).split("\\$");
                    jsonObject.put("mobile", arr[0]);
                    jsonObject.put("itemNames",arr[1]);
                    jsonObject.put("ratings",arr[2]);
                    totalArr.put(jsonObject);
                    jsonObject = new JSONObject();
                }
                jObj.put("Items", totalArr);
                finalArr.put(jObj);
                jObj = new JSONObject();
                jObj.put("Results", finalArr);
                input = new StringEntityHC4(jObj.toString());
                input.setContentType("application/json");
                input.setContentEncoding("UTF-8");
                makeHttpPostRequest();

            }catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.d("Test", "JSON"+jObj.toString() );
        }

    }

    private void makeHttpPostRequest() {
        // Show Progress Dialog
        prgDialog.show();
        try {
            String wsurl = Utility.URL+"hotpotFeedback/NewFeedback";
            AsyncHttpClient client = new AsyncHttpClient();

            client.post(context, wsurl, input, "application/json", new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    // Hide Progress Dialog
                    prgDialog.dismiss();
                    try {
                        // JSON Object
                        JSONObject obj = new JSONObject(new String(responseBody));
                        // When the JSON response has status boolean value assigned with true
                        if (obj.getBoolean("status")) {
                            if(mydb.clearTable("feedback")){
                                if(mydb.clearTable("user")) {
                                    Toast.makeText(context, "Sucessfully updated in DB", Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                        // Else display error message
                        else {
                            Toast.makeText(context, obj.getString("msg"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(context, Constants.jsonException, Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    // Hide Progress Dialog
                    prgDialog.dismiss();
                    // When Http response code is '404'
                    if (statusCode == 404) {
                        Toast.makeText(context, Constants.ErrorFour100, Toast.LENGTH_LONG).show();
                    }
                    // When Http response code is '500'
                    else if (statusCode == 500) {
                        Toast.makeText(context, Constants.ErrorFive100, Toast.LENGTH_LONG).show();
                    }
                    // When Http response code other than 404, 500
                    else {
                    //    Toast.makeText(context, Constants.netConnection, Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isNetworkConnected(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onResume(){
        super.onResume();

    }
}
