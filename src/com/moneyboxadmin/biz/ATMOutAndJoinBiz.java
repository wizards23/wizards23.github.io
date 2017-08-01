package com.moneyboxadmin.biz;

import java.net.SocketTimeoutException;
import java.util.List;

import android.os.Handler;
import android.os.Message;

import com.entity.BoxDetail;
import com.entity.User;
import com.golbal.pda.GolbalUtil;
import com.moneyboxadmin.service.ATMOutAndJoinService;

public class ATMOutAndJoinBiz {

	public static User user = null;
	public Handler handler;
	ATMOutAndJoinService aTMOutAndJoinService;

	/**
	 * 
	 * @param cbInfo
	 *            cbInfo:加钞计划编号;业务单号;操作方式;钞箱品牌信息;钞箱编号;钞箱状态;审核员ID;复核员ID;机构ID（多列时用
	 *            |分隔）
	 * @param corpId
	 *            机构ID
	 * @param roleId
	 *            角色ID
	 * @param cValue
	 *            特征值
	 * @return
	 * @throws Exception
	 */
	public void atmOutAndJoin(String cbInfo, String corpId, String roleId,
			byte[] cValue,String jiachao1,String jiachao2) {
		System.out.println("atmOutAndJoin[jiaojie1]:"+jiachao1+"[jiaojie2]"+jiachao2);
		if (GolbalUtil.onclicks) {
			GolbalUtil.onclicks = false;
			new Thread(new Run(cbInfo, corpId, roleId, cValue,jiachao1,jiachao2)).start();
		}
	}

	/**
	 * 
	 * @param plan
	 *            计划编号
	 * @param bizNum
	 *            业务单号
	 * @param list
	 *            钞箱集合
	 * @param userid1
	 *            用户ID
	 * @param userid2
	 *            用户ID
	 * @param corpid
	 *            机构ID
	 * @return
	 */
	public String getCbInfo(String plan, List<BoxDetail> list, String userid1,
			String userid2, String corpid) {
		StringBuffer sb = new StringBuffer();
		String c = ";";
		String r = "|";
		for (int i = 0; i < list.size(); i++) {
			sb.append(plan); // 加钞计划编号
			sb.append(c);
			sb.append(""); // 业务单号
			sb.append(c);
			sb.append("5"); // 操作方式
			sb.append(c);
			sb.append(list.get(i).getBrand()); // 钞箱品牌
			sb.append(c);
			sb.append(list.get(i).getNum()); // 钞箱编号
			sb.append(c);
			sb.append("02"); // 钞箱状态
			sb.append(c);
			sb.append(userid1); // 审核员
			sb.append(c);
			sb.append(userid2); // 复审核员
			sb.append(c);
			sb.append(corpid); // 机构ID
			sb.append(r);
		}
		String str = sb.substring(0, sb.length() - 1).toString();
		return str;
	}

	class Run implements Runnable {
		String cbInfo;
		String corpId;
		String roleId;
		byte[] cValue;
		String jiachao1;
		String jiachao2;
		Message m;

		public Run() {
		};

		public Run(String cbInfo, String corpId, String roleId, byte[] cValue,String jiachao1,String jiachao2) {
			this.cbInfo = cbInfo;
			this.corpId = corpId;
			this.roleId = roleId;
			this.cValue = cValue;
			this.jiachao1=jiachao1;
			this.jiachao2=jiachao2;
			m = handler.obtainMessage();
		};

		@Override
		public void run() {
			aTMOutAndJoinService = new ATMOutAndJoinService();
			try {
				user = aTMOutAndJoinService.saveAtmAddMoneyOut(cbInfo, corpId,
						roleId, cValue,jiachao1,jiachao2);
				if (user != null) {
					m.what = 1;
				} else {
					m.what = 0;
				}
			} catch (SocketTimeoutException e) {
				m.what = -4;
				e.printStackTrace();
			} catch (Exception e) {
				m.what = -1;
				e.printStackTrace();
			} finally {
				GolbalUtil.onclicks = true;
				handler.sendMessage(m);
			}

		}
	}
}
