package com.entity;

import java.io.Serializable;
import java.util.Date;


/**
 * 查库服务明细model， 对应表T_BANKINVENTORY_DETAIL
 * @author zhouKai
 *
 */
public class LookStorageDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1355131883939259597L;
	
	public static final String FAILURE = "0";
	public static final String SUCCESS = "1";
	public static final String CX = "01";
	public static final String KX = "02";
	public static final String ZZX = "03";

	private Integer id; 				// 流水号
	private String taskId; 			// 任务单号
	private String boxType; 		// 查库类型。01：钞箱，02：款箱，03：周转箱。
	private String boxNum; 			// 箱子编号
	private String state; 			// 清点状态。0：失败，1：成功。
	private Date operateTime; // 操作时间

	public LookStorageDetail(){
		
	}
	
	public LookStorageDetail(LookStorageDetail detail){
		this.id = detail.getId();
		this.taskId = detail.getTaskId();
		this.boxType = detail.getBoxType();
		this.boxNum = detail.getBoxNum();
		this.state = detail.state;
		this.operateTime = detail.operateTime;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getBoxType() {
		return boxType;
	}

	public void setBoxType(String boxType) {
		this.boxType = boxType;
	}

	public String getBoxNum() {
		return boxNum;
	}

	public void setBoxNum(String boxNum) {
		this.boxNum = boxNum;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	@Override
	public String toString() {
		return "LookStorageDetail [id=" + id + ", taskId=" + taskId + ", boxType=" + boxType + ", boxNum=" + boxNum
				+ ", state=" + state + ", operateTime=" + operateTime + "]";
	}

}
