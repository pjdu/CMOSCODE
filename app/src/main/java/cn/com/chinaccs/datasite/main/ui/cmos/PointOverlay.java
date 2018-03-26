package cn.com.chinaccs.datasite.main.ui.cmos;//package cn.com.chinaccs.datasite.inspect;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Point;
//import android.graphics.drawable.Drawable;
//
//import com.baidu.mapapi.GeoPoint;
//
//import com.baidu.mapapi.MapView;
//
//import com.baidu.mapapi.Projection;
//
//public class PointOverlay extends ItemizedOverlay<OverlayItem> {
//	private GeoPoint geoPoint = null;
//	private Drawable marker = null;
//	private String pointname = null;
//
//	public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
//
//	public PointOverlay(GeoPoint geoPoint, Drawable marker, String pointname) {
//		super(boundCenterBottom(marker));
//		this.geoPoint = geoPoint;
//		this.marker = marker;
//		this.pointname = pointname;
//		mGeoList.add(new OverlayItem(geoPoint, pointname, pointname));
//		populate();
//	}
//
//	public void updateOverlay() {
//		populate();
//	}
//
//	@Override
//	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
//		// TODO Auto-generated method stub
//
//		Point point = new Point();
//		// Projection接口用于屏幕像素坐标和经纬度坐标之间的变换
//		// android.content.ContextWrapper cw = new ContextWrapper(context);
//		Projection projection = mapView.getProjection();
//		projection.toPixels(geoPoint, point);
//		Paint paint = new Paint();
//		paint.setTextSize(15);
//		paint.setColor(Color.BLACK);
//		canvas.drawText(pointname, point.x - 20, point.y + 16, paint);
//		super.draw(canvas, mapView, shadow);
//		// 调整一个drawable边界，使得（0，0）是这个drawable底部最后一行中心的一个像素
//		boundCenterBottom(marker);
//	}
//
//	@Override
//	protected OverlayItem createItem(int arg0) {
//		// TODO Auto-generated method stub
//		return mGeoList.get(0);
//	}
//
//	@Override
//	public int size() {
//		// TODO Auto-generated method stub
//		return mGeoList.size();
//	}
//
//	@Override
//	// 处理当点击事件
//	protected boolean onTap(int i) {
//		return super.onTap(i);
//	}
//
//	@Override
//	public boolean onTap(GeoPoint arg0, MapView arg1) {
//		// TODO Auto-generated method stub
//		// 消去弹出的气泡
//		return super.onTap(arg0, arg1);
//	}
//
//}
