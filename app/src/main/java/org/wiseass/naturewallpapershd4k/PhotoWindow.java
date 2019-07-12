package org.wiseass.naturewallpapershd4k;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/*
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;*/

//new
//new
//new


/**
 * Created by Sanved on 21-05-2016.
 */
public class PhotoWindow extends AppCompatActivity {

    ArrayList<HashMap<String, String>> formList;
    private GridView mGridView;
    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;
    private AdView mAdView;
   /* private Tracker mTracker;*/
    private FirebaseAnalytics mFirebaseAnalytics;
    private Toolbar toolbar;
    TextView noint;
    Button reload;
    FloatingActionButton shuffle;
    SharedPreferences prefs;
    SharedPreferences.Editor ed;

    //Aug27New
    ImageLoader imageLoader;

    String extraString="",res="nagdi",res2 = "bai", nameStr="";
    private static final String[] EMAIL_FOR_RESPONSE = {"wiseassenter@gmail.com"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_view);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            extraString = extras.getString("STRING_I_NEED");
            nameStr = extras.getString("name");
        } else {
            extraString = (String) savedInstanceState.getSerializable("STRING_I_NEED");
            nameStr = (String) savedInstanceState.getSerializable("name");
        }

        initVals();

        if (isNetworkConnected())
            new MyAsyncTask().execute("");
        else{
            AlertDialog.Builder build = new AlertDialog.Builder(PhotoWindow.this);
            build
                    .setTitle("No Internet")
                    .setMessage("Do you want to turn Internet ON so that the app can download the images?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent();
                            i.setAction(Settings.ACTION_WIFI_SETTINGS);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            startActivity(i);
                            PhotoWindow.this.finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            PhotoWindow.this.finish();
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

    public void initVals(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //Aug27New
        setSupportActionBar(toolbar);
        //toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle(nameStr+" Wallpapers");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_36dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mAdView = (AdView) findViewById(R.id.ads);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        mGridView = (GridView) findViewById(R.id.gridView);

        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(this, R.layout.grid_item, mGridData);
        mGridView.setAdapter(mGridAdapter);

        noint = (TextView) findViewById(R.id.tvNoInt);
        reload = (Button) findViewById(R.id.bReload);

        shuffle = (FloatingActionButton) findViewById(R.id.fabShuffle);
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffleData();
            }
        });

        prefs = getSharedPreferences("football", MODE_PRIVATE);
        ed = prefs.edit();

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noint.setVisibility(View.GONE);
                reload.setVisibility(View.GONE);
                if (isNetworkConnected())
                    new MyAsyncTask().execute("");
                else {
                    AlertDialog.Builder build = new AlertDialog.Builder(PhotoWindow.this);
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
                                    PhotoWindow.this.finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    PhotoWindow.this.finish();
                                }
                            })
                            .setCancelable(false);

                    build.create().show();
                }
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get item at position
                GridItem item = (GridItem) parent.getItemAtPosition(position);

                Intent intent = new Intent(PhotoWindow.this, ActionScreen.class);
                ImageView imageView = (ImageView) v.findViewById(R.id.grid_item_image);

                //Bitmap data
                try{

                    BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();

                    int[] screenLocation = new int[2];
                    imageView.getLocationOnScreen(screenLocation);

                    saveFile(bitmap);

                    //Pass the image title and url to DetailsActivity
                    intent.putExtra("left", screenLocation[0]).
                            putExtra("top", screenLocation[1]).
                            putExtra("width", imageView.getWidth()).
                            putExtra("height", imageView.getHeight()).
                            putExtra("name", item.getTitle()).
                            putExtra("url", item.getImage());

                    //Start details activity
                    startActivity(intent);

                }catch(ClassCastException ce){
                    Toast.makeText(PhotoWindow.this, "Image not loaded yet", Toast.LENGTH_SHORT).show();
                    ce.printStackTrace();
                    Log.e("PhotoWindow",""+ce);
                }
            }
        });

        /*AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();*/
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    private void shuffleData(){
       /* mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Shuffled")
                .setAction("Images Shuffled")
                .build());*/
        Bundle params = new Bundle();
        params.putString("Shuffled", "PhotoWindow");
        mFirebaseAnalytics.logEvent("PhotoWindow", params);

        Collections.shuffle(mGridData, new Random(System.nanoTime()));
        mGridAdapter.notifyDataSetChanged();
    }

    public void saveFile(Bitmap pic){
        File cacheDir = getBaseContext().getCacheDir();
        File f = new File(cacheDir, "pic");

        try {
            FileOutputStream out = new FileOutputStream(
                    f);
            pic.compress(
                    Bitmap.CompressFormat.JPEG,
                    100, out);
            out.flush();
            out.close();

            Log.e("PhotoWindow", "File saved");

        } catch (FileNotFoundException e) {
            Log.e("PhotoWindow", "Error");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("PhotoWindow", "Error");
            e.printStackTrace();
        }

    }

    class MyAsyncTask extends AsyncTask<String, String, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(PhotoWindow.this);
        InputStream inputStream = null;
        String result = "";

        protected void onPreExecute() {
            progressDialog.setTitle("Downloading Image Data");
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    noint.setVisibility(View.VISIBLE);
                    reload.setVisibility(View.VISIBLE);
                    cancel(true);
                }
            });
            progressDialog.show();
            progressDialog.setCancelable(true);
        }

        @Override
        protected Void doInBackground(String... params) {
            ArrayList<String> param = new ArrayList<String>();
            URL url;

            /*try{
                url = new URL("http://www.sanved.com/app/wwe/"+extraString);
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
                url = new URL("http://www.wiseassenterprises.com/app/" + getResources().getString(R.string.folder) + "/" + extraString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setUseCaches(false);
                urlConnection.setConnectTimeout(4000);
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            }catch (SocketTimeoutException te){
                Log.e("Nagdi","Bai");
                try {
                    url = new URL("http://www.sanved.com/app/" + getResources().getString(R.string.folder) + "/" + extraString);
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
                    url = new URL("http://www.sanved.com/app/" + getResources().getString(R.string.folder) + "/"+ extraString);
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
                res = result;

            } catch (Exception e) {
                Log.e("StringBuilding", "Error converting result " + e.toString());
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            GridItem item;
            //parse JSON data
            try {
                //Taking JSON from Assets
                JSONObject jobj = new JSONObject(result);
                //Taking a JSON Array from the JSONObject created above
                JSONArray jarr = jobj.getJSONArray("links");

                /*
                //Making a HashMap to store the values
                HashMap<String, String> hmtemp;
                //Making a ArrayList to store the HashMap
                formList = new ArrayList<HashMap<String, String>>();*/

                //Loop to iterate all the values off the Array
                for (int i = 0; i < jarr.length(); i++) {
                    //Put the pointer on the object of the array
                    JSONObject jo_inside = jarr.getJSONObject(i);

                    Log.d("Name--", jo_inside.getString("name"));
                    Log.d("URL--", jo_inside.getString("url"));

                    //Taking the values and filling them in the HashMap
                    String name = jo_inside.getString("name");
                    String url = jo_inside.getString("url");
                    item = new GridItem();
                    item.setTitle(name);
                    item.setImage(url);

                    mGridData.add(item);
                    //Adding the objects to the ArrayList
                }

                // Shuffling the images

                Collections.shuffle(mGridData , new Random(System.nanoTime()));

                // Putting the data onto the griditem list

                mGridAdapter.setGridData(mGridData);
                mGridAdapter.notifyDataSetChanged();
                this.progressDialog.dismiss();

                // Displaying the usage of the shuffle button

                boolean firstUseTT = prefs.getBoolean("firstUseTT", true);

                if(firstUseTT) {

                    ed.putBoolean("firstUseTT", false).commit();

                    Drawable drw = ContextCompat.getDrawable(PhotoWindow.this, R.drawable.ic_shuffle_black_36dp);
                    TapTargetView.showFor(PhotoWindow.this, TapTarget.forView(findViewById(R.id.fabShuffle), "Press this to shuffle the images", "")
                                    .outerCircleColor(R.color.red)
                                    .outerCircleAlpha(0.96f)
                                    .targetCircleColor(R.color.white)
                                    .titleTextColor(R.color.white)
                                    .titleTextSize(20)
                                    .descriptionTextColor(R.color.white)
                                    .descriptionTextSize(15)
                                    .drawShadow(true)
                                    .icon(drw)
                                    .cancelable(false)
                                    .targetRadius(50)
                            , null);

                }


            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            } // catch (JSONException e)
        }

    }

    //Aug27New
    public class GridViewAdapter extends ArrayAdapter<GridItem> {

        private Context mContext;
        private int layoutResourceId;
        private ArrayList<GridItem> mGridData = new ArrayList<GridItem>();
        private ProgressDialog pg;
        private boolean fuse=true;
        ImageLoaderConfiguration config;

        public GridViewAdapter(Context mContext, int layoutResourceId, ArrayList<GridItem> mGridData) {
            super(mContext, layoutResourceId, mGridData);
            this.layoutResourceId = layoutResourceId;
            this.mContext = mContext;
            this.mGridData = mGridData;

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                    .imageDownloader(new BaseImageDownloader(mContext, 10 * 1000, 20 * 1000))
                    .build();
            ImageLoader.getInstance().init(config);
            imageLoader = ImageLoader.getInstance();

        }


        /**
         * Updates grid data and refresh grid items.
         *
         * @param mGridData
         */
        public void setGridData(ArrayList<GridItem> mGridData) {
            this.mGridData = mGridData;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            final ViewHolder holder;

            if (row == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new ViewHolder();
                //holder.titleTextView = (TextView) row.findViewById(R.id.grid_item_title);
                holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);
                holder.cp = (CircularProgressBar) row.findViewById(R.id.cpGrid);
                holder.percentage = (TextView) row.findViewById(R.id.percentage);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            GridItem item = mGridData.get(position);
            //holder.titleTextView.setText(Html.fromHtml(item.getTitle()));

            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.ic_error_black_48dp) // resource or drawable
                    .showImageOnFail(R.drawable.ic_error_black_48dp)
                    .showImageOnLoading(R.drawable.white)
                    .cacheInMemory(false)
                    .cacheOnDisk(true)
                    .build();

            imageLoader.displayImage(item.getImage(), holder.imageView, options , new SimpleImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    holder.cp.setProgress(0);
                    holder.percentage.setText("0%");
                    holder.percentage.setVisibility(View.VISIBLE);
                    holder.cp.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    super.onLoadingFailed(imageUri, view, failReason);
                    holder.cp.setVisibility(View.GONE);
                    holder.percentage.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.cp.setVisibility(View.GONE);
                    holder.percentage.setVisibility(View.GONE);
                    //notifyDataSetChanged();
                }
            }, new ImageLoadingProgressListener() {
                @Override
                public void onProgressUpdate(String imageUri, View view, int current, int total) {
                    holder.cp.setProgress(Math.round(100.0f * current / total));
                    holder.percentage.setText( Math.round(100.0f * current / total) + "%");
                }
            });


        // Old Image pickers

        /*Picasso
                .with(mContext)
                .load(item.getImage())
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.gif))
                .error(ContextCompat.getDrawable(mContext, R.drawable.ic_error_black_48dp))
                .memoryPolicy(MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });

        Glide
                .with(mContext)
                .load(item.getImage())
                .asBitmap()
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.gif))
                .error(ContextCompat.getDrawable(mContext, R.drawable.ic_error_black_48dp))
                .diskCacheStrategy( DiskCacheStrategy.NONE )
                .skipMemoryCache( true )
                .into(holder.imageView);*/


            return row;
        }

        class ViewHolder {
            ImageView imageView;
            CircularProgressBar cp;
            TextView percentage;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Aug27New
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Aug27New
            case R.id.about:
                Intent i = new Intent(PhotoWindow.this, About.class);
                startActivity(i);
                break;
            case R.id.suggest:
                AlertDialog.Builder build = new AlertDialog.Builder(PhotoWindow.this);

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
                                    Toast.makeText(PhotoWindow.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
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
                AlertDialog.Builder build2 = new AlertDialog.Builder(PhotoWindow.this);
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
                                    Toast.makeText(PhotoWindow.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
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
                params.putString("Share", "PhotoWindow");
                mFirebaseAnalytics.logEvent("PhotoWindow", params);

                String shareBody = "Hey, check out this app - " + getResources().getString(R.string.name) +" Wallpapers HD 4K with amazing " + getResources().getString(R.string.name) +" images\nhttps://play.google.com/store/apps/details?id=" + getPackageName();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
                break;

            //Aug27New
            case R.id.disclaim:
                Intent i7 = new Intent(PhotoWindow.this, AboutCopyright.class);
                startActivity(i7);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if (mAdView != null) {
            mAdView.destroy();
        }*/
        imageLoader.clearDiskCache();
        imageLoader.clearMemoryCache();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (mAdView != null) {
            mAdView.resume();
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        /*if (mAdView != null) {
            mAdView.pause();
        }*/
    }
}
