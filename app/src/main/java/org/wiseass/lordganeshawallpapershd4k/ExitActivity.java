//Aug27New
package org.wiseass.lordganeshawallpapershd4k;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

/**
 * Created by Sanved on 28-08-2016.
 */
public class ExitActivity extends AppCompatActivity {

    TextView tv;
    Button yes, no, rate;
    private RecyclerView rv;
    private SharedPreferences pref;
    private SharedPreferences.Editor ed;
    private ArrayList<JahiratClass> list;
    private FirebaseAnalytics mFirebaseAnalytics;
    RVAdapt2 adapt2;

    private static String json, url, image, name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about2);

        rv = (RecyclerView) findViewById(R.id.rvApps);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        tv = (TextView) findViewById(R.id.tv2);
        yes = (Button) findViewById(R.id.closeYes);
        no = (Button) findViewById(R.id.closeNo);
        rate = (Button) findViewById(R.id.brate);

        tv.setVisibility(View.VISIBLE);
        yes.setVisibility(View.VISIBLE);
        no.setVisibility(View.VISIBLE);
        rate.setVisibility(View.VISIBLE);

        pref = getSharedPreferences("football", MODE_PRIVATE);
        ed = pref.edit();

        list = new ArrayList<>();

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent(ExitActivity.this, StartScreen.class);
                startActivity(returnIntent);
                finish();
            }
        });

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://play.google.com/store/apps/details?id=" + getPackageName();
                Intent i2 = new Intent(Intent.ACTION_VIEW);
                i2.setData(Uri.parse(url));
                startActivity(i2);
            }
        });

        parseJson();

    }

    public void parseJson() {
        int num = pref.getInt("appNum", 0);

        for (int i = 1; i <= num; i++) {

            JahiratClass jahirat;

            name = pref.getString("name"+i, " ");
            url = pref.getString("url"+i, " ");
            image = pref.getString("image"+i, " ");

            jahirat = new JahiratClass(name, image, url);

            list.add(jahirat);
        }
        Context con = this.getApplication();
        adapt2 = new RVAdapt2(list, con);

        rv.setAdapter(adapt2);
        adapt2.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent(ExitActivity.this, StartScreen.class);
        startActivity(returnIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle params = new Bundle();
        params.putString("load", "ExitActivity");
        mFirebaseAnalytics.logEvent("ExitActivity", params);
    }
}
