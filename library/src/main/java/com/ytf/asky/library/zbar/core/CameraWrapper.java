/*
 * Created by AndyHua on 2017/8/22.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-08-19 15:22:50.
 */

package com.ytf.asky.library.zbar.core;

import android.hardware.Camera;
import android.support.annotation.NonNull;

public class CameraWrapper {
    public final Camera mCamera;
    public final int mCameraId;

    private CameraWrapper(@NonNull Camera camera, int cameraId) {
        if (camera == null) {
            throw new NullPointerException("Camera cannot be null");
        }
        this.mCamera = camera;
        this.mCameraId = cameraId;
    }

    public static CameraWrapper getWrapper(Camera camera, int cameraId) {
        if (camera == null) {
            return null;
        } else {
            return new CameraWrapper(camera, cameraId);
        }
    }
}
