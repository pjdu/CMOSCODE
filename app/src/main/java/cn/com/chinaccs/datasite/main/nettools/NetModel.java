package cn.com.chinaccs.datasite.main.nettools;

/**
 * Created by andyhua on 15-4-28.
 */
public abstract class NetModel {
    public static final int SUCCEED = 0;
    public static final int UNKNOWN_ERROR = 1;
    public static final int TCP_LINK_ERROR = 2;
    public static final int UNKNOWN_HOST_ERROR = 3;
    public static final int NETWORK_FAULT_ERROR = 4;
    public static final int NETWORK_SOCKET_ERROR = 5;
    public static final int NETWORK_IO_ERROR = 6;
    public static final int MALFORMED_URL_ERROR = 7;
    public static final int HTTP_CODE_ERROR = 7;
    public static final int SERVICE_NOT_AVAILABLE = 8;
    public static final int DOWNLOAD_ERROR = 9;
    public static final int ICMP_ECHO_FAIL_ERROR = 10;
    public static final int HOST_UNREACHABLE_ERROR = 11;
    public static final int DROP_DATA_ERROR = 12;

    public static final String PING = "ping";
    public static final String PING_LARGE = "PING";
    public static final String PING_FROM = "from";
    public static final String PING_FROM_LARGE = "From";
    public static final String PING_TIME = "time=";
    public static final String PING_PAREN_THESE_OPEN = "(";
    public static final String PING_PAREN_THESE_CLOSE = ")";
    public static final String PING_EXCEED = "exceed";
    public static final String PING_STATISTICS = "statistics";
    public static final String PING_TRANSMIT = "packets transmitted";
    public static final String PING_RECEIVED = "received";
    public static final String PING_ERRORS = "errors";
    public static final String PING_LOSS = "packet loss";
    public static final String PING_UNREACHABLE = "100%";
    public static final String PING_RTT = "rtt";
    public static final String PING_BREAK_LINE = "\n";
    public static final String PING_RATE = "%";
    public static final String PING_COMMA = ",";
    public static final String PING_EQUAL = "=";
    public static final String PING_SLASH = "/";
    public static final String TAG = "NetModel";
    public int error = SUCCEED;

    public static byte[] convertIpToByte(String ip) {
        String str[] = ip.split("\\.");
        byte[] bIp = new byte[str.length];
        try {
            for (int i = 0, len = str.length; i < len; i++) {
                bIp[i] = (byte) (Integer.parseInt(str[i], 10));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return bIp;
    }

    public int getError() {
        return error;
    }

    public abstract void start();

    public abstract void cancel();
}
