package com.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import cn.poka.util.CrashHandler;

import com.entity.BoxDetail;
import com.entity.Finger;
import com.entity.KuanXiangmingxi;
import com.entity.LookStorageDetail;
import com.entity.SystemUser;
import com.example.app.entity.KuanXiangBox;
import com.example.app.entity.KuanXiangChuRu;
import com.example.app.entity.S_box;
import com.example.app.entity.User;
import com.example.app.entity.UserInfo;

public class GApplication extends Application {
	private static  GApplication application = null;
	public static String loginUsername;//登录后返回的用户姓名  头部显示用到
	public static String organizationName;//登录后返回的机构名称 头部显示用到
	public static String loginname;
	public static String loginpwd; //修改密码用
	public static String loginJidouId;
	public static String loginjueseid;
	public static List<KuanXiangBox> boxHandoverList = new ArrayList<KuanXiangBox>();
	public static KuanXiangBox sk;
	public static KuanXiangChuRu kxc;
	public static SystemUser user;   //系统最外层登录人员
	public static User use;//库管员
	public static UserInfo userInfo;
	public static Finger finger_left;//出库指纹验证左人员信息
	public static Finger finger_right;//出库指纹验证右人员信息
	public static User wd_user1;//网点人员一
	public static User wd_user2;//网点人员二
	public static User kuguan1,kuguan2;//库管员一二
	public static Bitmap map;//储存第一位库管员的指纹图片
	public GApplication(){};
	public Map<String,Object> app_hash = new HashMap<String, Object>();
	private static LayoutInflater lf;
	public static List<BoxDetail> list_g=new ArrayList<BoxDetail>();//扫箱数据
	public static List<KuanXiangmingxi> mingxi_list;
	public static List<KuanXiangmingxi> mingxi_list2;
	public static List<KuanXiangChuRu> zaosonglist=new ArrayList<KuanXiangChuRu>();
	public static List<KuanXiangChuRu> wanrulist=new ArrayList<KuanXiangChuRu>();
	public static List<S_box> slist;//需要早送调去的信息
	public static List<S_box> zslist;//选中需要早送的款箱
	public static List<String> zaolist=new ArrayList<String>();//交接页面获取的明细  最后提交用(明天要早送的箱子)
	public static List<String> zyzaolist=new ArrayList<String>();//交接页面获取的明细  最后提交用  在用状态
	public static String curDate;//选择的早送日期
	public static int jiaojiestate=0;//1 早送交接  2 晚收交接  0 啥也不是  3 晚收交接(有早送)  指纹验证后的提交时用
	public static String jigouid;//交接最后提交用
	public static int churuShow=0;//出入库显示   1.表示早送列表点击后的显示  2.表示晚入列表点击后的显示
	public static int chukubiaoshi=0;//1 早送交接  2 晚收交接  0 啥也不是   指纹验证后的提交时用
	public static List<String> smlist = new ArrayList<String>();//选中需要早送的款箱
	public static List<String> zssqlist=new ArrayList<String>();	
	public static List<String> saolist=new ArrayList<String>();//款箱出库扫描临时集合
	public static List<BoxDetail> linshilist=new ArrayList<BoxDetail>();//扫箱临时数据
	
	public static String strBox;
	public static String pcDate;//从PC端获取得到的当前时间
	public static String wrong;//错误款箱
	public static String realName;//用户真实姓名
	public static String taskCorpId;//任务对应的会计科ID revised by zhangxuewei
	public static String getTaskCorpId() {
		return taskCorpId;
	}


	public static void setTaskCorpId(String taskCorpId) {
		GApplication.taskCorpId = taskCorpId;
	}
	/*
	 * 查库服务：每个任务单号对应的明细，
	 * 在进入LookStorageTaskListActivity时创建，退出时销毁，
	 * 在LookStorageTaskDetailActivity中使用
	 * @author zhouKai
	 */
	public static Map<String, List<LookStorageDetail>> taskDetails;
	
	
	public LayoutInflater getlf(Activity a){
		if(lf==null){
		  lf = a.getLayoutInflater();	
		}
		return lf;
	}
	
	
	/**
	 * 单例获取Application实例
	 * @param context
	 * @return
	 */
	public static GApplication getApplication(){
		if(application==null){
		   application = new GApplication();			
		}
		return application;
	}
		
	/**
	 * 0弹出，1为不弹出
	 */
	static Map<String,Activity> map_exit = new HashMap<String, Activity>();
		
	/**
	 * 
	 * @param a
	 * @param num  0为弹出，1为不弹出
	 */
	public static void addActivity(Activity a,String num){
		map_exit.put(num, a);
	}
	
	/**
	 * 
	 * @param boo  true为退出,false为不退出
	 */
	public static void exit(boolean boo){
		try {
			//退出
			if(boo){
				for (Map.Entry<String,Activity> item:map_exit.entrySet()) {
					Log.i(item+"",item.getValue()+"");
					item.getValue().finish();
					//android.os.Process.killProcess(android.os.Process.myPid());
				}						
				System.exit(0);	
			
			//弹栈
			}else{
				for (Map.Entry<String,Activity> item:map_exit.entrySet()) {
					if(item.getKey().substring(0,1).equals("0")){
					item.getValue().finish();	
					}				
				}	
			}
			//重置
			map_exit.clear();		
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}

	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler.getInstance().init(this);
	}
	
	
	
	
}
