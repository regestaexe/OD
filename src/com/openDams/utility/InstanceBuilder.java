package com.openDams.utility;

import java.lang.reflect.Method;

public class InstanceBuilder {

    public static void buildObject(Object obj){
    	buildObject(obj,(String[])null);
    }
    public static void buildObject(Object obj,String... except){
    	try {
	            Method methods[] = obj.getClass().getDeclaredMethods();
	            for (int i = 0; i < methods.length; i++){
	            	Method method = methods[i];
	            	Class<?>[] params =	method.getParameterTypes();	            	
	            	if(params.length>0){
		            	for (int x = 0; x < params.length; x++){
		            		Class<?> paramClass = params[x];
		            		if(!paramClass.isPrimitive() && !paramClass.isInterface()){
		            			if(except!=null){
		            				boolean invoke = true;
		            				for (int y = 0; y < except.length; y++){
		            					System.out.println(method.getName());
		            					if(except[y].equalsIgnoreCase(method.getName())){
		            						invoke = false;
		            						break;
		            					}	
		            				}
		            				if(invoke && obj.getClass().getDeclaredMethod(method.getName().replaceAll("set","get")).invoke(obj)==null){		            					
		            					method.invoke(obj, paramClass.newInstance());
		            				}
		            			}else if(obj.getClass().getDeclaredMethod(method.getName().replaceAll("set","get")).invoke(obj)==null){
		            				method.invoke(obj, paramClass.newInstance());
		            			}
		            		}		            		      
		            	}
	            	}
	            }
	         }
	         catch (Throwable e) {
	            e.printStackTrace();
	         }
    }
    public static void main(String... strings){
    	
    	
    }
}
