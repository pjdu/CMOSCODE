package cn.com.chinaccs.datasite.main.ui.cmos;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.chinaccs.datasite.main.CoConfig;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.ui.functions.FuncGetPhysicalSiteInfo;
import cn.com.chinaccs.datasite.main.widget.AnimatedExpandableListView;
import cn.com.chinaccs.datasite.main.widget.LoadToast;

/**
 * Created by asky on 16-6-13.
 */
public class PhysicalSiteInfoActivity extends BaseActivity {
    private Context mContext;
    private String bsId;
    private JSONArray datas;

    private AnimatedExpandableListView listView;
    private MyAdapter adapter;
    private List<Item> mGroupItems;

    private final String text = "加载中....";
    private LoadToast mLoadToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_site_info);

        this.mContext = this;

        initToolbar("站址详情");
        Bundle bundle = getIntent().getExtras();
        bsId = getIntent().getExtras().getString("BsId");
        mLoadToast = new LoadToast(this).setProgressColor(Color.BLUE).setBackgroundColor(Color.WHITE).setText(text).setTranslationY(360);
        this.buildPhysicalData();
    }

    private void buildPhysicalData() {
        mLoadToast.show();
        FuncGetPhysicalSiteInfo func = new FuncGetPhysicalSiteInfo(mContext);
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                try {
                    initPhysicalSiteInfoItems(output);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            ;
        };
        func.getData(lr, bsId);
    }

    private void initPhysicalSiteInfoItems(String output) throws JSONException {
        Log.i(CoConfig.LOG_TAG, output);
        if (output.equals(AppHttpConnection.RESULT_FAIL)) {
            Toast.makeText(mContext,
                    getResources().getString(R.string.common_not_network),
                    Toast.LENGTH_LONG).show();
            mLoadToast.error();
        } else {
            JSONObject json = new JSONObject(output);
            String result = json.getString("result");
            mGroupItems = new ArrayList<>();
            List<Item> hadParent = new ArrayList<>();
            List<Item> errorBs = new ArrayList<>();
            if (result.equals("-1")) {
                Toast.makeText(mContext, json.getString("msg"),
                        Toast.LENGTH_LONG).show();
                mLoadToast.error();
            } else {
                datas = json.getJSONArray("data");
                for (int i = datas.length() - 1; i >= 0; --i) {
                    JSONArray temp = null;
                    try {
                        temp = datas.getJSONArray(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (temp.getString(9).equals("1")) {
                            String content = "站点地址:" + temp.getString(2) + "\n站点类型:" + temp.getString(3) + "\n站点等级:"
                                    + temp.getString(4) + "\n站点产权:" + temp.getString(5) + "\n站点设备:" + temp.getString(6)
                                    + "\n站点网络类型:" + temp.getString(7) + "\n站点机房:" + temp.getString(8);
                            Item item = new Item(temp.getString(0), temp.getString(1), content, "基站(点击查看扇区)",
                                    mContext.getResources().getColor(R.color.white), "");
                            mGroupItems.add(item);
                        } else {
                            if (temp.getString(8).equals("")) {
                                String content = "扇区地址:" + temp.getString(2) + "\n扇区类型:" + temp.getString(3) + "\n扇区等级:"
                                        + temp.getString(4) + "\n扇区TAC:" + temp.getString(5) + "\n扇区CI:" + temp.getString(6)
                                        + "\n扇区Model:" + temp.getString(7);
                                Item item = new Item(temp.getString(0), temp.getString(1), content, "异址扇区",
                                        mContext.getResources().getColor(R.color.btn_nor_t), temp.getString(8));
                                errorBs.add(item);
                            } else {
                                String content = "扇区地址:" + temp.getString(2) + "\n扇区类型:" + temp.getString(3) + "\n扇区等级:"
                                        + temp.getString(4) + "\n扇区TAC:" + temp.getString(5) + "\n扇区CI:" + temp.getString(6)
                                        + "\n扇区Model:" + temp.getString(7);
                                Item item = new Item(temp.getString(0), temp.getString(1), content, "扇区",
                                        mContext.getResources().getColor(R.color.pick), temp.getString(8));
                                hadParent.add(item);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mLoadToast.success();
            }
            // init data and adapter
            for (int i = 0; i < mGroupItems.size(); ++i) {
                Item parent = mGroupItems.get(i);
                for (int j = 0; j < hadParent.size(); ++j) {
                    Item child = hadParent.get(j);
                    if (child.parentBsId.equals(parent.bsId)) {
                        parent.children.add(child);
                    }
                }
            }
            // 异址扇区归类
            Item item = new Item("", "异址扇区", "", "异址扇区(点击查看)",
                    mContext.getResources().getColor(R.color.white), "");
            item.children.addAll(errorBs);
            mGroupItems.add(item);
            adapter = new MyAdapter(this);
            adapter.setData(mGroupItems);

            listView = (AnimatedExpandableListView) findViewById(R.id.physical_site_list);
            listView.setGroupIndicator(null);
            listView.setAdapter(adapter);

            // In order to show animations, we need to use a custom click handler
            // for our ExpandableListView.
            listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    // We call collapseGroupWithAnimation(int) and
                    // expandGroupWithAnimation(int) to animate group
                    // expansion/collapse.
                    if (listView.isGroupExpanded(groupPosition)) {
                        listView.collapseGroupWithAnimation(groupPosition);
                    } else {
                        listView.expandGroupWithAnimation(groupPosition);
                    }
                    return true;
                }

            });
        }
    }

    /**
     * for list adapter item info
     */
    private class Item {
        String bsId;
        String title;
        String note;
        String info;
        int color;
        String parentBsId;
        List<Item> children = new ArrayList<>();

        /**
         * @param title
         * @param note
         * @param info
         * @param color
         */
        public Item(String bsId, String title, String note, String info, int color, String parentBsId) {
            this.bsId = bsId;
            this.title = title;
            this.note = note;
            this.info = info;
            this.color = color;
            this.parentBsId = parentBsId;
        }
    }

    /**
     *
     */
    private class Holder {
        TextView titleTextView;
        TextView noteTextView;
        TextView infoTextView;
    }

    /**
     * MyAdapter for our list of {@link Item}s.
     */
    private class MyAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
        private LayoutInflater inflater;

        private List<Item> items;

        public MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void setData(List<Item> items) {
            this.items = items;
        }

        @Override
        public Item getChild(int groupPosition, int childPosition) {
            return items.get(groupPosition).children.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            Holder holder;
            Item item = getChild(groupPosition, childPosition);
            if (convertView == null) {
                holder = new Holder();
                convertView = inflater.inflate(R.layout.list_item_physical_site_info, parent, false);
                holder.titleTextView = (TextView) convertView.findViewById(R.id.textTitle);
                holder.noteTextView = (TextView) convertView.findViewById(R.id.textContent);
                holder.infoTextView = (TextView) convertView.findViewById(R.id.textType);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            String title = item.title;
            String note = item.note;
            String info = item.info;
            int color = item.color;

            // Set text
            holder.titleTextView.setText(title);
            holder.noteTextView.setText(note);
            holder.infoTextView.setText(info);

            // Set visibilities
            holder.titleTextView.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
            holder.noteTextView.setVisibility(TextUtils.isEmpty(note) ? View.GONE : View.VISIBLE);

            // Set padding
            int paddingTop = (holder.titleTextView.getVisibility() != View.VISIBLE) ? 0
                    : convertView.getContext().getResources()
                    .getDimensionPixelSize(R.dimen.note_content_spacing);
            holder.noteTextView.setPadding(holder.noteTextView.getPaddingLeft(), paddingTop,
                    holder.noteTextView.getPaddingRight(), holder.noteTextView.getPaddingBottom());

            // Set background color
            convertView.setBackgroundColor(color);
            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            return items.get(groupPosition).children.size();
        }

        @Override
        public Item getGroup(int groupPosition) {
            return items.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return items.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            Holder holder;
            Item item = getGroup(groupPosition);
            if (convertView == null) {
                holder = new Holder();
                convertView = inflater.inflate(R.layout.list_item_physical_site_info, parent, false);
                holder.titleTextView = (TextView) convertView.findViewById(R.id.textTitle);
                holder.noteTextView = (TextView) convertView.findViewById(R.id.textContent);
                holder.infoTextView = (TextView) convertView.findViewById(R.id.textType);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            String title = item.title;
            String note = item.note;
            String info = item.info;
            int color = item.color;

            // Set text
            holder.titleTextView.setText(title);
            holder.noteTextView.setText(note);
            holder.infoTextView.setText(info);

            // Set visibilities
            holder.titleTextView.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
            holder.noteTextView.setVisibility(TextUtils.isEmpty(note) ? View.GONE : View.VISIBLE);

            // Set padding
            int paddingTop = (holder.titleTextView.getVisibility() != View.VISIBLE) ? 0
                    : convertView.getContext().getResources()
                    .getDimensionPixelSize(R.dimen.note_content_spacing);
            holder.noteTextView.setPadding(holder.noteTextView.getPaddingLeft(), paddingTop,
                    holder.noteTextView.getPaddingRight(), holder.noteTextView.getPaddingBottom());

            // Set background color
            convertView.setBackgroundColor(color);
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int arg0, int arg1) {
            return true;
        }

    }
}
