package com.rohan.barcodeqrcodescanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class ListActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    SharedPreferences preferences;
    JSONObject saved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ListActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(new MyAdap());

        preferences = getSharedPreferences("barcodeqrcode", Context.MODE_PRIVATE);
        Log.d("ListTesting", preferences.getString("saved",""));
        try {
            saved = new JSONObject(preferences.getString("saved",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class MyAdap extends RecyclerView.Adapter<MyAdap.Holder>{

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ListActivity.this).inflate(R.layout.my_layout, parent, false);
            Holder holder = new Holder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = mRecyclerView.getChildPosition(v);
                    Intent in = new Intent(ListActivity.this,ScanActivity.class);
                    in.putExtra("position",position);
                    startActivity(in);
                    finish();
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            try {
                holder.textView.setText(saved.getString("saved"+position));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return saved.length();
        }

        public class Holder extends RecyclerView.ViewHolder {
            TextView textView;
            public Holder(View view){
                super(view);
                textView = (TextView) view.findViewById(R.id.textView);
            }
        }
    }
}
