package cn.com.chinaccs.datasite.main.datasite.inspect.function;

import android.content.Context;
import android.os.Handler;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;


/**
 * @author Fddi
 * 
 */
public class FuncGetHistoryList {

	private Context context;

	public FuncGetHistoryList(Context context) {
		this.context = context;
	}

	public void getData(final OnGetDataFinishedListener lr,
			final String userid, final String datefrom, final String dateto,
			final int pagestart) {
		final Handler hr = new Handler();
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userid
						+ datefrom + dateto + pagestart);
				StringBuffer url = new StringBuffer(
						DataSiteStart.HTTP_SERVER_URL);
				url.append("InspectWLHistoryList.do?userid=").append(userid)
						.append("&datefrom=").append(datefrom)
						.append("&dateto=").append(dateto)
						.append("&pagestart=").append(pagestart)
						.append("&sign=").append(sign);
				AppHttpConnection conn = new AppHttpConnection(context,
						url.toString());
				final String conResult = conn.getConnectionResult();
				hr.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						lr.onFinished(conResult);
					}
				});
			}
		}.start();
	}

}
