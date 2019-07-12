package org.wiseass.naturewallpapershd4k;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
/*
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;*/

/**
 * Created by Sanved on 21-05-2016.
 */
public class AboutCopyright extends AppCompatActivity {

    Button email;
    //private Tracker mTracker;
    private FirebaseAnalytics mFirebaseAnalytics;

    private static final String[] EMAIL_FOR_RESPONSE = {"wiseassenter@gmail.com"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        /*AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();*/
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        email = (Button) findViewById(R.id.bEmail);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder build = new AlertDialog.Builder(AboutCopyright.this);
                build
                        .setTitle("Email")
                        .setMessage("Are you sure you want to email ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("message/rfc822");
                                i.putExtra(Intent.EXTRA_EMAIL, EMAIL_FOR_RESPONSE);
                                i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.name) + " Wallpaper App: Take down Notice");
                                i.putExtra(Intent.EXTRA_TEXT, "Here is the photo I want you to remove - <Link your photo here>. I can provide the necessary proof of it being my work.");
                                try{
                                    startActivity(Intent.createChooser(i, "Send email...."));

                                }catch(android.content.ActivityNotFoundException ex){
                                    Toast.makeText(AboutCopyright.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false);

                build.create().show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        /*mTracker.setScreenName("About");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());*/
        Bundle params = new Bundle();
        mFirebaseAnalytics.logEvent("AboutCopyright", null);
    }

}
