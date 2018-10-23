package com.example.kevin.henobarcodetest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FlagItem extends AppCompatActivity implements Scanner.DataListener, Scanner.StatusListener, EMDKManager.EMDKListener {

    private Button backBtn;
    private Button area1;
    private Button area2;
    private Button area3;
    private Button area4;
    private TextView scanTv;
    private EditText remarksTxt;
    private EMDKManager emdkManager;
    private BarcodeManager barcodeManager;
    private Scanner scanner;
    public String location;

    private RequestQueue queue;
    private final String URL = "http://test.nbbnets.net/v2/public/flag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag_item);

        backBtn = findViewById(R.id.backBtn);
        area1 = findViewById(R.id.area1);
        area2 = findViewById(R.id.area2);
        area3 = findViewById(R.id.area3);
        area4 = findViewById(R.id.area4);
        remarksTxt = findViewById(R.id.remarksTxt);
        scanTv = findViewById(R.id.scanTv);

        queue = Volley.newRequestQueue(getApplicationContext());

        EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);

        if(results.statusCode != EMDKResults.STATUS_CODE.SUCCESS){
            scanTv.setText("Scanner Failed");
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releaseScanner();
                finish();
            }
        });

        area1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                location = "AREA 1";
                openScanner();
            }
        });
    }

    private void releaseScanner(){
        if(this.emdkManager != null){
            this.emdkManager.release();
            this.emdkManager = null;
        }

        try{
            if(scanner != null){
                scanner.removeDataListener(this);
                scanner.removeStatusListener(this);
                scanner.disable();
                scanner = null;
            }
        }catch (ScannerException e){
            e.printStackTrace();
        }
    }

    private void initScanner() throws ScannerException {
        if(scanner == null){
            barcodeManager = (BarcodeManager) this.emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);
            scanner = barcodeManager.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT);
            scanner.addDataListener(this);
            scanner.addStatusListener(this);
            scanner.triggerType = Scanner.TriggerType.HARD;
            scanner.enable();
            scanner.read();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try{
            if(scanner != null){
                scanner.removeDataListener(this);
                scanner.removeStatusListener(this);
                scanner.disable();
                scanner = null;
            }
        }catch (ScannerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onData(ScanDataCollection scanDataCollection) {
        new AsyncDataUpdate().execute(scanDataCollection);
    }

    @Override
    public void onStatus(StatusData statusData) {
//        new AsynStatusUpdate().execute(statusData);
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {

    }

    public void openScanner(){
        this.emdkManager = emdkManager;

        try {
            initScanner();
        } catch (ScannerException e) {
            e.printStackTrace();
        }
        scanTv.setText("");
        Toast.makeText(getApplicationContext(),"Scan Barcode Now",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClosed() {
        releaseScanner();
    }

    private class AsyncDataUpdate extends AsyncTask<ScanDataCollection, Void, String> {

        @Override
        protected String doInBackground(ScanDataCollection... scanDataCollections) {
            String stat = "";
            ScanDataCollection sdc = scanDataCollections[0];

            if(sdc != null && sdc.getResult() == ScannerResults.SUCCESS){
                ArrayList<ScanDataCollection.ScanData> datas = sdc.getScanData();

                for(ScanDataCollection.ScanData data : datas){
                    stat = data.getData();
                }
            }
            return stat;
        }

        @Override
        protected void onPostExecute(String s) {
            scanTv.setText(s);
            releaseScanner();
            resetScanner();
            final String LOCATION = "AREA 1";
            final String CODE = s;
            final String remarks = remarksTxt.getText().toString();
            StringRequest r = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getApplicationContext(),"Item flag @ area 1",Toast.LENGTH_SHORT).show();
                }
            },null){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("code",CODE);
                    params.put("location",LOCATION);
                    params.put("remarks",remarks);
                    return super.getParams();
                }
            };
        }
    }

    public void resetScanner(){
        try{
            if(scanner != null){
                scanner.removeDataListener(this);
                scanner.removeStatusListener(this);
                scanner.disable();
                scanner = null;
            }
        }catch (ScannerException e){
            e.printStackTrace();
        }
    }
}
