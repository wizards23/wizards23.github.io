package com.moneyboxadmin.biz;

import java.net.SocketTimeoutException;
import java.util.List;

import com.entity.BoxDetail;
import com.entity.User;
import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;
import com.moneyboxadmin.service.SaveAuthLogService;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SaveAuthLogBiz {
	// 钞箱交接
	public Handler handler;
	public static User user;

	int num;
	Bundle bundle = new Bundle();

	SaveAuthLogService saveAuthLogService;

	SaveAuthLogService getSaveAuthLogService() {
		return saveAuthLogService = saveAuthLogService == null ? new SaveAuthLogService()
				: saveAuthLogService;
	}

	/**
	 * 
	 * @param planNum
	 *            计划编号或订单
	 * @param cashBoxNum
	 *            钞箱编号
	 * @param corpId
	 *            机构ID
	 * @param roleId
	 *            角色id
	 * @param cValue
	 *            指纹特征值
	 * @param authType
	 *            交接类型（空钞箱交接01 ，ATM加钞钞箱交接02，未清回收钞箱交接03，网点人员加钞钞箱交接，回收钞箱交接）
	 * @param perNum
	 *            第几个人交接 （一般2个人交接，如果第一人按指纹就传"1";如果第二人按指纹就传"2"）
	 * @param sumPerNum
	 *            一共需要几个人交接 如果1个人交接传"1"如果2个人交接传"2"
	 */
	public void getSaveAuthLog(String planNum, List<BoxDetail> list,
			String corpId, String roleId, byte[] cValue, String authType,
			String perNum, String sumPerNum) {
		if (GolbalUtil.onclicks) {
			GolbalUtil.onclicks = false;
			Log.i("list", list.size() + "");
			new Thread(new Run(planNum, getBoxNum(list), corpId, roleId,
					cValue, authType, perNum, sumPerNum)).start();
		}
	}

	/**
	 * 钞箱编号参数编辑
	 * 
	 * @param list
	 * @return
	 */
	private String getBoxNum(List<BoxDetail> list) {
		StringBuffer boxNum = new StringBuffer(); // 钞箱编号
		if (list.size() == 1) {
			return list.get(0).getNum();
		}

		for (int i = 0; i < list.size(); i++) {
			Log.i("list.get(i).getNum()", list.get(i).getNum());
			boxNum.append(list.get(i).getNum());
			boxNum.append("|");
		}

		return boxNum.toString().substring(0, boxNum.lastIndexOf("|"));
	}

	class Run implements Runnable {
		Message m;

		String planNum;
		String cashBoxNum;
		String corpId;
		String roleId;
		byte[] cValue;
		String authType;
		String perNum;
		String sumPerNum;

		public Run() {
		}

		public Run(String planNum, String cashBoxNum, String corpId,
				String roleId, byte[] cValue, String authType, String perNum,
				String sumPerNum) {

			m = handler.obtainMessage();
			this.planNum = planNum;
			this.cashBoxNum = cashBoxNum;
			this.corpId = corpId;
			this.roleId = roleId;
			this.cValue = cValue;
			this.authType = authType;
			this.perNum = perNum;
			this.sumPerNum = sumPerNum;

		}

		@Override
		public void run() {
			try {
				user = getSaveAuthLogService().saveAuthLog(planNum, cashBoxNum,
						corpId, roleId, cValue, authType, perNum, sumPerNum);
				Log.i("user", user + "");
				if (user != null) {
					Log.i("user", user.getName() + "");
					bundle.putString("username", user.getName()); // 用户名
					bundle.putString("userid", user.getId()); // 用户名
					num++;
					bundle.putInt("num", num);
					m.setData(bundle);
					m.what = 1;

				} else {
					m.what = 0;
				}
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				m.what = -4;
			} catch (NumberFormatException e1) {   //此处为离行加钞交接加钞员身份不合法专用
				e1.printStackTrace();
				m.what = 2;
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("PDA saveAuthLog method", e.getMessage());
				m.what = -1;
			} finally {
				handler.sendMessage(m);
				GolbalUtil.onclicks = true;
			}

		}

	}
}
