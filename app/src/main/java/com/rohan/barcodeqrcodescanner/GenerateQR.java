package com.rohan.barcodeqrcodescanner;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class GenerateQR extends AppCompatActivity {

    EditText et;
    Button bt;
    ImageView iv;
    Bitmap bmp = null;
    String qr = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);

        et = (EditText) findViewById(R.id.editText);
        bt = (Button) findViewById(R.id.generate);
        iv = (ImageView) findViewById(R.id.imageView);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et.getText().toString().equals("")) {
                    Toast.makeText(GenerateQR.this, "Please enter something to generate a QR", Toast.LENGTH_SHORT).show();
                } else {
                    qr = et.getText().toString();
                    QRCodeWriter writer = new QRCodeWriter();
                    try {
                        BitMatrix bitMatrix = writer.encode(qr, BarcodeFormat.QR_CODE, 512, 512);
                        int width = bitMatrix.getWidth();
                        int height = bitMatrix.getHeight();
                        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                        for (int x = 0; x < width; x++) {
                            for (int y = 0; y < height; y++) {
                                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                            }
                        }
                        iv.setImageBitmap(bmp);

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (bmp != null) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(GenerateQR.this);
                    alert.setTitle("SAVE??");
                    alert.setMessage("Do you want to save the generated QR code on to your device?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                                /*File file = new File(Environment.getExternalStorageDirectory()
                                        + File.separator + "Pictures/QR");
                                if (!file.exists()) {
                                    file.mkdirs();
                                }*/
                                File f = new File(Environment.getExternalStorageDirectory()
                                        + File.separator + "Pictures" /*+ File.separator + "QR" */+ File.separator + qr + ".jpg");
                                f.createNewFile();
                                FileOutputStream fo = new FileOutputStream(f);
                                fo.write(bytes.toByteArray());
                                fo.flush();
                                fo.close();
                                Toast.makeText(GenerateQR.this, "Successfully stored", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("Error", "catch");
                            }

                        }

                    });
                    alert.setNegativeButton("No", null);
                    alert.show();
                }

                return true;
            }
        });
    }
}
