package com.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 查库服务任务单model，对应表T_BANKINVENTORY
 * @author zhouKai
 * 
 */
public class LookStorage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id; 					// 任务单号
	private String lookedCorpId; 		// 查询机构
	private String checkerOne; 			// 查询人员1
	private String checkerTwo; 			// 查询人员2
	private String createCorpId; 		// 任务单的创建机构
	private String createPerson;		// 创建人员
	private String state; 				// 任务党状态：01:待审核,02:已审核,03:清点中,04:清点完
	private Date operateTime; 		// 操作时间
	
	public LookStorage() {
		// TODO Auto-generated constructor stub
	}

	public LookStorage(String id, String lookedCorpId, String checkerOne, String checkerTwo, String createCorpId,
			String createPerson, String state, Date operateTime) {
		super();
		this.id = id;
		this.lookedCorpId = lookedCorpId;
		this.checkerOne = checkerOne;
		this.checkerTwo = checkerTwo;
		this.createCorpId = createCorpId;
		this.createPerson = createPerson;
		this.state = state;
		this.operateTime = operateTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLookedCorpId() {
		return lookedCorpId;
	}

	public void setLookedCorpId(String lookedCorpId) {
		this.lookedCorpId = lookedCorpId;
	}

	public String getCheckerOne() {
		return checkerOne;
	}

	public void setCheckerOne(String checkerOne) {
		this.checkerOne = checkerOne;
	}

	public String getCheckerTwo() {
		return checkerTwo;
	}

	public void setCheckerTwo(String checkerTwo) {
		this.checkerTwo = checkerTwo;
	}

	public String getCreateCorpId() {
		return createCorpId;
	}

	public void setCreateCorpId(String createCorpId) {
		this.createCorpId = createCorpId;
	}

	public String getCreatePerson() {
		return createPerson;
	}

	public void setCreatePerson(String createPerson) {
		this.createPerson = createPerson;
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
		return "LookStorage [id=" + id + ", lookedCorpId=" + lookedCorpId + ", checkerOne=" + checkerOne
				+ ", checkerTwo=" + checkerTwo + ", createCorpId=" + createCorpId + ", createPerson=" + createPerson
				+ ", state=" + state + ", operateTime=" + operateTime + "]";
	}

	
	
	
}
