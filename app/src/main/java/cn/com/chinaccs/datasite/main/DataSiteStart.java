package cn.com.chinaccs.datasite.main;

public abstract class DataSiteStart {
    /**
     * http服务地址
     */
    public static final String HTTP_SERVER_URL = "http://222.221.16.153:8090/WDataSiteServer/";
    /**
     * 本地测试
     */

    //  public static final String HTTP_SERVER_URL="http://182.242.61.36:5051/WDataSiteServer/";
    //	public static final String HTTP_SERVER_URL="http://192.168.1.104:8080/WDataSiteServer/";
    //	public static final String HTTP_SERVER_URL="http://222.221.16.153:8090/WDataSiteServertest/";

    //public static final String HTTP_SERVER_URL = "http://182.242.61.4:5051/WDataSiteServer/";


    /**
     * http传输密钥
     */
    public static final String HTTP_KEYSTORE = "WDataSite";
    /**
     * http加密密钥
     */
    public static final String AES_KEY = "TL073DQTU3Z2BVMJ";
    /**
     *
     */
    public static final String APP_NAME = "DataSite.apk";
    /**
     * 区号，按拼音排序
     */
    public static final String[] AREACODE = {"yn", "0691", "0875", "0878",
            "0872", "0692", "0887", "0873", "0871", "0888", "0883", "0886",
            "0879", "0874", "0876", "0877", "0870"};

    /**
     * 地市名 按拼音排序
     */
    public static final String[] AREANAME = {"全省", "西双版纳州", "保山市", "楚雄州",
            "大理州", "德宏州", "迪庆州", "红河州", "昆明市", "丽江市", "临沧市", "怒江州", "普洱市",
            "曲靖市", "文山州", "玉溪市", "昭通市"};

    /**
     *
     */
    public static final String TYPE_AREA_YN = "yn";
    /**
     *
     */
    public static final String TYPE_EQ_MAIN = "produce_main_eq";
    /**
     *
     */
    public static final String TYPE_EQ_ASSORT = "produce_assort_eq";
    /**
     *
     */
    public static final int TYPE_BASESTATION = 1;
    /**
     *
     */
    public static final int TYPE_CELL = 2;
    /**
     *
     */
    public static final int TYPE_INDOOR = 3;
    /**
     *
     */
    public static final int TYPE_REPEATER = 4;
    /**
     *
     */
    public static final int TYPE_WIFI_HOT = 6;
    /**
     *
     */
    public static final int TYPE_PRODUCT_OFFER = 7;
    /**
     *
     */
    public static final int TYPE_QA_QUESTION = 8;
    /**
     *
     */
    public static final int TYPE_QA_ANSWER = 9;
    /**
     *
     */
    public static final int TYPE_LTE_BASESTATION = 10;
    /**
     *
     */
    public static final int TYPE_LTE_CELL = 11;
    /**
     *
     */
    public static final int TYPE_ASSETS = 12;

    /**
     *
     */
    public static final String SHARE_IS_MSG = "MAP_SHARE_IS_MSG";
    /**
     *
     */
    public static final String SHARE_IS_MSG_IT = "MAP_SHARE_IS_MSG_IT";
    /**
     *
     */
    public static final String SHARE_IS_MSG_DATA = "MAP_SHARE_IS_MSG_DATA";
    /**
     *
     */
    public static final String SHARE_IS_MSG_QA = "MAP_SHARE_IS_MSG_QA";
    /**
     *
     */
    public static final String SHARE_DATEITEM_CLEAR_IT_CACHE = "MAP_SHARE_DATEITEM_CLEAR_IT_CACHE";
}
