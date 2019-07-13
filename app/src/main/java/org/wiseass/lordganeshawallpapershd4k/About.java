package org.wiseass.lordganeshawallpapershd4k;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.text.method.LinkMovementMethod;
import android.text.Html;

import java.util.ArrayList;


/**
 * Created by Sanved on 27-08-2016.
 */
public class About extends AppCompatActivity {

    private TextView link, version, about;
    private RecyclerView rv;
    private SharedPreferences pref;
    private SharedPreferences.Editor ed;
    private ArrayList<JahiratClass> list;
    RVAdapt2 adapt2;

    private static String json, url, image, name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about2);

        initVals();

        parseJson();

        linkAndVersion();

    }

    public void initVals() {

        rv = (RecyclerView) findViewById(R.id.rvApps);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        link = (TextView) findViewById(R.id.tvLink);
        version = (TextView) findViewById(R.id.tvVersion);
        about = (TextView) findViewById(R.id.tv1);

        link.setVisibility(View.VISIBLE);
        about.setVisibility(View.VISIBLE);
        version.setVisibility(View.VISIBLE);

        pref = getSharedPreferences("football", MODE_PRIVATE);
        ed = pref.edit();

        list = new ArrayList<>();
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

    public void linkAndVersion(){

        //Version

        String abc = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            abc = pInfo.versionName;
        }catch(PackageManager.NameNotFoundException ne){
            ne.printStackTrace();
        }

        version.setText(abc);

        //Link

        link.setClickable(true);
        link.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='http://www.wiseassenterprises.com'> App developed by WiseAss Enterprises </a>";
        link.setText(Html.fromHtml(text));
    }
}

