package cn.com.chinaccs.datasite.main.datasite.inspect.function;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;


import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.ui.cmos.ImageDetailsActivity;

/**
 * 隐患列表适配器
 */
public class TroublePlanListAdapter extends BaseAdapter {
    Context context;
    private JSONArray array;
    private LayoutInflater flater;
    private String bsId;
    private boolean isRRu;
    private String cellId;
    private Map<Integer, String[]> urls;
    private MyClickListener listener;
    private ImageLoader mImageLoader;
    public TroublePlanListAdapter(Context context, JSONArray array, String bsId,
                                  Boolean isRRu, String cellId) {
        this.context = context;
        this.array = array;
        flater = LayoutInflater.from(context);
        this.bsId = bsId;
        this.isRRu = isRRu;
        this.cellId = cellId;
        urls = new HashMap<Integer, String[]>();
        listener = new MyClickListener();
        RequestQueue mQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return array.length();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        try {
            return array.get(arg0);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        try {
            return array.getJSONArray(position).getLong(0);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    //获取到PlanId，用来从后台获取作业内容
    public long getPlanId(int position) {
        // TODO Auto-generated method stub
        try {
            return array.getJSONArray(position).getLong(4);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    //获取到PlanId，用来从后台获取作业内容
    public long getTroubleId(int position) {
        // TODO Auto-generated method stub
        try {
            return array.getJSONArray(position).getLong(0);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = flater.inflate(
                    R.layout.item_list_troublehandleplan, null);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = (TextView) convertView
                    .findViewById(R.id.tv_titlename);
            viewHolder.tvDes = (TextView) convertView
                    .findViewById(R.id.tv_des);
            viewHolder.tvAuto = (TextView) convertView
                    .findViewById(R.id.tv_auto);

            viewHolder.mImgView1 = (ImageView) convertView
                    .findViewById(R.id.img_dis1);
            viewHolder.mImgView2 = (ImageView) convertView
                    .findViewById(R.id.img_dis2);
            viewHolder.mImgView3 = (ImageView) convertView
                    .findViewById(R.id.img_dis3);

            viewHolder.mImgView1.setTag(position);
            viewHolder.mImgView1.setTag(position);
            viewHolder.mImgView1.setTag(position);

            viewHolder.mImgView1.setOnClickListener(listener);
            viewHolder.mImgView2.setOnClickListener(listener);
            viewHolder.mImgView3.setOnClickListener(listener);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        View view = flater.inflate(R.layout.item_list_troublehandleplan, null);
//        TextView tvTitle = (TextView) view.findViewById(R.id.tv_titlename);
//        TextView tvDes = (TextView) view.findViewById(R.id.tv_des);
//        final ImageView mImgView1 = (ImageView) view.findViewById(R.id.img_dis1);
//        final ImageView mImgView2 = (ImageView) view.findViewById(R.id.img_dis2);
//        final ImageView mImgView3 = (ImageView) view.findViewById(R.id.img_dis3);
//        mImgView1.setTag(position);
//        mImgView2.setTag(position);
//        mImgView3.setTag(position);
//        mImgView1.setOnClickListener(listener);
//        mImgView2.setOnClickListener(listener);
//        mImgView3.setOnClickListener(listener);
//        TextView tvAuto = (TextView) view.findViewById(R.id.tv_auto);
        try {
            viewHolder.tvTitle.setText("作业计划名称   :  " + array.getJSONArray(position).getString(5));
            viewHolder.tvDes.setText("隐  患  描  述   :  " + array.getJSONArray(position).getString(1));
            viewHolder.tvAuto.setText("隐患上报人  :  " + array.getJSONArray(position).getString(3));
            String urlString = array.getJSONArray(position).getString(2);
            Log.e("imgURL", urlString);
            String[] strArray = urlString.split(",");
            urls.put(Integer.valueOf(position), strArray);
            for (int i = 0; i < strArray.length; i++) {
                if (i == 0) {
                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(viewHolder.mImgView1, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
                    mImageLoader.get(strArray[i], listener,90,90);
                    viewHolder.mImgView1.setVisibility(View.VISIBLE);
                }
                if (i == 1) {
                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(viewHolder.mImgView2, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
                    mImageLoader.get(strArray[i], listener,90,90);
                    viewHolder.mImgView2.setVisibility(View.VISIBLE);
                }
                if (i == 2) {
                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(viewHolder.mImgView3, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
                    mImageLoader.get(strArray[i], listener,90,90);
                    viewHolder.mImgView3.setVisibility(View.VISIBLE);
                }
            }

        } catch (JSONException e) {
            Log.e("imgURL", "is fail");
            e.printStackTrace();
        }
        return convertView;
    }

    //查看大图按钮监听器
    public class MyClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            int positon = (Integer) v.getTag();
            String[] urlsStrings = urls.get(Integer.valueOf(positon));
            String url = "";
            switch (v.getId()) {
                case R.id.img_dis1:
                    url = urlsStrings[0];
                    break;
                case R.id.img_dis2:
                    url = urlsStrings[1];
                    break;
                case R.id.img_dis3:
                    url = urlsStrings[2];
                    break;
                default:
                    break;
            }
            Log.e("ImageDetailURL", "url=" + url + "posion=" + positon + "v.getId=" + v.getId());
            Intent intent = new Intent(context, ImageDetailsActivity.class);
            intent.putExtra("image_path", url);
            context.startActivity(intent);
        }
    }

    public class ViewHolder {
        TextView tvTitle;
        TextView tvDes;
        TextView tvAuto;
        ImageView mImgView1;
        ImageView mImgView2;
        ImageView mImgView3;
    }

    public class BitmapCache implements ImageLoader.ImageCache {
        private LruCache<String, Bitmap> mCache;
        public BitmapCache() {
            int maxSize = 10 * 1024 * 1024;
            mCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes() * value.getHeight();
                }

            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url, bitmap);
        }

    }
}

