/*
 * Copyright 2006-2008 AbleSky, Inc. All rights reserved. This program is an
 * unpublished work fully protected by the United States, P.R. China and
 * International copyright laws and is considered a trade secret belonging to
 * AbleSky, Inc. It is not to be divulged or used by parties who have not
 * received written authorization from AbleSky, Inc. AbleSky, Inc. 539 Chiquita
 * Ave Mountain View, CA 94041, USA http://www.ablesky.com Email:
 * support@ablesky.com Copyright 2006-2008 AbleSky, Inc. All rights reserved.
 */
package com.ablesky.im.util;

public class CommonStaticConst {
	public static final String TOTALCOUNT = "totalCount";
	public static final String LIST = "list";

	public static final String ALL = "all";
	public static final String DEFAULTPRICETYPE = "1";// free

	public static final String PAYMENTSERVICEURL = "http://127.0.0.1:8080/ableskypayment/services/PaymentService";

	public static final String COURSEID = "courseId";
	public static final String CURRENYTYPE = "currencyType";

	public static final String GUEST = "guest";
	public static final String ADMIN = "admin";

	public static final String CURRENCY_USD = "USD";
	public static final String CURRENCY_RMB = "RMB";
	public static final String TOTALEARNINGRMB = "earningRmb";
	public static final String TOTALEARNINGUSD = "earningUsd";
	public static final String TOTALCONSUMRMB = "consumRmb";
	public static final String TOTALCONSUMUSD = "consumUsd";

	// 首页高级编辑
	public static final String FILETYPE_JSP = "jsp";

	// 发布课程时文件类型
	public static final String COURSE_FILETYPE_VIDEO = "avi,mov,wmv,mpg,mp4,asf,flv,rmvb,rm,ram,mpeg,mpe,dat,swf,mts";
	public static final String COURSE_FILETYPE_DOCUMENT = "pdf,avi,mov,wmv,mpg,mp4,asf,flv,rmvb,rm,ram,mpeg,mpe,dat,swf,mts";
	public static final String COURSE_FILETYPE_ALL = "mp3,wma,pdf,avi,mov,wmv,mpg,mp4,asf,flv,rmvb,rm,ram,mpeg,mpe,dat,swf,mts";

	/**
	 * ActionType for PaymentTransaction 以“_”结尾的需要加上相应的id
	 */
	public static final String ACTIONTYPE_COURSE = "courseDownloadTrade_";
	public static final String ACTIONTYPE_COURSE_GROUPON = "courseDownloadTradeGroupon_";
	public static final String ACTIONTYPE_COURSE_SUPERIOR = "superiorCourse_";
	public static final String ACTIONTYPE_LIVESESSION = "livesessionRequestAccess_";
	public static final String ACTIONTYPE_MOSTWANTED = "mostwantedRequestAccess_";
	public static final String ACTIONTYPE_ADDFUNDS = "addFundsToDescendant";
	public static final String ACTIONTYPE_TRANSFERFUNDS = "transferFundsToOrg";
	public static final String ACTIONTYPE_CASHWITHDRAW = "cashWithdraw";
	public static final String ACTIONTYPE_ORGAUTHORIZATION = "orgAuthorization";
	public static final String ACTIONTYPE_BONUS_INVITE = "inviteBonus";
	public static final String ACTIONTYPE_ASCLICK_AUTHORIZATION = "asClickAuthorization";
	public static final String ACTIONTYPE_PAYMENTCARD_MONEY_HANDLINGCHARGE = "paymentCard_money_";
	public static final String ACTIONTYPE_PAYMENTCARD_COURSE_HANDLINGCHARGE = "paymentCard_course_";
	public static final String ACTIONTYPE_PAYMENTCARD_MISSEDCOURSE_HANDLINGCHARGE = "paymentCard_missedcourse_";

	public static final String SERVICETYPE_TOTALCOURSE = "totalCourse";
	public static final String SERVICETYPE_PACKAGE = "package";
	public static final String SERVICETYPE_COURSE = "course";
	public static final String SERVICETYPE_PRESELL_PACKAGE = "presell_package";
	public static final String SERVICETYPE_COURSE_AND_PACKAGE = "course_and_package";
	public static final String SERVICETYPE_PACKAGE_AND_PRESELL_PACKAGE = "package_and_presell_package";
	public static final String COURSE_TYPE_PRESELL_PACKAGE = "presell_package";
	public static final String SERVICETYPE_LIVESESSION = "livesession";
	public static final String SERVICETYPE_MOSTWANTED = "mostwanted";
	public static final String SERVICETYPE_PPROJECT = "project";

	public static final String CHARGETYPE_FREE = "free";
	public static final String CHARGETYPE_FIX = "fix";
	public static final String CHARGETYPE_HOURLY = "hourly";

	public static int CHARGETYPE_All_INT = 0;
	public static int CHARGETYPE_HOURLY_INT = 1;
	public static int CHARGETYPE_FIX_INT = 2;
	public static int CHARGETYPE_FREE_INT = 3;

	public static String VISITOR_TYPE_OWNER = "owner";
	public static String VISITOR_TYPE_ADMIN = "admin";
	public static String VISITOR_TYPE_FREE_COURSE = "freeCourse";
	public static String VISITOR_TYPE_FREEAUTORIZATION = "freeAuthorization";
	public static String VISITOR_TYPE_BUYER = "buyer";
	public static String VISITOR_TYPE_BUYER_OF_PARENTCOURSE = "buyerOfParentCourse";
	public static String VISITOR_TYPE_ORGNIZATIONADMIN = "organizationAdmin";

	// 新增course, 影响entityCnt,rmbStatistic,usdStatistic. 或者livesession,
	// 影响entityCnt
	public static String OPERATION_ADD = "add";
	// 删除course, 影响entityCnt,rmbStatistic,usdStatistic. 或者livesession,
	// 影响entityCnt
	public static String OPERATION_MINUS = "minus";
	// 下架course, 影响entityCnt, rmbStatistic, usdStatistic, disabledEntityCnt,
	// disabledRmbStatistic. 或者livesession, 影响entityCnt, disabledEntityCnt
	public static String OPERATION_DISABLE = "disable";
	// 上架course, 影响entityCnt, rmbStatistic, usdStatistic, disabledEntityCnt,
	// disabledRmbStatistic. 或者livesession, 影响entityCnt, disabledEntityCnt
	public static String OPERATION_ENABLE = "enable";

	// 只增下架的
	public static String OPERATION_UPDATE_DISABLE = "update_disable";
	// 只减下架的
	public static String OPERATION_MINUS_DISABLE = "minus_disable";

	public static final String PAYMENT_TYPE_FLAG_ADDFUNDS = "af";
	public static final String PAYMENT_TYPE_FLAG_BUYASCLICK = "bac";

	// 排序
	public static final String SORT_BY_TIME = "time";
	public static final String SORT_BY_HOT = "hot";
	public static final String SORT_BY_PRICE = "price";
	public static final String SORT_BY_VIEW = "view";
	public static final String SORT_BY_FEEDBACK = "feedback";
	public static final String SORT_BY_DEADLINE = "deadline";
	public static final String SORT_BY_PRICERANGE = "pricerange";
	public static final String SORT_BY_INCOME = "income";
	public static final String SORT_BY_CONTENT = "content";
	public static final String SORT_BY_CREDIT = "credit";

	public static final String DIR_BY_DESC = "desc";
	public static final String DIR_BY_ASC = "asc";

	// 评价类型
	public static final String FEEDBACK_TYPE_ALL = "all";
	public static final String FEEDBACK_TYPE_SELLER = "seller";
	public static final String FEEDBACK_TYPE_BUYER = "buyer";

	// 用户类型
	public static final String ACCOUNT_ALL = "all";
	public static final String ACCOUNT_BEST = "best";
	public static final String ACCOUNT_ORG = "org";
	public static final String ACCOUNT_AVERAGE = "average";
	public static final String ACCOUNT_NOTORG = "notorg";

	// 听课中心类型
	public static String COURSE_CENTER_TYPE = "total";
	public static String COURSE_CENTER_TYPE_BUY = "fromBuy";
	public static String COURSE_CENTER_TYPE_CARD = "fromCourseCard";
	public static String COURSE_CENTER_TYPE_MISSEDCOURSECARD = "fromMissedCourseCard";
	public static String COURSE_CENTER_TYPE_OPENED = "fromOpened";

	// 创建和导�?
	public static String IMPORT = "import";
	public static String EXPORT = "export";
	public static String CREATE = "create";

	// cookie
	public static String COOKIE_AID = "AUSS";
	public static String COOKIE_SHOW_ABLEMALL = "ASMALL";
	public static String REMEMBER_ME = "RM";
	public static String REMEMBER_ACCOUNT = "SA";
	public static String AUTOLOGIN = "AUTOLOGIN";
	public static String ACEGI_SECURITY_HASHED_REMEMBER_ME_COOKIE = "ACEGI_SECURITY_HASHED_REMEMBER_ME_COOKIE";
	public static long TOKEN_VALIDITY_SECONDS = 1209600; // 14 days
															// 跟acegi源码保持一致
	// idc cookie lifecycle
	public static int IDC_COOKIE_LIFECYCLE = 365 * 24 * 3600;
	public static int IDCS_TEST_COOKIE_LIFECYCLE = 24 * 3600;
	
	//shopping cart cookie lifecycle
	public static int SHOPPING_CART_COOKIE_LIFECYCLE = 365 * 24 * 3600;

	/**
	 * notify client type{pay, comment, charge} transtype{course, livesession,
	 * mostwanted}
	 */
	public static String WEB_NOTIFY_CLIENT_TYPE_PAY = "pay";
	public static String WEB_NOTIFY_CLIENT_TYPE_COMMENT = "comment";
	public static String WEB_NOTIFY_CLIENT_TYPE_CHARGE = "charge";
	public static String WEB_NOTIFY_CLIENT_TYPE_POSTREPAT = "postRepay";
	public static String WEB_NOTIFY_CLIENT_TYPE_ACCEPTREPAY = "acceptRepay";
	public static String WEB_NOTIFY_CLIENT_TYPE_REJECTREPAY = "rejectRepay";
	public static String WEB_NOTIFY_CLIENT_TYPE_GIVEUPREPAY = "giveupRepay";
	public static String WEB_NOTIFY_CLIENT_TYPE_ARBITRATEREPAY = "arbitrateRepay";

	/**
	 * 充值类型
	 */
	public static String ADD_FUNDS_BY_PAYPAL = "paypal";
	public static String ADD_FUNDS_BY_PAYPALCREDIT = "paypal_credit";
	public static String ADD_FUNDS_BY_ICBC = "icbc";
	public static String ADD_FUNDS_BY_CMBC = "cmbc";
	public static String ADD_FUNDS_BY_CMBCUP = "cmbcup";
	public static String ADD_FUNDS_BY_CMB = "cmb";
	public static String ADD_FUNDS_BY_ZHIFUBAO = "zhifubao";
	public static String ADD_FUNDS_BY_PREPARED = "prepaid_card";

	/**
	 * 充值方式
	 */
	public static String ADD_FUNDS_BY_EBANK = "ebank";
	public static String ADD_FUNDS_BY_THIRDPARTY = "thirdparty";

	/**
	 * 充值渠道
	 */
	public static String ADD_FUNDS_BY_YEEPAY_ICBC_NET = "ICBC-NET";
	public static String ADD_FUNDS_BY_YEEPAY_MEMBER_NET = "1000000-NET";
	public static String ADD_FUNDS_BY_ALIPAY = "zhifubao";
	public static String ADD_FUNDS_BY_MOBILE = "mobile";

	/**
	 * 付费方式
	 */
	public static String PAYMENT_TYPE_RMB = "RMB";
	public static String PAYMENT_TYPE_USD = "USD";
	public static String PAYMENT_TYPE_ORG = "ORG";
	public static String PAYMENT_TYPE_EBANK = "EBANK";
	public static String PAYMENT_TYPE_THIRDPARTY = "THIRDPARTY";

	/**
	 * 卡组相关
	 */
	public static String CARD_NO = "cardNo";
	public static String CARD_PASSWORD = "cardPwd";

	/**
	 * 机构目录树缓存相关
	 */
	public static String CACHE_CATEGORYTREE_OPTION_GET = "get";
	public static String CACHE_CATEGORYTREE_OPTION_UPDATE = "update";
	
	/**
	 * 学习课程流水号cookie key前缀
	 */
	public static String STUDY_COURSE_FLOW_UUID_COOKIE_KEY_PREFIX = "SCFUCKP_";
	
	/**
	 * sql语句in最大常数量限制
	 */
	public static int SQL_IN_MAX_LIMIT = 500;
}
