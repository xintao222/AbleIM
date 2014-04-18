package com.ablesky.im.test;

import org.junit.After;
import org.junit.Before;

public class BaseTest {
	@Before
	public void beforeTest() {
		println("-------测试开始--------");
	}
	
	@After
	public void afterTest() {
		println("-------测试结束--------");
	} 

	public void println(String str) {
		 System.out.println(str);
	}
	
	public void print(String str) {
		 System.out.print(str);
	}
}
