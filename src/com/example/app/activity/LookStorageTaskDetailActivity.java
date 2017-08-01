package com.example.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.application.GApplication;
import com.entity.LookStorageDetail;
import com.example.pda.R;
import com.imple.getnumber.LookStorageScan;
import com.manager.classs.pad.ManagerClass;
import com.o.service.LookStorageService;

/**
 * 查库服务任务单明细操作界面
 * 
 * @author zhouKai
 *
 */
@SuppressLint("HandlerLeak")
public class LookStorageTaskDetailActivity extends Activity implements OnClickListener {

	public static final int SCAN_BOX = 0; // 扫描箱子
	public static final int LOAD_TASK_DETAIL_SUCCESS = 1; // 加载查库服务任务明细 成功
	public static final int LOAD_TASK_DETAIL_FAILURE = 2; // 加载查库服务任务明细 失败

	private TextView tv_corp_name; // 机构名
	private TextView tv_name_one;  // 查库员一姓名
	private TextView tv_name_two;  // 查库员二姓名
	private TextView tv_task_id; // 查库任务单号

	private TextView tv_untreat_box_num; // 未清点数量
	private TextView tv_completed_box_num; // 已清点数量
	private ListView lv_task_detail; // 任务明细列表
	private SimpleAdapter adapter; // 任务明细列表数据适配器

	private TextView tv_task_detail; // 清点任务
	private TextView tv_task_process; // 清点进度

	private LinearLayout ll_task_detail; // 任务明细区域
	private LinearLayout ll_task_process; // 任务清点进度区域
	private LinearLayout ll_task_loading; // 正在加载提示框
	// 清点结果统计
	private TextView tv_atm_total; // ATM箱子总数
	private TextView tv_atm_completed; // 已清点的ATM箱子个数
	private TextView tv_atm_untreated; // 未清点的ATM箱子个数
	private TextView tv_zz_total; // 周转箱总数
	private TextView tv_zz_completed; // 已清点的周转箱个数
	private TextView tv_zz_untreated; // 未清点的周转箱个数
	private TextView tv_kx_total; // 款箱总数
	private TextView tv_kx_completed; // 已清点的款箱个数
	private TextView tv_kx_untreated; // 未清点的款箱个数

	private String taskId; // 查库任务单id
	private static ManagerClass managerClass = new ManagerClass();

	// 已排序的任务单号对应的明细， 已清点的在前，未清点的在后
	List<Map<String, Object>> orderedModel;
	//用于显示的已排序的任务单明细
	List<Map<String, Object>> orderedView;
	
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LookStorageTaskDetailActivity.LOAD_TASK_DETAIL_SUCCESS:
				ll_task_loading.setVisibility(View.GONE);
				updateScanResult();
				lv_task_detail.setAdapter(adapter);
				break;
			case LookStorageTaskDetailActivity.LOAD_TASK_DETAIL_FAILURE:
				ll_task_loading.setVisibility(View.GONE);
				Toast.makeText(LookStorageTaskDetailActivity.this, "网络异常", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_look_storage_task_detail);
		tv_corp_name = (TextView) findViewById(R.id.tv_corp_name);
		tv_name_one = (TextView) findViewById(R.id.tv_name_one);
		tv_name_two = (TextView) findViewById(R.id.tv_name_two);

		tv_task_id = (TextView) findViewById(R.id.tv_task_id);
		tv_untreat_box_num = (TextView) findViewById(R.id.tv_untreat_box_num);
		tv_completed_box_num = (TextView) findViewById(R.id.tv_completed_box_num);
		lv_task_detail = (ListView) findViewById(R.id.lv_task_detail);

		tv_task_detail = (TextView) findViewById(R.id.tv_task_detail);
		tv_task_process = (TextView) findViewById(R.id.tv_task_process);
		ll_task_detail = (LinearLayout) findViewById(R.id.ll_task_detail);
		ll_task_process = (LinearLayout) findViewById(R.id.ll_task_process);
		ll_task_loading = (LinearLayout) findViewById(R.id.ll_task_loading);

		tv_atm_total = (TextView) findViewById(R.id.tv_atm_total);
		tv_atm_completed = (TextView) findViewById(R.id.tv_atm_completed);
		tv_atm_untreated = (TextView) findViewById(R.id.tv_atm_untreated);
		tv_zz_total = (TextView) findViewById(R.id.tv_zz_total);
		tv_zz_completed = (TextView) findViewById(R.id.tv_zz_completed);
		tv_zz_untreated = (TextView) findViewById(R.id.tv_zz_untreated);
		tv_kx_total = (TextView) findViewById(R.id.tv_kx_total);
		tv_kx_completed = (TextView) findViewById(R.id.tv_kx_completed);
		tv_kx_untreated = (TextView) findViewById(R.id.tv_kx_untreated);

		this.ll_task_detail.setVisibility(View.VISIBLE);
		this.ll_task_process.setVisibility(View.GONE);

		tv_task_detail.setOnClickListener(this);
		tv_task_process.setOnClickListener(this);

		Intent intent = getIntent();
		this.tv_corp_name.setText(intent.getStringExtra("corpName"));
		this.tv_name_one.setText(intent.getStringExtra("nameOne"));
		this.tv_name_two.setText(intent.getStringExtra("nameTwo"));
		this.taskId = intent.getStringExtra("taskId");
		this.tv_task_id.setText(taskId);

		this.orderedModel = new ArrayList<Map<String, Object>>();
		this.orderedView = new ArrayList<Map<String, Object>>();
		adapter = new SimpleAdapter(this, 
									this.orderedView, 
									R.layout.storage_detail_item, 
									new String[] {"boxType", "boxNum", "boxState" }, 
									new int[] { R.id.tv_box_type, R.id.tv_box_num,
												R.id.tv_box_state });
		Toast toast = new Toast(getApplicationContext());
		toast.setDuration(Toast.LENGTH_SHORT);
		TextView textView = new TextView(getApplicationContext());
		textView.setTextSize(50);
		if (!GApplication.taskDetails.containsKey(this.taskId)) {
			textView.setText("查询");
			toast.setView(textView);
			toast.show();
			this.loadTaskDetail(taskId);
		} else {
			textView.setText("不查询");
			toast.setView(textView);
			toast.show();
			this.initOrderedModel(GApplication.taskDetails.get(this.taskId));
			this.updateScanResult();
			this.sortTaskDetail();
			this.modelToView(orderedModel);
			this.lv_task_detail.setAdapter(adapter);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 开启扫描
		managerClass.getRfid().addNotifly(new LookStorageScan());
		managerClass.getRfid().open_a20();
		if (LookStorageScan.handler == null) {
			LookStorageScan.handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					switch (msg.what) {
					case LookStorageTaskDetailActivity.SCAN_BOX:
						String boxNum = msg.obj.toString();
						// 遍历taskDetails，把扫描到的boxNum的清点状态修改为“已清点”
						List<LookStorageDetail> list = GApplication.taskDetails.get(taskId);
						if (list != null && list.size() > 0) {
							for (LookStorageDetail lsd : list) {
								if (lsd.getBoxNum().equals(boxNum)) {
									lsd.setState(LookStorageDetail.SUCCESS);
								}
							}
						}
						// 遍历orderedModel，把扫描到的boxNum的清点状态修改为“已清点”
						if (orderedModel.size() > 0) {
							for (Map<String, Object> map : orderedModel) {
								if (map.get("boxNum").equals(boxNum)) {
									map.put("boxState", LookStorageDetail.SUCCESS);
									break;
								}
							}
						}
						//更新未清点数量和已清点数量
						updateScanResult();
						//对orderedModel进行排序，未清点在前，已清点在后
						sortTaskDetail();
						//把model转化为view用于显示
						modelToView(orderedModel);
						//更新列表
						adapter.notifyDataSetChanged();
						break;
					default:
						break;
					}
				}
			};
		}
	}

	/**
	 * 根据任务单号加载其对应的明细
	 * @param taskId
	 */
	private void loadTaskDetail(final String taskId) {
		ll_task_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				Message msg = handler.obtainMessage();
				try {
					List<LookStorageDetail> detail = LookStorageService.getTaskDetail(taskId);
					GApplication.taskDetails.put(taskId, detail);
					// 把初始化明细数据添加到orderedModel
					initOrderedModel(detail);
					//把加载到的数据转化为可显示的view
					modelToView(orderedModel);
					msg.what = LookStorageTaskDetailActivity.LOAD_TASK_DETAIL_SUCCESS;
				} catch (Exception e) {
					msg.what = LookStorageTaskDetailActivity.LOAD_TASK_DETAIL_FAILURE;
					e.printStackTrace();
				} finally {
					handler.sendMessage(msg);
				}
			};
		}.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_task_detail:
			tv_task_detail.setBackgroundResource(R.color.gray_msg_bg);
			tv_task_process.setBackgroundResource(R.color.blue_title);
			this.ll_task_detail.setVisibility(View.VISIBLE);
			this.ll_task_process.setVisibility(View.GONE);
			managerClass.getRfid().addNotifly(new LookStorageScan());
			managerClass.getRfid().open_a20();
			break;
		case R.id.tv_task_process:
			tv_task_process.setBackgroundResource(R.color.gray_msg_bg);
			tv_task_detail.setBackgroundResource(R.color.blue_title);
			this.ll_task_detail.setVisibility(View.GONE);
			this.ll_task_process.setVisibility(View.VISIBLE);
			this.updateTaskProgress();
			managerClass.getRfid().close_a20();
			break;
		default:
			break;
		}
	}

	/**
	 * 返回按钮
	 * @param v
	 */
	public void back(View v) {
		this.finish();
	}

	/**
	 * 更新已完成和未处理的箱子个数
	 * @param boxType
	 */
	private void updateScanResult() {
		int completedCnt = 0; // 箱子已清点的个数
		List<LookStorageDetail> list = GApplication.taskDetails.get(this.taskId);
		if (list != null && list.size() > 0) {
			for (LookStorageDetail lsd : list) {
				if (lsd.getState().equals(LookStorageDetail.SUCCESS)) {
					completedCnt++;
				}
				this.tv_untreat_box_num.setText("" + (list.size() - completedCnt));
			}
		}
		this.tv_completed_box_num.setText("" + completedCnt);
	}

	/**
	 * 更新orderedModel中数据的顺序，未清点的在前，已清点的在后
	 */
	private void sortTaskDetail() {
		List<Map<String, Object>> completed = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> untreat = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : this.orderedModel) {
			if (map.get("boxState").equals(LookStorageDetail.SUCCESS)) {
				completed.add(map);
			} else {
				untreat.add(map);
			}
		}
		this.orderedModel.clear();
		this.orderedModel.addAll(untreat);
		this.orderedModel.addAll(completed);
	}

	/**
	 * 更新清点进度表格
	 */
	private void updateTaskProgress() {
		int atmTotal = 0; // 任务单中ATM钞箱的个数 01
		int kxTotal = 0; // 任务单中款箱的个数 02
		int zzTotal = 0; // 任务单中周转箱的个数 03
		int atmCompletedCnt = 0;
		int kxCompletedCnt = 0;
		int zzCompletedCnt = 0;

		for (LookStorageDetail de : GApplication.taskDetails.get(this.taskId)) {
			if (de.getBoxType().equals(LookStorageDetail.CX)) {
				atmTotal++;
				if (de.getState().equals(LookStorageDetail.SUCCESS)) {
					atmCompletedCnt++;
				}
			} else if (de.getBoxType().equals(LookStorageDetail.KX)) {
				kxTotal++;
				if (de.getState().equals(LookStorageDetail.SUCCESS)) {
					kxCompletedCnt++;
				}
			} else if (de.getBoxType().equals(LookStorageDetail.ZZX)) {
				zzTotal++;
				if (de.getState().equals(LookStorageDetail.SUCCESS)) { 
					zzCompletedCnt++;
				}
			}
		}

		this.tv_atm_total.setText(atmTotal + "");
		this.tv_atm_completed.setText(atmCompletedCnt + "");
		this.tv_atm_untreated.setText((atmTotal - atmCompletedCnt) + "");
		this.tv_kx_total.setText(kxTotal + "");
		this.tv_kx_completed.setText(kxCompletedCnt + "");
		this.tv_kx_untreated.setText((kxTotal - kxCompletedCnt) + "");
		this.tv_zz_total.setText(zzTotal + "");
		this.tv_zz_completed.setText(zzCompletedCnt + "");
		this.tv_zz_untreated.setText((zzTotal - zzCompletedCnt) + "");
	}

	/**
	 * 提交清点完成的任务
	 */
	public void commitTask(View v) {
		new Thread(){
			public void run() {
				try {
					LookStorageService.commitTask(GApplication.taskDetails.get(LookStorageTaskDetailActivity.this.taskId));
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(LookStorageTaskDetailActivity.this, "数据已经提交", Toast.LENGTH_LONG).show();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(LookStorageTaskDetailActivity.this, "数据提交失败", Toast.LENGTH_LONG).show();
						}
					});
				}				
			}
		}.start();
	}

	/**
	 * 把初始化明细数据添加到orderedModel
	 * 
	 * @param detail
	 */
	private void initOrderedModel(List<LookStorageDetail> detail) {
		for (LookStorageDetail de : detail) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("boxType", de.getBoxType());
			map.put("boxNum", de.getBoxNum());
			map.put("boxState", de.getState());
			orderedModel.add(map);
		}
	}

	/**
	 * 使orderedView从orderedModel中加载数据
	 * 把箱子类型、清点状态有标识码转化为对应的汉字名称
	 * @param orderedModel
	 */
	private void modelToView(List<Map<String, Object>> model){
		this.orderedView.clear();
		for (Map<String, Object> mapM : model) {
			Map<String, Object> mapV = new HashMap<String, Object>();
			mapV.put("boxType", this.parseBoxType((String) mapM.get("boxType")));
			mapV.put("boxNum", mapM.get("boxNum"));
			mapV.put("boxState", mapM.get("boxState").equals(LookStorageDetail.FAILURE) ? "未清点" : "已清点");
			this.orderedView.add(mapV);
		}
		System.out.println(this.orderedView);
	}

	/**
	 * 把箱子的编码解析为汉字
	 * @param boxType
	 * @return
	 */
	private String parseBoxType(String boxType) {
		return boxType.equals(LookStorageDetail.CX) ? "钞箱" : 
				boxType.equals(LookStorageDetail.KX) ? "款箱" : 
				boxType.equals(LookStorageDetail.ZZX) ? "周转箱" : "类别有误";
	}

	@Override
	protected void onStop() {
		super.onStop();
		//关闭扫描
		managerClass.getRfid().close_a20();
		this.orderedModel.clear();
		this.orderedView.clear();
	}


}
