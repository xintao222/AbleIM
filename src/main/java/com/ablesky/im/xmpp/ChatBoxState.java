package com.ablesky.im.xmpp;


public class ChatBoxState {
	public static final String L_0 = "0";
	public static final String L_1 = "1";
	public static final String L_2 = "2";
	public static final String R_0 = "0";
	public static final String R_1 = "1";

	public static final String TARGET_L = "l";
	public static final String TARGET_R = "r";
	public static final String TARGET_USERLIST_ADD = "add";
	public static final String TARGET_USERLIST_DELETE = "del";

	private String l_state;
	private String r_state;
	private String current_username;
	private String current_screenname;
//	private ArrayList<String> userlist;
//	
//	public void setUserlist(ArrayList<String> userlist){
//		this.userlist = userlist;
//	}
//	
//	public ArrayList<String> getUserlist(){
//		return this.userlist;
//	}

	public void setCurrent_username(String current_username) {
		this.current_username = current_username;
	}

	public String getCurrent_username() {
		return this.current_username;
	}

	public void setCurrent_screenname(String current_screenname) {
		this.current_screenname = current_screenname;
	}

	public String getCurrent_screenname() {
		return this.current_screenname;
	}

	public void setL_state(String l_state) {
		this.l_state = l_state;
	}

	public String getL_state() {
		return this.l_state;
	}

	public void setR_state(String r_state) {
		this.r_state = r_state;
	}

	public String getR_state() {
		return this.r_state;
	}

}
