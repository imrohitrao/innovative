package com.digitald4.iis.model;

import com.digitald4.common.model.GeneralData;

public enum GenData {
	UserType(null,1), 
	UserType_Admin(UserType,1),
	UserType_Standard(UserType,2),
	ASS_CAT(null,2);
	
	private GenData group;
	private Integer inGroupId;
	private GeneralData instance;

	private GenData(GenData group, Integer inGroupId) {
		this.group = group;
		this.inGroupId = inGroupId;
	}
	
	public Integer getInGroupId() {
		return inGroupId;
	}
	
	public GeneralData get() {
		if(instance==null) {
			instance = GeneralData.getInstance(group==null?null:group.get(), inGroupId);
			if(instance==null)
				System.err.println("Missing General Data: "+this);
		}
		return instance;
	}
}