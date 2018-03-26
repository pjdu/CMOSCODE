package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.widget.ZoomImageView;


/**
 * 查看大图的界面
 * @author Administrator
 *
 */
public class ImageDetailsActivity extends Activity {
    private ZoomImageView imageView;
  private TextView tv;
    RequestQueue mRequestQueue = null;
    int width;
    int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.image_details);
        getHeWi();
        mRequestQueue = Volley.newRequestQueue(this);
        imageView = (ZoomImageView) findViewById(R.id.zoom_image_view);
        tv=(TextView) findViewById(R.id.tv_state);
        String imagePath = getIntent().getStringExtra("image_path");
        ImageRequest imageRequest = new ImageRequest(
                imagePath,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                        imageView.setVisibility(View.VISIBLE);
                        tv.setVisibility(View.GONE);
                    }
                }, width, height, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                imageView.setImageResource(R.drawable.default_image);
            }
        });
        mRequestQueue.add(imageRequest);
    }

    private void getHeWi() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;// 屏幕宽度（像素）
        height = metric.heightPixels;// 屏幕高度（像素）
    }
}