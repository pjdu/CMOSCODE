package cn.com.chinaccs.datasite.main.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.datasite.function.FuncBuildGrid;
import cn.com.chinaccs.datasite.main.ui.cmos.ProductMainActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.ProductModelIndexActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.QAQuestionMainActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.TMobileSuggestActivity;
import cn.com.chinaccs.datasite.main.widget.pager.HorizontalPager;

/**
 * 知识库
 * Created by Asky on 2016/4/1.
 */
public class KnowledgeBaseActivity extends BaseActivity {
    private Context context;
    private HorizontalPager pager;
    private GridView gridKnowledge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_knowledge_base);
        initToolbar(context.getResources().getString(R.string.datasite_knowledge));
        this.findViews();
        this.buildGrids();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    private void buildGrids() {
        // 知识库 列表
        final int[] productItems = {R.string.know_master,
                R.string.know_assort, R.string.know_iquery, R.string.know_qa,
                R.string.suggest};
        final int[] productImgs = {R.drawable.ic_item_master,
                R.drawable.ic_item_assort, R.drawable.ic_item_qy,
                R.drawable.ic_item_know, R.drawable.ic_item_suggest};
        Intent ipdMain = new Intent(context, ProductMainActivity.class);
        ipdMain.putExtra("type", DataSiteStart.TYPE_EQ_MAIN);
        Intent ipdAssort = new Intent(context, ProductMainActivity.class);
        ipdAssort.putExtra("type", DataSiteStart.TYPE_EQ_ASSORT);
        Intent ipdModuleIndex = new Intent(context,
                ProductModelIndexActivity.class);
        Intent ipdKnow = new Intent(context, QAQuestionMainActivity.class);
        Intent ipdSuggest = new Intent(context, TMobileSuggestActivity.class);
        final Intent[] pdIntent = {ipdMain, ipdAssort, ipdModuleIndex,
                ipdKnow, ipdSuggest};
        FuncBuildGrid productBg = new FuncBuildGrid(context, gridKnowledge);
        productBg.buildGrid(productItems, productImgs);
        productBg.attachEvent(pdIntent);
    }

    private void findViews() {
        pager = (HorizontalPager) findViewById(R.id.pager);
        gridKnowledge = (GridView) findViewById(R.id.grid_knowledge);
    }
}
