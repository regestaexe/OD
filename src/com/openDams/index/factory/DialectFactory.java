package com.openDams.index.factory;

import org.apache.lucene.store.jdbc.dialect.Dialect;

public class DialectFactory {
     public static Dialect getDialect(String dialect) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
    	 Class<?> c = Class.forName(dialect);
    	 return (Dialect)c.newInstance();
     }
}
