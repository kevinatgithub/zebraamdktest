package com.example.kevin.henobarcodetest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;

import java.util.ArrayList;

public class Register extends AppCompatActivity implements EMDKManager.EMDKListener, Scanner.DataListener, Scanner.StatusListener {

    private Button backBtn;
    private TextView scanTv;
    private TextView stateTv;
    private EMDKManager emdkManager;
    private BarcodeManager barcodeManager;
    private Scanner scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        backBtn = findViewById(R.id.backBtn);
        scanTv = findViewById(R.id.scanTv);
        stateTv = findViewById(R.id.stateTv);

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
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
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
        new AsyncDataUpdate().execute(scanDataCollection).getStatus();
    }

    @Override
    public void onStatus(StatusData statusData) {
        new AsynStatusUpdate().execute(statusData);
    }

    private class AsyncDataUpdate extends AsyncTask<ScanDataCollection, Void, String>{

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
            RegisterSave save = new RegisterSave(getApplicationContext(),s);
            save.save();
            scanTv.append(s + "\n");
            resetScanner();
            try {
                initScanner();
            } catch (ScannerException e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(),"Item has been registered!",Toast.LENGTH_SHORT).show();

            try {
                initScanner();
            } catch (ScannerException e) {
                e.printStackTrace();
            }

            Toast.makeText(getApplicationContext(),"Scan Barcode Now",Toast.LENGTH_LONG).show();
        }
    }

    private class AsynStatusUpdate extends AsyncTask<StatusData, Void, String>{

        @Override
        protected String doInBackground(StatusData... statusData) {
            String status = "";
            StatusData data = statusData[0];
            StatusData.ScannerStates state = data.getState();

            switch (state){
                case IDLE:
                    status = "IDLE";
                    break;
                case ERROR:
                    status = "ERROR";
                    break;
                case WAITING:
                    status = "WAITING";
                    break;
                case SCANNING:
                    status = "SCANNING";
                    break;
                case DISABLED:
                    status = "DISABLED";
                    break;
            }
            return status;
        }

        private String prevState = "";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            stateTv.setText(s);
            if(s == "SCANNING" && prevState == ""){
                prevState = "SCANNING";
            }else if(s != "IDLE"){
                prevState = "";
            }
            if (s == "IDLE" && prevState == "SCANNING"){
                resetScanner();
                try {
                    initScanner();
                } catch (ScannerException e) {
                    e.printStackTrace();
                }
            }
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
