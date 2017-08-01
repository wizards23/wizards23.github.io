package com.manager.classs.pad;

import hdjc.rfid.operator.RFID_Device;

import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;
import com.loginsystem.biz.SystemLoginBiz;
import com.messagebox.Abnormal;
import com.messagebox.GoBack;
import com.messagebox.IrfidMsg;
import com.messagebox.MenuShow;
import com.messagebox.ResultMsg;
import com.messagebox.Runing;
import com.messagebox.SureCancelButton;

public class ManagerClass {
	private Abnormal abnormal;
	private MenuShow menushow;
	private ResultMsg resultmsg;
	private Runing runing;
	private SureCancelButton sureCancel;
	private GolbalUtil golbalutil;
	private GolbalView golbalView;
	private IrfidMsg irfidmsg;
	private GoBack goBack;
	
	private RFID_Device rfid;
	public RFID_Device getRfid(){
		if(rfid==null){
			rfid = new RFID_Device();
		}
		return rfid;
	}
	
	public IrfidMsg getIrfidmsg() {
		if(irfidmsg==null){
			irfidmsg = new IrfidMsg();
		}
		return irfidmsg;
	}
	
	public GoBack getGoBack() {
		if(goBack==null){
			goBack = new GoBack();
		}
		return goBack;
	}
	
	public Abnormal getAbnormal() {
		if(abnormal==null){
			abnormal = new Abnormal();
		}
		return abnormal;
	}
	public MenuShow getMenushow() {
		if(menushow==null){
			menushow = new MenuShow();	
		}
		return menushow;
	}
	public ResultMsg getResultmsg() {
		if(resultmsg==null){
			resultmsg = new ResultMsg();	
		}
		return resultmsg;
	}
	public Runing getRuning() {
		if(runing==null){
		  runing = new Runing();	
		}
		return runing;
	}
	public SureCancelButton getSureCancel() {
		if(sureCancel==null){
			sureCancel = new SureCancelButton();	
		}
		return sureCancel;
	}
	public GolbalUtil getGolbalutil() {
		if(golbalutil==null){
			golbalutil = new GolbalUtil();	
		}
		return golbalutil;
	}
	public GolbalView getGolbalView() {
		if(golbalView==null){
			golbalView = new GolbalView();	
		}
		return golbalView;
	}
	
	
	
	
}
