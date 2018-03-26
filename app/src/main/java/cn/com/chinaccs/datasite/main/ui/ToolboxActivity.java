package cn.com.chinaccs.datasite.main.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.datasite.function.FuncBuildGrid;
import cn.com.chinaccs.datasite.main.ui.cmos.BoxCompassActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.BoxConversionActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.BoxDowntiltActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.BoxMapActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.GPSConnActivity;
import cn.com.chinaccs.datasite.main.widget.pager.HorizontalPager;

/**
 * 工具箱
 * Created by Asky on 2016/4/5.
 */
public class ToolboxActivity extends BaseActivity{
    private GridView gridAgent;
    private Context context;
    private HorizontalPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context=this;
        setContentView(R.layout.activity_toolbox);
        initToolbar("工具箱");
        findViews();
        buildGrids();
    }



    private void findViews(){
        pager= (HorizontalPager) findViewById(R.id.pager);
        gridAgent = (GridView) findViewById(R.id.grid_toolbox);

    }

    private void buildGrids(){
        final int[] boxItems = { R.string.box_unitc, R.string.box_calculator,
                R.string.box_azimuth, R.string.box_downtilt, R.string.box_lac,
                R.string.box_gps };
        final int[] boxImgs = { R.drawable.ic_item_unitc,
                R.drawable.ic_item_calculator, R.drawable.ic_item_fwj,
                R.drawable.ic_item_xqj, R.drawable.ic_item_loc,
                R.drawable.ic_item_gps };
        FuncBuildGrid boxBg = new FuncBuildGrid(context, gridAgent);
        boxBg.buildGrid(boxItems, boxImgs);
        Intent cit = new Intent();
        /*cit.setClassName("com.android.calculator2",
                "com.android.calculator2.Calculator");*/
        cit.setClassName("com.android.calculator2","com.android.calculator2.Calculator");
        Intent[] boxIntent = {
                new Intent(context, BoxConversionActivity.class), cit,
                new Intent(context, BoxCompassActivity.class),
                new Intent(context, BoxDowntiltActivity.class),
                new Intent(context, BoxMapActivity.class),
                new Intent(context, GPSConnActivity.class) };
        boxBg.attachEvent(boxIntent);
    }

}
