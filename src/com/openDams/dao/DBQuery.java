package com.openDams.dao;

public class DBQuery {
    public static final int INSERT = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;
    protected int operation = 0;
    protected Object genericObject = null;
    public DBQuery(int operation,Object genericObject){
    	this.operation = operation;
    	this.genericObject = genericObject;
    }
}
