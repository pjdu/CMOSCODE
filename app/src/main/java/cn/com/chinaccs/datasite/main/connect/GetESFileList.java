package cn.com.chinaccs.datasite.main.connect;

import android.content.Context;
import android.os.Handler;

import cn.com.chinaccs.datasite.main.CoConfig;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.util.SignMD5;


public class GetESFileList {
	private Context context;

	public GetESFileList(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public void getData(final OnGetDataFinishedListener lr, final String type) {
		// TODO Auto-generated method stub
		final Handler hr = new Handler();
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				StringBuffer url = new StringBuffer(CoConfig.HTTP_SERVER_URL);
				String sign = SignMD5.execute(CoConfig.HTTP_KEYSTORE + type);
				url.append("ESFileQuery.do?type=").append(type)
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
