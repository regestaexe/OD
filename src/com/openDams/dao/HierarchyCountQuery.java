package com.openDams.dao;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.openDams.bean.Records;

public class HierarchyCountQuery {
	private String query;
	private String queryToReturn;
	private Map<Integer, String> params;
	public String getQuery() {
		return query;
	}
	public Map<Integer, String> getParams() {
		return params;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public void setParams(Map<Integer, String> params) {
		this.params = params;
	}
	public String buildQuery(Records records){
		queryToReturn = query;
		for(Map.Entry<Integer, String> entry : params.entrySet()){
			 Class<? extends Records> aClass = records.getClass();
			 Method method = null;
			 try {
				 method = aClass.getDeclaredMethod("get"+StringUtils.capitalize(entry.getValue()), null);
				 String result = null;
				 if(method.getReturnType()==String.class){
					result = (String)method.invoke(records, null);
				 }else if(method.getReturnType()==Integer.class){
					result = Integer.toString((Integer)method.invoke(records, null));
				 }
				 replaceAt(entry.getKey(),result);
			 }catch (Exception nsme) {
			     nsme.printStackTrace();
			 }
		}
		//System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"+queryToReturn+">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		return queryToReturn;
	}
	private void replaceAt(int startIndex,String value){
		 Pattern p = Pattern.compile("\\?");
		 Matcher m = p.matcher(queryToReturn);
		 String output;
		 if (m.find(startIndex)) {
		     StringBuffer sb = new StringBuffer();
		     m.appendReplacement(sb, value);
		     m.appendTail(sb);
		     output = sb.toString();	     
		 } else {
			 output = queryToReturn;
		 }
		 queryToReturn = output;
	}
	public static void main(String[] args) {
		HierarchyCountQuery hierarchyCountQuery = new HierarchyCountQuery();
		Records records = new Records();
		records.setIdRecord(1);
		records.setDepth(5);
		Map<Integer,String> params = new HashMap<Integer, String>();
		params.put(1, "idRecord");
		params.put(2, "depth");
		hierarchyCountQuery.setParams(params);
		hierarchyCountQuery.setQuery("select pippo from tabella where pippo=? and topolino=?");
		//System.out.println(hierarchyCountQuery.buildQuery(records));
	}
}
