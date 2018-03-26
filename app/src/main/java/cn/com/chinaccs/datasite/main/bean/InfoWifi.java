package cn.com.chinaccs.datasite.main.bean;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

public class InfoWifi {
    private WifiManager wm;
    private List<Collector> wifis = null;

    public InfoWifi(Context context) {
        wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifis = new ArrayList<Collector>();
    }

    public List<Collector> getWifiInfo(List<Collector> list) {
        list.add(new Collector("Wifi-Info", "", 6, Collector.DATA_TYPE_TITLE));
        boolean isEnabled = wm.isWifiEnabled();
        list.add(new Collector("WifiState", Boolean.toString(isEnabled), 6,
                Collector.DATA_TYPE_UPLOAD));
        WifiInfo info = wm.getConnectionInfo();
        list.add(new Collector("SupplicantState", info.getSupplicantState()
                .toString(), 6, Collector.DATA_TYPE_UPLOAD));
        list.add(new Collector("SSID", info.getSSID() == null ? "" : info
                .getSSID(), 6, Collector.DATA_TYPE_UPLOAD));
        list.add(new Collector("wifi-signal", Integer.toString(info.getRssi()),
                6, Collector.DATA_TYPE_UPLOAD, Collector.UNIT_SIGNAL_DBM));
        list.add(new Collector("BSSID", info.getBSSID() == null ? "" : info
                .getBSSID(), 6, Collector.DATA_TYPE_UPLOAD));
        list.add(new Collector("HiddenSSID", Boolean.toString(info
                .getHiddenSSID()), 6));
        list.add(new Collector("NetworkId", Integer.toString(info
                .getNetworkId()), 6));
        list.add(new Collector("Speed", info.getLinkSpeed() + "Mbps", 6,
                Collector.DATA_TYPE_UPLOAD));
        DhcpInfo dhcp = wm.getDhcpInfo();
        List<Collector> list_c = new ArrayList<Collector>();
        list_c.add(new Collector("ip", this.longToIP(dhcp.ipAddress), 6));
        list_c.add(new Collector("gateway", this.longToIP(dhcp.gateway), 6));
        list_c.add(new Collector("netmask", this.longToIP(dhcp.netmask), 6));
        list_c.add(new Collector("dns1", this.longToIP(dhcp.dns1), 6));
        list_c.add(new Collector("dns2", this.longToIP(dhcp.dns2), 6));
        list_c.add(new Collector("server", this.longToIP(dhcp.serverAddress), 6));
        list_c.add(new Collector("lease", Integer.toString(dhcp.leaseDuration),
                6, Collector.UNIT_SECONDS));
        list.add(new Collector("dhcp", list_c, 6, Collector.DATA_TYPE_LIST
                | Collector.DATA_TYPE_DHCP));

        list.add(new Collector("Wifi-scan-list", "", 7,
                Collector.DATA_TYPE_TITLE));
        list = this.changeWifi(list);
        return list;
    }

    public List<Collector> signalChange(List<Collector> list) {
        WifiInfo info = wm.getConnectionInfo();
        list.get(getPosition("wifi-signal", list)).setDataValue(
                Integer.toString(info.getRssi()));
        return list;
    }

    public List<Collector> changeWifi(List<Collector> list) {
        if (wifis != null){
            list.removeAll(wifis);
            wifis.clear();
        }

        boolean isEnabled = wm.isWifiEnabled();
        if (isEnabled) {
            List<ScanResult> scanResults = wm.getScanResults();
            if (scanResults != null) {
                for (ScanResult scan : scanResults) {
                    ArrayList<Collector> list_c1 = new ArrayList<Collector>();
                    list_c1.add(new Collector("BSSID", scan.BSSID, 7));
                    list_c1.add(new Collector("signal", Integer
                            .toString(scan.level), 7, Collector.UNIT_SIGNAL_DBM));
                    list_c1.add(new Collector("frequency", Integer
                            .toString(scan.frequency), 7,
                            Collector.UNIT_FREQUENCY));
                    list_c1.add(new Collector("capabilites", scan.capabilities,
                            7));
                    if (getPosition(scan.SSID, wifis) == -1) {
                        wifis.add(new Collector(scan.SSID, list_c1, 7,
                                Collector.DATA_TYPE_LIST
                                        | Collector.DATA_TYPE_UPLOAD
                                        | Collector.DATA_TYPE_FULLLINE
                                        | Collector.DATA_TYPE_WIFISCAN));
                    }
                }
                list.addAll(wifis);
            }
        }
        return list;
    }

    private int getPosition(String name, List<Collector> list) {
        int position = -1;
        for (int i = 0; i < list.size(); i++) {
            if (name.equalsIgnoreCase(list.get(i).getDataName())) {
                position = i;
                break;
            }
        }
        return position;
    }

    public String longToIP(long ip10) {
        String ip = "";
        long temp = 0;
        for (int i = 0; i <= 3; i++) {
            temp = Math.abs(ip10 / (long) Math.pow(256, i) % 256);
            if (i == 0) {
                ip = ip + temp;
            } else {
                ip = ip + "." + temp;
            }
        }
        return ip;
    }
}
