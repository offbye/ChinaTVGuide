package com.offbye.chinatvguide.util;

public class AppException extends Exception {
	private static final long serialVersionUID = 1L;
    private int errorCode;
    private String[] params;
    
    public AppException(int errorCode, String... params)
    {
        this.errorCode = errorCode;
        this.params = params;
    }

}
