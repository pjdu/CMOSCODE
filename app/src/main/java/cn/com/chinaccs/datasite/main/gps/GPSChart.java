package cn.com.chinaccs.datasite.main.gps;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.View;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;

public class GPSChart {

	public View execute(Context context, String[] prns, double[] snrs) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		SimpleSeriesRenderer r = new SimpleSeriesRenderer();
		r.setDisplayChartValues(true);
		r.setColor(Color.YELLOW);
		renderer.addSeriesRenderer(r);

		renderer.setAxisTitleTextSize(20);
		renderer.setChartTitleTextSize(25);
		renderer.setLabelsTextSize(20);
		renderer.setLegendTextSize(20);
		renderer.setChartTitle("卫星接收信噪比");
		renderer.setXTitle("噪声码");
		renderer.setYTitle("SNR");
		renderer.setAxesColor(Color.WHITE);
		renderer.setLabelsColor(Color.LTGRAY);
		renderer.setOrientation(Orientation.HORIZONTAL);
		renderer.setYLabelsAlign(Align.LEFT);
		renderer.setYAxisMin(0);
		renderer.setYAxisMax(50);
		// 密度配置；
		renderer.setXLabels(0);
		renderer.setYLabels(12);
		renderer.setShowGrid(true);
		renderer.setBarSpacing(1);
		renderer.setScale(1.15f);
		renderer.setPanEnabled(true);
		renderer.setZoomEnabled(true);
		renderer.setZoomButtonsVisible(false);
		renderer.setClickEnabled(false);

		for (int i = 0; i < prns.length; i++) {
			renderer.addXTextLabel(i + 1, prns[i]);
		}

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		CategorySeries series = new CategorySeries("卫星");
		for (int i = 0; i < snrs.length; i++) {
			series.add(snrs[i]);
		}
		dataset.addSeries(series.toXYSeries());

		return ChartFactory.getBarChartView(context, dataset, renderer,
				Type.STACKED);
	}
}
