package org.wiseass.naturewallpapershd4k;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

/*
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;*/


/**
 * Created by Sanved on 21-05-2016.
 */
public class RVAdapt extends RecyclerView.Adapter<RVAdapt.DataHolder> {

    ArrayList<DataKaRakhwala> list;
    static ArrayList<DataKaRakhwala> list2;
    //private static Tracker mTracker;
    private static FirebaseAnalytics mFirebaseAnalytics;

    Context context;

    RVAdapt(ArrayList<DataKaRakhwala> list, Context context) {
        this.list = list;
        list2 = list;
        this.context = context;
        /*AnalyticsApplication application = (AnalyticsApplication) context;
        mTracker = application.getDefaultTracker();*/
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static class DataHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPics;
        //RelativeLayout rl;
        CardView cv;

        DataHolder(final View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.tvListName);
            tvPics = (TextView) v.findViewById(R.id.numpics);
            //rl = (RelativeLayout) v.findViewById(R.id.rlitem);
            cv = (CardView) v.findViewById(R.id.cvList);
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Selecting " + v.getContext().getResources().getString(R.string.startscr2))
                            .setAction(list2.get(getAdapterPosition()).getName())
                            .build());*/
                    Bundle params = new Bundle();
                    params.putString("Selecting " + v.getContext().getResources().getString(R.string.startscr1), "ListLoader");
                    params.putString("super",list2.get(getAdapterPosition()).getName());
                    mFirebaseAnalytics.logEvent("ListLoader", params);

                    Intent i = new Intent(v.getContext(), PhotoWindow.class);
                    String strName = "" + list2.get(getAdapterPosition()).getLink();
                    i.putExtra("STRING_I_NEED", strName);
                    i.putExtra("name", list2.get(getAdapterPosition()).getName());
                    v.getContext().startActivity(i);
                }

            });
        }

    }

    @Override
    public DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        DataHolder dh = new DataHolder(v);
        return dh;
    }

    @Override
    public void onBindViewHolder(DataHolder holder, int position) {
        holder.tvName.setText(list.get(position).getName());
        holder.tvPics.setText("" + list.get(position).getPics() + " wallpapers");
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
