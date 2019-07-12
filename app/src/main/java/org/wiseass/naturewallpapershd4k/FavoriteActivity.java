package org.wiseass.naturewallpapershd4k;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/*
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;*/

/**
 * Created by Sanved on 14-08-2016.
 */
public class FavoriteActivity extends AppCompatActivity {

    ArrayList<HashMap<String, String>> formList;
    SharedPreferences pref;
    SharedPreferences.Editor ed;
    private GridView mGridView;
    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;
    //private AdView mAdView;
    private int keyfav;
    private Toolbar toolbar;
   // private Tracker mTracker;
   private FirebaseAnalytics mFirebaseAnalytics;

    //Aug27New
    ImageLoader imageLoader;

    String nameStr = "", res = "nagdi", res2 = "bai";
    private static final String[] EMAIL_FOR_RESPONSE = {"wiseassenter@gmail.com"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_view);

        initVals();

        if (isNetworkConnected())
            getFavoriteData();
        else {
            AlertDialog.Builder build = new AlertDialog.Builder(FavoriteActivity.this);
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
                            FavoriteActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            FavoriteActivity.this.finish();
                        }
                    })
                    .setCancelable(false);

            build.create().show();
        }
    }

    public void getFavoriteData() {
        ProgressDialog pd = new ProgressDialog(FavoriteActivity.this);
        pd.setTitle("Downloading Image Data");
        pd.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        pd.show();

        mGridData.clear();

        //parse SharedPref data
        GridItem item;
        keyfav = pref.getInt("keyfav", 1);

        for (int i = 1; i < keyfav; i++) {
            String name = "name";
            String url = pref.getString("link" + i, "");
            item = new GridItem();
            item.setTitle(name);
            item.setImage(url);

            mGridData.add(item);
            //Adding the HashMap to the ArrayList
        }

        if (mGridData.isEmpty()) {
            Toast.makeText(FavoriteActivity.this, "Favorites are empty, go to an image to add it to favorites", Toast.LENGTH_LONG).show();
        } else {
            mGridAdapter = new GridViewAdapter(this, R.layout.grid_item, mGridData);
            mGridView.setAdapter(mGridAdapter);
            mGridAdapter.setGridData(mGridData);
            mGridAdapter.notifyDataSetChanged();
        }
        pd.dismiss();

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public void initVals() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //Aug27New
        setSupportActionBar(toolbar);
        //toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle("Favorite Wallpapers");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_36dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

       /* AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        mAdView = (AdView) findViewById(R.id.ads);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);*/

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mGridView = (GridView) findViewById(R.id.gridView);

        mGridData = new ArrayList<>();


        pref = getSharedPreferences("football", MODE_PRIVATE);
        ed = pref.edit();

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get item at position
                GridItem item = (GridItem) parent.getItemAtPosition(position);

                Intent intent = new Intent(FavoriteActivity.this, ActionScreen.class);
                ImageView imageView = (ImageView) v.findViewById(R.id.grid_item_image);

                //Bitmap data
                try {

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

                } catch (ClassCastException ce) {
                    Toast.makeText(FavoriteActivity.this, "Image not loaded yet", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int position, long arg3) {
                final CharSequence[] items = {"Delete"};

                AlertDialog.Builder builder = new AlertDialog.Builder(arg1.getContext());
                builder.setTitle("Options");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                AlertDialog.Builder builder = new AlertDialog.Builder(FavoriteActivity.this);
                                builder.setMessage("Are you sure you want to Delete the Event ?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                int pos = position + 1;
                                                deleteItem(pos);
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();

                                break;
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });
    }

    public void deleteItem(int pos) {

        int n;
        keyfav = pref.getInt("keyfav", 1);

        if (pos == keyfav) {
            ed.putInt("keyfav", keyfav - 1);
            ed.commit();
        } else {
            for (int i = pos; i < keyfav; i++) {
                n = i + 1;
                String name = "name";
                ed.putString("link" + i, pref.getString("link" + n, ""));
            }
            ed.putInt("keyfav", keyfav - 1);
            ed.commit();
        }

        //Aug27New
        /*mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Favorites")
                .setAction("FavoriteRemoved" + pref.getString("link"+pos,""))
                .build());*/

        Bundle params = new Bundle();
        params.putString("FavRemoved", "Favorites");
        params.putString("image",pref.getString("link"+pos,""));
        mFirebaseAnalytics.logEvent("Favorites", params);

        Toast.makeText(FavoriteActivity.this, "Deleted from Favorites", Toast.LENGTH_SHORT).show();
        getFavoriteData();
    }

    public void saveFile(Bitmap pic) {
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

            Log.e("Dummy", "File saved");

        } catch (FileNotFoundException e) {
            Log.e("Dummy", "Error");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Dummy", "Error");
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent i = new Intent(FavoriteActivity.this, About.class);
                startActivity(i);
                break;
            case R.id.suggest:
                AlertDialog.Builder build = new AlertDialog.Builder(FavoriteActivity.this);

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
                                    Toast.makeText(FavoriteActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
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
                AlertDialog.Builder build2 = new AlertDialog.Builder(FavoriteActivity.this);
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
                                    Toast.makeText(FavoriteActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
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
                /*mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Share")
                        .setAction("App Shared")
                        .build());*/
                Bundle params = new Bundle();
                params.putString("Share", "Favorites");
                mFirebaseAnalytics.logEvent("Favorites", params);

                String shareBody = "Hey, check out this app - " + getResources().getString(R.string.name) +" Wallpapers HD 4K with amazing " + getResources().getString(R.string.name) +" images\nhttps://play.google.com/store/apps/details?id=" + getPackageName();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
                break;

            case R.id.disclaim:
                Intent i7 = new Intent(FavoriteActivity.this, AboutCopyright.class);
                startActivity(i7);
                break;

        }
        return super.onOptionsItemSelected(item);
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

            config = new ImageLoaderConfiguration.Builder(mContext).build();
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
    protected void onDestroy() {
        super.onDestroy();
        /*if (mAdView != null) {
            mAdView.destroy();
        }*/
    }

    //Aug27New
    @Override
    protected void onResume() {
        super.onResume();
        /*mTracker.setScreenName("Favorites");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        if (mAdView != null) {
            mAdView.resume();
        }*/
        getFavoriteData();
    }

    @Override
    public void onPause() {
        super.onPause();
        /*if (mAdView != null) {
            mAdView.pause();
        }*/
    }
}


