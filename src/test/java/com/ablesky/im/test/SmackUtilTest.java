package com.ablesky.im.test;

import static org.junit.Assert.*; 

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.junit.Test;

import com.ablesky.im.smack.SmackUtil;

public class SmackUtilTest extends BaseTest{
	private static SmackUtil smackUtil = new SmackUtil();	
	
	@Test
	public void testGetOFConnection() {
		 XMPPConnection connection = smackUtil.getOFConnection();
		 println("获取的链接id："+connection.getConnectionID());
		 assertNotNull("获取openfire的链接出错", connection);
	}
	
	
	
}
