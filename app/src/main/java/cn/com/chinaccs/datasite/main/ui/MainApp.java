package cn.com.chinaccs.datasite.main.ui;

import android.app.Application;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.blankj.ALog;

import cn.albatross.anchovy.whale.Whale;
import cn.com.chinaccs.datasite.main.BuildConfig;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.datasite.function.BDGeoLocation;
import cn.com.chinaccs.datasite.main.gps.GpsHandler;

public class MainApp extends Application {
    public static MainApp app = null;
    public static String MAP_KEY = "0F6F1AE89046CF599F0FDA3B25C60E6AA0D8AFE1";
    //	public static BDGeoLocation geoBD;
    public String imagePath;
    public GpsHandler gpsHandler;
    /*AppContext中的属性*/
    public static int RESTART_CODE = 0;
    /**
     * 退出code
     */
    public static final int CODE_EXIT = 1;
    /**
     * 重新登录code
     */
    public static final int CODE_RELOGIN = 2;
    public static BDGeoLocation geoBD;


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        // added by wuhua for
        // 集团公司提供的插件库文件嵌入到天翼感知APP上形成插件版，使我省APP安装数量有保底的增长。
        Whale.start(this);
        // ended by wuhua 20151104
        app = this;
        SDKInitializer.initialize(getApplicationContext());
        geoBD = new BDGeoLocation(getApplicationContext());
//		geoBD.requestByGBOption();
        geoBD.requestByBDOption();
//		geoBD.locClient.start();
        gpsHandler = new GpsHandler(this);

        initALog();
    }

    // init it in ur application
    public void initALog() {
        /*
         * init                    : 初始化
         getConfig               : 获取log配置
         Config.setLogSwitch     : 设置log总开关
         Config.setConsoleSwitch : 设置log控制台开关
         Config.setGlobalTag     : 设置log全局tag
         Config.setLogHeadSwitch : 设置log头部信息开关
         Config.setLog2FileSwitch: 设置log文件开关
         Config.setDir           : 设置log文件存储目录
         Config.setFilePrefix    : 设置log文件前缀
         Config.setBorderSwitch  : 设置log边框开关
         Config.setConsoleFilter : 设置log控制台过滤器
         Config.setFileFilter    : 设置log文件过滤器
         Config.setStackDeep     : 设置log栈深度
         v                       : Verbose日志
         d                       : Debug日志
         i                       : Info日志
         w                       : Warn日志
         e                       : Error日志
         a                       : Assert日志
         file                    : log到文件
         json                    : log字符串之json
         xml                     : log字符串之xml
         */
        ALog.Config config = ALog.init(this)
                .setLogSwitch(BuildConfig.DEBUG)// 设置log总开关，包括输出到控制台和文件，默认开
                .setConsoleSwitch(BuildConfig.DEBUG)// 设置是否输出到控制台开关，默认开
                .setGlobalTag(null)// 设置log全局标签，默认为空
                // 当全局标签不为空时，我们输出的log全部为该tag，
                // 为空时，如果传入的tag为空那就显示类名，否则显示tag
                .setLogHeadSwitch(true)// 设置log头信息开关，默认为开
                .setLog2FileSwitch(false)// 打印log时是否存到文件的开关，默认关
                .setDir("")// 当自定义路径为空时，写入应用的/cache/log/目录中
                .setFilePrefix("")// 当文件前缀为空时，默认为"alog"，即写入文件为"alog-MM-dd.txt"
                .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
                .setConsoleFilter(ALog.V)// log的控制台过滤器，和logcat过滤器同理，默认Verbose
                .setFileFilter(ALog.V)// log文件过滤器，和logcat过滤器同理，默认Verbose
                .setStackDeep(1);// log栈深度，默认为1
        ALog.d(config.toString());
    }

    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        imagePath = null;
        if (geoBD.locClient != null && geoBD.locClient.isStarted()) {
            geoBD.locClient.unRegisterLocationListener(geoBD.locListnner);
            Log.d(App.LOG_TAG, "unregister gblocationListener");
            geoBD.locClient.stop();
            geoBD.locClient = null;
            geoBD = null;
        }
        if (gpsHandler != null) {
            gpsHandler.stopGps();
            gpsHandler = null;
        }
        super.onTerminate();
    }

    public static MainApp getInstance() {
        return app;
    }

}
