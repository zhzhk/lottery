package org.web.entity;

import javax.persistence.Transient;

public class PageEntity {
	@Transient 
	public static Integer page =1;
	
	@Transient
	public static Integer rows = 10;

//	public Integer getPage() {
//		return page;
//	}
//
//	public void setPage(Integer page) {
//		this.page = page;
//	}
//
//	public Integer getRows() {
//		return rows;
//	}
//
//	public void setRows(Integer rows) {
//		this.rows = rows;
//	}
	
	
}
