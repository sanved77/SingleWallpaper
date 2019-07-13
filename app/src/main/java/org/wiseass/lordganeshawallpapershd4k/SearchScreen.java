package org.wiseass.lordganeshawallpapershd4k;

import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView.OnEditorActionListener;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
//NEW
import android.widget.Button;
import android.view.LayoutInflater;

/*
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;*/

import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Sanved on 05-08-2016.
 */
public class SearchScreen extends AppCompatActivity {

    RecyclerView rv;
    Toolbar toolbar;
    TextView nores;
    EditText search;
    ImageButton searchB;
    Button req;

    //private Tracker mTracker;
    private FirebaseAnalytics mFirebaseAnalytics;

    RVAdapt adapt;

    ArrayList<DataKaRakhwala> list;

    private static String term;
    private static final String[] EMAIL_FOR_RESPONSE = {"wiseassenter@gmail.com"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_loader);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            term = extras.getString("term");
        } else {
            term = (String) savedInstanceState.getSerializable("term");
        }

        char dummy = term.charAt(term.length() -1);
        if(dummy == ' '){
            term = term.substring(0, term.length()-2);
        }
        term = term.toLowerCase();

        /*AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Search")
                .setAction("Searched - "+ term)
                .build());*/

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle params = new Bundle();
        params.putString("Search", "SearchScreen");
        params.putString("term",term);
        mFirebaseAnalytics.logEvent("SearchScreen", params);

        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //Aug27New
        setSupportActionBar(toolbar);
        //toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle("Searching " + term);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_36dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        nores = (TextView) findViewById(R.id.tvNores);
        req = (Button) findViewById(R.id.bReq);

        req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPlayer();
            }
        });

        search = (EditText) findViewById(R.id.etSearchTermQues);
        search.setText(term);
        search.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId==EditorInfo.IME_ACTION_SEARCH) {
                    searchStuff();
                }
                return false;
            }
        });

        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (search.getRight() - search.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        search.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        searchB = (ImageButton) findViewById(R.id.bSearchQues);
        searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchStuff();
            }
        });

        list = new ArrayList<DataKaRakhwala>();

        if (isNetworkConnected())
            new MyAsyncTask().execute("");
        else{
            AlertDialog.Builder build = new AlertDialog.Builder(SearchScreen.this);
            build
                    .setTitle("No Internet")
                    .setMessage("Do you want to turn Internet ON so that the app can download the data?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent();
                            i.setAction(Settings.ACTION_WIFI_SETTINGS);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            startActivity(i);
                            SearchScreen.this.finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            SearchScreen.this.finish();
                        }
                    })
                    .setCancelable(false);

            build.create().show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* mTracker.setScreenName("SearchScreen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());*/

        mFirebaseAnalytics.logEvent("SearchScreen", null);
    }

    public void searchStuff(){
        if(search.getText().toString().isEmpty()){
            Toast.makeText(SearchScreen.this, "Enter something in the search bar", Toast.LENGTH_SHORT).show();
        }else{
            nores.setVisibility(View.GONE);
            req.setVisibility(View.GONE);
            list.clear();
            term = search.getText().toString();
            char dummy = term.charAt(term.length() -1);
            if(dummy == ' '){
                term = term.substring(0, term.length()-2);
            }
            term = term.toLowerCase();
            term = term.replace("\n","");
            getSupportActionBar().setTitle("Searching " + term);
            new MyAsyncTask().execute("");
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    class MyAsyncTask extends AsyncTask<String, String, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(SearchScreen.this);
        InputStream inputStream = null, inputStream2 = null, inputStream3 = null;
        String result = "", result2 = "", result3 = "";

        protected void onPreExecute() {
            progressDialog.setTitle("Searching "+ term);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancel(true);
                }
            });
            progressDialog.show();
            progressDialog.setCancelable(true);
        }

        @Override
        protected Void doInBackground(String... params) {
            URL url, url2, url3;

            try{
                url = new URL("http://www.wiseassenterprises.com/app/" + getResources().getString(R.string.folder) + "/" + getResources().getString(R.string.json1));
                //url2 = new URL("http://www.wiseassenterprises.com/app/" + getResources().getString(R.string.folder) + "/" + getResources().getString(R.string.json2));

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //HttpURLConnection urlConnection2 = (HttpURLConnection) url2.openConnection();

                urlConnection.setUseCaches(false);
                //urlConnection2.setUseCaches(false);

                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                //inputStream2 = new BufferedInputStream(urlConnection2.getInputStream());

            }catch (MalformedURLException malle){
                Log.e("Mal", ""+malle);
                malle.printStackTrace();
            }catch (IOException ioe){
                Log.e("IO", ""+ioe);
                ioe.printStackTrace();
                try {
                    url = new URL("http://www.sanved.com/app/" + getResources().getString(R.string.folder) + "/" + getResources().getString(R.string.json1));
                    //url2 = new URL("http://www.sanved.com/app/" + getResources().getString(R.string.folder) + "/" + getResources().getString(R.string.json2));

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    //HttpURLConnection urlConnection2 = (HttpURLConnection) url2.openConnection();

                    urlConnection.setUseCaches(false);
                    //urlConnection2.setUseCaches(false);

                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    //inputStream2 = new BufferedInputStream(urlConnection2.getInputStream());
                }catch (MalformedURLException me){
                    Log.e("Mal", ""+me);
                }catch(IOException iee){
                    Log.e("IO", ""+iee);
                }
                progressDialog.setCancelable(true);
            }

            // Convert response to string using String Builder
            try {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                //BufferedReader bReader2 = new BufferedReader(new InputStreamReader(inputStream2, "utf-8"), 8);

                StringBuilder sBuilder = new StringBuilder();
                //StringBuilder sBuilder2 = new StringBuilder();


                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line + "\n");
                }

                /*String line2 = null;
                while ((line2 = bReader2.readLine()) != null) {
                    sBuilder2.append(line2 + "\n");
                }*/


                inputStream.close();
                inputStream2.close();

                result = sBuilder.toString();
                //result2 = sBuilder2.toString();


            } catch (Exception e) {
                Log.e("StringBuilding", "Error converting result " + e.toString());
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            DataKaRakhwala rakhwala;
            //parse JSON data
            try {
                //Taking JSON from Assets
                JSONObject jobj = new JSONObject(result);
                JSONObject jobj2 = new JSONObject(result2);

                //Taking a JSON Array from the JSONObject created above
                JSONArray jarr = jobj.getJSONArray("links");
                JSONArray jarr2 = jobj2.getJSONArray("links");


                //Loop to iterate all the values off the Array
                for (int i = 0; i < jarr.length(); i++) {

                    JSONObject jo_inside = jarr.getJSONObject(i);
                    String name = jo_inside.getString("name");

                    if(name.toLowerCase().contains(term) || term.contains(name.toLowerCase())){
                        String url = jo_inside.getString("url");
                        int pics = jo_inside.getInt("pics");
                        rakhwala = new DataKaRakhwala(name, pics, url);
                        list.add(rakhwala);
                    }

                }

                for (int i = 0; i < jarr2.length(); i++) {

                    JSONObject jo_inside2 = jarr2.getJSONObject(i);

                    String name2 = jo_inside2.getString("name");

                    if(name2.toLowerCase().contains(term) || term.contains(name2.toLowerCase())){
                        String url = jo_inside2.getString("url");
                        int pics = jo_inside2.getInt("pics");
                        rakhwala = new DataKaRakhwala(name2, pics, url);
                        list.add(rakhwala);
                    }
                }

                this.progressDialog.dismiss();
                Context con = SearchScreen.this.getApplication();
                adapt = new RVAdapt(list,con);

                rv.setAdapter(adapt);

                if(list.isEmpty()) {
                    nores.setVisibility(View.VISIBLE);
                    req.setVisibility(View.VISIBLE);
                }

            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            } // catch (JSONException e)
        }

    }

    public void requestPlayer(){
        AlertDialog.Builder build = new AlertDialog.Builder(SearchScreen.this);

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog, null);

        final EditText bug = (EditText) dialogView.findViewById(R.id.etBug);
        bug.setText(term);

        build
                .setTitle("Suggest")
                .setView(dialogView)
                .setMessage("Which " + getResources().getString(R.string.to_add) + " do you want to Suggest?")
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL, EMAIL_FOR_RESPONSE);
                        i.putExtra(Intent.EXTRA_SUBJECT,  getResources().getString(R.string.name) + " Wallpaper App: Suggestion");
                        i.putExtra(Intent.EXTRA_TEXT, "I want " + bug.getText().toString()+ " to be included in the app");
                        try{
                            startActivity(Intent.createChooser(i, "Send email...."));
                        }catch(android.content.ActivityNotFoundException ex){
                            Toast.makeText(SearchScreen.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);

        build.create().show();
    }

}
