package com.t0p47.imagesliderpinchzoom.Activity;

/**
 * Created by 01Laptop on 26.02.2018.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.widget.GridView;
import android.widget.Toast;

import com.t0p47.imagesliderpinchzoom.Adapter.GridViewImageAdapter;
import com.t0p47.imagesliderpinchzoom.Helper.AppConstant;
import com.t0p47.imagesliderpinchzoom.Helper.SessionManager;
import com.t0p47.imagesliderpinchzoom.Helper.StorageHelper;
import com.t0p47.imagesliderpinchzoom.Helper.Utils;
import com.t0p47.imagesliderpinchzoom.R;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

public class GridViewActivity extends Activity {

    private static final String TAG = "LOG_TAG";

    private Utils utils;
    private ArrayList<String> imagePaths = new ArrayList<String>();
    private GridViewImageAdapter adapter;
    private GridView gridView;
    private int columnWidth;

    private SessionManager session;

    private int REQUEST_DIRECTORY = 90;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);

        session = new SessionManager(this);

        if(!session.isFirstStart()){
            Log.d(TAG,"GridViewActivity: FirstStart");
            StorageHelper helper = new StorageHelper();
            ArrayList<StorageHelper.MountDevice> allDevices = helper.getAllMountedDevices();

            getInternalPublicDir(this, "AlbumTRY");

            for(StorageHelper.MountDevice device : allDevices){
                Log.d(TAG,"Device - "+device.getPath()+", type - "+device.getType());

                if(device.getType().equals(StorageHelper.MountDeviceType.EXTERNAL_SD_CARD)){
                    Log.d(TAG,"SD card found path - "+device.getPath());
                    String filepath =device.getPath()+"/Android/data/"+getPackageName()+"/files";
                    if(saveFileToSD(filepath)){
                        session.setFirstStart(true);
                        session.setCurrentImgFolder(filepath);
                        Log.d(TAG,"GridViewActivity: setFirstStart");
                    }
                }
            }
        }else{
            Log.d(TAG,"GridViewActivity: photoNotSet");
        }

        final Intent chooserIntent = new Intent(this, DirectoryChooserActivity.class);

        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("DirChooserSample")
                .allowReadOnlyDirectory(true)
                .allowNewDirectoryNameModification(true)
                .build();

        chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config);

// REQUEST_DIRECTORY is a constant integer to identify the request, e.g. 0
        startActivityForResult(chooserIntent, REQUEST_DIRECTORY);

        gridView = (GridView) findViewById(R.id.grid_view);

        utils = new Utils(this);

        // Initilizing Grid View
        /*InitilizeGridLayout();

        // loading all image paths from SD card
        imagePaths = utils.getImagesFilePaths();

        // Gridview adapter
        adapter = new GridViewImageAdapter(GridViewActivity.this, imagePaths,
                columnWidth);

        // setting grid view adapter
        gridView.setAdapter(adapter);*/
    }

    private boolean saveFileToSD(String path){
        boolean result = false;

        Log.d(TAG,"Path to sd "+path);
        try{
            File myFile = new File(path+"/save.txt");
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append("TextSaveGridView");
            myOutWriter.close();
            fOut.close();
            Toast.makeText(this, "Done writing SD 'mysdfile.txt'", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"Done writing SD 'mysdfile.txt'");
            if(copyDefaultImageToInternalSD(path)){
                Log.d(TAG,"GridViewActivity: Files copied.");
                result = true;
            }else{
                Log.d(TAG,"GridViewActivity: can't copy files.");
                result = false;
            }

        }catch(Exception e){
            Log.e(TAG,"Error opening file. Ex-"+e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    private boolean copyDefaultImageToInternalSD(String internalPath){

        boolean response = false;
        String resourceName = "img";
        String pathToImg = null;

        List<Integer> imgResList = AppConstant.DEFAULT_IMG;

        for(int i = 0;i < imgResList.size();i++){
            pathToImg = internalPath+"/"+resourceName+String.valueOf(i)+".jpg";
            try{
                InputStream in = getResources().openRawResource(imgResList.get(i));
                FileOutputStream out = null;
                out = new FileOutputStream(pathToImg);
                byte[] buff = new byte[1024];
                int read = 0;
                try{
                    while((read = in.read(buff)) > 0){
                        out.write(buff,0, read);
                    }
                    response = true;
                }finally {
                    in.close();
                    out.close();
                }
            }catch (FileNotFoundException e){
                e.printStackTrace();
                response = false;
            }catch (IOException e){
                e.printStackTrace();
                response = false;
            }
        }

        return response;
    }

    private File getInternalPublicDir(Context context, String albumName){

        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),albumName);
        if(!file.mkdirs()){
            Log.e(TAG,"Directory not created!");
        }
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DIRECTORY) {
            Log.i(TAG, String.format("Return from DirChooser with result %d",
                    resultCode));

            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED){

                String selectedDirectory = data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);
                session.setCurrentImgFolder(selectedDirectory);

                Log.d(TAG,"GridViewActivity: selected dir "+selectedDirectory);

                prepareImagePath(selectedDirectory);
            } else {
                Log.d(TAG,"GridViewActivity: nothing selected");
                String savedImgDirectory= session.getCurrentImgFolder();
                if(savedImgDirectory != null){
                    Log.d(TAG,"GridViewActivity: something in SessionManager "+savedImgDirectory);
                    prepareImagePath(savedImgDirectory);
                }
            }
        }
    }

    private void prepareImagePath(String imgDirectory){
        // Initilizing Grid View
        InitilizeGridLayout();

        // loading all image paths from SD card
        imagePaths = utils.getImagesFilePaths(imgDirectory);

        // Gridview adapter
        adapter = new GridViewImageAdapter(GridViewActivity.this, imagePaths,
                columnWidth);

        // setting grid view adapter
        gridView.setAdapter(adapter);
    }

    private void InitilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConstant.GRID_PADDING, r.getDisplayMetrics());

        columnWidth = (int) ((utils.getScreenWidth() - ((AppConstant.NUM_OF_COLUMNS + 1) * padding)) / AppConstant.NUM_OF_COLUMNS);

        gridView.setNumColumns(AppConstant.NUM_OF_COLUMNS);
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_MENU:
                Log.d(TAG,"GridViewActivity: menu button ");
                return true;
        }

        return super.onKeyDown(keycode, e);
    }
}