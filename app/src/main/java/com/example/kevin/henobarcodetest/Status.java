package com.example.kevin.henobarcodetest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class Status extends AppCompatActivity {

    private Button refreshBtn;
    private Button registerBtn;
    private Button flagBtn;
    private Button resetBtn;
    private ListView itemsLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        refreshBtn = findViewById(R.id.refreshBtn);
        registerBtn = findViewById(R.id.backBtn);
        flagBtn = findViewById(R.id.flagBtn);
        resetBtn = findViewById(R.id.resetBtn);

        itemsLv = findViewById(R.id.itemsLv);


        GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                final String url = "http://test.nbbnets.net/v2/public/heno/status";
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Resp resp = gson.fromJson(response.toString(),Resp.class);

                            ArrayList<HenoItem> items = new ArrayList<HenoItem>(Arrays.asList(resp.items));

                            HenoItemsAdapter adapter = new HenoItemsAdapter(getApplicationContext(),items);

                            itemsLv.setAdapter(adapter);
                            Toast.makeText(getApplicationContext(),"Refresh Done.",Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                });

                queue.add(getRequest);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
            }
        });

        flagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FlagItem.class);
                startActivity(intent);
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                final String url = "http://test.nbbnets.net/v2/public/heno/reset";
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,null, null);
                queue.add(getRequest);
            }
        });
    }

    public class Resp{

        public HenoItem[] items;

        public Resp(HenoItem[] items) {
            this.items = items;
        }

    }

    public class HenoItemsAdapter extends ArrayAdapter<HenoItem>{

        public HenoItemsAdapter(Context context, ArrayList<HenoItem> items){
            super(context, 0, items);

        }

        @Override
        public View getView(int position,View convertView,ViewGroup parent) {
            HenoItem item = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.heno_item_row, parent, false);
            }

            TextView codeTv = convertView.findViewById(R.id.codeTv);
            TextView locationTv = convertView.findViewById(R.id.locationTv);
            TextView remarksTv = convertView.findViewById(R.id.remarksTv);
            TextView timestampsTv = convertView.findViewById(R.id.timestampsTv);

            codeTv.setText(item.getCode());
            locationTv.setText(item.getLocation());
            remarksTv.setText(item.getRemarks());
            timestampsTv.setText(item.getTimestamps());

            return convertView;

        }
    }
}
