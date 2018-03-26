package cn.com.chinaccs.datasite.main.datasite.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadPictrue {

    private String uri;
    private View imageView;
    private byte[] picByte;
    private ImgCallBack callBack;

    public void getPicture(String uri, ImgCallBack callBack) {
        this.uri = uri;
        this.callBack = callBack;
        new Thread(runnable).start();
    }

    @SuppressLint("HandlerLeak")
    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (picByte != null) {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    //���ŵı����������Ǻ��Ѱ�׼���ı����������ŵģ���ֵ�������ŵı�����SDK�н�����ֵ��2��ָ��ֵ,ֵԽ��ᵼ��ͼƬ������
                    opts.inSampleSize = 4;
                    opts.inPreferredConfig = Bitmap.Config.RGB_565;
                    opts.inPurgeable = true;
                    opts.inInputShareable = true;
                    Bitmap bmp = null;
                    bmp = BitmapFactory.decodeStream(new ByteArrayInputStream(picByte));
//					Bitmap bitmap = BitmapFactory.decodeByteArray(picByte, 0, picByte.length);
                    callBack.setImg(bmp);
                }
            }
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(uri);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);

                if (conn.getResponseCode() == 200) {
                    InputStream fis = conn.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] bytes = new byte[1024];
                    int length = -1;
                    while ((length = fis.read(bytes)) != -1) {
                        bos.write(bytes, 0, length);
                    }
                    picByte = bos.toByteArray();
                    bos.close();
                    fis.close();

                    Message message = new Message();
                    message.what = 1;
                    handle.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public interface ImgCallBack {
        public void setImg(Bitmap bitmap);
    }
}
