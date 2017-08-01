package com.ljsw.tjbankpda.main;

import hdjc.rfid.operator.RFID_Device;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.GApplication;
import com.example.pda.R;
import com.ljsw.tjbankpda.qf.entity.QuanbieXinxi;
import com.ljsw.tjbankpda.qf.entity.TianJiaXianJin;
import com.ljsw.tjbankpda.qf.entity.TianJiaZhongKong;
import com.ljsw.tjbankpda.qf.entity.ZhongkongXinxi;
import com.ljsw.tjbankpda.qf.service.QingfenRenwuService;
import com.ljsw.tjbankpda.util.BianyiType;
import com.ljsw.tjbankpda.util.BiaohaoJiequ;
import com.ljsw.tjbankpda.util.MySpinner;
import com.ljsw.tjbankpda.util.NumFormat;
import com.ljsw.tjbankpda.util.ShangjiaoDizhiSaomiaoUtil;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.StringGetNum;
import com.ljsw.tjbankpda.util.Table;
import com.ljsw.tjbankpda.util.TurnListviewHeight;
import com.manager.classs.pad.ManagerClass;

/**
 * 清分员清分activity
 * 
 * @author yuyunheng
 * 
 */
public class ShangJiaoQingFen_o_qf extends FragmentActivity implements
		OnClickListener {
	private LinearLayout spinner_layout_juanbie, spinner_layout_zhuangtai,
			spinner_layout_pingzheng;
	private TextView spinner_text_juanbie, spinner_text_zhuangtai,
			xianjin_heji, zhongkong_yiqingdian, zhongkong_zhonglei, dizhi_heji,
			peisongdan;
	private MySpinner spinner;
	private String[] str_zhuangtai;
	private String juanbie, zhuangtai, pingzheng;// 接收spinner所选信息
	private Button tianjia_xianjin, tianjia_zhongkong, tianjia_dizhi,
			openSaomiao, qingdfen_ok;
	private EditText xianjin_count, pingzhengbianhao, pingzheng_haoduan,
			dizhi_bianhao;
	private ListView xianjin_listview, pingzheng_listview, dizhi_listview;// 现金,重空,抵质的ListView
	private ImageView back;
	private String orderNum;

	private List<QuanbieXinxi> quanbieList = new ArrayList<QuanbieXinxi>(); // 创建券别信息集合
	private List<ZhongkongXinxi> zhongkongList = new ArrayList<ZhongkongXinxi>(); // 创建重空信息集合

	String xianjingMsg;// 现金msg
	String zhongkongMsg;// 重空凭证msg
	String dizhiMsg;// 抵质押品msg
	String zhongkongSubmit; // 重空提交msg

	private StringGetNum getnum = new StringGetNum(); // 截取数字工具类
	private BiaohaoJiequ jiequ = new BiaohaoJiequ(); // 截取编号工具类

	boolean isCanOpenSaomiao = true;

	XianJinAdapter adapter;
	ZhongKongAdapter zkadapter;
	DiZhiAdapter dzadapter;

	private String peisongId;

	private int isok = -1;
	private int zkok = -1;
	private double heji_xianjin = 0;
	private int dzok = -1;
	private int heji_dizhi;
	private ShangjiaoDizhiSaomiaoUtil dizhiSaomiaoUtil = new ShangjiaoDizhiSaomiaoUtil();
	private RFID_Device rfid;

	List<TianJiaXianJin> xianjinlist = new ArrayList<TianJiaXianJin>();
	List<TianJiaZhongKong> zhongkonglist = new ArrayList<TianJiaZhongKong>(); // 券别信息集合
	List<String> dizhilist = new ArrayList<String>();
	private Table[] shangjiaoMark; // 对比信息
	private Table[] shangjiaoRenwu; // 上缴登记信息
	private Table[] subimtMark; // 提交信息

	private RFID_Device getRfid() {
		if (rfid == null) {
			rfid = new RFID_Device();
		}
		return rfid;
	}

	private ManagerClass manager;// 弹出框

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shangjiaoqingfen_qf);
		peisongId = super.getIntent().getExtras().getString("peisongId");
		manager = new ManagerClass();
		manager.getResultmsg().setHandler(submitHandler);
		manager.getResultmsg().setHandler2Button(isSubmitHandler);
		dizhiSaomiaoUtil.setHand(handler);

		adapter = new XianJinAdapter();
		zkadapter = new ZhongKongAdapter();
		dzadapter = new DiZhiAdapter();
		load();
		orderNum = super.getIntent().getExtras().getString("orderNum");
		bangdingList();
		getDate(); // 获取券别等级信息

	}

	/*
	 * listView 绑定
	 */
	private void bangdingList() {
		zkadapter = new ZhongKongAdapter();
		pingzheng_listview.setAdapter(zkadapter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getRfid().scanclose();
		manager.getRuning().remove();
	}

	public void load() {
		peisongdan = (TextView) findViewById(R.id.zhouzhuan_hedui_left_text);
		spinner_layout_juanbie = (LinearLayout) findViewById(R.id.shangjiaoqingfen_spinner_layout);
		spinner_layout_juanbie.setOnClickListener(this);
		spinner_text_juanbie = (TextView) findViewById(R.id.shangjiaoqingfen_spinner_text);
		spinner_text_zhuangtai = (TextView) findViewById(R.id.shangjiaoqingfen_spinner_text_zhuangtai);
		spinner_layout_zhuangtai = (LinearLayout) findViewById(R.id.shangjiaoqingfen_spinner_layout_zhuangtai);
		spinner_layout_zhuangtai.setOnClickListener(this);
		tianjia_xianjin = (Button) findViewById(R.id.shangjiaoqingfen_xianjin_tianjia);
		tianjia_xianjin.setOnClickListener(this);
		xianjin_count = (EditText) findViewById(R.id.shangjiaoqingfen_edit);
		xianjin_listview = (ListView) findViewById(R.id.qf_shangjiaoqingfen_xianjin_listView1);
		back = (ImageView) findViewById(R.id.shangjiaoqingfen_back);
		back.setOnClickListener(this);
		xianjin_heji = (TextView) findViewById(R.id.shangjiaoqingfen_heji);
		// 重空清点数量
		zhongkong_yiqingdian = (TextView) findViewById(R.id.shangjiaoqingfen_zhongkong_yiqingdian);
		pingzheng_listview = (ListView) findViewById(R.id.qf_shangjiaoqingfen_zhongkong_listView1);
		pingzheng_listview.setAdapter(adapter);
		pingzhengbianhao = (EditText) findViewById(R.id.shangjiaoqingfen_zhongkong_edit);
		// 重空种类spinner显示
		zhongkong_zhonglei = (TextView) findViewById(R.id.shangjiaoqingfen_zhongkong_spinner_text);
		spinner_layout_pingzheng = (LinearLayout) findViewById(R.id.shangjiaoqingfen_spinner_zhongkong_layout);
		spinner_layout_pingzheng.setOnClickListener(this);
		pingzheng_haoduan = (EditText) findViewById(R.id.shangjiaoqingfen_haoduan_edit);
		tianjia_zhongkong = (Button) findViewById(R.id.shangjiaoqingfen_zhongkong_tianjia);
		tianjia_zhongkong.setOnClickListener(this);
		xianjin_heji.setText("");
		peisongdan.setText(peisongId);
		// 抵质押品
		dizhi_heji = (TextView) findViewById(R.id.shangjiaoqingfen_dizhiyapin_text);
		tianjia_dizhi = (Button) findViewById(R.id.shangjiaoqingfen_dizhiyapin_tianjia);
		tianjia_dizhi.setOnClickListener(this);
		openSaomiao = (Button) findViewById(R.id.shangjiaoqingfen_dizhiyapin_tianjia_saomiao);
		openSaomiao.setOnClickListener(this);
		dizhi_bianhao = (EditText) findViewById(R.id.shangjiaoqingfen_dizhiyapin_edit);
		dizhi_listview = (ListView) findViewById(R.id.qf_shangjiaoqingfen_dizhiyapin_listView1);

		// 完成清分功能
		qingdfen_ok = (Button) findViewById(R.id.shangjiaoqingfen_qingdianwancheng);
		qingdfen_ok.setOnClickListener(this);

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		// 选择劵别
		case R.id.shangjiaoqingfen_spinner_layout:
			spinner = new MySpinner(this, spinner_layout_juanbie,
					spinner_text_juanbie);
			spinner.setSpinnerHeight(spinner_layout_juanbie.getHeight() * 2);
			spinner.setListXJ(this, quanbieList);
			spinner.showPopupWindow(spinner_layout_juanbie);
			spinner.setListXJ(this, quanbieList, 40);

			break;
		case R.id.shangjiaoqingfen_spinner_layout_zhuangtai:
			spinner = new MySpinner(this, spinner_layout_zhuangtai,
					spinner_text_zhuangtai);
			spinner.setSpinnerHeight(spinner_layout_zhuangtai.getHeight() * 2);
			spinner.setList(this, str_zhuangtai);
			spinner.showPopupWindow(spinner_layout_zhuangtai);
			spinner.setList(this, str_zhuangtai, 40);

			break;
		case R.id.shangjiaoqingfen_xianjin_tianjia:
			addXianJin();
			break;
		case R.id.shangjiaoqingfen_spinner_zhongkong_layout:
			spinner = new MySpinner(this, spinner_layout_pingzheng,
					zhongkong_zhonglei);
			spinner.setSpinnerHeight(spinner_layout_pingzheng.getHeight() * 2);
			spinner.setListZK(this, zhongkongList);
			spinner.showPopupWindow(spinner_layout_pingzheng);
			spinner.setListZK(this, zhongkongList, 40);
			break;

		case R.id.shangjiaoqingfen_zhongkong_tianjia:
			addZhongKong();
			break;
		case R.id.shangjiaoqingfen_dizhiyapin_tianjia:
			// 抵质押品添加
			addDizhiYapin();
			break;

		case R.id.shangjiaoqingfen_dizhiyapin_tianjia_saomiao:
			// 开启扫描功能
			System.out.println(isCanOpenSaomiao + "");
			if (isCanOpenSaomiao == true) {
				getRfid().addNotifly(dizhiSaomiaoUtil);
				getRfid().scanOpen();
				isCanOpenSaomiao = false;
				System.out.println("--------关闭扫描");
				openSaomiao.setText("关闭扫描");
			} else {
				getRfid().scanclose();
				isCanOpenSaomiao = true;
				openSaomiao.setText("开启扫描");
				System.out.println("--------开启扫描");
			}

			break;
		case R.id.shangjiaoqingfen_qingdianwancheng:
			// 清点完成
			manager.getResultmsg().resultmsgHas2(this, "是否提交数据?");
			break;
		case R.id.shangjiaoqingfen_back:
			ShangJiaoQingFen_o_qf.this.finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 添加现金
	 * 
	 * @author shimao
	 */
	public void addXianJin() {
		String xjcount = xianjin_count.getText().toString();
		juanbie = spinner_text_juanbie.getText().toString();
		System.out.println("juanbie--->>>>>:" + juanbie);
		if (!"".equals(juanbie) && null != juanbie) {
			// 遍历卷别集合 找出与名称对应的券别id
			for (int i = 0; i < this.quanbieList.size(); i++) {
				if (juanbie.equals(quanbieList.get(i).getQuanbieName())) {
					juanbie = quanbieList.get(i).getQuanbieId();
				}
			}

		}

		// 将残损状态名称改为残损状态id
		zhuangtai = spinner_text_zhuangtai.getText().toString();
		if (!"".equals(zhuangtai) && null != zhuangtai) {
			if (zhuangtai.equals("完整券")) {
				zhuangtai = "0";
			} else if (zhuangtai.equals("半损券")) {
				zhuangtai = "2";
			} else if (zhuangtai.equals("全损券")) {
				zhuangtai = "1";
			}
		}
		if (juanbie.equals("请选择")) {
			manager.getResultmsg().resultmsg(this, "请选择券别类型", false);
		} else {
			if (TextUtils.isEmpty(xjcount)) {
				manager.getResultmsg().resultmsg(this, "请输入数量", false);
			} else {
				if (zhuangtai.equals("请选择")) {
					manager.getResultmsg().resultmsg(this, "请选择状态", false);
				} else {
					for (int i = 0; i < xianjinlist.size(); i++) {
						if (xianjinlist.get(i).getJuanbie().equals(juanbie)
								&& xianjinlist.get(i).getZhuangtai()
										.equals(zhuangtai)) {
							isok = i;
						}
					}

					// 根据券别名称 查询该券别的id 全额价值 残损价值
					String quanJiazhi = "";
					String canshuJiazhi = "";
					for (int i = 0; i < quanbieList.size(); i++) {
						if (juanbie.equals(quanbieList.get(i).getQuanbieId())) {
							quanJiazhi = quanbieList.get(i).getQuanJiazhi();
							canshuJiazhi = quanbieList.get(i)
									.getCanshunJiazhi();
						}
					}

					if (isok == -1) {
						System.out.println("现金集合开始添加-->");
						xianjinlist.add(new TianJiaXianJin(juanbie, quanJiazhi,
								canshuJiazhi, zhuangtai, xjcount));
					} else {
						int listcount = Integer.parseInt(xianjinlist.get(isok)
								.getCount());
						int xjshuliang = Integer.parseInt(xjcount);
						int count = listcount + xjshuliang;
						xianjinlist.get(isok).setCount(count + "");
						isok = -1;
					}

					Heji();
					xianjin_listview.setAdapter(adapter);
					new TurnListviewHeight(xianjin_listview);
					System.out.println("现金集合的数量----->");
					for (int i = 0; i < xianjinlist.size(); i++) {
						System.out.println(xianjinlist.get(i).getJuanbie()
								+ "----->>" + xianjinlist.get(i).getCount());
					}

				}
			}
		}
	}

	/**
	 * 重空添加 SM
	 */
	public void addZhongKong() {
		String zhongkongQishihao = pingzhengbianhao.getText().toString(); // 重空起始号
		int weiShuzi = getnum.getNum(zhongkongQishihao); // 获取号段末尾的数字
		String weishuziString = getnum.getStringNum(zhongkongQishihao); // 获取号段末尾的数字
		String shouZimu = getnum.getChar(zhongkongQishihao, weishuziString); // 获取号段前面的字母
		String lianxuShu = pingzheng_haoduan.getText().toString(); // 连续数
		pingzheng = zhongkong_zhonglei.getText().toString();
		if (TextUtils.isEmpty(zhongkongQishihao)
				&& TextUtils.isEmpty(lianxuShu) && pingzheng.equals("请选择")) {
			manager.getResultmsg().resultmsg(this, "重空添加信息不可为空", false);
		} else {
			int countNum = 0; // 已清点数据
			if (zhongkonglist.size() == 0) {
				// 将信息添加金号段集合
				addZhongkong(zhongkongQishihao, weiShuzi, shouZimu, pingzheng,
						lianxuShu);
			} else {
				int size = zhongkonglist.size();
				for (int i = 0; i < size; i++) {
					TianJiaZhongKong zk = zhongkonglist.get(i);
					// 首先比较前面的号段字母是否相同
					String zimu = zk.getHaoZimu();
					if (shouZimu.equals(zimu)) {
						// 如果号段前面字母相同 检查输入的号码的范围是否已经被录入
						int shuzi = zk.getHaoShuzi(); // 取出该号段的末尾数字
						int count = zk.getXianxushu(); // 取出连续数量
						int endShuzi = shuzi + count - 1; // 计算出结尾号段

						if (weiShuzi >= shuzi && weiShuzi <= endShuzi) {
							// 如果输入的在起始号段在 有些号段之间 提示用户不能添加
							manager.getResultmsg().resultmsg(this, "凭证编号或号段重复",
									false);
							return;
						}

					}

					if (i == zhongkonglist.size() - 1) {
						// 遍历完成 未发现不合格 将号段添加
						addZhongkong(zhongkongQishihao, weiShuzi, shouZimu,
								pingzheng, lianxuShu);
					}
				}
			}
			for (TianJiaZhongKong zk : zhongkonglist) {
				countNum += zk.getXianxushu();
			}

			zhongkong_yiqingdian.setText("" + countNum);
			pingzheng_listview.setAdapter(zkadapter);
			new TurnListviewHeight(pingzheng_listview);
		}
	}

	/*
	 * 添加重空集合信息
	 */
	private void addZhongkong(String zhongkongQishihao, int weiShuzi,
			String shouZimu, String pingzheng, String lianxuShu) {
		int num = Integer.parseInt(lianxuShu.trim()); // 将连续数量转换成int
		String zhongkongId = "";
		for (ZhongkongXinxi zk : zhongkongList) {
			if (zk.getZhongkongName().equals(pingzheng)) {
				zhongkongId = zk.getZhongkongId();
			}
		}

		String jieshuHao = getnum.jisuanweiHao(zhongkongQishihao, num);
		System.out.println("结束号段为");
		// 创建 添加重空对象
		TianJiaZhongKong tjzk = new TianJiaZhongKong(zhongkongQishihao,
				shouZimu, weiShuzi, zhongkongId, num, pingzheng, jieshuHao);
		// 将对象添加进入集合
		this.zhongkonglist.add(tjzk);
	}

	/**
	 * 抵制押品添加 SM
	 */
	public void addDizhiYapin() {
		String dz_bianhao = dizhi_bianhao.getText().toString();
		if (TextUtils.isEmpty(dz_bianhao)) {
			// manager.getResultmsg().resultmsg(this, "请输入抵质押品编号", false);
		} else {
			for (int i = 0; i < dizhilist.size(); i++) {
				if (dizhilist.get(i).equals(dz_bianhao)) {
					dzok = i;
				} else {
					dzok = -1;
				}
			}
			if (dzok == -1) {
				dizhilist.add(dz_bianhao);
			} else {
				manager.getResultmsg().resultmsg(this, "抵质押品编号重复", false);
			}
			// dzadapter.notifyDataSetChanged();
			dizhi_listview.setAdapter(dzadapter);
			new TurnListviewHeight(dizhi_listview);
			dizhi_heji.setText("" + dizhilist.size());
		}
	}

	/**
	 * 合计
	 */
	public void Heji() {

		heji_xianjin = 0;
		if (xianjinlist.size() != 0) {

			for (int i = 0; i < xianjinlist.size(); i++) {
				int count = Integer.parseInt(xianjinlist.get(i).getCount()
						.trim());
				double quanJiazhi = Double.parseDouble(xianjinlist.get(i)
						.getQuanJiazhi().trim());
				double canshuJiazhi = Double.parseDouble(xianjinlist.get(i)
						.getCanshunJiazhi().trim());
				int zhuangtai = Integer.parseInt(xianjinlist.get(i)
						.getZhuangtai().trim());

				switch (zhuangtai) {
				case 0: // 全额
					heji_xianjin += (double) (quanJiazhi * count);
					break;
				case 1: // 全损
					heji_xianjin += (double) (quanJiazhi * count);
					break;
				case 2: // 半损
					heji_xianjin += (double) (canshuJiazhi * count);
					break;
				}
			}
		}
		xianjin_heji.setText(new NumFormat().format(heji_xianjin + ""));
	}

	class XianJinAdapter extends BaseAdapter {
		LayoutInflater lf = LayoutInflater.from(ShangJiaoQingFen_o_qf.this);
		ViewHodler view;

		@Override
		public int getCount() {
			return xianjinlist.size();
		}

		@Override
		public Object getItem(int arg0) {
			return xianjinlist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				arg1 = lf.inflate(R.layout.adapter_shangjiaoqingfen_tianjia,
						null);
				view = new ViewHodler();
				view.juanbie = (TextView) arg1
						.findViewById(R.id.sj_qf_title_tv1);
				view.zhuangtai = (TextView) arg1
						.findViewById(R.id.sj_qf_title_tv2);
				view.shuliang = (TextView) arg1
						.findViewById(R.id.sj_qf_title_tv3);
				view.shanchu = (Button) arg1
						.findViewById(R.id.sj_qf_title_button);
				arg1.setTag(view);
			} else {
				view = (ViewHodler) arg1.getTag();
			}
			view.juanbie.setText(xianjinlist.get(position).getJuanbie());
			view.zhuangtai.setText(xianjinlist.get(position).getZhuangtai());
			view.shuliang.setText(xianjinlist.get(position).getCount());
			view.shanchu.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					xianjinlist.remove(position);
					Heji();
					adapter.notifyDataSetChanged();
					new TurnListviewHeight(xianjin_listview);
				}
			});
			return arg1;
		}
	}

	public static class ViewHodler {
		TextView juanbie, zhuangtai, shuliang;
		Button shanchu;
	}

	class ZhongKongAdapter extends BaseAdapter {
		LayoutInflater lf = LayoutInflater.from(ShangJiaoQingFen_o_qf.this);
		ZhongKongHolder view;

		@Override
		public int getCount() {
			return zhongkonglist.size();
		}

		@Override
		public Object getItem(int arg0) {
			return zhongkonglist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				arg1 = lf.inflate(R.layout.adapter_shangjiaoqingfen_tianjia2,
						null);
				view = new ZhongKongHolder();
				view.bianhao = (TextView) arg1
						.findViewById(R.id.sj_qf_title_zk_tv1);
				view.zhonglei = (TextView) arg1
						.findViewById(R.id.sj_qf_title_zk_tv2);
				view.haoduan = (TextView) arg1
						.findViewById(R.id.sj_qf_title_zk_tv3);
				view.shanchu = (Button) arg1
						.findViewById(R.id.sj_qf_title_zk_button);
				arg1.setTag(view);
			} else {
				view = (ZhongKongHolder) arg1.getTag();
			}
			view.bianhao.setText(zhongkonglist.get(position).getKaishiHao());
			view.zhonglei.setText(zhongkonglist.get(position).getZhongleiId());
			view.haoduan.setText(zhongkonglist.get(position).getXianxushu()
					+ "");
			view.shanchu.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					zhongkonglist.remove(position);
					zhongkong_yiqingdian.setText("" + zhongkonglist.size());
					zkadapter.notifyDataSetChanged();
					new TurnListviewHeight(pingzheng_listview);
				}
			});
			return arg1;
		}

	}

	public static class ZhongKongHolder {
		TextView bianhao, zhonglei, haoduan;
		Button shanchu;
	};

	class DiZhiAdapter extends BaseAdapter {
		DiZhiHolder view;
		LayoutInflater lf = LayoutInflater.from(ShangJiaoQingFen_o_qf.this);

		@Override
		public int getCount() {
			return dizhilist.size();
		}

		@Override
		public Object getItem(int arg0) {
			return dizhilist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				view = new DiZhiHolder();
				arg1 = lf.inflate(R.layout.adapter_shangjiaoqingfen_tianjia3,
						null);
				view.bianhao = (TextView) arg1
						.findViewById(R.id.adapter_shangjiaoqingfen_tianjia_dizhiyapin);
				view.shanchu = (Button) arg1
						.findViewById(R.id.adapter_shangjiaoqingfen_shanchu_dizhiyapin);
				arg1.setTag(view);
			} else {
				view = (DiZhiHolder) arg1.getTag();
			}
			view.bianhao.setText(dizhilist.get(position));
			view.shanchu.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dizhilist.remove(position);
					dzadapter.notifyDataSetChanged();
					dizhi_listview.setAdapter(dzadapter);
					new TurnListviewHeight(dizhi_listview);
					dizhi_heji.setText("" + dizhilist.size());
				}
			});
			return arg1;
		}

	}

	public static class DiZhiHolder {
		TextView bianhao;
		Button shanchu;
	}

	/**
	 * 获取券别数据
	 */
	public void getSpinnerData() {
		manager.getRuning().runding(ShangJiaoQingFen_o_qf.this, "数据加载中...");
		// 获取券别信息
		Thread thread = new Thread(new GetQuanbieXinxi());
		thread.start();
	}

	/**
	 * 获取重空
	 */
	private void getZhongkong() {
		manager.getRuning().runding(ShangJiaoQingFen_o_qf.this, "数据加载中...");
		// 获取重空信息
		Thread thread = new Thread(new GetZhongkong());
		thread.start();
	}

	/**
	 * 从服务器获取券别信息
	 * 
	 * @author yuyunheng
	 */
	private class GetQuanbieXinxi implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = create.obtainMessage();
			try {
				QingfenRenwuService service = new QingfenRenwuService();
				String quanbieXinxi = service.getQuanbieList();
				if (quanbieXinxi != null && !quanbieXinxi.equals("")) {
					msg.obj = quanbieXinxi;
					msg.what = 1;
				} else {
					msg.what = 3; // 获取券别信息失败
				}
			} catch (SocketTimeoutException ee) {
				// TODO: handle exception
				msg.what = 2;
			} catch (Exception e) {
				// TODO: handle exception
				msg.what = 3; // 获取券别信息失败
			}

			create.sendMessage(msg); // 发送消息
		}

	}

	/**
	 * 获取重空信息
	 * 
	 * @author yuyunheng
	 * 
	 */
	private class GetZhongkong implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = create.obtainMessage();
			try {
				QingfenRenwuService service = new QingfenRenwuService();
				String zhongkongxinxi = service.getZhongkongList();
				if (zhongkongxinxi != null && !zhongkongxinxi.equals("")) {
					msg.obj = zhongkongxinxi;
					msg.what = 4;
				} else {
					msg.what = 6; // 获取券别信息失败
				}
			} catch (SocketTimeoutException ee) {
				// TODO: handle exception
				msg.what = 5;
			} catch (Exception e) {
				// TODO: handle exception
				msg.what = 6; // 获取券别信息失败
			}

			create.sendMessage(msg); // 发送消息
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			ShangJiaoQingFen_o_qf.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 提交数据
	 * 
	 */
	private void submit() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("调用存储过程的方法------->开始");
				for (int i = 0; i < xianjinlist.size(); i++) {
					System.out.println("数量------->>>>:"
							+ xianjinlist.get(i).getCount());
				}
				togerDate();// 拼接数据
				try {
					System.out.println("任务单号:" + orderNum);
					System.out.println("清分员帐号:"
							+ GApplication.user.getLoginUserName());
					System.out.println("清分员帐号:"
							+ GApplication.user.getLoginUserName());
					System.out.println("现金-->" + xianjingMsg);
					System.out.println("------------------------------数据上传");
					System.out.println("ShangJiaoQingFen_o_qf:"
							+ GApplication.user);
					/*
					 * revised by zhangxuewei 后台传值name 改为ID
					 */
					boolean isOk = new QingfenRenwuService().submitShangjiao(
							orderNum, peisongId,
							(GApplication.getApplication().app_hash.get("login_username")).toString(), xianjingMsg,
							zhongkongSubmit, dizhiMsg);
					if (isOk) {
						System.out
								.println("------------------------------上传成功");
						okHandle.sendEmptyMessage(0);
					} else {
						okHandle.sendEmptyMessage(2);
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					System.out.println("上传超时");
					timeoutHandle.sendEmptyMessage(00);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("上传失败");
					timeoutHandle.sendEmptyMessage(10);
				}
			}
		}).start();
	}

	/**
	 * 获取清分登记信息
	 */
	private void getDate() {
		manager.getRuning().runding(ShangJiaoQingFen_o_qf.this, "数据加载中...");
		new Thread(new Runnable() {
			@Override
			public void run() {
				String param;
				try {
					param = new QingfenRenwuService().isCanSubmit(peisongId);
					shangjiaoRenwu = Table.doParse(param);
					shangjiaoMark = shangjiaoRenwu;
					okHandle.sendEmptyMessage(1);
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					timeoutHandle.sendEmptyMessage(01);
				} catch (Exception e) {
					e.printStackTrace();
					timeoutHandle.sendEmptyMessage(11);
				}
			}
		}).start();
	}

	/**
	 * 判断当前清分的数据是否符合系统上的数据
	 * 
	 * @param RenwuData
	 * @param xianjinlistNew
	 * @param zhongkonglistNew
	 * @param dizhilistNew
	 * @return
	 */
	private boolean isEquals(Table[] RenwuData,
			List<TianJiaXianJin> xianjinlistNew,
			List<TianJiaZhongKong> zhongkonglistNew, List<String> dizhilistNew) {
		System.out.println("进入比对方法");
		System.out.println("现金清点集合长度：" + xianjinlistNew.size());
		System.out.println("重空清点集合长度：" + zhongkonglistNew.size());
		System.out.println("抵质押品清点集合长度：" + dizhilistNew.size());

		// 解析数据,获得现金明细,判断现金券别,残损,数量是否相同.
		List<String> x_quanbie = RenwuData[0].get("quanbieId").getValues();
		System.out.println("登记券别集合长度：" + x_quanbie.size());
		List<String> x_canshun = RenwuData[0].get("canshun").getValues();
		System.out.println("登记残损集合长度：" + x_canshun.size());
		List<String> x_shuliang = RenwuData[0].get("shuliang").getValues();
		System.out.println("登记数量集合长度：" + x_shuliang.size());
		if (x_quanbie.size() != xianjinlist.size()) {
			System.out.println("现金集合长度与登记集合长度不匹配");
			return false;
		} else {
			for (int i = 0; i < xianjinlist.size(); i++) {
				boolean flag1 = false;
				for (int j = 0; j < x_quanbie.size(); j++) {
					// 如果当清点现金的券别等于登记信息的券别，并且清点现金的残损状态等于登记信息的残损状态
					if (xianjinlist.get(i).getJuanbie()
							.equals(x_quanbie.get(j))
							&& xianjinlist.get(i).getZhuangtai()
									.equals(x_canshun.get(j))) {
						flag1 = true; // 如果找到了该卷别和该状态 把flag1设置为true
						if (!(xianjinlist.get(i).getCount().equals(x_shuliang
								.get(j)))) { // 判断金钱的数量是否与等级的想匹配
							System.out.println("现金清点不匹配1");
							return false;
						}
					}
				}
				if (!flag1) {
					// 如果没有找到那状态和该券别 表示清点数据不相符
					System.out.println("现金清点不匹配2");
					return false;
				}
			}

		}
		// 解析数据,获得重空凭证明细,判断重空类型,数量是否相同.
		List<String> z_leibie = RenwuData[1].get("leibie").getValues();
		List<String> z_leibieName = RenwuData[1].get("leibieName").getValues();
		List<String> z_beginHao = RenwuData[1].get("zkKaishi").getValues();
		List<String> z_jieshu = RenwuData[1].get("zkJieshu").getValues();

		if (z_leibie.size() != zhongkonglist.size()) {
			System.out.println("重空集合长度与登记集合长度不匹配");
			return false;
		} else {
			// 解析数据,获得抵质押品明细,判断抵质编号,数量是否相同.

			for (int i = 0; i < zhongkonglist.size(); i++) {
				TianJiaZhongKong tj = zhongkonglist.get(i);
				for (int j = 0; j < z_leibie.size(); j++) {
					String typeId = tj.getZhongleiId(); // 取出录入的 类别id
					String bg = tj.getKaishiHao(); // 取出该类别的开始号段
					String ed = tj.getHaoZimu(); // 取出结束号段

					String djtypeId = z_leibie.get(j);// 取出登记的 类别id
					String djbg = z_beginHao.get(j);// 取出登记的 类别开始号段
					String djed = z_jieshu.get(j); // 取出登记的结束号段
					if (typeId.equals(djtypeId) && bg.equals(djbg)) {
						// 如果类别相同 开始号段也相同 则比较结尾号段
						if (!ed.equals(typeId)) {
							System.out.println("尾号不相同");
							return false;
						} else {
							break;
						}
					}

					if (j == z_leibie.size() - 1) {
						System.out.println("类别与开始号匹配不成功");
						return false;
					}

				}
			}
		}

		return true;
	}

	private void togerDate() {
		for (int i = 0; i < xianjinlist.size(); i++) {
			System.out.println("数量:" + xianjinlist.get(i).getCount());
		}

		// 拼接现金字符串
		StringBuffer sbX_quanbie = new StringBuffer();
		StringBuffer sbX_cansun = new StringBuffer();
		StringBuffer sbX_count = new StringBuffer();
		sbX_quanbie.append("quanbieId:");
		sbX_cansun.append("canshun:");
		sbX_count.append("shuliang:");
		int flag = 0;
		for (TianJiaXianJin xianjin : xianjinlist) {
			flag++;
			sbX_quanbie.append(xianjin.getJuanbie());
			sbX_cansun.append(xianjin.getZhuangtai());
			sbX_count.append(xianjin.getCount());
			System.out.println("现金数量count--->" + xianjin.getCount());
			if (flag < xianjinlist.size()) {
				sbX_quanbie.append(BianyiType.douhao);
				sbX_cansun.append(BianyiType.douhao);
				sbX_count.append(BianyiType.douhao);
			}
		}
		// 拼接重空凭证字符串
		StringBuffer sbZ_leibie = new StringBuffer();
		StringBuffer sbZ_leibieSubmit = new StringBuffer();
		StringBuffer sbZ_leibieName = new StringBuffer();
		StringBuffer sbZ_kaishishu = new StringBuffer();
		StringBuffer sbZ_jieshushu = new StringBuffer();
		StringBuffer sbZ_bianhao = new StringBuffer();
		StringBuffer sbZ_shuliang = new StringBuffer();

		sbZ_leibie.append("leibie:");
		sbZ_leibieName.append("leibieName:");
		sbZ_kaishishu.append("zkKaishi:");
		sbZ_jieshushu.append("zkJieshu:");

		sbZ_leibieSubmit.append("zhongkongtype:");
		sbZ_bianhao.append("bianhao:");
		sbZ_shuliang.append("shuliang:");
		flag = 0;
		for (TianJiaZhongKong zhongkong : zhongkonglist) {
			flag++;
			sbZ_leibie.append(zhongkong.getZhongleiId());
			sbZ_leibieName.append(zhongkong.getZhongleiName());
			sbZ_kaishishu.append(zhongkong.getKaishiHao());
			sbZ_jieshushu.append(zhongkong.getJieshuHao());

			sbZ_leibieSubmit.append(zhongkong.getZhongleiId());
			sbZ_bianhao.append(zhongkong.getKaishiHao());
			sbZ_shuliang.append(zhongkong.getXianxushu());
			if (flag < zhongkonglist.size()) {
				sbZ_leibie.append(BianyiType.douhao);
				sbZ_bianhao.append(BianyiType.douhao);
				sbZ_shuliang.append(BianyiType.douhao);
			}
		}
		// 拼接抵质押品字符串
		StringBuffer sbD = new StringBuffer();
		sbD.append("dizhibianhao:");
		flag = 0;
		for (String dizhi : dizhilist) {
			flag++;
			sbD.append(dizhi);
			if (flag < dizhilist.size()) {
				sbD.append(BianyiType.xiahuaxian);
			}
		}
		xianjingMsg = sbX_quanbie + BianyiType.fenge + sbX_cansun
				+ BianyiType.fenge + sbX_count;// 现金msg
		zhongkongMsg = sbZ_leibie + BianyiType.fenge + sbZ_leibieName
				+ BianyiType.fenge + sbZ_kaishishu + BianyiType.fenge
				+ sbZ_jieshushu; // 重空msg
		zhongkongSubmit = sbZ_leibieSubmit + BianyiType.fenge + sbZ_bianhao
				+ BianyiType.fenge + sbZ_shuliang;// 重空凭证提交msg
		dizhiMsg = "dizhishu:" + dizhilist.size() + BianyiType.fenge + sbD;// 抵质押品msg
		System.out.println("我是现金:" + xianjingMsg);
		System.out.println("我是中控:" + zhongkongMsg);
		System.out.println("我是地址押品:" + dizhiMsg);
		String str = xianjingMsg + BianyiType.jianduan + zhongkongMsg
				+ BianyiType.jianduan + dizhiMsg;
		String subStr = xianjingMsg + BianyiType.jianduan + zhongkongSubmit
				+ BianyiType.jianduan + dizhiMsg;
		shangjiaoMark = Table.doParse(str);
		subimtMark = Table.doParse(subStr);

		System.out.println("比对信息为：--------------------" + shangjiaoMark);
		System.out.println("比提交信息为：--------------------" + subimtMark);
	}

	/**
	 * 判断数组里面是否含有某个值
	 * 
	 * @param arr
	 *            数组
	 * @param targetValue
	 *            要查询的值
	 * @return 有-返回true,没有-返回false;
	 */
	private boolean useLoop(String[] arr, String targetValue) {
		for (String s : arr) {
			if (s.equals(targetValue))
				return true;
		}
		return false;
	}

	private Handler okHandle = new Handler() {// 数据上传成功handler
		public void handleMessage(Message msg) {

			if (msg.what == 0) {
				manager.getRuning().remove();
				System.out.println("------------------------------页面跳转");
				Skip.skip(ShangJiaoQingFen_o_qf.this, QingFenJinDu_qf.class,
						null, 0);
			}
			if (msg.what == 1) {
				getSpinnerData(); // 获取成功以后继续 获取券别信息
				System.out.println("你好");
			}
			if (msg.what == 2) {
				System.out.println("aaaaaaaaaaaaaaaaaaaaaa");
				manager.getRuning().remove();
				manager.getAbnormal().timeout(ShangJiaoQingFen_o_qf.this,
						"提交失败", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								manager.getRuning().runding(
										ShangJiaoQingFen_o_qf.this, "提交中...");
								submit();
							}
						});
			}
		};
	};

	/**
	 * 数据提交Handler
	 */
	private Handler submitHandler = new Handler() {
		public void handleMessage(Message msg) {
			manager.getResultmsg().remove();
			if (msg.what == 0) {
				System.out.println("------------------------------数据匹配");
				manager.getRuning().runding(ShangJiaoQingFen_o_qf.this,
						"提交中...");
				// 添加新的数据
				System.out.println("清空数据了");
				// addXianJin();
				// addZhongKong();
				// addDizhiYapin();
				// 必须现金|中控|抵制押品 3者为空时不能进行提交操作 zhongkonglist xianjinlist
				// dizhilist
				if (zhongkonglist.size() == 0 && xianjinlist.size() == 0
						&& dizhilist.size() == 0) {
					Toast.makeText(ShangJiaoQingFen_o_qf.this,
							"有现金|重空|抵制押品三者不能同时为空", Toast.LENGTH_SHORT).show();
					manager.getRuning().remove();
				} else {
					submit();
				}
			}
			if (msg.what == 1) {
				togerDate();
				shangjiaoRenwu = shangjiaoMark;
				xianjinlist.clear();
				zhongkonglist.clear();
				dizhilist.clear();
				xianjin_listview.setAdapter(adapter);
				new TurnListviewHeight(xianjin_listview);
				pingzheng_listview.setAdapter(zkadapter);
				new TurnListviewHeight(pingzheng_listview);
				dizhi_listview.setAdapter(dzadapter);
				new TurnListviewHeight(dizhi_listview);
				xianjin_heji.setText("0");
				dizhi_heji.setText("0");
				zhongkong_yiqingdian.setText("0");
				spinner_text_juanbie.setText("请选择");
				xianjin_count.setText("");
				spinner_text_zhuangtai.setText("请选择");
				pingzhengbianhao.setText("");
				zhongkong_zhonglei.setText("请选择");
				pingzheng_haoduan.setText("");
				dizhi_bianhao.setText("");

			}
		};
	};
	private Handler timeoutHandle = new Handler() {// 连接超时handler
		public void handleMessage(Message msg) {
			manager.getRuning().remove();
			if (msg.what == 00) {

				System.out.println("我是提交超时的时候---");
				manager.getAbnormal().timeout(ShangJiaoQingFen_o_qf.this,
						"数据连接超时", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								manager.getRuning().runding(
										ShangJiaoQingFen_o_qf.this, "提交中...");
								submit();
							}
						});
			}
			if (msg.what == 10) {

				manager.getAbnormal().timeout(ShangJiaoQingFen_o_qf.this,
						"网络连接失败", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								manager.getRuning().runding(
										ShangJiaoQingFen_o_qf.this, "提交中...");
								submit();

							}
						});
			}
			if (msg.what == 01) {
				manager.getAbnormal().timeout(ShangJiaoQingFen_o_qf.this,
						"数据连接超时", new OnClickListener() {
							@Override
							public void onClick(View arg0) {

								manager.getAbnormal().remove();
								manager.getRuning().runding(
										ShangJiaoQingFen_o_qf.this, "数据加载中...");
								getDate();
							}
						});
			}
			if (msg.what == 11) {
				manager.getAbnormal().timeout(ShangJiaoQingFen_o_qf.this,
						"网络连接失败", new OnClickListener() {
							@Override
							public void onClick(View arg0) {

								manager.getAbnormal().remove();
								manager.getRuning().runding(
										ShangJiaoQingFen_o_qf.this, "数据加载中...");
								getDate();
							}
						});
			}
		};
	};
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String dizhiNum = msg.getData().getString("Num");
			for (int i = 0; i < dizhilist.size(); i++) {
				if (dizhilist.get(i).equals(dizhiNum)) {
					dzok = i;
				} else {
					dzok = -1;
				}
			}
			if (dzok == -1) {
				dizhilist.add(dizhiNum);
			} else {
				manager.getResultmsg().resultmsg(ShangJiaoQingFen_o_qf.this,
						"抵质押品编号重复", false);
			}
			// dzadapter.notifyDataSetChanged();
			dizhi_listview.setAdapter(dzadapter);
			new TurnListviewHeight(dizhi_listview);
			dizhi_heji.setText("" + dizhilist.size());
		}
	};

	/**
	 * 清分对比 handler
	 */
	private Handler isSubmitHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			// 如果什么都没记录
			if (xianjinlist.size() == 0 && zhongkonglist.size() == 0
					&& dizhilist.size() == 0) {
				manager.getResultmsg().resultmsgHas1(
						ShangJiaoQingFen_o_qf.this, "请输入清分信息", false, 1);
			} else {

				manager.getRuning().runding(ShangJiaoQingFen_o_qf.this,
						"比对中...");
				boolean isCanSubmit = isEquals(shangjiaoRenwu, xianjinlist,
						zhongkonglist, dizhilist);
				manager.getRuning().remove();
				if (isCanSubmit) {
					manager.getResultmsg().resultmsgHas2(
							ShangJiaoQingFen_o_qf.this, "清分数据匹配,是否提交", true, 0);
				} else {
					shangjiaoRenwu = shangjiaoMark;
					manager.getResultmsg()
							.resultmsgHas1(ShangJiaoQingFen_o_qf.this,
									"清分数据不匹配,请复清", false, 1);
				}
			}
		}
	};

	// ------————————————————————————————————————————————————————————————————————————————————————————————————————————
	/*
	 * 页面初始化 Handler
	 */
	private Handler create = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			manager.getRuning().remove(); // 关闭弹窗
			switch (msg.what) {
			case 1: // 正常获取
				// 设置券别信息
				str_zhuangtai = new String[] { "完整券", "半损券", "全损券" };
				String quanbieXinxi = msg.obj.toString();
				System.out.println("获取的券别信息为：" + quanbieXinxi);
				Table[] table = Table.doParse(quanbieXinxi);
				List<String> quanbieIds = table[0].get("quanbieId").getValues();
				List<String> quanbieNames = table[0].get("quanbieName")
						.getValues();
				List<String> quanjiazhis = table[0].get("quanjiazhi")
						.getValues();
				List<String> canshunjiazhis = table[0].get("canshunjiazhi")
						.getValues();

				// 将券别信息添加进入集合中
				for (int i = 0; i < quanbieIds.size(); i++) {
					QuanbieXinxi xinxi = new QuanbieXinxi();
					xinxi.setCanshunJiazhi(canshunjiazhis.get(i));
					xinxi.setQuanbieId(quanbieIds.get(i));
					xinxi.setQuanbieName(quanbieNames.get(i));
					xinxi.setQuanJiazhi(quanjiazhis.get(i));
					quanbieList.add(xinxi);
				}
				getZhongkong(); // 继续获取重空
				break;

			case 2: // 连接超时
				manager.getAbnormal().timeout(ShangJiaoQingFen_o_qf.this,
						"数据连接超时", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								getSpinnerData(); // 点击重新获取
							}
						});
				break;

			case 3: // 获取失败
				manager.getAbnormal().timeout(ShangJiaoQingFen_o_qf.this,
						"券别信息获取失败", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								getSpinnerData(); // 点击重新获取
							}
						});
				break;
			case 4: // 重空信息获取成功
				String zhongkongXinxi = msg.obj.toString();
				Table[] table2 = Table.doParse(zhongkongXinxi);
				List<String> zhongkongIdList = table2[0].get("zhongkongId")
						.getValues();
				List<String> zhongkongNameList = table2[0].get("zhongkongName")
						.getValues();
				for (int i = 0; i < zhongkongIdList.size(); i++) {
					String zhongkongId = zhongkongIdList.get(i);
					String zhongkongName = zhongkongNameList.get(i);
					zhongkongList.add(new ZhongkongXinxi(zhongkongId,
							zhongkongName));
				}

				// str_pingzheng = new String[] { "存折", "银行卡", "支票", "网银盾" };
				break;
			case 5: // 重空连接超时
				manager.getAbnormal().timeout(ShangJiaoQingFen_o_qf.this,
						"数据连接超时", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								getZhongkong(); // 点击重新获取
							}
						});
				break;
			case 6: // 重空获取失败
				manager.getAbnormal().timeout(ShangJiaoQingFen_o_qf.this,
						"重空信息获取失败", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								getZhongkong(); // 点击重新获取
							}
						});
				break;

			}
		}
	};

}
