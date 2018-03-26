package cn.com.chinaccs.datasite.main.connect;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.XFastFactory.XDbUtils.XDbUtils;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.adapter.SiteGridAdapter;
import cn.com.chinaccs.datasite.main.bean.HttpTestRecords;
import cn.com.chinaccs.datasite.main.bean.HttpTestRecordsInfo;
import cn.com.chinaccs.datasite.main.bean.WebSite;
import cn.com.chinaccs.datasite.main.common.AppNetWork;
import dingran.curltest.test.JniTest;


public class GetWebSite {
    private static final String TAG = GetWebSite.class.getSimpleName();
    public List resultList;
    public StringBuffer busiResult;
    public long siteFirstDefer;
    public long siteFullDefer;
    public long siteSpeed;
    public int nums;
    public JSONObject busiDatas;
    public JSONObject speedDatas;
    Context context;


    public GetWebSite(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
        busiResult = new StringBuffer("测试结果：");
        busiDatas = new JSONObject();
        speedDatas = new JSONObject();
        resultList=new ArrayList<HttpTestRecordsInfo>();
    }

    public void getData(final OnGetDataFinishListener lr, final List<WebSite> list,
                        SiteGridAdapter ad, final String testTime, final String addr) {
        // TODO Auto-generated method stub
        final SiteTestHandler hr = new SiteTestHandler(ad);
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                XDbUtils db = new XDbUtils(context);
                try {
                    db.createTable(HttpTestRecordsInfo.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < list.size(); i++) {
                    WebSite site = list.get(i);
                    if (!site.isChecked())
                        continue;
                    Message m = null;
                    try {
                        site.setState(WebSite.STATE_TEST);
                        m = hr.obtainMessage();
                        hr.sendMessage(m);
                        Thread.sleep(1000);
                        if (!AppNetWork.isNetWork(context)) {
                            site.setClassify(0);
                            site.setState(WebSite.STATE_ERROR);
                            m = hr.obtainMessage();
                            hr.sendMessage(m);
                            continue;
                        }
                        URL url = new URL(site.getUrl());
                        String siteName = site.getName();

                        JniTest curlUrl = new JniTest();
                        String result = curlUrl.curlInit(site.getUrl());
                        JSONObject resultData = getHttpResultToJson(result);
                        //Log.i(CoConfig.LOG_TAG, site.getName() + ":[" + result + "]");

						/*long l1 = SystemClock.uptimeMillis();
                        URLConnection conn = url.openConnection();
						conn.setUseCaches(false);
						conn.setDoInput(true);
						conn.setDoOutput(false);
						conn.setConnectTimeout(5000);
						conn.setReadTimeout(10000);
						conn.connect();
						int ch = -1;
						int total = 0;
						byte[] buf = new byte[1024];
						long l3 = SystemClock.uptimeMillis() - l1; 
						InputStream bis = conn.getInputStream();
//						bis.read();
						
						
						while ((ch = bis.read(buf)) != -1 && total <= 7000) {
							total += ch;
						}
						while (bis.read(new byte[8192]) > 0){}
						long l2 = SystemClock.uptimeMillis() - l1;
						Log.d(CoConfig.LOG_TAG, l2 + "/" + total);
						int speed = (int) ((total * 8 / 1024 * 1000) / l2);
						site.setPing(l2);
						site.setSpeed(speed);
						site.setClassify(typeTest(l2));
						site.setState(WebSite.STATE_FINISH);
						if (l3<10) {
							l3 = l3 + 20;
						}*/

                        int http_response_code =
                                Integer.parseInt(resultData.getString("http_response_code"));
                        long http_namelookup_time =
                                (long) (Float.parseFloat(resultData.getString("http_namelookup_time")) * 1000);
                        long http_connect_time =
                                (long) (Float.parseFloat(resultData.getString("http_connect_time")) * 1000);
                        long http_pretransfer_time =
                                (long) (Float.parseFloat(resultData.getString("http_pretransfer_time")) * 1000);
                        long http_starttransfer_time =
                                (long) (Float.parseFloat(resultData.getString("http_starttransfer_time")) * 1000);
                        long http_first_delay = (long) ((Float.parseFloat(resultData.getString("http_namelookup_time"))
                                + Float.parseFloat(resultData.getString("http_connect_time"))
                                + Float.parseFloat(resultData.getString("http_pretransfer_time"))) * 1000);
                        long http_total_time =
                                (long) (Float.parseFloat(resultData.getString("http_total_time")) * 1000);
                        String http_primary_ip = resultData.getString("http_primary_ip");
                        String http_local_ip = resultData.getString("http_local_ip");
                        long http_size_download = (long) (Float.parseFloat(resultData.getString("http_size_download")) * 8 / 1024);
                        long http_speed_download = (long) (Float.parseFloat(resultData.getString("http_speed_download")) * 8 / 1024);

                        site.setPing(http_total_time);
                        site.setSpeed(http_speed_download);
                        site.setClassify(typeTest(http_total_time));
                        site.setState(WebSite.STATE_FINISH);

                        siteFirstDefer += http_first_delay;
                        siteFullDefer += http_total_time;
                        siteSpeed += http_speed_download;
                        nums += 1;

                        String type = level(i, typeTest(http_total_time) + "");
                        HttpTestRecordsInfo httpTestRecordsInfo = new HttpTestRecordsInfo(testTime,
                                addr,
                                siteName,
                                http_response_code + "",
                                http_namelookup_time + "ms",
                                http_connect_time + "ms",
                                http_pretransfer_time + "ms",
                                http_starttransfer_time + "ms",
                                http_first_delay + "ms",
                                http_total_time + "ms",
                                type,
                                http_primary_ip,
                                http_local_ip,
                                http_size_download + "Kb",
                                http_speed_download + "Kbps");
                        db.insert(httpTestRecordsInfo);

                        /*busiDatas.put("http_effective_url" + i, siteName);
                        busiDatas.put("http_response_code" + i, http_response_code);
                        busiDatas.put("http_namelookup_time" + i, http_namelookup_time + "ms");
                        busiDatas.put("http_connect_time" + i, http_connect_time + "ms");
                        busiDatas.put("http_pretransfer_time" + i, http_pretransfer_time + "ms");
                        busiDatas.put("http_starttransfer_time" + i, http_starttransfer_time + "ms");
                        busiDatas.put("http_first_delay" + i, http_first_delay + "ms");
                        busiDatas.put("http_total_time" + i, http_total_time + "ms");
                        busiDatas.put("siteType" + i, typeTest(http_total_time));
                        busiDatas.put("http_primary_ip" + i, http_primary_ip);
                        busiDatas.put("http_local_ip" + i, http_local_ip);
                        busiDatas.put("http_size_download" + i, http_size_download + "Kb");
                        busiDatas.put("http_speed_download" + i, http_speed_download + "Kbps");*/
                        speedDatas.put("http_first_delay" + i, http_first_delay + "ms");
                        speedDatas.put("http_total_time" + i, http_total_time + "ms");
                        speedDatas.put("http_speed_download" + i, http_speed_download + "Kbps");
                        speedDatas.put("siteType" + i, typeTest(http_total_time));

                        busiResult.append(siteName).append("：页面首包时延：").append(http_first_delay).append("ms，")
                                .append("页面打开时延：").append(http_total_time).append("ms，")
                                .append("访问速率：").append(http_speed_download).append("kbps；\n");

                        m = hr.obtainMessage();
                        hr.sendMessage(m);
                        resultList.add(httpTestRecordsInfo);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        site.setClassify(0);
                        site.setState(WebSite.STATE_ERROR);
                        m = hr.obtainMessage();
                        hr.sendMessage(m);
                    }
                }
                siteFirstDefer = siteFirstDefer / nums;
                siteFullDefer = siteFullDefer / nums;
                siteSpeed = siteSpeed / nums;
                busiResult.append("\n")
                        .append("平均首包时延：").append(siteFirstDefer).append("ms，")
                        .append("平均打开时延：").append(siteFullDefer).append("ms，")
                        .append("平均访问速率：").append(siteSpeed).append("Kbps。");

                HttpTestRecords httpTestRecords = new HttpTestRecords(testTime, addr, siteFirstDefer + "ms", siteFullDefer + "ms", siteSpeed + "Kbps");
                try {
                    db.createTable(HttpTestRecords.class);
                    db.insert(httpTestRecords);
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    busiDatas.put("avgFirDefer", siteFirstDefer + "ms");
                    busiDatas.put("avgFuDefer", siteFullDefer + "ms");
                    busiDatas.put("avgSpeed", siteSpeed + "Kbps");

                    speedDatas.put("avgFirDefer", siteFirstDefer + "ms");
                    speedDatas.put("avgFuDefer", siteFullDefer + "ms");
                    speedDatas.put("avgSpeed", siteSpeed + "Kbps");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                lr.onFinishJSON(busiDatas);
                lr.onFinished(speedDatas.toString());
                lr.onFinishList(resultList);
            }
        }.start();
    }

    /**
     * @param result
     * @return
     */
    private JSONObject getHttpResultToJson(String result) {
        JSONObject data = new JSONObject();
        int start = 0;
        int index = 0;
        while ((index = result.indexOf(",")) != -1) {
            String sub = result.substring(start, index);
            int divider = 0;
            try {
                if ((divider = sub.indexOf(":")) != -1) {
                    data.put(sub.substring(start, divider), sub.substring(divider + 1));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            result = result.substring(index + 1);
        }
        return data;
    }

    /**
     * @param ms
     * @return
     */
    private int typeTest(long ms) {
        int type = 1;
        if (ms >= 5000) {
            type = 1;
        } else if (ms >= 2000) {
            type = 2;
        } else if (ms >= 500) {
            type = 3;
        } else {
            type = 4;
        }
        return type;
    }

    private String level(int i, String type) {
        String str = "";
        if (i == 0) {
            if (type.equals("1")) {
                str = context.getString(R.string.busi_baidu_ave4);
            } else if (type.equals("2")) {
                str = context.getString(R.string.busi_baidu_ave3);
            } else if (type.equals("3")) {
                str = context.getString(R.string.busi_baidu_ave2);
            } else {
                str = context.getString(R.string.busi_baidu_ave1);
            }
        } else if (i == 1) {
            if (type.equals("1")) {
                str = context.getString(R.string.busi_leshi_ave4);
            } else if (type.equals("2")) {
                str = context.getString(R.string.busi_leshi_ave3);
            } else if (type.equals("3")) {
                str = context.getString(R.string.busi_leshi_ave2);
            } else {
                str = context.getString(R.string.busi_leshi_ave1);
            }
        } else if (i == 2) {
            if (type.equals("1")) {
                str = context.getString(R.string.busi_taobao_ave4);
            } else if (type.equals("2")) {
                str = context.getString(R.string.busi_taobao_ave3);
            } else if (type.equals("3")) {
                str = context.getString(R.string.busi_taobao_ave2);
            } else {
                str = context.getString(R.string.busi_taobao_ave1);
            }
        } else if (i == 3 || i == 4) {
            if (type.equals("1")) {
                str = context.getString(R.string.busi_zx_ave4);
            } else if (type.equals("2")) {
                str = context.getString(R.string.busi_zx_ave3);
            } else if (type.equals("3")) {
                str = context.getString(R.string.busi_zx_ave2);
            } else {
                str = context.getString(R.string.busi_zx_ave1);
            }
        } else if (i == 5) {
            if (type.equals("1")) {
                str = context.getString(R.string.busi_zt_ave4);
            } else if (type.equals("2")) {
                str = context.getString(R.string.busi_zt_ave3);
            } else if (type.equals("3")) {
                str = context.getString(R.string.busi_zt_ave2);
            } else {
                str = context.getString(R.string.busi_zt_ave1);
            }
        }
        return str;
    }

    static class SiteTestHandler extends Handler {
        SiteGridAdapter ad;

        public SiteTestHandler(SiteGridAdapter ad) {
            this.ad = ad;
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            ad.notifyDataSetChanged();
        }
    }
}
