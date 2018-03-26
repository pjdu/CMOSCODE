package cn.com.chinaccs.datasite.main.datasite.function;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import cn.com.chinaccs.datasite.main.common.App;


/**
 * @author Fddi
 * 
 */
public class CompressImage {

	/**
	 * @param file
	 *            待压缩图片文件
	 * @param optHeight
	 *            缩放高度
	 * @param optWidth
	 *            缩放宽度
	 * @param size
	 *            最高品质（单位Kb）
	 * @return
	 */
	public Bitmap setCompress(File file, int optHeight, int optWidth, int size) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > optWidth) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / optWidth);
		} else if (w < h && h > optHeight) {// 如果高度高的话根据高度固定大小缩放
			be = (int) (newOpts.outHeight / optHeight);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(file.getPath(), newOpts);
		// System.out.println(srcPath);

		return compressImage(file, bitmap, size);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 质量压缩
	 * 
	 * @param image
	 * @return
	 */
	private Bitmap compressImage(File file, Bitmap image, int size) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > size) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			options -= 10;// 每次都减少10
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
		}
		Log.d(App.LOG_TAG, "options------" + options);
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		try {
			FileOutputStream fo = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, fo);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
	}
}
