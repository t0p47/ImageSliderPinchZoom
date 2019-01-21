package com.t0p47.imagesliderpinchzoom.Helper;

import android.net.Uri;

import com.t0p47.imagesliderpinchzoom.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 01Laptop on 26.02.2018.
 */

public class AppConstant {

    // Number of columns of Grid View
    public static final int NUM_OF_COLUMNS = 3;

    // Gridview image padding
    public static final int GRID_PADDING = 8; // in dp

    // SD card image directory
    public static final String PHOTO_ALBUM = "ImageSlider";

    // supported file formats
    public static final List<String> FILE_EXTN = Arrays.asList("jpg", "jpeg",
            "png");

    //Default image filepath in raw
    public static final List<Integer> DEFAULT_IMG =  Arrays.asList(R.raw.img1, R.raw.img2, R.raw.img3, R.raw.img4, R.raw.img5, R.raw.img6, R.raw.img7, R.raw.img8, R.raw.img9);
}
