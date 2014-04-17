package com.ablesky.im.test;

import org.junit.After;
import org.junit.Before;

public class BaseTest {
	@Before
	public void beforeTest() {
		println("-------²âÊÔ¿ªÊ¼--------");
	}
	
	@After
	public void afterTest() {
		println("-------²âÊÔ½áÊø--------");
	} 

	public void println(String str) {
		 System.out.println(str);
	}
}
