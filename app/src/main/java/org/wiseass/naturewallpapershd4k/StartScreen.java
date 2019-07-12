package org.wiseass.naturewallpapershd4k;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/*
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;*/
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

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

public class StartScreen extends AppCompatActivity implements CardView.OnClickListener {

    static CardView playa, team, fav, newUpdates, adcard;
    TextView adholder, pri;
    //Aug27New
    ImageView ivAd;

    //private Tracker mTracker;
    private FirebaseAnalytics mFirebaseAnalytics;

    String json, res = "nagdi", res2 = "bai";
    private int PERMISSION_REQUEST = 100;
    SharedPreferences pref;
    SharedPreferences.Editor ed;
    private static String mAdlink = "www.sanved.com";
    private static int textSize = 25;
    private static boolean extAds = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);

        /*AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();*/
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        initVals();

        FirebaseMessaging.getInstance().subscribeToTopic("nagdi");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) checkPermissionStuff();

        if (isNetworkConnected())
            new MyAsyncTask().execute("");
        else{
            android.support.v7.app.AlertDialog.Builder build = new android.support.v7.app.AlertDialog.Builder(StartScreen.this);
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
                            //ListLoader.this.finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //ListLoader.this.finish();
                        }
                    })
                    .setCancelable(false);

            build.create().show();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* mTracker.setScreenName("StartScreen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());*/
    }

    public void initVals() {
        pref = getSharedPreferences("football", MODE_PRIVATE);
        ed = pref.edit();
        playa = (CardView) findViewById(R.id.cvPlayer);
        //team = (CardView) findViewById(R.id.cvTeam);
        playa.setOnClickListener(this);
        //team.setOnClickListener(this);

        fav = (CardView) findViewById(R.id.fav);
        fav.setOnClickListener(this);

        adcard = (CardView) findViewById(R.id.adcard);
        adcard.setOnClickListener(this);

        newUpdates = (CardView) findViewById(R.id.newUpdates);
        newUpdates.setOnClickListener(this);

        adholder = (TextView) findViewById(R.id.adholder);
        //Aug27New
        ivAd = (ImageView) findViewById(R.id.adimage);

        pri = findViewById(R.id.pri);

        pri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StartScreen.this, PrivacyPolicy.class);
                startActivity(i);
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvPlayer:
                /*mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Open")
                        .setAction(getResources().getString(R.string.startscr1))
                        .build());*/

                Bundle params = new Bundle();
                params.putString("Opened", "StartScreen");
                params.putString("selected",getResources().getString(R.string.startscr1));
                mFirebaseAnalytics.logEvent("StartScreen", params);

                /*Intent i = new Intent(StartScreen.this, ListLoader.class);
                i.putExtra("link", getResources().getString(R.string.json1));
                i.putExtra("name", getResources().getString(R.string.startscr1));
                startActivity(i);*/

                Intent i = new Intent(v.getContext(), PhotoWindow.class);
                i.putExtra("STRING_I_NEED", getString(R.string.json1));
                i.putExtra("name", getString(R.string.startscr1));
                v.getContext().startActivity(i);

                break;

            /*case R.id.cvTeam:
                /*mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Open")
                        .setAction(getResources().getString(R.string.startscr2))
                        .build());

                Bundle params2 = new Bundle();
                params2.putString("Opened", "StartScreen");
                params2.putString("selected",getResources().getString(R.string.startscr1));
                mFirebaseAnalytics.logEvent("StartScreen", params2);

                Intent i2 = new Intent(StartScreen.this, ListLoader.class);
                i2.putExtra("link", getResources().getString(R.string.json2));
                i2.putExtra("name", getResources().getString(R.string.startscr2));
                startActivity(i2);
                break;*/
            case R.id.fav:
                /*mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Open")
                        .setAction("Favorites")
                        .build());*/

                Bundle params3 = new Bundle();
                params3.putString("Opened", "StartScreen");
                params3.putString("selected","Favorites");
                mFirebaseAnalytics.logEvent("StartScreen", params3);

                Intent i3 = new Intent(StartScreen.this, FavoriteActivity.class);
                startActivity(i3);
                break;
            case R.id.newUpdates:
               /* mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Open")
                        .setAction("New Updates")
                        .build());*/
                Bundle params4 = new Bundle();
                params4.putString("Opened", "StartScreen");
                params4.putString("selected","New Updates");
                mFirebaseAnalytics.logEvent("StartScreen", params4);

                AlertDialog.Builder build = new AlertDialog.Builder(StartScreen.this);
                build.setTitle("New Additions")
                        .setMessage(pref.getString("changelog","Enjoy the wallpapers"))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                build.show();
                break;
            case R.id.adcard:
                /*mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Ad")
                        .setAction("Sponsorship")
                        .build());*/
                Intent i23 = new Intent(Intent.ACTION_VIEW);
                i23.setData(Uri.parse(mAdlink));
                startActivity(i23);
                break;
        }
    }


    class MyAsyncTask extends AsyncTask<String, String, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(StartScreen.this);
        InputStream inputStream = null,inputStream2 = null;
        String result = "", result2="";

        protected void onPreExecute() {
            progressDialog.setTitle("Starting app");
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
            ArrayList<String> param = new ArrayList<String>();
            URL url, url2;

            /*try{
                url = new URL("http://www.sanved.com/app/wwe/update2.json");
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
			
			//Aug27New

            try{
                url = new URL("http://www.wiseassenterprises.com/app/" + getResources().getString(R.string.folder) + "/update2.json");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setUseCaches(false);
                urlConnection.setConnectTimeout(4000);
                inputStream = new BufferedInputStream(urlConnection.getInputStream());

                url2 = new URL("http://www.wiseassenterprises.com/app/promo.json");
                HttpURLConnection urlConnection2 = (HttpURLConnection) url2.openConnection();
                urlConnection2.setUseCaches(false);
                urlConnection2.setConnectTimeout(4000);
                inputStream2 = new BufferedInputStream(urlConnection2.getInputStream());

            }catch (SocketTimeoutException te){
                Log.e("Nagdi","Bai");
                try {
                    url = new URL("http://www.sanved.com/app/" + getResources().getString(R.string.folder) + "/update2.json");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setUseCaches(false);
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                    url2 = new URL("http://www.sanved.com/app/promo.json");
                    HttpURLConnection urlConnection2 = (HttpURLConnection) url2.openConnection();
                    urlConnection2.setUseCaches(false);
                    inputStream2 = new BufferedInputStream(urlConnection2.getInputStream());

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
                    url = new URL("http://www.sanved.com/app/" + getResources().getString(R.string.folder) + "/update2.json");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setUseCaches(false);
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                    url2 = new URL("http://www.sanved.com/app/promo.json");
                    HttpURLConnection urlConnection2 = (HttpURLConnection) url2.openConnection();
                    urlConnection2.setUseCaches(false);
                    inputStream2 = new BufferedInputStream(urlConnection2.getInputStream());

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

                BufferedReader bReader2 = new BufferedReader(new InputStreamReader(inputStream2, "utf-8"), 8);
                StringBuilder sBuilder2 = new StringBuilder();

                String line2 = null;
                while ((line2 = bReader2.readLine()) != null) {
                    sBuilder2.append(line2 + "\n");
                }

                inputStream2.close();
                result2 = sBuilder2.toString();

            } catch (Exception e) {
                Log.e("StringBuilding", "Error converting result " + e.toString());
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            DataKaRakhwala rakhwala;

			//Aug27new
			
			//Exit app list data
            try {
                JSONObject jobj = new JSONObject(result2);

                JSONArray jarr = jobj.getJSONArray("links");

                for(int i = 0; i < jarr.length(); i++) {

                    JahiratClass jahirat;

                    JSONObject jo_inside = jarr.getJSONObject(i);

                    int j = i+1;

                    ed.putString("name" + j, jo_inside.getString("name"));
                    ed.putString("url" + j, jo_inside.getString("url"));
                    ed.putString("image" + j, jo_inside.getString("image"));
                    ed.commit();
                }

                ed.putInt("appNum", jarr.length()).apply();

            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            }

            //parse JSON data
            try {
                //Taking JSON from Assets
                JSONObject jobj = new JSONObject(result);
                //Taking a JSON Array from the JSONObject created above

                int isUpdated = jobj.getInt("datac");
                int isDown = jobj.getInt("down");
                int newVer = jobj.getInt("appc");
                int textsize = jobj.getInt("textsize");
                extAds = jobj.getBoolean("extAds");
                String changelog = jobj.getString("changelog");
                String title = jobj.getString("title");
                String adlink = jobj.getString("adlink");
                String adimage = jobj.getString("adimage");
                String downText = jobj.getString("downText");

                //Aug27New
                //Add work
                adholder.setText(""+title);
                mAdlink = adlink;
                adholder.setTextSize(TypedValue.COMPLEX_UNIT_SP,textsize);
                Picasso.get()
                        .load(adimage)
                        .into(ivAd);

                int a = pref.getInt("isUpdated", 1);
                int b = 0;

                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    b = pInfo.versionCode;
                }catch(PackageManager.NameNotFoundException ne){
                    ne.printStackTrace();
                }

                if(isDown == 1){
                    /*mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Update")
                            .setAction("Server Down")
                            .build());*/

                    Bundle params3 = new Bundle();
                    params3.putString("Update", "StartScreen");
                    params3.putString("msg","Server Down");
                    mFirebaseAnalytics.logEvent("StartScreen", params3);

                    AlertDialog.Builder build = new AlertDialog.Builder(StartScreen.this);
                    build.setTitle("Oops !")
                            .setMessage(downText)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    build.show();
                }

                else {
                    if (a < isUpdated) {
                       /* mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Update")
                                .setAction("Data Update")
                                .build());*/

                        Bundle params3 = new Bundle();
                        params3.putString("Update", "StartScreen");
                        params3.putString("msg","Data Update");
                        mFirebaseAnalytics.logEvent("StartScreen", params3);

                        AlertDialog.Builder build = new AlertDialog.Builder(StartScreen.this);
                        build.setTitle("App Updated")
                                .setMessage(changelog)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        build.show();
                        ed.putInt("isUpdated", isUpdated);
                        ed.putString("changelog",changelog);
                        ed.commit();
                    }
                }

                if(b < newVer){
                   /* mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Update")
                            .setAction("App Update")
                            .build());*/

                    Bundle params3 = new Bundle();
                    params3.putString("Update", "StartScreen");
                    params3.putString("msg","App Update");
                    mFirebaseAnalytics.logEvent("StartScreen", params3);

                    AlertDialog.Builder build = new AlertDialog.Builder(StartScreen.this);
                    build.setTitle("Need to update")
                            .setMessage("The app needs to be updated to get the latest features.")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                                    startActivity(browserIntent);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    build.show();
                }



                progressDialog.dismiss();

            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            } // catch (JSONException e)
        }

    }


    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermissionStuff() {

        /*mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Android M")
                .setAction("Request Permission")
                .build());

*/



        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                AlertDialog.Builder build = new AlertDialog.Builder(this);
                build
                        .setTitle("Permission")
                        .setMessage("The App needs permission to apply Wallpaper")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
                            }
                        });
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    //Aug27New
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(extAds) {
            Intent i = new Intent(this, ExitActivity.class);
            startActivity(i);
        }
    }
}
