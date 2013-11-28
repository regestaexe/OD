package com.openDams.utility;

import java.util.HashMap;

public class ArchiveColorFactory {
    private static HashMap<Integer,String> archiveColors = null;
    
    public static String getHTMLArchiveColor(int idArchive){
    	if(archiveColors==null){
    		archiveColors = new HashMap<Integer, String>();
    		archiveColors.put(8,"#fec2ff");
    		archiveColors.put(9,"#ca9afe");
    		archiveColors.put(5,"#fffd9d");
    		archiveColors.put(2,"#ffffff");
    		archiveColors.put(7,"#fffd9d");
    		archiveColors.put(6,"#fffd9d");
    		archiveColors.put(4,"#ccffff");
    		archiveColors.put(10,"#ccffff");
    		archiveColors.put(11,"#e6e6e6");
    		archiveColors.put(15,"#e6e6e6");
    		archiveColors.put(14,"#e6e6e6");
    		archiveColors.put(12,"#fffd9d");
    		archiveColors.put(13,"#ffffff");
    		archiveColors.put(1,"#eeeff1");
    		archiveColors.put(16,"#eeeff1");
    		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><<<ArchiveColorFactoryArchiveColorFactoryArchiveColorFactory");
    	}
    	if(archiveColors.get(idArchive)!=null){
    		return archiveColors.get(idArchive);
    	}else{
    		return "#ffffff";
    	}
    	
    }
    public static void main(String[] args) {
		
	}
}
