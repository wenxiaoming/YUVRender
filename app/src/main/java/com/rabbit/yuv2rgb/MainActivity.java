package com.rabbit.yuv2rgb;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    Yuv2RgbView mYuv2rgbView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button)findViewById(R.id.button_start);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view)
            {
                ShaderManager.loadFromFile(MainActivity.this.getResources());
                mYuv2rgbView = new Yuv2RgbView(MainActivity.this);

                mYuv2rgbView.requestFocus();
                mYuv2rgbView.setFocusableInTouchMode(true);

                setContentView(mYuv2rgbView);
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
