package cn.com.chinaccs.datasite.main.datasite.function;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.chinaccs.datasite.main.DataModify;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.WGSTOGCJ02;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.gps.GpsHandler;
import cn.com.chinaccs.datasite.main.ui.MainApp;
import cn.com.chinaccs.datasite.main.ui.cmos.BoxCompassActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.BoxDowntiltActivity;
import cn.com.chinaccs.datasite.main.ui.asset.mobile.MobileCardsActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.DSCommonModifyActivity;


//import cn.com.chinaccs.datasite.main.ContratsMapActivity;

public class ModifyViewFactory {
    private Context context;

    public ModifyViewFactory(Context context) {
        this.context = context;
    }

    public boolean addNormalView(DataModify dm, LinearLayout layout,
                                 List<EditText> listControl) {
        boolean show = true;
        try {
            LayoutInflater lit = LayoutInflater.from(context);
            View view = lit.inflate(R.layout.item_modify, null);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_item_title);
            TextView tvTips = (TextView) view.findViewById(R.id.tv_item_tips);
            EditText etValue = (EditText) view
                    .findViewById(R.id.et_item_modify);
            Button btnAction = (Button) view.findViewById(R.id.btn_item_modify);
            btnAction.setVisibility(View.GONE);
            tvTitle.setText(dm.getTitle());
            String tips = dm.getTips();
            if (tips != null && !tips.equals("")) {
                tvTips.setVisibility(View.VISIBLE);
                tvTips.setText(tips);
            } else {
                tvTips.setVisibility(View.GONE);
            }
            etValue.setText(dm.getValue());
            etValue.setEnabled(dm.isCustom());
            layout.addView(view);
            listControl.add(etValue);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return show;
    }

    public boolean addRadioView(final DataModify dm, LinearLayout layout,
                                List<EditText> listControl) {
        boolean show = true;
        try {
            LayoutInflater lit = LayoutInflater.from(context);
            View view = lit.inflate(R.layout.item_modify, null);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_item_title);
            TextView tvTips = (TextView) view.findViewById(R.id.tv_item_tips);
            final EditText etValue = (EditText) view
                    .findViewById(R.id.et_item_modify);
            Button btnAction = (Button) view.findViewById(R.id.btn_item_modify);
            btnAction.setVisibility(View.VISIBLE);
            btnAction.setText("点击选择");
            OnClickListener lr = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    FuncGetFieldOptions fos = new FuncGetFieldOptions(context);
                    OnGetDataFinishedListener oflr = new OnGetDataFinishedListener() {

                        @Override
                        public void onFinished(String output) {
                            // TODO Auto-generated method stub
                            try {
                                JSONArray array = new JSONArray(output);
                                final String[] items = new String[array
                                        .length()];
                                for (int i = 0; i < array.length(); i++) {
                                    JSONArray data = array.getJSONArray(i);
                                    items[i] = data.getString(0);
                                }

                                DialogInterface.OnClickListener dlr = new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int index) {
                                        // TODO Auto-generated method stub
                                        etValue.setText(items[index]);
                                        dialog.dismiss();
                                    }
                                };
                                new AlertDialog.Builder(context)
                                        .setTitle(dm.getTitle())
                                        .setSingleChoiceItems(items, -1, dlr)
                                        .show();
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    };
                    fos.getData(oflr, dm.getFieldName(), dm.getFieldBelong());
                }

            };
            btnAction.setOnClickListener(lr);
            tvTitle.setText(dm.getTitle());
            String tips = dm.getTips();
            if (tips != null && !tips.equals("")) {
                tvTips.setVisibility(View.VISIBLE);
                tvTips.setText(tips);
            } else {
                tvTips.setVisibility(View.GONE);
            }
            etValue.setText(dm.getValue());
            etValue.setEnabled(dm.isCustom());
            layout.addView(view);
            listControl.add(etValue);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return show;
    }


    public boolean addCheckboxView(final DataModify dm, LinearLayout layout,
                                   List<EditText> listControl) {
        boolean show = true;
        try {
            LayoutInflater lit = LayoutInflater.from(context);
            View view = lit.inflate(R.layout.item_modify, null);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_item_title);
            TextView tvTips = (TextView) view.findViewById(R.id.tv_item_tips);
            final EditText etValue = (EditText) view
                    .findViewById(R.id.et_item_modify);
            Button btnAction = (Button) view.findViewById(R.id.btn_item_modify);
            btnAction.setVisibility(View.VISIBLE);
            btnAction.setText("点击选择");
            OnClickListener lr = new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    FuncGetFieldOptions fos = new FuncGetFieldOptions(context);
                    OnGetDataFinishedListener oflr = new OnGetDataFinishedListener() {

                        @Override
                        public void onFinished(String output) {
                            // TODO Auto-generated method stub
                            try {
                                JSONArray array = new JSONArray(output);
                                final String[] items = new String[array
                                        .length()];
                                for (int i = 0; i < array.length(); i++) {
                                    JSONArray data;

                                    data = array.getJSONArray(i);
                                    items[i] = data.getString(0);

                                }
                                final boolean[] listSel = new boolean[items.length];
                                for (int i = 0; i < items.length; i++) {
                                    listSel[i] = false;
                                }
                                OnMultiChoiceClickListener mcclr = new OnMultiChoiceClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which, boolean isChecked) {
                                        // TODO Auto-generated method stub
                                        listSel[which] = isChecked;
                                    }
                                };
                                DialogInterface.OnClickListener dlr = new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int index) {
                                        // TODO Auto-generated method stub
                                        String selectedStr = "";
                                        for (int i = 0; i < listSel.length; i++) {
                                            if (listSel[i]) {
                                                selectedStr += "," + items[i];
                                            }
                                        }
                                        if (!selectedStr.equals("")) {
                                            selectedStr = selectedStr
                                                    .substring(1);
                                        }
                                        etValue.setText(selectedStr);
                                        dialog.dismiss();
                                    }
                                };
                                new AlertDialog.Builder(context)
                                        .setTitle(dm.getTitle())
                                        .setMultiChoiceItems(items, listSel,
                                                mcclr)
                                        .setPositiveButton("确定", dlr).show();
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    };
                    fos.getData(oflr, dm.getFieldName(), dm.getFieldBelong());
                }
            };
            btnAction.setOnClickListener(lr);
            tvTitle.setText(dm.getTitle());
            String tips = dm.getTips();
            if (tips != null && !tips.equals("")) {
                tvTips.setVisibility(View.VISIBLE);
                tvTips.setText(tips);
            } else {
                tvTips.setVisibility(View.GONE);
            }
            etValue.setText(dm.getValue());
            etValue.setEnabled(dm.isCustom());
            layout.addView(view);
            listControl.add(etValue);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return show;
    }

    /**
     * 定位表单编辑
     *
     * @param layout
     * @param listControl
     * @param cross
     * @return
     */
    public boolean addLocationView(final Location loc, DataModify dm,
                                   LinearLayout layout, List<EditText> listControl, final int cross,
                                   final String v1, final String name) {
        boolean show = true;
        try {
            LayoutInflater lit = LayoutInflater.from(context);
            View view = lit.inflate(R.layout.item_modify, null);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_item_title);
            TextView tvTips = (TextView) view.findViewById(R.id.tv_item_tips);
            final EditText etValue = (EditText) view
                    .findViewById(R.id.et_item_modify);
            Button btnAction = (Button) view.findViewById(R.id.btn_item_modify);
            btnAction.setVisibility(View.VISIBLE);
            btnAction.setText("获取位置");
            OnClickListener lr = new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Map<String, Double> locs = getLocs(loc);
                    if (locs != null) {
                        String text;
                        double lng = locs.get("lng");
                        double lat = locs.get("lat");
                        switch (cross) {
                            case 1:
                                text = "获取定位信息成功：\n[经度]" + locs.get("lng");
                                locationShow(text, lng, lat, etValue, v1, name,
                                        cross);
                                break;
                            case 2:
                                text = "获取定位信息成功：\n[纬度]" + locs.get("lat");
                                locationShow(text, lng, lat, etValue, v1, name,
                                        cross);
                                break;
                        }
                    } else {
                        Toast.makeText(context, "获取定位信息失败！", Toast.LENGTH_LONG)
                                .show();
                    }
                }
            };
            btnAction.setOnClickListener(lr);

            tvTitle.setText(dm.getTitle());
            String tips = dm.getTips();
            if (tips != null && !tips.equals("")) {
                tvTips.setVisibility(View.VISIBLE);
                tvTips.setText(tips);
            } else {
                tvTips.setVisibility(View.GONE);
            }
            etValue.setText(dm.getValue());
            etValue.setEnabled(dm.isCustom());
            layout.addView(view);
            listControl.add(etValue);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return show;
    }

    private Map<String, Double> getLocs(Location loc) {
        if (loc != null) {// GPS定位
            long ot = new Date().getTime() - loc.getTime();
            if (ot <= GpsHandler.TIME_OUT_LOC) {
                Map<String, Double> locs = new HashMap<String, Double>();
                locs.put("lng", loc.getLongitude());
                locs.put("lat", loc.getLatitude());
                Log.d(App.LOG_TAG, "location by GPS --" + locs.get("lng") + "/"
                        + locs.get("lat"));
                return locs;
            }
        }
        if (MainApp.geoBD != null && MainApp.geoBD.locClient != null
                && MainApp.geoBD.locClient.isStarted()) {
            MainApp.geoBD.locClient.requestLocation();
            BDLocation bloc = MainApp.geoBD.location;
            if (bloc != null) {// 百度接口定位
                WGSTOGCJ02 wg = new WGSTOGCJ02();
                Map<String, Double> wgsloc = wg.gcj2wgs(bloc.getLongitude(),
                        bloc.getLatitude());
                Map<String, Double> locs = new HashMap<String, Double>();
                locs.put("lng", wgsloc.get("lon"));
                locs.put("lat", wgsloc.get("lat"));
                Log.d(App.LOG_TAG, "location by baidu --" + locs.get("lng")
                        + "/" + locs.get("lat"));
                return locs;
            }
        }
        return null;
    }

    /**
     * 定位窗口
     *
     * @param text
     * @param et
     * @param v1
     * @param name
     * @param cross
     */
    @SuppressWarnings("deprecation")
    private void locationShow(String text, final double lng, final double lat,
                              final EditText et, final String v1, final String name,
                              final int cross) {
        LayoutInflater li = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.pop_dialog_location, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_pop_loc);
        Button btnMap = (Button) view.findViewById(R.id.btn_pop_show_map);
        Button btnClose = (Button) view.findViewById(R.id.btn_pop_close);
        Button btnGet = (Button) view.findViewById(R.id.btn_pop_getloc);
        final PopupWindow pop = new PopupWindow(view, LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
        pop.setBackgroundDrawable(context.getResources().getDrawable(
                R.drawable.draw_bg_opactiy));
        pop.setFocusable(true);
        OnClickListener lr = new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.btn_pop_show_map:
                        Bundle be = new Bundle();
                        if (cross == 1) {
                            be.putString("bsLng", et.getText().toString());
                            be.putString("bsLat", v1);

                        } else if (cross == 2) {
                            be.putString("bsLng", v1);
                            be.putString("bsLat", et.getText().toString());
                        }
                        be.putString("ctLng", String.valueOf(lng));
                        be.putString("ctLat", String.valueOf(lat));
                        be.putString("bsName", name);
//					Intent i = new Intent(context, ContratsMapActivity.class);
//					i.putExtras(be);
//					context.startActivity(i);
                        break;
                    case R.id.btn_pop_close:
                        pop.dismiss();
                        break;
                    case R.id.btn_pop_getloc:
                        pop.dismiss();
                        if (cross == 1) {
                            et.setText(String.valueOf(lng));
                        } else {
                            et.setText(String.valueOf(lat));
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        tv.setText(text);
        btnMap.setOnClickListener(lr);
        btnClose.setOnClickListener(lr);
        btnGet.setOnClickListener(lr);
        pop.showAtLocation(((Activity) context).findViewById(R.id.main_by_pop),
                Gravity.CENTER, 0, 0);
    }

    /**
     * 方位角测量表单编辑
     *
     * @param layout
     * @param listControl
     * @return
     */
    public boolean addAzimuthView(DataModify dm, LinearLayout layout,
                                  List<EditText> listControl, final int index) {
        boolean show = true;
        try {
            LayoutInflater lit = LayoutInflater.from(context);
            View view = lit.inflate(R.layout.item_modify, null);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_item_title);
            TextView tvTips = (TextView) view.findViewById(R.id.tv_item_tips);
            EditText etValue = (EditText) view
                    .findViewById(R.id.et_item_modify);
            Button btnAction = (Button) view.findViewById(R.id.btn_item_modify);
            btnAction.setVisibility(View.VISIBLE);
            btnAction.setText("现场测量");
            OnClickListener lr = new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Bundle be = new Bundle();
                    be.putInt("index", index);
                    Intent i = new Intent(context, BoxCompassActivity.class);
                    i.putExtras(be);
                    ((Activity) context).startActivityForResult(i,
                            DSCommonModifyActivity.REQUEST_CODE_ANGLE);
                }
            };
            btnAction.setOnClickListener(lr);

            tvTitle.setText(dm.getTitle());
            String tips = dm.getTips();
            if (tips != null && !tips.equals("")) {
                tvTips.setVisibility(View.VISIBLE);
                tvTips.setText(tips);
            } else {
                tvTips.setVisibility(View.GONE);
            }
            etValue.setText(dm.getValue());
            etValue.setEnabled(dm.isCustom());
            layout.addView(view);
            listControl.add(etValue);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return show;
    }

    /**
     * 下倾角测量表单编辑
     *
     * @param layout
     * @param listControl
     * @return
     */
    public boolean addDowntiltView(DataModify dm, LinearLayout layout,
                                   List<EditText> listControl, final int index) {
        boolean show = true;
        try {
            LayoutInflater lit = LayoutInflater.from(context);
            View view = lit.inflate(R.layout.item_modify, null);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_item_title);
            TextView tvTips = (TextView) view.findViewById(R.id.tv_item_tips);
            EditText etValue = (EditText) view
                    .findViewById(R.id.et_item_modify);
            Button btnAction = (Button) view.findViewById(R.id.btn_item_modify);
            btnAction.setVisibility(View.VISIBLE);
            btnAction.setText("现场测量");
            OnClickListener lr = new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Bundle be = new Bundle();
                    be.putInt("index", index);
                    Intent i = new Intent(context, BoxDowntiltActivity.class);
                    i.putExtras(be);
                    ((Activity) context).startActivityForResult(i,
                            DSCommonModifyActivity.REQUEST_CODE_DOWNTILT);
                }
            };
            btnAction.setOnClickListener(lr);

            tvTitle.setText(dm.getTitle());
            String tips = dm.getTips();
            if (tips != null && !tips.equals("")) {
                tvTips.setVisibility(View.VISIBLE);
                tvTips.setText(tips);
            } else {
                tvTips.setVisibility(View.GONE);
            }
            etValue.setText(dm.getValue());
            etValue.setEnabled(dm.isCustom());
            layout.addView(view);
            listControl.add(etValue);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return show;
    }

    /**
     * 固定资产编号扫码表单编辑
     *
     * @param layout
     * @param listControl
     * @return
     */
    public boolean addQrCodeView(DataModify dm, LinearLayout layout,
                                 List<EditText> listControl, final int index) {
        boolean show = true;
        try {
            LayoutInflater lit = LayoutInflater.from(context);
            View view = lit.inflate(R.layout.item_modify, null);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_item_title);
            TextView tvTips = (TextView) view.findViewById(R.id.tv_item_tips);
            EditText etValue = (EditText) view
                    .findViewById(R.id.et_item_modify);
            Button btnAction = (Button) view.findViewById(R.id.btn_item_modify);
            btnAction.setVisibility(View.VISIBLE);
            btnAction.setCompoundDrawablesWithIntrinsicBounds(
                    context.getResources().getDrawable(R.drawable.icon_qr_code),
                    null, null, null);
            btnAction.setText("点击扫码");
            OnClickListener lr = new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                   /* Bundle be = new Bundle();
                    be.putInt("index", index);
                    Intent intent = new Intent(context, CaptureActivity.class);
                    intent.putExtras(be);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ((Activity) context).startActivityForResult(intent,
                            CaptureActivity.REQUEST_ASSETS_CODE_SCANNING);*/
                    Intent intent = new Intent(context, MobileCardsActivity.class);
                    ((Activity) context).startActivity(intent);
                }
            };
            btnAction.setOnClickListener(lr);

            tvTitle.setText(dm.getTitle());
            String tips = dm.getTips();
            if (tips != null && !tips.equals("")) {
                tvTips.setVisibility(View.VISIBLE);
                tvTips.setText(tips);
            } else {
                tvTips.setVisibility(View.GONE);
            }
            etValue.setText(dm.getValue());
            etValue.setEnabled(dm.isCustom());
            layout.addView(view);
            listControl.add(etValue);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return show;
    }
}
