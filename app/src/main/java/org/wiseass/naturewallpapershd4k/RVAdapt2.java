//Aug27New
package org.wiseass.naturewallpapershd4k;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
/*
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;*/

/**
 * Created by Sanved on 27-08-2016.
 */

public class RVAdapt2 extends RecyclerView.Adapter<RVAdapt2.DataHolder> {

    ArrayList<JahiratClass> list;
    static ArrayList<JahiratClass> list2;
   // private static Tracker mTracker;
   private static FirebaseAnalytics mFirebaseAnalytics;

    Context context;

    RVAdapt2(ArrayList<JahiratClass> list, Context context) {
        this.list = list;
        list2 = list;
        this.context = context;
        /*AnalyticsApplication application = (AnalyticsApplication) context;
        mTracker = application.getDefaultTracker();*/
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static class DataHolder extends RecyclerView.ViewHolder {

        TextView tvApp;
        ImageView ivApp;
        LinearLayout ll;

        DataHolder(final View v) {
            super(v);
            tvApp = (TextView) v.findViewById(R.id.tvApp);
            ivApp = (ImageView) v.findViewById(R.id.ivApp);
            ll = (LinearLayout) v.findViewById(R.id.llapps);
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Selecting App")
                            .setAction(list2.get(getAdapterPosition()).getName())
                            .build());*/
                    Bundle params = new Bundle();
                    params.putString("SelectingApp", "ExitActivity");
                    params.putString("super",list2.get(getAdapterPosition()).getName());
                    mFirebaseAnalytics.logEvent("ExitActivity", params);

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(""+list2.get(getAdapterPosition()).getUrl()));
                    v.getContext().startActivity(browserIntent);
                }

            });
        }

    }

    @Override
    public DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_item, parent, false);
        DataHolder dh = new DataHolder(v);
        return dh;
    }

    @Override
    public void onBindViewHolder(DataHolder holder, int position) {
        holder.tvApp.setText(list.get(position).getName());
        Picasso
                .get()
                .load(list.get(position).getImage())
                .resize(150,150)
                .into(holder.ivApp);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

