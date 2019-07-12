package org.wiseass.naturewallpapershd4k;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
/*
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;*/

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
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
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Sanved on 21-05-2016.
 */
public class ListLoader extends AppCompatActivity {

    RecyclerView rv;
    Toolbar toolbar;
    EditText search;
    ImageButton searchB, downloads;
   // private Tracker mTracker;
   private FirebaseAnalytics mFirebaseAnalytics;
    TextView noint;
    Button reload;
    private AdView mAdView;

    RVAdapt adapt;

    ArrayList<DataKaRakhwala> list;

    private static String link, name;

    private static final String[] EMAIL_FOR_RESPONSE = {"wiseassenter@gmail.com"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_loader);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            link = extras.getString("link");
            name = extras.getString("name");
        } else {
            link = (String) savedInstanceState.getSerializable("link");
            name = (String) savedInstanceState.getSerializable("name");
        }

        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //Aug27New
        setSupportActionBar(toolbar);
        //toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle("Select " + name);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_36dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        noint = (TextView) findViewById(R.id.tvNoInt);
        reload = (Button) findViewById(R.id.bReload);

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noint.setVisibility(View.GONE);
                reload.setVisibility(View.GONE);
                if (isNetworkConnected())
                    new MyAsyncTask().execute("");
                else {
                    AlertDialog.Builder build = new AlertDialog.Builder(ListLoader.this);
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
                                    ListLoader.this.finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    ListLoader.this.finish();
                                }
                            })
                            .setCancelable(false);

                    build.create().show();
                }
            }
        });

        search = (EditText) findViewById(R.id.etSearchTermQues);

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId==EditorInfo.IME_ACTION_SEARCH) {
                    searchStuff();
                }
                return false;
            }
        });

        search.setOnTouchListener(new OnTouchListener() {
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

        downloads = (ImageButton) findViewById(R.id.bDownloads);
        downloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_VIEW, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivity(galleryIntent);
            }
        });

        MobileAds.initialize(this, getString(R.string.admob_app_id));

        mAdView = (AdView) findViewById(R.id.ads);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        list = new ArrayList<DataKaRakhwala>();

        if (isNetworkConnected())
            new MyAsyncTask().execute("");
        else{
            AlertDialog.Builder build = new AlertDialog.Builder(ListLoader.this);
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
                            ListLoader.this.finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            ListLoader.this.finish();
                        }
                    })
                    .setCancelable(false);

            build.create().show();
        }

        /*AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();*/
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    public void searchStuff(){
        if (search.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter something in the search bar", Toast.LENGTH_SHORT).show();
        } else {
            String abc = search.getText().toString();
            Intent i2 = new Intent(this, SearchScreen.class);
            i2.putExtra("term", abc);
            search.setText("");
            startActivity(i2);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    class MyAsyncTask extends AsyncTask<String, String, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(ListLoader.this);
        InputStream inputStream = null;
        String result = "";

        protected void onPreExecute() {
            progressDialog.setTitle("Downloading " + name + " List");
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    noint.setVisibility(View.VISIBLE);
                    reload.setVisibility(View.VISIBLE);
                    cancel(true);
                }
            });
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(String... params) {
            ArrayList<String> param = new ArrayList<String>();
            URL url;

            /*try{
                url = new URL("http://www.sanved.com/app/wwe/" + link);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            }catch (MalformedURLException malle){
                Log.e("Mal", ""+malle);
                malle.printStackTrace();
            }catch (IOException ioe){
                Log.e("IO", ""+ioe);
                ioe.printStackTrace();
                progressDialog.setCancelable(true);
            }*/

            try{
                url = new URL("http://www.wiseassenterprises.com/app/" + getResources().getString(R.string.folder) + "/" + link);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setUseCaches(false);
                urlConnection.setConnectTimeout(4000);
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            }catch (SocketTimeoutException te){
                Log.e("Nagdi","Bai");
                try {
                    url = new URL("http://www.sanved.com/app/" + getResources().getString(R.string.folder) + "/" + link);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setUseCaches(false);
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }catch (MalformedURLException me){
                    Log.e("Mal", ""+me);
                }catch(IOException iee){
                    Log.e("IO", ""+iee);
                }
                progressDialog.setCancelable(true);
            }catch (MalformedURLException malle){
                Log.e("Mal", ""+malle);
                malle.printStackTrace();
            }catch (IOException ioe){
                Log.e("IO", ""+ioe);
                ioe.printStackTrace();
                try {
                    url = new URL("http://www.sanved.com/app/" + getResources().getString(R.string.folder) + "/"+ link);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setUseCaches(false);
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
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
                StringBuilder sBuilder = new StringBuilder();

                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line + "\n");
                }

                inputStream.close();
                result = sBuilder.toString();

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
                //Taking a JSON Array from the JSONObject created above
                JSONArray jarr = jobj.getJSONArray("links");

                //Loop to iterate all the values off the Array
                for (int i = 0; i < jarr.length(); i++) {
                    //Put the pointer on the object of the array
                    JSONObject jo_inside = jarr.getJSONObject(i);

                    Log.d("Name--", jo_inside.getString("name"));
                    Log.d("Pics--", ""+jo_inside.getInt("pics"));
                    Log.d("URL--", jo_inside.getString("url"));

                    //Taking the values and filling them in the HashMap
                    String name = jo_inside.getString("name");
                    String url = jo_inside.getString("url");
                    int pics = jo_inside.getInt("pics");
                    rakhwala = new DataKaRakhwala(name, pics, url);

                    list.add(rakhwala);
                    //Adding the HashMap to the ArrayList
                }
                this.progressDialog.dismiss();
                Context con = ListLoader.this.getApplication();
                adapt = new RVAdapt(list,con);

                rv.setAdapter(adapt);

            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            } // catch (JSONException e)
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle params = new Bundle();
        mFirebaseAnalytics.logEvent("ListLoader", null);
       /* mTracker.setScreenName("ListLoader - " + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        if (mAdView != null) {
            mAdView.resume();
        }*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            //Aug27New
            case R.id.about:
                Intent i = new Intent(ListLoader.this, About.class);
                startActivity(i);
                break;
            case R.id.suggest:
                AlertDialog.Builder build = new AlertDialog.Builder(ListLoader.this);

                LayoutInflater inflater = this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog, null);

                final EditText bug = (EditText) dialogView.findViewById(R.id.etBug);

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
                                i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.name) +" Wallpaper App: Suggestion");
                                i.putExtra(Intent.EXTRA_TEXT, "I want " + bug.getText().toString() + " to be included in the app");
                                try {
                                    startActivity(Intent.createChooser(i, "Send email...."));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(ListLoader.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
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
                break;
            case R.id.bug:
                AlertDialog.Builder build2 = new AlertDialog.Builder(ListLoader.this);
                LayoutInflater inflater2 = this.getLayoutInflater();
                final View dialogView2 = inflater2.inflate(R.layout.dialog, null);

                final EditText bug2 = (EditText) dialogView2.findViewById(R.id.etBug);

                build2
                        .setTitle("Report Bugs")
                        .setView(dialogView2)
                        .setMessage("What was the bug/error ?")
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("message/rfc822");
                                i.putExtra(Intent.EXTRA_EMAIL, EMAIL_FOR_RESPONSE);
                                i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.name) + " Wallpaper App: Error Bugs");
                                i.putExtra(Intent.EXTRA_TEXT, "I found a bug in the app. The bug is - " + bug2.getText().toString());
                                try {
                                    startActivity(Intent.createChooser(i, "Send email...."));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(ListLoader.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
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

                build2.create().show();
                break;
            case R.id.rate:
                String url = "https://play.google.com/store/apps/details?id=" + getPackageName();
                Intent i2 = new Intent(Intent.ACTION_VIEW);
                i2.setData(Uri.parse(url));
                startActivity(i2);
                break;

            case R.id.mshare:
               /* mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Share")
                        .setAction("App Shared")
                        .build());*/
                Bundle params = new Bundle();
                params.putString("Share", "ListLoader");
                mFirebaseAnalytics.logEvent("ListLoader", params);

                String shareBody = "Hey, check out this app - " + getResources().getString(R.string.name) +" Wallpapers HD 4K with amazing " + getResources().getString(R.string.name) +" images\nhttps://play.google.com/store/apps/details?id=" + getPackageName();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
                break;

            //Aug27New
            case R.id.disclaim:
                Intent i7 = new Intent(ListLoader.this, AboutCopyright.class);
                startActivity(i7);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }
}


