package com.openDams.utility;

import static java.lang.System.out;

import java.io.IOException;
import java.util.Properties;


public class PropertyFactory {
	
	/**
	 * carica le properties relative alla classe a cui appartiene un certo oggetto 
	 * @param obj - Object è l'instanza della classe di cui vogliamo ricavare le Properties
	 * @return	- oggetto Properties associato all'object
	 * @throws IOException
	 */
	public static Properties loadProperties(final Object obj) throws IOException{
		return loadProperties(obj.getClass());
	}
	
	/**
	 * carica le Properties relative ad una classe
	 * @param myClass - classe di cui caricare le Properties: ci aspettiamo
	 * 	nel classpath un file NomeClasse.properties
	 * @return - Properties della classe
	 */
	public static Properties loadProperties(final Class<?> myClass){
		final String fileName = new String(myClass.getSimpleName()+".properties");
		final Properties p = new Properties();
		try {
			p.load(myClass.getClassLoader().getResourceAsStream(fileName));
		} catch (Throwable t) {
			System.err.println("cannot find Properties file for class " + myClass);
		}
		return p;
	}
	
	
}
