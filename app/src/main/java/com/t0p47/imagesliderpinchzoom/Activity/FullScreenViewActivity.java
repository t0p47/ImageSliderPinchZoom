package com.t0p47.imagesliderpinchzoom.Activity;

/**
 * Created by 01Laptop on 26.02.2018.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.t0p47.imagesliderpinchzoom.Adapter.FullScreenImageAdapter;
import com.t0p47.imagesliderpinchzoom.Helper.SessionManager;
import com.t0p47.imagesliderpinchzoom.Helper.Utils;
import com.t0p47.imagesliderpinchzoom.R;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

public class FullScreenViewActivity extends Activity{

    private static final String TAG = "LOG_TAG";

    private Utils utils;
    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;
    private SessionManager session;
    int imgPosition = 0;

    private int REQUEST_DIRECTORY = 91;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_view);

        viewPager = (ViewPager) findViewById(R.id.pager);
        session = new SessionManager(this);

        utils = new Utils(getApplicationContext());

        Intent i = getIntent();
        imgPosition = i.getIntExtra("imgPosition", 0);

        String currentImgFilepath = session.getCurrentImgFolder();
        //Если есть путь до картинок
        if(currentImgFilepath!=null){
            adapter = new FullScreenImageAdapter(FullScreenViewActivity.this,
                    utils.getImagesFilePaths(session.getCurrentImgFolder()));

            viewPager.setAdapter(adapter);

            // displaying selected image first
            viewPager.setCurrentItem(imgPosition);
        //Нет пути до картинок
        }else{
            final Intent chooserIntent = new Intent(this, DirectoryChooserActivity.class);

            final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                    .newDirectoryName("DirChooserSample")
                    .allowReadOnlyDirectory(true)
                    .allowNewDirectoryNameModification(true)
                    .build();

            chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config);

// REQUEST_DIRECTORY is a constant integer to identify the request, e.g. 0
            startActivityForResult(chooserIntent, REQUEST_DIRECTORY);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DIRECTORY) {
            Log.i(TAG, String.format("Return from DirChooser with result %d",
                    resultCode));

            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {

                String selectedDirectory = data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);

                Log.d(TAG,"FullScreenViewActivity: selected dir "+selectedDirectory);

                adapter = new FullScreenImageAdapter(FullScreenViewActivity.this,
                        utils.getImagesFilePaths(session.getCurrentImgFolder()));

                viewPager.setAdapter(adapter);

                // displaying selected image first
                viewPager.setCurrentItem(imgPosition);


            } else {
                Log.d(TAG,"FullScreenViewActivity: nothing selected");
            }
        }
    }
}

