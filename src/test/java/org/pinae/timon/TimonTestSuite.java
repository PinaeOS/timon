package org.pinae.timon;

import java.util.Enumeration;

import org.apache.log4j.Logger;

import junit.framework.Test;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * Timon测试集合
 * 
 * @author Huiyugeng
 *
 */
public class TimonTestSuite {
	
	private static Logger logger = Logger.getLogger(TimonTestSuite.class);
	
	public static Test suite() {
		TestSuite suite = new TestSuite("Timon-Test");

		return suite;
	}
	
	public static void main(String arg[]){
		TestResult result = new TestResult();
		suite().run(result);
		
		logger.info(result.runCount() + " tests execute");
		
		if(result.wasSuccessful()){
			logger.info("All Test Pass!");
		}else{
			logger.error("Test No Pass");
			
			Enumeration<TestFailure> failures = result.failures();
			if(failures.hasMoreElements()){
				TestFailure failure = failures.nextElement();
				logger.error("Failure:" + failure.exceptionMessage());
			}
			
			Enumeration<TestFailure> errors = result.errors();
			if(errors.hasMoreElements()){
				TestFailure error = errors.nextElement();
				logger.error("Error: " + error.exceptionMessage());
			}
		}
	}
}
