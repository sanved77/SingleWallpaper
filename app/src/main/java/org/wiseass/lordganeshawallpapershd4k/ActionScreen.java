package org.wiseass.lordganeshawallpapershd4k;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/*
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;*/
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Sanved on 21-05-2016.
 */
public class ActionScreen extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor ed;
    private ImageView imageView;
    FloatingActionButton fab, fab2, fab3;
    private FrameLayout frameLayout;
    private ColorDrawable colorDrawable;
    private InputStream is;
    private Bitmap bmp;

    private static final int ANIM_DURATION = 600;
    private int mLeftDelta;
    private int mTopDelta;
    private float mWidthScale;
    private float mHeightScale;
    private int thumbnailTop;
    private int thumbnailLeft;
    private int thumbnailWidth;
    private int thumbnailHeight;
    private String image;
    private int keynum, keyfav;
    //private Tracker mTracker;
    private InterstitialAd mInterstitialAd;
    private FirebaseAnalytics mFirebaseAnalytics;


    //New
    int pos = 0;
    int width, height;
    File cacheDir;
    Uri downloadUri;
    Uri abc;
    UCrop.Options options;
    boolean freePhone = true;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting details screen layout
        setContentView(R.layout.action_screen);

        pref = getSharedPreferences("football", MODE_PRIVATE);
        ed = pref.edit();

        fab = (FloatingActionButton) findViewById(R.id.udtahuyaButton);
        fab2 = (FloatingActionButton) findViewById(R.id.udtahuyaButton2);
        fab3 = (FloatingActionButton) findViewById(R.id.udtahuyaButton3);

        /*AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();*/
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //retrieves the thumbnail data
        Bundle bundle = getIntent().getExtras();
        thumbnailTop = bundle.getInt("top");
        thumbnailLeft = bundle.getInt("left");
        thumbnailWidth = bundle.getInt("width");
        thumbnailHeight = bundle.getInt("height");

        image = bundle.getString("url");

        //Aug27New

        //Favorite on off
        if (getFavoriteData()) {
            fab3.setImageResource(R.drawable.ic_favorite_black_36dp);
        }

        //Get file
        Bitmap bmp = getFile();

        //Set image url
        imageView = (ImageView) findViewById(R.id.grid_item_image);

        if (bmp == null) {
            Toast.makeText(ActionScreen.this, "Error occured. Press back and open again.", Toast.LENGTH_SHORT).show();
            Drawable dr = ContextCompat.getDrawable(this, R.drawable.ic_error_black_48dp);
            imageView.setImageDrawable(dr);
            Log.e("ActionScreen", "Error");
        } else {
            imageView.setImageBitmap(bmp);
            Log.e("ActionScreen", "File applied");
        }

        //Set the background color to black
        frameLayout = (FrameLayout) findViewById(R.id.main_background);
        colorDrawable = new ColorDrawable(Color.BLACK);
        frameLayout.setBackground(colorDrawable);

        // Only run the animation if we're coming from the parent activity, not if
        // we're recreated automatically by the window manager (e.g., device rotation)
        /*if (savedInstanceState == null) {
            ViewTreeObserver observer = imageView.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    imageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Figure out where the thumbnail and full size versions are, relative
                    // to the screen and each fav
                    int[] screenLocation = new int[2];
                    imageView.getLocationOnScreen(screenLocation);
                    mLeftDelta = thumbnailLeft - screenLocation[0];
                    mTopDelta = thumbnailTop - screenLocation[1];

                    // Scale factors to make the large version the same size as the thumbnail
                    mWidthScale = (float) thumbnailWidth / imageView.getWidth();
                    mHeightScale = (float) thumbnailHeight / imageView.getHeight();


                    return true;
                }
            });
        }*/


        // TODO: 29-08-2016 Full page ad unit

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.full_page));

        requestNewInterstitial();

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                /*mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Full Page Ad")
                        .setAction("Opened")
                        .build());*/
                Bundle params = new Bundle();
                params.putString("FullPageAd", "ActionScreen");
                mFirebaseAnalytics.logEvent("ActionScreen", params);
            }
        });

        // // TODO: 29-08-2016 Banner add Aug27New 
        mAdView = (AdView) findViewById(R.id.ads);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActionScreen.this);
                builder.setMessage("Use this as Wallpaper?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                cropper();
                                /*mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("ImageOpened")
                                        .setAction("Wallpaper - " + image)
                                        .build());*/
                                Bundle params = new Bundle();
                                params.putString("ImageUsed", "ActionScreen");
                                params.putString("image",image);
                                mFirebaseAnalytics.logEvent("ActionScreen", params);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActionScreen.this);
                builder.setMessage("Want to download this image ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                                } else {
                                    downloadFile(image);
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        //Aug27New
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteImage();
            }
        });

        /*mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("ImageOpened")
                .setAction("Opened - " + image)
                .build());*/

        Bundle params = new Bundle();
        params.putString("ImageOpened", "ActionScreen");
        params.putString("image",image);
        mFirebaseAnalytics.logEvent("ActionScreen", params);

    }

    public Bitmap getFile() {
        File cacheDir = getBaseContext().getCacheDir();
        File f = new File(cacheDir, "pic");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            Log.e("ActionScreen", "File taken");
            return bitmap;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("ActionScreen", "Error");
            return null;
        }

    }

    public void wallpaperSetter(Uri res1) {
        try {
            bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), res1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        WallpaperManager myWallpaperManager
                = WallpaperManager.getInstance(getApplicationContext());
        try {
            myWallpaperManager.setBitmap(bmp);
            AlertDialog.Builder builder2 = new AlertDialog.Builder(ActionScreen.this);
            builder2.setMessage("Wallpaper has been set :-)")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            }
                        }
                    });
            AlertDialog alert = builder2.create();
            alert.show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ActionScreen.this, "Oops ! Something went wrong", Toast.LENGTH_LONG).show();
        }

    }

    //Aug27New
    public void cropper() {

        //Setting up the cropper

        cacheDir = getBaseContext().getCacheDir();
        downloadUri = Uri.fromFile(new File(cacheDir, "pic"));
        //File f = new File(cacheDir, "pic");

        abc = Uri.fromFile(new File(getCacheDir(), "1"));

        options = new UCrop.Options();
        options.setCompressionQuality(100);
        options.setMaxBitmapSize(10000);
        options.setToolbarColor(ContextCompat.getColor(this, R.color.white));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.black));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.black));

        //Alert Dialog Code

        final androidx.appcompat.app.AlertDialog.Builder build = new androidx.appcompat.app.AlertDialog.Builder(ActionScreen.this);

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.crop_dialog, null);

        final ImageButton free = (ImageButton) dialogView.findViewById(R.id.ivFreeCrop);
        final ImageButton phonesize = (ImageButton) dialogView.findViewById(R.id.ivPhoneSize);
        final TextView tfree = (TextView) dialogView.findViewById(R.id.tvFreeCrop);
        final TextView tphonesize = (TextView) dialogView.findViewById(R.id.tvPhoneSize);

        build
                .setTitle("Crop Options")
                .setView(dialogView)
                .setMessage("Select a way to use crop")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true);

        final androidx.appcompat.app.AlertDialog diag = build.create();

        phonesize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                width = size.x;
                height = size.y;
                UCrop.of(downloadUri, abc)
                        .withMaxResultSize(width, height)
                        .withAspectRatio(width, height)
                        .withOptions(options)
                        .start(ActionScreen.this);
                diag.dismiss();
                freePhone = false;
            }
        });

        tphonesize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                width = size.x;
                height = size.y;
                UCrop.of(downloadUri, abc)
                        .withMaxResultSize(width, height)
                        .withAspectRatio(width, height)
                        .withOptions(options)
                        .start(ActionScreen.this);
                diag.dismiss();
                freePhone = false;
            }
        });

        free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UCrop.of(downloadUri, abc)
                        .withOptions(options)
                        .start(ActionScreen.this);
                diag.dismiss();
                freePhone = true;
            }
        });

        tfree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UCrop.of(downloadUri, abc)
                        .withOptions(options)
                        .start(ActionScreen.this);
                diag.dismiss();
                freePhone = true;
            }
        });

        diag.show();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            wallpaperSetter(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    public void downloadFile(String uRl) {
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/" + getResources().getString(R.string.name) + "Wallpapers");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        DownloadManager mgr = (DownloadManager) getSystemService(this.DOWNLOAD_SERVICE);

        keynum = pref.getInt("keyNum", 1);

        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(getResources().getString(R.string.name) + " Wallpaper")
                .setDescription("Downloaded using " + getResources().getString(R.string.name) + " Wallpapers HD app")
                .setDestinationInExternalPublicDir("/" + getResources().getString(R.string.name) + "Wallpaper", getResources().getString(R.string.name) + keynum + ".jpg")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        mgr.enqueue(request);

        keynum++;
        ed.putInt("keyNum", keynum);
        ed.apply();

        if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActionScreen.this);
            builder.setMessage("Image is being downloaded")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            }
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        /*mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("ImageOpened")
                .setAction("Download - " + image)
                .build());*/

        Bundle params = new Bundle();
        params.putString("ImageDownloaded", "ActionScreen");
        params.putString("image",image);
        mFirebaseAnalytics.logEvent("ActionScreen", params);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadFile(image);
            AlertDialog.Builder builder = new AlertDialog.Builder(ActionScreen.this);
            builder.setMessage("Image is being downloaded")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            }
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    //Aug27New
    public void favoriteImage() {

        boolean alreadyThere = false, deleteThis = false;

        keyfav = pref.getInt("keyfav", 1);


        for (int i = 1; i < keyfav; i++) {

            //Checking if file exists
            String a = pref.getString("link" + i, "");

            if (a.equals(image)) {
                pos = i;
                AlertDialog.Builder builder = new AlertDialog.Builder(ActionScreen.this);
                builder.setMessage("Remove this from the Favorites list?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                //Adding to favorites
                                int n;
                                keyfav = pref.getInt("keyfav", 1);

                                if (pos == keyfav) {
                                    ed.putInt("keyfav", keyfav - 1);
                                    ed.commit();
                                } else {
                                    for (int i = pos; i < keyfav; i++) {
                                        n = i + 1;
                                        ed.putString("link" + i, pref.getString("link" + n, ""));
                                    }
                                    ed.putInt("keyfav", keyfav - 1);
                                    ed.commit();
                                }
                                fab3.setImageResource(R.drawable.ic_favorite_border_black_36dp);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                alreadyThere = true;
                break;
            }
        }

        if (!alreadyThere) {

            AlertDialog.Builder builder = new AlertDialog.Builder(ActionScreen.this);
            builder.setMessage("Add this to the Favorites list?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            //Adding to favorites
                            ed.putString("link" + keyfav, image);
                            keyfav++;
                            ed.putInt("keyfav", keyfav);
                            ed.apply();
                            Toast.makeText(ActionScreen.this, "Image added in Favorites", Toast.LENGTH_SHORT).show();
                            fab3.setImageResource(R.drawable.ic_favorite_black_36dp);

                            /*mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("ImageOpened")
                                    .setAction("Favorite - " + image)
                                    .build());*/
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public boolean getFavoriteData() {
        //parse SharedPref data
        keyfav = pref.getInt("keyfav", 1);

        for (int i = 1; i < keyfav; i++) {
            String url = pref.getString("link" + i, "");
            if (url.equals(image)) {
                return true;
            }
        }
        return false;
    }

    /*public Bitmap makeBigAssWallpaper(Bitmap bmp){

        //Taking screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        //Making a blank canvas to put both the images
        Bitmap bit2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bit2);

        //Making a black bitmap
        Bitmap bitblack = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitblack.eraseColor(Color.BLACK);


        //Doing the ratio math
        float scale = width/bmp.getWidth();
        float xtrans = 0.0f, ytrans = (height - bmp.getHeight() * scale)/ 2.0f;
        Matrix trans = new Matrix();
        trans.postTranslate(xtrans, ytrans);
        trans.preScale(scale,scale);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        c.drawBitmap(bmp,trans, paint);

        WallpaperManager myWallpaperManager
                = WallpaperManager.getInstance(getApplicationContext());
        try {
            myWallpaperManager.setBitmap(bitblack);
            AlertDialog.Builder builder2 = new AlertDialog.Builder(ActionScreen.this);
            builder2.setMessage("Wallpaper nagdi been set :-)")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            }
                        }
                    });
            AlertDialog alert = builder2.create();
            alert.show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ActionScreen.this, "Oops ! Something went wrong", Toast.LENGTH_LONG).show();
        }

        //Doing the x,y math
        int mid1, mid2, initx;
        mid1 = height / 2;
        mid2 = bmp.getHeight() / 2;
        initx = mid1 - mid2;



        return bmp;
    }*/

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAnalytics.logEvent("ActionScreen", null);
        if (mAdView != null) {
            mAdView.resume();
        }
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
