package cn.com.chinaccs.datasite.main.util;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class UtilMemory {
	/**
	 * 当前可用内存大小
	 * 
	 * @param context
	 * @return
	 */
	public static long getAvailMemory(Context context) {
		// 获取android当前可用内存大小
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// mi.availMem; 当前系统的可用内存

		// return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
		return mi.availMem / (1024 * 1024);
	}

	/**
	 * 系统总内存大小
	 * 
	 * @param context
	 * @return
	 */
	public static long getTotalMemory(Context context) {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;

		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

			arrayOfString = str2.split("\\s+");
			for (String num : arrayOfString) {
				Log.i(str2, num + "\t");
			}

			initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
			localBufferedReader.close();

		} catch (IOException e) {
		}
		// return Formatter.formatFileSize(context, initial_memory);//
		// Byte转换为KB或者MB，内存大小规格化
		return initial_memory / (1024 * 1024);
	}

	/**
	 * 清理内存
	 * 
	 * @param context
	 */
	public static int clearMemory(Context context) {
		int total = 0;
		ActivityManager activityManger = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> list = activityManger
				.getRunningAppProcesses();
		if (list != null)
			for (int i = 0; i < list.size(); i++) {
				ActivityManager.RunningAppProcessInfo apinfo = list.get(i);
				String[] pkgList = apinfo.pkgList;
				if (apinfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) {
					// Process.killProcess(apinfo.pid);
					for (int j = 0; j < pkgList.length; j++) {
						// 2.2以上是过时的,请用killBackgroundProcesses代替
						// activityManger.restartPackage(pkgList[j]);
						// activityManger.killBackgroundProcesses(pkgList[j]);
						Log.e("Asky---", pkgList[j].toString());
						total++;
					}
				}
			}
		return total;
	}
}
