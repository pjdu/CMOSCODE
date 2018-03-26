/*
 * Created by AndyHua on 2017/8/22.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-08-19 15:22:50.
 */

package com.ytf.asky.library.zbar;

public class Result {
    private String mContents;
    private BarcodeFormat mBarcodeFormat;

    public void setContents(String contents) {
        mContents = contents;
    }

    public void setBarcodeFormat(BarcodeFormat format) {
        mBarcodeFormat = format;
    }

    public BarcodeFormat getBarcodeFormat() {
        return mBarcodeFormat;
    }

    public String getContents() {
        return mContents;
    }
}