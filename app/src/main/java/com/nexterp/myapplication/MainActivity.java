package com.nexterp.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;

import com.googlecode.tesseract.android.TessBaseAPI;

public class MainActivity extends AppCompatActivity {
    public static String recognizedText;
    protected String _path;
    protected Button camera;
    protected TextView tv;
    protected static final String PHOTO_TAKEN = "photo_taken";
    public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() ;
    public static final String lang = "eng";
    private static final String TAG = "MainActivity.java";
    protected boolean _taken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }

        }

        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {
                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + lang + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();

                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }
**/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
       // FileInputStream fis;
        //File path;
        tv = (TextView) findViewById(R.id.sample_text);
        camera=(Button) findViewById(R.id.camerabutton);
        camera.setOnClickListener(new CameraClickHandler());
       /** try{
            ClassLoader classLoader = getClass().getClassLoader();
                path = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/1.png");

            fis = new FileInputStream(path);}
        catch(FileNotFoundException e){
            tv.setText("lol");
            e.printStackTrace();
            return;
        }**/
        _path = DATA_PATH + "/1.jpg";
/**
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
    // Example of a call to a native method

        ClassLoader classLoader = getClass().getClassLoader();
        TessBaseAPI tessTwo = new TessBaseAPI();


        tessTwo.init(Environment.getExternalStorageDirectory().getAbsolutePath(), "eng2");
            tessTwo.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK);

        tessTwo.setImage(bitmap);
        recognizedText = tessTwo.getUTF8Text();
        tv.setText(recognizedText);        //tv.setText(stringFromJNI());

   **/
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public class CameraClickHandler implements View.OnClickListener {
        public void onClick(View view) {
           // Log.v(TAG, "Starting Camera app");
            startCameraActivity();
        }
    }

    protected void startCameraActivity() {
        File file = new File(_path);
        Uri outputFileUri = Uri.fromFile(file);

        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "resultCode: " + resultCode);

        if (resultCode == -1) {
            onEquationPhotoTaken();
        } else {
            Log.v(TAG, "User cancelled");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(MainActivity.PHOTO_TAKEN, _taken);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState()");
        if (savedInstanceState.getBoolean(MainActivity.PHOTO_TAKEN)) {
            onEquationPhotoTaken();
        }
    }

    protected void onEquationPhotoTaken() {
        _taken = true;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile(_path, options);

        try {
            // we are required to convert to ARGB_8888 for the tesseract engine
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        } catch (Exception e){
            Log.e(TAG, "Could not convert bitmap to ARGB_8888: " + e.toString());
        }

        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(DATA_PATH, lang);
        baseApi.setImage(bitmap);

        String recognizedText = baseApi.getUTF8Text();

        baseApi.end();

        // Strip returned text down to alpha-numerics
        Log.v(TAG, "Raw OCR text: " + recognizedText);

        // only strip if lang is eng
        if ( lang.equalsIgnoreCase("eng") ) {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        }

        recognizedText = recognizedText.trim();

        if (recognizedText.length() != 0) {
            tv.setText(recognizedText);
            //_field.setSelection(_field.getText().toString().length());
        }
    }

}
