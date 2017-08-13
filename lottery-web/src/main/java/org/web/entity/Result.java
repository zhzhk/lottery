package org.web.entity;

public class Result<T> {
	//提示码
	private Integer code;
	//提示信息
	private String msg;
	//返回数据
	private T data;
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getData() {
		return data;
	}
	public void setData(T object) {
		this.data = object;
	}
}
