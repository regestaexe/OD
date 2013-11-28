package com.openDams.endpoint.utility;

import java.util.Map;

public class ReflectClass {
	public String invockingClass =""; 
	public String invockingMethod ="";
	public boolean multiInvocking = false;
	public Map<String, Object> args; 
	public String getInvockingClass() {
		return invockingClass;
	}
	public void setInvockingClass(String invockingClass) {
		this.invockingClass = invockingClass;
	}
	public String getInvockingMethod() {
		return invockingMethod;
	}
	public void setInvockingMethod(String invockingMethod) {
		this.invockingMethod = invockingMethod;
	}
	public Map<String, Object> getArgs() {
		return args;
	}
	public void setArgs(Map<String, Object> args) {
		this.args = args;
	}
	public boolean isMultiInvocking() {
		return multiInvocking;
	}
	public void setMultiInvocking(boolean multiInvocking) {
		this.multiInvocking = multiInvocking;
	}
	@Override
	public String toString() {
		return "ReflectClass [invockingClass=" + invockingClass
				+ ", invockingMethod=" + invockingMethod + ", multiInvocking="
				+ multiInvocking + ", args=" + args + "]";
	}
	
}
