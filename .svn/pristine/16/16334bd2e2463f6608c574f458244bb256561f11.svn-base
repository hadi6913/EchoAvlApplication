package com.ar.echoafcavlapplication.Camera;

import android.hardware.Camera;

import java.util.Comparator;

public class MyPictureSizeComparator implements Comparator<Camera.Size> {
    // Used for sorting in ascending order of
    // roll name
    public int compare(Camera.Size a, Camera.Size b) {
        return (b.height * b.width) - (a.height * a.width);
    }
}
