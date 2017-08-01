package com.example.app.activity;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.GApplication;
import com.entity.LookStorage;
import com.entity.LookStorageDetail;
import com.example.pda.R;
import com.o.service.LookStorageService;

/**
 * 查库服务任务列表操作界面
 * @author zhouKai
 *
 */
public class LookStorageTaskListActivity extends Activity {
	
	public static final int LOAD_TASK_SUCCESS = 0; // 加载任务列表

	public static final int NET_EXCEPTION = 1; // 网络异常
	
	private TextView tv_name_one; // 查库员一
	private TextView tv_name_two; // 查库员二
	private TextView tv_none_task; // 提示文字“没有任务”
	private ListView lv_task_list; // 查库任务列表
	private LinearLayout ll_task_loading; // 任务加载中提示框

	private List<LookStorage> tasks;			//任务单号数据
	private LookStorageAdapter adapter; // 查库任务列表适配器
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				// 加载任务列表
			case LOAD_TASK_SUCCESS:
				if (tasks == null || tasks.size() == 0) {
					ll_task_loading.setVisibility(View.GONE);
					tv_none_task.setVisibility(View.VISIBLE);
				} else {
					ll_task_loading.setVisibility(View.GONE);
					lv_task_list.setAdapter(adapter);
				}
				break;
			case NET_EXCEPTION:
					ll_task_loading.setVisibility(View.GONE);
					tv_none_task.setVisibility(View.VISIBLE);
					Toast.makeText(LookStorageTaskListActivity.this, "网络异常", Toast.LENGTH_LONG).show();;
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_look_storage_task_list);
		tv_name_one = (TextView) findViewById(R.id.tv_name_one);
		tv_name_two = (TextView) findViewById(R.id.tv_name_two);
		tv_none_task = (TextView) findViewById(R.id.tv_none_task);
		lv_task_list = (ListView) findViewById(R.id.lv_task_list);
		ll_task_loading = (LinearLayout) findViewById(R.id.ll_task_loading);
		
		this.adapter = new LookStorageAdapter();
		ll_task_loading.setVisibility(View.VISIBLE);
		
		Bundle bundle = this.getIntent().getExtras();
		tv_name_one.setText("查库员一：" + bundle.getString("nameOne"));
		tv_name_two.setText("查库员二：" + bundle.getString("nameTwo"));		
		this.loadTask(bundle.getString("codeOne"), bundle.getString("codeTwo"));
		
		this.lv_task_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(LookStorageTaskListActivity.this, LookStorageTaskDetailActivity.class);
				LookStorage lookStorage = tasks.get(position);
				intent.putExtra("taskId", lookStorage.getId());
				intent.putExtra("nameOne", tv_name_one.getText().toString());
				intent.putExtra("nameTwo", tv_name_two.getText().toString());
				intent.putExtra("corpName", lookStorage.getLookedCorpId());
				startActivity(intent);
			}
		});
		GApplication.taskDetails = new HashMap<String, List<LookStorageDetail>>();
	}
	
	private class LookStorageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return tasks.size();
		}

		@Override
		public Object getItem(int position) {
			return tasks.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(LookStorageTaskListActivity.this, R.layout.storage_item, null);
			}
			LookStorage lookStorage = tasks.get(position);
			TextView value = (TextView) convertView.findViewById(R.id.tv_storage_num);
			value.setText(lookStorage.getId());
			return convertView;
		}
		
	}
	
	/**
	 * 刷新按钮
	 * @param v
	 */
	public void refresh(View v){
		Bundle bundle = this.getIntent().getExtras();
		this.loadTask(bundle.getString("codeOne"), bundle.getString("codeTwo"));
		this.adapter.notifyDataSetChanged();
	}
	
	/**
	 * 返回按钮
	 * @param v
	 */
	public void back(View v){
		this.finish();		
	}
	
	/**
	 * 从服务端获取任务列表
	 * @param codeOne 查库员一的userId
	 * @param codeTwo 查库员二的userId
	 */
	private void loadTask(final String codeOne, final String codeTwo){
		this.ll_task_loading.setVisibility(View.VISIBLE);
		new Thread(){
			public void run() {
				Message message = handler.obtainMessage();
				try {
					tasks = LookStorageService.getTask(codeOne, codeTwo);
					message.what = LookStorageTaskListActivity.LOAD_TASK_SUCCESS;
					message.obj = tasks;
				} catch (Exception e) {
					message.what = LookStorageTaskListActivity.NET_EXCEPTION;
					e.printStackTrace();
				} finally {
					handler.sendMessage(message);
				}
			};
		}.start();
	}

	@Override
	public void finish() {
		this.tasks = null;
		super.finish();
	}
	
	@Override
	protected void onDestroy() {
		this.tasks = null;
		GApplication.taskDetails = null;
		super.onDestroy();
	}
	
	
	
	
	

}
