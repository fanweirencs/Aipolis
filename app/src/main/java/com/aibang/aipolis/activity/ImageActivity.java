package com.aibang.aipolis.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.aibang.aipolis.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        ImageLoader.getInstance().displayImage(getIntent().getStringExtra("imagePath"),
                                             (ImageView)findViewById(R.id.activity_image_picture));
    }
    public void finish(View view){
        finish();
    }
}
