package com.renjunzheng.vendingmachine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.HashMap;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class QRCodeDetail extends AppCompatActivity {
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private ImageView mImageView;
    private TextView mTextView;
    private boolean mVisible;

    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;

    private static final HashMap<Character, String> myMap;
    static
    {
        myMap = new HashMap<>();
        myMap.put('0', "d");
        myMap.put('1', "g");
        myMap.put('2', "i");
        myMap.put('3', "e");
        myMap.put('4', "z");
        myMap.put('5', "v");
        myMap.put('6', "f");
        myMap.put('7', "x");
        myMap.put('8', "c");
        myMap.put('9', "m");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String code = intent.getStringExtra(Intent.EXTRA_TEXT);

        setContentView(R.layout.activity_qrcode_detail);

        mTextView = (TextView) findViewById(R.id.qrcode_content_textview);
        mTextView.setText("Receipt code is: " + code);
        mVisible = true;
        //mControlsView = findViewById(R.id.fullscreen_content_controls);
        mImageView = (ImageView)findViewById(R.id.qrcode_content_imageview);

        Toast.makeText(this, "Tap on the QR code so QR scanner can have a better image.", Toast.LENGTH_SHORT).show();

        try {
            Bitmap bitmap = encodeAsBitmap("uvm:"+myMap.get(code.charAt(0))+myMap.get(code.charAt(1))+myMap.get(code.charAt(2))+myMap.get(code.charAt(3)));
            if(bitmap == null) throw new RuntimeException("Cannot generate QR code");
            else mImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Toast.makeText(this, "Sorry it appears we cannot generate QR code for this number. Please try key in the number on keypad!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        // Set up the user interaction to manually show or hide the system UI.
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int LENGTH = size.x - 40;
        Log.d("qr code detail activity", "width of the qr code is: " + Integer.toString(LENGTH));
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, LENGTH, LENGTH, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            iae.printStackTrace();
            return null;
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }


    /*@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        immediateHide();
    }*/

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        //mImageView.setRotation(-90);
        /*View mLinearLayoutView = findViewById(R.id.fullscreen_content);
        mImageView.setHeight(mLinearLayoutView.getHeight());
        mImageView.setWidth(mLinearLayoutView.getHeight());
        Log.d("full screen activity", "height of the linearlayout is: " + Float.toString(mLinearLayoutView.getHeight()));
        mImageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 80);*/
        mVisible = false;
        //mControlsView.setVisibility(View.GONE);

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
        //mHideHandler.post(mHidePart2Runnable);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mImageView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            mTextView.setVisibility(View.INVISIBLE);
        }
    };

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mImageView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
        //mHideHandler.post(mShowPart2Runnable);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }

            //mControlsView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.VISIBLE);
        }
    };

    private final Handler mHideHandler = new Handler();
    /*private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };*/

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    /*private void immediateHide() {
        mHideHandler.removeCallbacks(mHideRunnable);
        //mHideHandler.postDelayed(mHideRunnable, delayMillis);
        mHideHandler.post(mHideRunnable);
    }*/
}
