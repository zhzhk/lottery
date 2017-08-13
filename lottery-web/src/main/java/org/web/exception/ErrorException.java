package org.web.exception;

import org.web.enums.ResultEnum;

public class ErrorException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer code;
	public ErrorException(ResultEnum resultEnum){
		super(resultEnum.getMsg());
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
}
