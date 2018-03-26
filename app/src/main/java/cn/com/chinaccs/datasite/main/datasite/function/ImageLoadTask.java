package cn.com.chinaccs.datasite.main.datasite.function;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageLoadTask extends AsyncTask<Object, Object, Bitmap> {
	private ImageView img = null;
	private ProgressBar pb = null;
	private TextView tv = null;

	public ImageLoadTask(ImageView img, ProgressBar pb, TextView tv) {
		this.img = img;
		this.pb = pb;
		this.tv = tv;
		if (pb != null) {
			pb.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected Bitmap doInBackground(Object... params) {
		// TODO Auto-generated method stub
		Bitmap bmp = null;
		try {
			bmp = BitmapFactory.decodeStream(new URL((String) params[0])
					.openStream());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bmp;
	}

	protected void onPostExecute(Bitmap result) {
		if (img != null)
			img.setImageBitmap(result);
		if (pb != null)
			pb.setVisibility(View.GONE);
		if (result == null && tv != null)
			tv.append("--暂无照片！");
		result = null;
	}
}
