package com.example.app.activity;

import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.application.GApplication;
import com.example.app.entity.S_box;
import com.example.app.util.Skip;
import com.example.pda.R;
import com.manager.classs.pad.ManagerClass;
import com.o.service.KuanxiangChuruService;

/**
 * 早送申请列表
 * @author Administrator
 */
public class ZaoSongshenqingActivity extends Activity implements
		OnClickListener, OnDateSetListener,OnCheckedChangeListener {
	//private Calendar calendar = Calendar.getInstance(Locale.CHINESE);
	private Button bt;
	private TextView lineName, netName, zaoDate;// 线路名 网点名 早送时间
	private ListView listView;
	Date d ;
	Calendar cal;
	private ManagerClass manager;
	List<S_box> list = new ArrayList<S_box>();
	// 选中的集合
	List<S_box> clist = new ArrayList<S_box>();
	ZaoSsqing zs;
	private int only = 0;//标识
	private CheckBox check;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.activity_zaosongshenqing);	
		manager = new ManagerClass();
		cal = Calendar.getInstance();
		getDate();
		initView();
	}
	
	
	
	private Handler handler = new Handler(){
		
		public void handleMessage(Message msg) {
			manager.getRuning().remove();
			switch (msg.what) {
			case 0:
				
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");	
					try {
						d = format.parse(GApplication.pcDate);
						long time = d.getTime();
						long yiyian = 1000 * 60 * 60 * 24; // 一天的毫秒
						long mingtian = time + yiyian; // 获取明天的毫秒
						d = new Date(mingtian); // 将毫秒再转成时间
						
						String[] zhi = format.format(d).split("-");
						int nian=Integer.parseInt(zhi[0]);
					    int yue=Integer.parseInt(zhi[1]);
					    int ri=Integer.parseInt(zhi[2]);
					    cal.set(nian, yue-1, ri, 0, 0, 0);		
						System.out.println("我是日期:"+format.format(d));
					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
				String riqi = format.format(d);	
				zaoDate.setText(riqi);
				bt.setBackgroundResource(R.drawable.buttom_selector_bg);
				bt.setEnabled(true);
				break;
			case 1://超时
				manager.getAbnormal().timeout(ZaoSongshenqingActivity.this,
						"获取日期超时,重试?", new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								getDate();
								return;
							}
						});
				break;
			case 2://获取失败
				manager.getAbnormal().timeout(ZaoSongshenqingActivity.this,
						"获取日期失败,重试?", new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								getDate();
								return;
							}
						});
				break;
			case 3://加载异常
				manager.getAbnormal().timeout(ZaoSongshenqingActivity.this,
						"加载日期异常,重试?", new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								getDate();
								return;
							}
						});
				break;
			}
		};
	};
	
	

	private void resizePikcer(FrameLayout tp) {
		List<NumberPicker> npList = findNumberPicker(tp);
		for (NumberPicker np : npList) {
			resizeNumberPicker(np);
		}
	}

	private void resizeNumberPicker(NumberPicker np) {
		//LayoutParams.WRAP_CONTENT
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, LayoutParams.WRAP_CONTENT);
		params.setMargins(10, 0, 10, 0);
		np.setLayoutParams(params);
	}

	private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
		List<NumberPicker> npList = new ArrayList<NumberPicker>();
		View child = null;
		if (null != viewGroup) {
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				child = viewGroup.getChildAt(i);
				if (child instanceof NumberPicker) {
					npList.add((NumberPicker) child);
				} else if (child instanceof LinearLayout) {
					List<NumberPicker> result = findNumberPicker((ViewGroup) child);
					if (result.size() > 0) {
						return result;
					}
				}
			}
		}
		return npList;
	}
	
	
	/**
	 * 初始化控件
	 */
	public void initView() {
		if(GApplication.slist!=null){
			list = GApplication.slist;
		}
		zs = new ZaoSsqing(this);
		check = (CheckBox) this.findViewById(R.id.zaosong_checkeDbox);
		bt = (Button) findViewById(R.id.zaochushenqing_button2);
		listView = (ListView) this.findViewById(R.id.zaosqlistView);
		lineName = (TextView) this.findViewById(R.id.z_lineName);
		netName = (TextView) this.findViewById(R.id.z_netName);
		zaoDate = (TextView) this.findViewById(R.id.z_date);
		if (GApplication.sk != null) {
			netName.setText(GApplication.sk.getNetName());
		}
		if(GApplication.kxc!=null){
			lineName.setText(GApplication.kxc.getChaochexianlu());
		}
			
		bt.setOnClickListener(this);
		check.setOnCheckedChangeListener(this);
		findViewById(R.id.btnTime).setOnClickListener(this);
	//	findViewById(R.id.zcsqBack).setOnClickListener(this);
	}

	/**
	 * 滚动事件
	 * @param lv
	 */
	public void turngaodu(ListView lv) {
		// 获取listView对应的Adapter
		ListAdapter adapter = lv.getAdapter();
		// 返回数据项的数目
		int hangshu = adapter.getCount();
		int totalhanggao = 0;
		for (int i = 0; i < hangshu; i++) {
			View v = adapter.getView(i, null, lv);
			/*
			 * 在没有构建View之前无法取得View的宽度。选定measure 调用getMeasuredHeight()的方法
			 */
			v.measure(View.MeasureSpec.UNSPECIFIED,
					View.MeasureSpec.UNSPECIFIED);
			totalhanggao += v.getMeasuredHeight();
		}
		double fenggefu = lv.getDividerHeight() * (hangshu - 1);// 得到分隔符的高度
		totalhanggao += fenggefu;// 得到整个高度
		LayoutParams lp = lv.getLayoutParams();
		lp.height = totalhanggao;// 设置总高度
		lv.setLayoutParams(lp);
	}
	
	
	
	
	/**
	 * 获取任务日期
	 */
	public void getDate(){
		manager.getRuning().runding(ZaoSongshenqingActivity.this, "获取日期中...");
		new Thread() {
			@Override
			public void run() {
				super.run();
				KuanxiangChuruService ks = new KuanxiangChuruService();
				try {//日期系统接口
					 
					GApplication.pcDate=ks.getSysTime();
				//	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");		
				//	GApplication.pcDate = sdf.format(sdf.parse(date));
				//	System.out.println("Date :"+GApplication.pcDate);
					if(null!=GApplication.pcDate){
						handler.sendEmptyMessage(0);
					}
		//			System.out.println("GApplication.pcDate  OK:"+GApplication.pcDate);
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(1);
				} catch (NullPointerException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(2);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(3);
				}
			}

		}.start();
	}
	
	
	

	/**
	 * 自定义样式
	 * @author Administrator
	 */
	class ZaoSsqing extends BaseAdapter {

		// 记录checkbox的状态
		private HashMap<Integer, Boolean> isSelected;
		
		// 构造器
		public ZaoSsqing(Context context) {
			isSelected = new HashMap<Integer, Boolean>();
			// 初始化数据
			initDate();
		}

		// 初始化isSelected的数据
		private void initDate() {
			for (int i = 0; i < list.size(); i++) {
				getIsSelected().put(i, true);
			}
		}

		public HashMap<Integer, Boolean> getIsSelected() {
			return isSelected;
		}

		public void setIsSelected(HashMap<Integer, Boolean> isSelected) {
			this.isSelected = isSelected;
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int arg0, View arg1, ViewGroup arg2) {
			final KongjianEntity entity;
			if (arg1 == null) {
				entity = new KongjianEntity();
				arg1 = LayoutInflater.from(ZaoSongshenqingActivity.this)
						.inflate(R.layout.item_zaosongshenqing, null);
				entity.textView1 = (TextView) arg1.findViewById(R.id.s_boxId);
				entity.textView2 = (TextView) arg1.findViewById(R.id.s_state);
				entity.checkBox = (CheckBox) arg1.findViewById(R.id.isZaosong);
				entity.editText = (EditText) arg1.findViewById(R.id.suokou_edit);
				arg1.setTag(entity);
			} else {
				entity = (KongjianEntity) arg1.getTag();
			}
			entity.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					only =1;
					if (isChecked) {
						isSelected.put(arg0, true);			
						clist.add(list.get(arg0));
						if(isSelected.containsValue(false)){
							check.setChecked(false);
						}else{
							check.setChecked(true);
							only = 0;
						}
					} else {
						isSelected.put(arg0, false);				
						check.setChecked(false);
						clist.remove(list.get(arg0));
					}
				}
			});
			
			entity.editText.addTextChangedListener(new TextWatcher() {
				public void onTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {

				}

				public void beforeTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {
				}
				@Override
				public void afterTextChanged(Editable s) {
					list.get(arg0).setEdit(s.toString());
				}
			});
			System.out.println("entity.checkBox --:"+entity.checkBox);
			entity.checkBox.setChecked(getIsSelected().get(arg0));
			entity.textView1.setText(list.get(arg0).getBoxId());
			entity.textView2.setText(list.get(arg0).getState());
		//	System.out.println("+++==++"+list.get(arg0).getState());
			if(list.get(arg0).getState().equals("在用")){
				entity.editText.setEnabled(true);
			}else{
				entity.editText.setEnabled(false);
			}
			
			entity.editText.setText(list.get(arg0).getEdit());
				
			return arg1;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if (clist.size() > 0 && clist != null) {
			clist.clear();
		}
		listView.setAdapter(zs);
		turngaodu(listView);

	}

	/*
	 * listView 控件实体类
	 */
	private class KongjianEntity {
		public TextView textView1;
		public TextView textView2;
		public CheckBox checkBox;
		public EditText editText;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.zaochushenqing_button2:// 跳转晚收款箱交接
			if(clist.size()>0){
				clist.clear();
			}
			if(GApplication.zaolist.size()>0){
				GApplication.zaolist.clear();
			}
			if(GApplication.zssqlist.size()>0){
				GApplication.zssqlist.clear();
			}
			if(GApplication.zyzaolist.size()>0){
				GApplication.zyzaolist.clear();
			}
			/**
			 * 后面提交的箱子
			 */
			for (int i = 0; i < list.size(); i++) {
				if(list.get(i).getState().equals("在用")){
					GApplication.zyzaolist.add(list.get(i).getBoxId());
					//存放捆签
					GApplication.zssqlist.add(list.get(i).getEdit());
				}
			}
			/**
			 * 明天早送的箱子
			 */
			for (int i = 0; i < list.size(); i++) {
				if(zs.isSelected.get(i)==true){
					//存放款箱
					GApplication.zaolist.add(list.get(i).getBoxId());//指勾中状态
					
				}
			}
			
			/*if (GApplication.zssqlist != null) {
				//GApplication.zslist = clist;
				for (int i = 0; i < GApplication.zssqlist.size(); i++) {
					System.out.println("GApplication.zssqlist:"+GApplication.zssqlist.get(i).getEdit());
					System.out.println("GApplication.zssqlist:"+GApplication.zssqlist.get(i).getBoxId());
				}
			}*/
			manager.getRuning().runding(ZaoSongshenqingActivity.this,"数据获取中...");
			GApplication.curDate=zaoDate.getText().toString();
			Skip.skip(ZaoSongshenqingActivity.this, WanShouXiangActivity.class,
					null, 0);
			break;
		/*case R.id.zcsqBack:
			ZaoSongshenqingActivity.this.finish();
			break;*/
		case R.id.btnTime:		
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.timedialog);
		    final DatePicker date=(DatePicker)dialog.findViewById(R.id.datePicker1);
		    long time = cal.getTimeInMillis();
//			new Date().getTime();
		    date.setCalendarViewShown(false);
		    date.setMinDate(time);
//		    Date d = new Date();
//			long dangqian = d.getTime(); // 获取当前毫秒
//			long yiyian = 1000 * 60 * 60 * 24; // 一天的毫秒
//			long mingtian = dangqian + yiyian; // 获取明天的毫秒
//			d = new Date(mingtian); // 将毫秒再转成时间

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String riqi = format.format(d);
			String[] zhi = riqi.split("-");
		   
		    System.out.println("riqi:"+riqi);
		    int nian=Integer.parseInt(zhi[0]);
		    int yue=Integer.parseInt(zhi[1]);
		    int ri=Integer.parseInt(zhi[2]);
		    // 方法参数月份下标从0开始  所以月份要减1  
		    date.updateDate(nian,yue-1,ri);
		    System.out.println("修改了486行");
		//    resizePikcer(date);
	        Button timeBt = (Button) dialog.findViewById(R.id.button12121);
	        Button quXBt = (Button) dialog.findViewById(R.id.btn12121);
	        timeBt.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View arg0) {				
					int nian=date.getYear();
					int yue=date.getMonth()+1;
					int ri=date.getDayOfMonth();
					zaoDate.setText(nian+"-"+yue+"-"+ri);
					dialog.dismiss();
				}
			});
	        quXBt.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View arg0) {				
					dialog.dismiss();
				}
			});       
			dialog.show();
			break;
		}
	}
	
	@Override
	public void onDateSet(DatePicker dialog, int year, int month, int day) {
		/*//calendar.set(year, month, day);
		String date = year + "-" + (month + 1) + "-" + day;
		zaoDate.setText(date.toString());*/
	}

	@Override
	protected void onStop() {
		super.onStop();
		manager.getRuning().remove();
	}

	
	/**
	 * checkBox 全选全不选控制事件
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(only==0){
			if(isChecked){
				for (int i = 0; i < list.size(); i++) {
					zs.isSelected.put(i, true);
				}
				listView.setAdapter(zs);
			//	zs.notifyDataSetChanged();
			//	turngaodu(listView);
			}else{
				for (int i = 0; i < list.size(); i++) {
					zs.isSelected.put(i, false);
				}
				listView.setAdapter(zs);
			//	zs.notifyDataSetChanged();
			//	turngaodu(listView);
			}
		}		
	}

}
