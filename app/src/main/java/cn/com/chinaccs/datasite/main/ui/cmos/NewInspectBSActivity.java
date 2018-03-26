package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCDMABaseStation;
import cn.com.chinaccs.datasite.main.datasite.function.BDGeoLocation;
import cn.com.chinaccs.datasite.main.datasite.function.FuncServerInset;
import cn.com.chinaccs.datasite.main.datasite.function.NewBSListAdapter;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.FuncGetBSInsPlanList;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.datasite.utils.SoftKeyboardStateHelper;
import cn.com.chinaccs.datasite.main.db.model.BaseStation;
import cn.com.chinaccs.datasite.main.db.model.CurrentCell;
import cn.com.chinaccs.datasite.main.widget.TabDrawTop;


/**
 * Created by liu on 15/9/1.
 * <p/>
 * 新基站巡检activity,等业务再熟悉一点，要将这一堆变量封装成一个model，太乱了
 */
public class NewInspectBSActivity extends BaseActivity implements SoftKeyboardStateHelper
        .SoftKeyboardStateListener {
    private final String TAG = NewInspectBSActivity.class.getSimpleName();
    private Context context;
    private Button btnIBs;   //当前接入基站
    private Button btnCell;  //当前接入小区
    private ListView lvRRU; // 独立室分巡检
    private ListView lvNews;    //新基站列表
    private ListView lvHiddens;    //隐患列表
    private ListView lvInspected;    //已巡检基站列表
    private TabHost tabMain;

    // 独立室分巡检
    private TextView tvstate_rru;
    private TextView tvstate_new;
    private TextView tvstate_hidden;
    private TextView tvstate_inspected;
    private String ci;      //基站小区号 cellId(估计是统一硬件id) 扇区，天线
    private String sid;    // 系统标识(估计是统一硬件id)
    //新基站列表
    private List<BaseStation> newsList;
    //独立室分巡检
    private List<BaseStation> rruList;
    //已巡检基站列表
    private List<BaseStation> inspectedList;
    //隐患列表
    private List<BaseStation> hiddensList;

    //当前接入基站和小区
    private BaseStation cStation;
    private CurrentCell iCell;
    //用于存储新基站和隐患基站的集合
    // 独立室分巡检
    private List<String> rruNames;
    private List<String> rruNamesTelecom;
    private List<String> rruNamesTower;
    private List<String> newBsNames;
    private List<String> NewBsNamesTelecom;
    private List<String> NewBsNamesTower;

    private List<String> hiddenBsNames;
    private ArrayList<String> inspectedBsNames;
    // 独立室分巡检
    private EditText searchEt_rru;
    private EditText searchEt_new;
    private EditText searchEt_hidden;
    private EditText searchEt_inspected;
    // 独立室分巡检
    private NewBSListAdapter adapterRRU;
    private NewBSListAdapter adapterRR_telecom;
    private NewBSListAdapter adapterRRU_tower;

    private NewBSListAdapter adapterNews;
    private NewBSListAdapter adapterNews_telecom;
    private NewBSListAdapter adapterNews_tower;

    private NewBSListAdapter adapterHidden;
    private NewBSListAdapter adapterInspected;
    private LinearLayout topLayout;
    private SoftKeyboardStateHelper helper;
    private View root;
    //新增的电信产权，电塔产权
    private RadioGroup radioGroup_new;
    private RadioGroup radioGroup_rru;
    private RadioButton new_btnTeleCom;
    private RadioButton new_btnTower;
    private RadioButton rru_btnTeleCom;
    private RadioButton rru_btnTower;
    private List<String> newlist;
    private List<String> rrulist;

    // 判断是否进行签到
    private double lng = 4.9E-324;
    private double lat = 4.9E-324;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        root = View.inflate(this, R.layout.activity_inspect_bs_new_change, null);
        setContentView(root);
        initToolbar("选择巡检基站");
        helper = new SoftKeyboardStateHelper(root);
        helper.addSoftKeyboardStateListener(this);
        this.findViews();
        this.initTabhost();
        ci = AppCDMABaseStation.getCi(context);
        sid = AppCDMABaseStation.getCDMASid(context);
        this.buildInsertBs();
        this.buildList();
        btnIBs.setOnClickListener(clr);
        btnCell.setOnClickListener(clr);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_sign) {
            //巡检签到
            Intent intent = new Intent(this, InspectSignActivity.class);
            this.startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        ci = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private View.OnClickListener clr = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_insetbs:
                    //这里加了个cStation==null，防止获取不到icell导致程序奔溃
                    if (cStation == null || cStation.getBsIds() == null) {
                        Toast.makeText(context, "获取接入基站失败！", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        Toast.makeText(context, "对不起、请在基站列表中进行操作....", Toast.LENGTH_LONG)
                                .show();
                    }
                    break;
                case R.id.btn_insetcell:
                    //这里加了个iCell==null，防止获取不到icell导致程序奔溃
                    if (iCell == null || iCell.getiCellId() == null) {
                        Toast.makeText(context, "获取接入小区失败！", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        if (iCell.getIsSingleRru() != null && iCell.getIsSingleRru().equals("0")) {
                            Toast.makeText(context, "该小区不是独立物理RRU站点，请选择基站巡检！", Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            Toast.makeText(context, "对不起、请在基站列表中进行操作....", Toast.LENGTH_LONG)
                                    .show();
                            String str;

                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };


    // 待巡检基站
    private AdapterView.OnItemClickListener itemClrNew = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int index,
                                long arg3) {
            if (!checkSignSuccesed()){
                Toast.makeText(context, "请先打打开GPS签到、再使用该功能....", Toast.LENGTH_LONG)
                        .show();
                //巡检签到
                Intent intent = new Intent(context, InspectSignActivity.class);
                startActivity(intent);
                return;
            }
            Bundle be = new Bundle();
            for (BaseStation bsItem : newsList) {
                // changed by wuhua for 点击列表与内容对不上
//                if (bsItem.getBsName() == newBsNames.get(index)) {
//                    be.putSerializable("bs", bsItem);
//                }
                if (bsItem.getBsNames() == adapterNews.getItem(index)) {
                    Log.i(App.LOG_TAG, bsItem.toString());
                    be.putSerializable("bs", bsItem);
                }
            }
            Intent i = null;
            /*if (newsList.get(index).getBsState() != null && newsList.get(index).getBsState().equals("1")) {
                i = new Intent(context, InspectCellOptionActivity_newbs.class);
            } else {*/
            be.putBoolean("isRRU", false);
            i = new Intent(context, NewInspectPlanActivity.class);
            // }
            i.putExtras(be);
            startActivity(i);
        }
    };

    // 已巡检基站
    private AdapterView.OnItemClickListener itemClrInspected = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int index,
                                long arg3) {
            /*if (!checkSignSuccesed()){
                Toast.makeText(context, "请先打打开GPS签到、再使用该功能....", Toast.LENGTH_LONG)
                        .show();
                //巡检签到
                Intent intent = new Intent(context, InspectSignActivity.class);
                startActivity(intent);
                return;
            }*/
            Bundle be = new Bundle();
            for (BaseStation bsItem : inspectedList) {
                // changed by wuhua for 点击列表与内容对不上
//                if (bsItem.getBsName() == inspectedBsNames.get(index)) {
//                    be.putSerializable("bs", bsItem);
//                }
                if (bsItem.getBsNames() == adapterInspected.getItem(index)) {
                    be.putSerializable("bs", bsItem);
                }
            }
            Intent i = null;
           /* if (inspectedList.get(index).getBsState() != null && inspectedList.get(index).getBsState().equals("1")) {
                i = new Intent(context, InspectCellOptionActivity_newbs.class);
            } else {*/
            be.putBoolean("isRRU", false);
            i = new Intent(context, NewInspectPlanActivity.class);
            // }
            i.putExtras(be);
            startActivity(i);
        }
    };

    // 隐患
    private AdapterView.OnItemClickListener itemClrHidden = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int index,
                                long arg3) {
            /*if (!checkSignSuccesed()){
                Toast.makeText(context, "请先打打开GPS签到、再使用该功能....", Toast.LENGTH_LONG)
                        .show();
                //巡检签到
                Intent intent = new Intent(context, InspectSignActivity.class);
                startActivity(intent);
                return;
            }*/
            Bundle be = new Bundle();
            for (BaseStation bshidden : hiddensList) {
                // changed by wuhua for 点击列表与内容对不上
//                if (bshidden.getBsName() == hiddenBsNames.get(index)) {
//                    be.putSerializable("bs", bshidden);
//                }
                if (bshidden.getBsNames() == adapterHidden.getItem(index)) {
                    be.putSerializable("bs", bshidden);
                }
            }

            Intent i = null;
//	            if (bsList.get(index).getBsState() != null && bsList.get(index).getBsState().equals("1")) {
//	                i = new Intent(context, InspectCellOptionActivity_newbs.class);
//	            } else {
            be.putBoolean("isRRU", false);
            i = new Intent(context, TroubleInfoActivity.class);
//	            }
            i.putExtras(be);
            startActivity(i);
        }
    };

    // 独立室分巡检
    private AdapterView.OnItemClickListener itemClrRRU = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int index,
                                long arg3) {
            if (!checkSignSuccesed()){
                Toast.makeText(context, "请先打打开GPS签到、再使用该功能....", Toast.LENGTH_LONG)
                        .show();
                //巡检签到
                Intent intent = new Intent(context, InspectSignActivity.class);
                startActivity(intent);
                return;
            }
            Bundle be = new Bundle();
            for (BaseStation bsItem : rruList) {
                // changed by wuhua for 点击列表与内容对不上
//                if (bsItem.getBsName() == newBsNames.get(index)) {
//                    be.putSerializable("bs", bsItem);
//                }
                if (bsItem.getBsNames() == adapterRRU.getItem(index)) {
                    be.putSerializable("bs", bsItem);
                }
            }
            Intent i = null;
           /* if (rruList.get(index).getBsState() != null && rruList.get(index).getBsState().equals("1")) {
                i = new Intent(context, InspectCellOptionActivity_newbs.class);
            } else {*/
            if (iCell != null) {
                be.putBoolean("isRRU", true);
            } else {
                be.putBoolean("isRRU", false);
            }
            be.putSerializable("cell", iCell);
            i = new Intent(context, NewInspectPlanActivity.class);
            // }
            i.putExtras(be);
            startActivity(i);
        }
    };

    //任务基站初始化
    private void buildList() {
        tvstate_new.setVisibility(View.VISIBLE);
        tvstate_hidden.setVisibility(View.VISIBLE);
        tvstate_rru.setVisibility(View.VISIBLE);
        lvHiddens.setVisibility(View.GONE);
        lvNews.setVisibility(View.GONE);
        lvRRU.setVisibility(View.GONE);
        //用于在不同列表显示
        newBsNames = new ArrayList<String>();
        NewBsNamesTelecom=new ArrayList<String>();
        NewBsNamesTower=new ArrayList<String>();
        newsList = new ArrayList<BaseStation>();
        rruNames = new ArrayList<>();
        rruNamesTelecom=new ArrayList<String>();
        rruNamesTower=new ArrayList<String>();
        rruList = new ArrayList<BaseStation>();
        hiddenBsNames = new ArrayList<String>();
        hiddensList = new ArrayList<BaseStation>();
        inspectedBsNames = new ArrayList<String>();
        inspectedList = new ArrayList<BaseStation>();
        getBSListData("", "");
    }

    //任务基站初始化
    private void getBSListData(String longitude, String latitude) {
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                Log.i(App.LOG_TAG, output);
                if (output.equals("fail")) {
                    //					tvState.setText(getResources().getString(
                    //							R.string.common_not_network));
                    Toast.makeText(
                            context,
                            getResources().getString(
                                    R.string.common_not_network),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    JSONObject json = new JSONObject(output);
                    String result = json.getString("result");
                    String msg = json.getString("msg");
                    if (result.equals("1")) {
                        JSONArray data = json.getJSONArray("data");
                        Log.e("data", "data" + data.toString());
                        BaseStation bs;
                        for (int i = 0; i < data.length(); i++) {
                            // 数据有度，也有度分秒，需统一
                            String lon = data.getJSONArray(i).getString(2);
                            if (lon.contains("°")) {
                                lon = BDGeoLocation.getDFMToD(lon);
                            }
                            String lat = data.getJSONArray(i).getString(3);
                            if (lat.contains("°")) {
                                lat = BDGeoLocation.getDFMToD(lat);
                            }
                            bs = new BaseStation(data.getJSONArray(i).getString(0),
                                    data.getJSONArray(i).getString(1),
                                    lon,
                                    lat,
                                    data.getJSONArray(i).getString(4),
                                    data.getJSONArray(i).getString(5),
                                    data.getJSONArray(i).getString(6),
                                    data.getJSONArray(i).getString(7));
                            //用于存放搜索数据
                            //基站巡检
                            if (bs.getType().equals("0")) {
                                newBsNames.add(bs.getBsNames());
                                newsList.add(bs);

                            }
                            if (bs.getType().equals("0")&&bs.getotherInfo().equals("1")){
                                NewBsNamesTelecom.add(bs.getBsNames());

                            }
                            if (bs.getType().equals("0")&&bs.getotherInfo().equals("2")){
                                NewBsNamesTower.add(bs.getBsNames());

                            }
                            if (bs.getType().equals("1")&&bs.getotherInfo().equals("1")){
                                rruNamesTelecom.add(bs.getBsNames());

                            }
                            if (bs.getType().equals("1")&&bs.getotherInfo().equals("2")){
                                rruNamesTower.add(bs.getBsNames());

                            }

                            // 室分巡检
                            if (bs.getType().equals("1")) {
                                rruNames.add(bs.getBsNames());
                                rruList.add(bs);
                            }
                            //隐患
                            if (bs.getType().equals("2")) {
                                hiddenBsNames.add(bs.getBsNames());
                                hiddensList.add(bs);
                            }
                            //已巡检列表
                            if (bs.getType().equals("3")) {
                                inspectedBsNames.add(bs.getBsNames());
                                inspectedList.add(bs);
                            }
                        }
                        Log.i(TAG,"NewBsNamesTelecom"+NewBsNamesTelecom);
                        Log.i(TAG,"NewBsNamesTower"+NewBsNamesTower);
                        Log.i(TAG,"rruNamesTelecom"+rruNamesTelecom);
                        Log.i(TAG,"rruNamesTower"+rruNamesTower);

                        if (new_btnTeleCom.isChecked()){
                            adapterNews = new NewBSListAdapter(
                                    context, NewBsNamesTelecom);
                            lvNews.setAdapter(adapterNews);
                            newlist=NewBsNamesTelecom;
                        }if (rru_btnTeleCom.isChecked()){
                            adapterRRU = new NewBSListAdapter(
                                    context, rruNamesTelecom);
                            lvRRU.setAdapter(adapterRRU);
                            rrulist=rruNamesTelecom;
                        }

                        radioGroup_new.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                if (checkedId==new_btnTeleCom.getId()){
                                    adapterNews = new NewBSListAdapter(
                                            context, NewBsNamesTelecom);
                                    Log.i(TAG,"new_btnTeleCom");
                                    lvNews.setAdapter(adapterNews);
                                    newlist=NewBsNamesTelecom;
                                    searchEt_new.setText("");
                                    initSearch(newlist,rrulist);
                                }if (checkedId==new_btnTower.getId()){
                                    adapterNews = new NewBSListAdapter(
                                            context, NewBsNamesTower);
                                    Log.i(TAG,"new_btnTower");
                                    lvNews.setAdapter(adapterNews);
                                    newlist=NewBsNamesTower;
                                    searchEt_new.setText("");
                                    initSearch(newlist,rrulist);
                                }
                            }
                        });
                        radioGroup_rru.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                if (checkedId==rru_btnTeleCom.getId()){
                                    adapterRRU = new NewBSListAdapter(
                                            context, rruNamesTelecom);
                                    Log.i(TAG,"rru_btnTeleCom");
                                    lvRRU.setAdapter(adapterRRU);
                                    rrulist=rruNamesTelecom;
                                    searchEt_rru.setText("");
                                    initSearch(newlist,rrulist);
                                }if (checkedId==rru_btnTower.getId()){
                                    adapterRRU = new NewBSListAdapter(
                                            context, rruNamesTower);
                                    Log.i(TAG,"rru_btnTower");
                                    lvRRU.setAdapter(adapterRRU);
                                    rrulist=rruNamesTower;
                                    searchEt_rru.setText("");
                                    initSearch(newlist,rrulist);
                                }
                            }
                        });
                        //待巡检
                        /*adapterNews = new NewBSListAdapter(
                                context, newBsNames);*/
                        // 室分巡检
                        //adapterRRU = new NewBSListAdapter(context, rruNames);
                        adapterHidden = new NewBSListAdapter(
                                context, hiddenBsNames);
                        adapterInspected = new NewBSListAdapter(
                                context, inspectedBsNames);

                        lvHiddens.setAdapter(adapterHidden);
                        // 室分巡检
                        //lvRRU.setAdapter(adapterRRU);
                        //lvNews.setAdapter(adapterNews);
                        lvInspected.setAdapter(adapterInspected);

                        lvHiddens.setOnItemClickListener(itemClrHidden);
                        // 室分巡检
                        lvRRU.setOnItemClickListener(itemClrRRU);
                        lvNews.setOnItemClickListener(itemClrNew);
                        lvInspected.setOnItemClickListener(itemClrInspected);

                        tvstate_new.setVisibility(View.GONE);
                        // 室分巡检
                        tvstate_rru.setVisibility(View.GONE);
                        tvstate_hidden.setVisibility(View.GONE);
                        tvstate_inspected.setVisibility(View.GONE);

                        lvNews.setVisibility(View.VISIBLE);
                        // 室分巡检
                        lvRRU.setVisibility(View.VISIBLE);
                        lvHiddens.setVisibility(View.VISIBLE);
                        lvInspected.setVisibility(View.VISIBLE);
                        //数据加载完成后初始化搜索栏
                        initSearch(newlist,rrulist);
                    } else {
                        tvstate_new.setText(msg);
                        // 室分巡检
                        tvstate_rru.setText(msg);
                        tvstate_hidden.setText(msg);
                        tvstate_inspected.setText(msg);
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        FuncGetBSInsPlanList func = new FuncGetBSInsPlanList(context);
        func.getData(lr);
    }

    //接入基站和小区信息初始化
    private void buildInsertBs() {
        btnIBs.setText("暂无数据");
        btnCell.setText("暂无数据");
        final ProgressDialog pd = App.progressDialog(context, getResources()
                .getString(R.string.common_request));
        pd.show();
        FuncServerInset func = new FuncServerInset(context);
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                Log.d(TAG, "获取到的icell数据为" + output.toString());
                Log.i(App.LOG_TAG, "--------" + output + "-----------");
                pd.dismiss();
                if (output.equals("fail")) {
                    Toast.makeText(
                            context,
                            getResources().getString(
                                    R.string.common_not_network),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    JSONObject json = new JSONObject(output);
                    String result = json.getString("result");
                    String msg = json.getString("msg");
                    if (result.equals("1")) {
                        JSONArray abs = json.getJSONArray("bs");
                        cStation = new BaseStation();
                        cStation.setBsIds(abs.getString(0));
                        cStation.setBsNames(abs.getString(1));
                        cStation.setBsLongitude(abs.getString(4));
                        cStation.setBsLatitude(abs.getString(5));

                        btnIBs.setText(cStation.getBsNames());
                        JSONArray cellOption = json.getJSONArray("cell");
                        iCell = new CurrentCell(cellOption.getString(2),
                                cellOption.getString(3),
                                cellOption.getString(0),
                                cellOption.getString(1),
                                cellOption.getString(4),
                                cellOption.getString(5),
                                cellOption.getString(6));
                        btnCell.setText(iCell.getiCellName());
                    } else {
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Log.i(App.LOG_TAG, "ci : " + ci + "----------------" + "sid : " + sid);
        func.insetQuery(lr, ci, sid);
    }

    private void findViews() {
        btnIBs = (Button) findViewById(R.id.btn_insetbs);
        btnCell = (Button) findViewById(R.id.btn_insetcell);
        lvNews = (ListView) findViewById(R.id.lv_new);
        // 独立室分巡检
        lvRRU = (ListView) findViewById(R.id.lv_rru);
        lvHiddens = (ListView) findViewById(R.id.lv_hidden);
        lvInspected = (ListView) findViewById(R.id.lv_inspected);
        tabMain = (TabHost) findViewById(R.id.tab_main_new);
        tvstate_new = (TextView) findViewById(R.id.tv_state_new);
        // 独立室分巡检
        tvstate_rru = (TextView) findViewById(R.id.tv_state_rru);
        tvstate_hidden = (TextView) findViewById(R.id.tv_state_hidden);
        tvstate_inspected = (TextView) findViewById(R.id.tv_state_inspected);
        searchEt_new = (EditText) findViewById(R.id.searchEt_new);
        // 独立室分巡检
        searchEt_rru = (EditText) findViewById(R.id.searchEt_rru);
        searchEt_hidden = (EditText) findViewById(R.id.searchEt_hidden);
        searchEt_inspected = (EditText) findViewById(R.id.searchEt_inpected);

        topLayout = (LinearLayout) findViewById(R.id.top_layout);
        new_btnTeleCom = (RadioButton) findViewById(R.id.btn_new_telecom);
        new_btnTower = (RadioButton) findViewById(R.id.btn_new_tower);
        rru_btnTeleCom = (RadioButton) findViewById(R.id.btn_rru_telecom);
        rru_btnTower = (RadioButton) findViewById(R.id.btn_rru_tower);
        radioGroup_new= (RadioGroup) findViewById(R.id.radiogroup_new);
        radioGroup_rru= (RadioGroup) findViewById(R.id.radiogroup_rru);
    }

    private void initSearch(List<String> newlist,List<String> rrulist) {
        searchEt_new.addTextChangedListener(new MyTextWatcher(newlist, searchEt_new, adapterNews));
        // 独立室分巡检
        searchEt_rru.addTextChangedListener(new MyTextWatcher(rrulist, searchEt_rru, adapterRRU));
        searchEt_hidden.addTextChangedListener(new MyTextWatcher(hiddenBsNames, searchEt_hidden, adapterHidden));
        searchEt_inspected.addTextChangedListener(new MyTextWatcher(inspectedBsNames, searchEt_inspected, adapterInspected));
    }

    private class MyTextWatcher implements TextWatcher {
        List<String> mList_old;
        List<String> mList_new;
        NewBSListAdapter adapter;
        EditText edit;

        public MyTextWatcher(List<String> mList_old, EditText edit, NewBSListAdapter adapter) {
            this.mList_old = mList_old;
            this.edit = edit;
            this.adapter = adapter;
            mList_new = new ArrayList<>();
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            mList_new.clear();
//            if (edit.getText().toString().trim() == null) {
//                adapter.setContent(mList_old);
//                return;
//            }
            // changed by wuhua for 点击列表与内容对不上
            if (edit.getText().toString().trim().equals("")) {
                adapter.setContent(mList_old);
                return;
            }
            for (String bsName : mList_old) {
                if (bsName.contains(edit.getText().toString().trim())) {
                    mList_new.add(bsName);
                }
            }
            adapter.setContent(mList_new);
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }

    }

    private void initTabhost() {
        View vc = (View) LayoutInflater.from(context).inflate(
                R.layout.item_tab_widget, null);
        TextView tv1 = (TextView) vc.findViewById(R.id.tab_label);
        tv1.setText("待巡检基站");
        tv1.setTextColor(getResources().getColor(R.color.colorOrangeLight));

        // 独立室分巡检
        View rru = (View) LayoutInflater.from(context).inflate(
                R.layout.item_tab_widget, null);
        TextView tv4 = (TextView) rru.findViewById(R.id.tab_label);
        tv4.setText("室分巡检");

        View vp = (View) LayoutInflater.from(context).inflate(
                R.layout.item_tab_widget, null);
        TextView tv2 = (TextView) vp.findViewById(R.id.tab_label);
        tv2.setText("已巡检基站");

        View vi = (View) LayoutInflater.from(context).inflate(
                R.layout.item_tab_widget, null);
        TextView tv3 = (TextView) vi.findViewById(R.id.tab_label);
        tv3.setText("隐患排查");


        tabMain.setup();
        tabMain.addTab(tabMain.newTabSpec("tab1").setIndicator(vc)
                .setContent(R.id.layout_new));
        tabMain.addTab(tabMain.newTabSpec("tab4").setIndicator(rru)
                .setContent(R.id.layout_rru));
        tabMain.addTab(tabMain.newTabSpec("tab2").setIndicator(vp)
                .setContent(R.id.layout_inspected));
        tabMain.addTab(tabMain.newTabSpec("tab3").setIndicator(vi)
                .setContent(R.id.layout_hidden));

        tabMain.setCurrentTab(0);
        TabDrawTop tabDraw = new TabDrawTop(tabMain);
        tabDraw.getTabWidget();

        tabMain.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                TabWidget tabWidget = tabMain.getTabWidget();
                for (int i = 0; i < tabWidget.getChildCount(); i++) {
                    final TextView tv = (TextView) tabWidget.getChildAt(i)
                            .findViewById(R.id.tab_label);
                    if (tabMain.getCurrentTab() == i) {
                        tv.setTextColor(getResources().getColor(
                                R.color.colorOrangeLight));
                    } else {
                        tv.setTextColor(getResources().getColor(R.color.black));
                    }
                }
            }
        });
    }

    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
        topLayout.setVisibility(View.GONE);
    }

    @Override
    public void onSoftKeyboardClosed() {
        topLayout.setVisibility(View.VISIBLE);
    }

    private boolean checkSignSuccesed(){
        SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
        lng = share.getFloat(InspectSignActivity.SHARE_SIGN_LNG, 0);
        lat = share.getFloat(InspectSignActivity.SHARE_SIGN_LAT, 0);
        if (lng == 0 || lng == 4.9E-324 || lat == 0) {
            return false;
        }
        return true;
    }
}
