package cn.com.chinaccs.datasite.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.ui.cmos.ProductMainActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.ProductModelIndexActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.QAQuestionMainActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.TMobileSuggestActivity;

/**
 * Created by Asky on 2016/4/5.
 */
public class KnowledgeBaseAdapter extends BaseAdapter {

    private Context context;
    // 知识库 列表
    private final int[] productItems = {R.string.know_master,
            R.string.know_assort, R.string.know_iquery, R.string.know_qa,
            R.string.suggest};
    private final int[] productImgs = {R.drawable.ic_item_master,
            R.drawable.ic_item_assort, R.drawable.ic_item_qy,
            R.drawable.ic_item_know, R.drawable.ic_item_suggest};

    /**
     * @param context
     */
    public KnowledgeBaseAdapter(Context context) {
        this.context = context;
        Intent ipdMain = new Intent(context, ProductMainActivity.class);
        ipdMain.putExtra("type", DataSiteStart.TYPE_EQ_MAIN);
        Intent ipdAssort = new Intent(context, ProductMainActivity.class);
        ipdAssort.putExtra("type", DataSiteStart.TYPE_EQ_ASSORT);
        Intent ipdModuleIndex = new Intent(context,
                ProductModelIndexActivity.class);
        //Intent ipdKnow = new Intent(context, QAQuestionActivity.class);
        Intent ipdKnow = new Intent(context, QAQuestionMainActivity.class);
        Intent ipdSuggest = new Intent(context, TMobileSuggestActivity.class);
        final Intent[] pdIntent = {ipdMain, ipdAssort, ipdModuleIndex,
                ipdKnow, ipdSuggest};
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
