package com.rohan.barcodeqrcodescanner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class ScanActivity extends AppCompatActivity {

    TextView hello;
    Uri uri;
    Button copy, scan, open, save;
    String copyText = "";
    JSONObject saved = new JSONObject();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        init();

        Intent in = getIntent();
        if (in.getIntExtra("position", -1) != -1) {

            try {
                if (!preferences.getString("saved", "").equals(""))
                    saved = new JSONObject(preferences.getString("saved", ""));
                hello.setText(saved.getString("saved"+in.getIntExtra("position",0)));
                copyText = saved.getString("saved"+in.getIntExtra("position",0));
                changeView(1);
                try {
                    URL url = new URL(copyText);
                    uri = Uri.parse(copyText);
                    open.setText("Open Link");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    uri = Uri.parse("http://google.com/search?q=" + copyText);
                    open.setText("Search Web");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            changeView(0);
            openScanner();
        }


        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (copyText.equals("")) {
                    Toast.makeText(ScanActivity.this, "Nothing to copy", Toast.LENGTH_SHORT).show();
                } else {
                    ClipboardManager clipMan = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("QRTest", copyText);
                    clipMan.setPrimaryClip(clip);
                    Toast.makeText(ScanActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScanner();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!copyText.equals("")) {
                    try {
                        if (!preferences.getString("saved", "").equals(""))
                            saved = new JSONObject(preferences.getString("saved", ""));
                        saved.put("saved" + saved.length(), copyText);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("testing", saved + "");
                    editor.putString("saved", saved.toString());
                    editor.apply();
                    Intent intent = new Intent(ScanActivity.this, ListActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ScanActivity.this, "Sorry.. Scan a barcode or QR code and try again..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        preferences = getSharedPreferences("barcodeqrcode", Context.MODE_PRIVATE);
        editor = preferences.edit();
        copy = (Button) findViewById(R.id.copy);
        open = (Button) findViewById(R.id.open);
        scan = (Button) findViewById(R.id.scan);
        save = (Button) findViewById(R.id.save);
        hello = (TextView) findViewById(R.id.hello);
    }

    private void changeView(int view) {
        if (view == 0) {
            copy.setVisibility(View.INVISIBLE);
            open.setVisibility(View.INVISIBLE);
            save.setVisibility(View.INVISIBLE);
        } else {
            copy.setVisibility(View.VISIBLE);
            open.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);
        }
    }

    private void openScanner() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(ScanActivity.this);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setPrompt("Scan the barcode or QR code to get the data!");
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("ScanActivity", "Cancelled scan");
                hello.setText("OOPS.. You did not scan anything");
                changeView(0);
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                copyText = result.getContents();
                Log.d("ScanActivity", "Scanned");
                hello.setText(result.getContents());
                try {
                    URL url = new URL(result.getContents());
                    uri = Uri.parse(result.getContents());
                    open.setText("Open Link");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    uri = Uri.parse("http://google.com/search?q=" + result.getContents());
                    open.setText("Search Web");
                }
                changeView(1);
//                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if(preferences.getString("saved","").equals("")){
                Toast.makeText(ScanActivity.this, "Nothing saved yet", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent in = new Intent(ScanActivity.this,ListActivity.class);
                startActivity(in);
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
