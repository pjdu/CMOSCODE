package cn.com.chinaccs.datasite.main.ui.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.ui.models.PhysicalSiteListBean;

/**
 * 站点规划功能列表显示
 * <p/>
 * Created by AndyHua on 7/18/2015.
 * <p/>
 * MyAdapter for the all items screen.
 */
public class PhysicalSiteListAdapter extends RecyclerView.Adapter<PhysicalSiteListAdapter.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<PhysicalSiteListBean> mBeans;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, PhysicalSiteListBean data);
    }

    public PhysicalSiteListAdapter(Context context, List<PhysicalSiteListBean> beans) {
        this.mContext = context;
        this.mBeans = beans;
    }

    public void setData(List<PhysicalSiteListBean> items) {
        this.mBeans = items;
        notifyDataSetChanged();
    }

    public void addData(PhysicalSiteListBean item) {
        this.mBeans.add(item);
        notifyDataSetChanged();
    }


    public void clearData() {
        this.mBeans.clear();
        notifyDataSetChanged();
    }

    @Override
    public PhysicalSiteListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_physical_site_list, parent,
                false);
        //将创建的View注册点击事件
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PhysicalSiteListBean noteModel = mBeans.get(position);
        String title = noteModel.getTitle();
        String note = noteModel.getNote();
        String info = noteModel.getInfo();
        int infoImage = noteModel.getInfoImage();
        int color = noteModel.getColor();

        // Set text
        holder.titleTextView.setText(title);
        holder.noteTextView.setText(note);
        holder.infoTextView.setText(info);

        // Set image
        if (infoImage != 0) {
            holder.infoImageView.setImageResource(infoImage);
        }

        // Set visibilities
        holder.titleTextView.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
        holder.noteTextView.setVisibility(TextUtils.isEmpty(note) ? View.GONE : View.VISIBLE);
        holder.infoLayout.setVisibility(TextUtils.isEmpty(info) ? View.GONE : View.VISIBLE);

        // Set padding
        int paddingTop = (holder.titleTextView.getVisibility() != View.VISIBLE) ? 0
                : holder.itemView.getContext().getResources()
                .getDimensionPixelSize(R.dimen.note_content_spacing);
        holder.noteTextView.setPadding(holder.noteTextView.getPaddingLeft(), paddingTop,
                holder.noteTextView.getPaddingRight(), holder.noteTextView.getPaddingBottom());

        // Set background color
        ((CardView) holder.itemView).setCardBackgroundColor(color);
        //将数据保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(mBeans.get(position));
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return mBeans.size();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (PhysicalSiteListBean) v.getTag());
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public TextView noteTextView;
        public LinearLayout infoLayout;
        public TextView infoTextView;
        public ImageView infoImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.note_title);
            noteTextView = (TextView) itemView.findViewById(R.id.note_text);
            infoLayout = (LinearLayout) itemView.findViewById(R.id.note_info_layout);
            infoTextView = (TextView) itemView.findViewById(R.id.note_info);
            infoImageView = (ImageView) itemView.findViewById(R.id.note_info_image);
        }
    }

}
