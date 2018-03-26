package cn.com.chinaccs.datasite.main.bean;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import cn.com.chinaccs.datasite.main.util.AppScaffolding;

/**
 * android系统信息
 * 
 * @author fddi
 * 
 */
public class InfoSystem {

	public InfoSystem() {

	}

	public InfoSystem(Context context, List<Collector> list) {

	}

	public List<Collector> getSystemInfo(Context context, List<Collector> list) {
		Collector data = new Collector("系统信息(free/total)", "", 1,
				Collector.DATA_TYPE_TITLE);
		list.add(data);
		list = this.getVersion(list, context);
		list = this.getRam(list, context);
		list = this.getRom(list, context);
		list = this.getSDCard(list, context);
		list = this.getCpu(list, context);
		list.add(new Collector("Battery_free", "100", 1, Collector.UNIT_RATE));
		list.add(new Collector("Gps_state", AppScaffolding.isGpsOpen(context)
				+ "", 1, Collector.DATA_TYPE_UPLOAD));
		return list;
	}

	public List<Collector> getVersion(List<Collector> list, Context context) {
		String str1 = "/proc/version";
		String str2;
		String[] arrayOfString;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			list.add(new Collector("KernelVersion(liunx)", arrayOfString[2]
					+ "", 1));
			localBufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		list.add(new Collector("firmware version", Build.VERSION.RELEASE, 1,
				Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("model", Build.MODEL, 1,
				Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("system version", Build.DISPLAY, 1));
		return list;
	}

	public List<Collector> getRam(List<Collector> list, Context context) {
		String str1 = "/proc/meminfo";
		String str2 = "";
		String[] arrayOfString;
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			str2 = localBufferedReader.readLine();// 文件第一行为内存总数
			arrayOfString = str2.split("\\s+");
			Long size = Long.parseLong(arrayOfString[1]);
			String total = this.formatSize(size * 1024);
			str2 = localBufferedReader.readLine();// 文件第二行为内存可用数
			arrayOfString = str2.split("\\s+");
			size = Long.parseLong(arrayOfString[1]);
			String free = this.formatSize(size * 1024);
			list.add(new Collector("RAM", free + "/" + total, 1));
		} catch (IOException e) {
		}
		return list;
	}

	@SuppressWarnings("deprecation")
	public List<Collector> getRom(List<Collector> list, Context context) {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		long totalSize = totalBlocks * blockSize;

		long availableBlocks = stat.getAvailableBlocks();
		long availableSize = blockSize * availableBlocks;
		String rom = this.formatSize(availableSize) + "/"
				+ this.formatSize(totalSize);
		list.add(new Collector("ROM", rom, 1));
		return list;
	}

	@SuppressWarnings("deprecation")
	public List<Collector> getSDCard(List<Collector> list, Context context) {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long bSize = sf.getBlockSize();
			long bCount = sf.getBlockCount();
			long availBlocks = sf.getAvailableBlocks();

			long totalSize = bSize * bCount;
			long availSize = bSize * availBlocks;

			String card = this.formatSize(availSize) + "/"
					+ this.formatSize(totalSize);
			list.add(new Collector("SDCard", card, 1));
		}
		return list;
	}

	public List<Collector> getCpu(List<Collector> list, Context context) {
		String str1 = "/proc/cpuinfo";
		String str2 = "";
		String cpuM = "";
		String cpuV = "";
		String[] arrayOfString;
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			for (int i = 2; i < arrayOfString.length; i++) {
				cpuM += arrayOfString[i] + " ";// cpu型号
			}
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			cpuV += arrayOfString[2] + "";// cpu频率
			localBufferedReader.close();
		} catch (IOException e) {
		}
		list.add(new Collector("CPU", cpuM + "/" + cpuV, 1));
		return list;
	}

	public String formatSize(long size) {
		String suffix = null;
		float fSize = 0;

		if (size >= 1024) {
			suffix = "KB";
			fSize = size / 1024;
			if (fSize >= 1024) {
				suffix = "MB";
				fSize /= 1024;
			}
			if (fSize >= 1024) {
				suffix = "GB";
				fSize /= 1024;
			}
		} else {
			fSize = size;
		}
		java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
		StringBuilder resultBuffer = new StringBuilder(df.format(fSize));
		if (suffix != null)
			resultBuffer.append(suffix);
		return resultBuffer.toString();
	}
}
