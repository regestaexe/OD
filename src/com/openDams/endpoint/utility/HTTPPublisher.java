package com.openDams.endpoint.utility;


public class HTTPPublisher extends EndpointPostAction{
	
	public String url,user,password;
	
	public void afterAdd(){
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>HTTPPublisher.afterAdd()");
		System.out.println(toString());
	}
	public void afterUpdate(){
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>HTTPPublisher.afterUpdate()");
		System.out.println(toString());
	}
	public void afterDelete(){
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>HTTPPublisher.afterDelete()");
		System.out.println(toString());
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public String toString() {
		return "HTTPPublisher [url=" + url + ", user=" + user + ", password="
				+ password + ", endpointRecordContainerList="
				+ endpointRecordContainerList + "]";
	}
}
