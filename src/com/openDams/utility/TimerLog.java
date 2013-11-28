package com.openDams.utility;

public class TimerLog {

	// teniamo traccia della memoria libera e dei millisecondi di running
	private long now = 0;
	private long mem = 0;

	/**
	 * costruttore di default: all'avvio reset del tempo e della memoria
	 */
	public TimerLog(){
		this.now = System.currentTimeMillis();
		this.mem = Runtime.getRuntime().freeMemory();
	}
	
	/**
	 *	tracciamo il tempo trascoso dall'avvio o dal reset 
	 * @return
	 */
	public float logTime() {
		final long now = System.currentTimeMillis(); 
		return (now - this.now)/1000.0F;
	}

	/**
	 * tracciamo la memoria utilizzata dall'avvio o dal reset
	 * @return
	 */
	public float logMem() {
		final long mem = Runtime.getRuntime().freeMemory(); 
		return (mem - this.mem)/1024.0F;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		final StringBuffer sb = new StringBuffer();
		sb.append(String.format("time: %10.02f s", this.logTime()));
		sb.append(String.format("\tmem: %10.02f kb", this.logMem()));
		sb.append("\n");
		return sb.toString();
	}
	
	/*
	 * main per i TEST: genero un po' di concatenazioni per poter stampare qualcosa
	 */
	public static void main(String[] args) {
		final TimerLog log = new TimerLog();
		System.out.println("log: "+log);
		String test = "TEST - ";
		for(int i=2000;i>0;i--){
			test += i;
		}
		System.out.println("log: "+log);		
	}

}
