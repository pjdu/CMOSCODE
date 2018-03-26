package cn.com.chinaccs.datasite.main.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.View;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;
import cn.com.chinaccs.datasite.main.R;



public class DynamicCoverChart {
	private XYSeries series;
	private XYMultipleSeriesDataset dataset;
	private GraphicalView chart;
	private XYMultipleSeriesRenderer renderer;
	private Context context;
	private int addX = -1;
	int[] xv = new int[10];
	int[] yv = new int[10];

	public DynamicCoverChart(Context context) {
		this.context = context;
	}

	public void updateChart(float addY, float maxSpeed) {
		renderer.setYAxisMax(maxSpeed);
		// 设置好下一个需要增加的节点
		addX = 0;

		// 移除数据集中旧的点集
		dataset.removeSeries(series);
		// 判断当前点集中到底有多少点，因为屏幕总共只能容纳100个，所以当点数超过100时，长度永远是100
		int length = series.getItemCount();
		if (length > 10) {
			length = 10;
		}
		// 将旧的点集中x和y的数值取出来放入backup中，并且将x的值加1，造成曲线向右平移的效果
		for (int i = 0; i < length; i++) {
			xv[i] = (int) series.getX(i) + 1;
			yv[i] = (int) series.getY(i);
		}
		// 点集先清空，为了做成新的点集而准备
		series.clear();

		// 将新产生的点首先加入到点集中，然后在循环体中将坐标变换后的一系列点都重新加入到点集中
		// 这里可以试验一下把顺序颠倒过来是什么效果，即先运行循环体，再添加新产生的点
		series.add(addX, addY);
		for (int k = 0; k < length; k++) {
			series.add(xv[k], yv[k]);
		}

		// 在数据集中添加新的点集
		dataset.addSeries(series);
		length = renderer.getSeriesRendererCount();
		// 填充面积
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer seriesRenderer = (XYSeriesRenderer) renderer
					.getSeriesRendererAt(i);
			if (i == length - 1) {
				FillOutsideLine fill = new FillOutsideLine(
						FillOutsideLine.Type.BOUNDS_ALL);
				fill.setColor(context.getResources().getColor(R.color.blue1));
				seriesRenderer.addFillOutsideLine(fill);
			}
			seriesRenderer.setLineWidth(2.5f);
			// seriesRenderer.setDisplayChartValues(true);
			seriesRenderer.setChartValuesTextSize(10f);
		}

		// 视图更新，没有这一步，曲线不会呈现动态
		// 如果在非UI主线程中，需要调用postInvalidate()，具体参考api
		// chart.invalidate();
		if (chart != null)
			chart.postInvalidate();
	}

	public View execute(String title, String lineTitle, String xTitle,
						String yTitle) {
		// 这个类用来放置曲线上的所有点，是一个点的集合，根据这些点画出曲线
		series = new XYSeries(lineTitle);
		// 创建一个数据集的实例，这个数据集将被用来创建图表
		dataset = new XYMultipleSeriesDataset();
		// 将点集添加到这个数据集中
		dataset.addSeries(series);
		renderer = new XYMultipleSeriesRenderer();

		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(context.getResources().getColor(R.color.blue1));
		r.setPointStyle(PointStyle.CIRCLE);
		r.setFillPoints(true);
		r.setLineWidth(3);
		renderer.addSeriesRenderer(r);

		renderer.setShowGrid(true);
		renderer.setPanEnabled(false);
		renderer.setZoomEnabled(false);
		renderer.setZoomButtonsVisible(false);
		renderer.setClickEnabled(false);

		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);

		renderer.setAxisTitleTextSize(24.0f);
		renderer.setChartTitleTextSize(30.0f);
		renderer.setLabelsTextSize(24.0f);

		renderer.setLabelsColor(Color.BLACK);
		renderer.setGridColor(Color.BLACK);
		renderer.setAxesColor(Color.BLACK);
		renderer.setXLabelsColor(Color.BLACK);
		renderer.setYLabelsColor(0, Color.BLACK);
		renderer.setYLabelsAlign(Align.LEFT);
		renderer.setMarginsColor(Color.WHITE);

		renderer.setXAxisMin(0);
		renderer.setXAxisMax(10);
		renderer.setYAxisMin(0);
		renderer.setYAxisMax(150);
		// 密度配置；
		renderer.setXLabels(8);
		renderer.setYLabels(5);

		renderer.setBarSpacing(1);
		renderer.setScale(1.15f);
		chart = ChartFactory.getLineChartView(context, dataset, renderer);
		return chart;
	}
}
