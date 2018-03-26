package cn.com.chinaccs.datasite.main.util;

import android.util.Log;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.com.chinaccs.datasite.main.CoConfig;


public class MultiDownLoadUtil {
    private static final String TAG = "MultiDownLoadUtil";

    // 定义下载资源的路径
    private String path;
    // 指定所下载的文件的保存位置
    private String targetFile;
    // 定义需要使用多少线程下载资源
    private int threadNum;
    // 定义下载的文件的总大小
    private long fileSize = 0;
    // 定义下载的线程对象
    private DownloadThread[] threads;

    // 定义下载异常操作监听事件
    private boolean isException = false;

    public MultiDownLoadUtil(String path, String targetFile, int threadNum) {
        this.path = path;
        this.threadNum = threadNum;
        // 初始化threads数组
        threads = new DownloadThread[threadNum];
        this.targetFile = targetFile;

        // 设置异常默认值
        isException = false;
    }

    public void download() throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setReadTimeout(10 * 1000);
        conn.setRequestMethod("GET");
        // 得到文件大小
        fileSize = conn.getContentLength();
        Log.i(CoConfig.LOG_TAG, "FILE_SIZE : " + fileSize);
        conn.disconnect();
        long currentPartSize = fileSize / threadNum;

        RandomAccessFile file = new RandomAccessFile(targetFile, "rw");
        // 设置本地文件的大小
        file.setLength(fileSize);
        file.close();
        for (int i = 0; i < threadNum; i++) {
            // 计算每条线程的下载的开始位置
            long startPos = i * currentPartSize;
            // 每个线程使用一个RandomAccessFile进行下载
            RandomAccessFile currentPart = new RandomAccessFile(targetFile,
                    "rw");
            // 定位该线程的下载位置
            currentPart.seek(startPos);
            // 创建下载线程
            threads[i] = new DownloadThread(startPos, currentPartSize,
                    currentPart);
            // 启动下载线程
            threads[i].start();
        }
    }

    /**
     * @return
     */
    public double getCompleteValue() {
        // 统计多条线程已经下载的总大小
        long sumSize = 0;
        for (int i = 0; i < threadNum; i++) {
            // 判断在网络异常情况下，download()方法无法实例化threads数组
            if (threads[i] != null) {
                sumSize += threads[i].length;
            }
        }
        //
        return sumSize * 8 / 1024;
    }

    /**
     * @return
     */
    public double getTotal() {
        return fileSize * 8 / 1024;
    }

    /**
     * 判断是否异常发生
     *
     * @return
     */
    public boolean isException() {
        return isException;
    }

    /**
     * 获取下载完成的百分比
     *
     * @return
     */
    public double getCompleteRate() {
        // 统计多条线程已经下载的总大小
        long sumSize = 0;
        for (int i = 0; i < threadNum; i++) {
            // 判断在网络异常情况下，download()方法无法实例化threads数组
            if (threads[i] != null){
                sumSize += threads[i].length;
            }
        }
        // 返回已经完成的百分比
        return sumSize / fileSize;
    }

    private class DownloadThread extends Thread {
        // 当前线程的下载位置
        private long startPos;
        // 定义当前线程负责下载的文件大小
        private long currentPartSize;
        // 当前线程需要下载的文件块
        private RandomAccessFile currentPart;
        // 定义该线程已下载的字节数
        private long length = 0;

        public DownloadThread(long startPos, long currentPartSize,
                              RandomAccessFile currentPart) {
            this.startPos = startPos;
            this.currentPartSize = currentPartSize;
            this.currentPart = currentPart;
        }

        public void run() {
            try {
                // 设置异常默认值
                isException = false;

                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.setConnectTimeout(10 * 1000);
                conn.setReadTimeout(10 * 1000);
                conn.setRequestMethod("GET");
                InputStream is = conn.getInputStream();
                // 跳过startPos个字符，表明该线程只下载自己负责那部分文件
                is.skip(startPos);
                byte[] by = new byte[2048];
                int hasRead = 0;
                // 读取网络数据，并写入本地文件
                while (length < currentPartSize
                        && (hasRead = is.read(by)) != -1) {
                    currentPart.write(by, 0, hasRead);
                    // 累计该线程下载的总大小
                    length += hasRead;
                }
                currentPart.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
                isException = true;
                Log.d(TAG, "e : " + e.toString());
            }
        }
    }
}
