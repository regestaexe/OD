package com.openDams.search_builder.utility;

public class ArchiveUtils {
   public static String getExcludeSLQFields(String excludeString){
	   String result = "";
	   if(excludeString!=null && excludeString.indexOf(",")!=-1){
		   String[] toExclude = excludeString.split(",");
		   for (int i = 0; i < toExclude.length; i++) {
			result+=" and not archives.id_archive="+toExclude[i];
		   }
	   }
	   return result;
   }
}
