package Test;

import common.java.httpServer.booter;
import common.java.nlogger.nlogger;

public class Test {
	 public static void main(String[] args) {
	    	
	        booter booter = new booter();
	        try {
	            System.out.println("Hotel");
	            System.setProperty("AppName", "Hotel");
	            booter.start(1006);
	        } catch (Exception e) {
	            nlogger.logout(e);
	        }

	       
	    }
}
