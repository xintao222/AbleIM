package com.ablesky.im.cache;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import net.rubyeye.xmemcached.AbleskyXMemcachedClient;
import net.rubyeye.xmemcached.AbleskyXMemcachedClientBuilder;
import net.rubyeye.xmemcached.GetsResponse;
import net.rubyeye.xmemcached.command.TextCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;
import net.rubyeye.xmemcached.transcoders.TokyoTyrantTranscoder;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ablesky.im.util.AbleskyImConfig;

public class XmemcachedUtil {
	/**
	 * memcachedServer
	 */
	protected static AbleskyXMemcachedClient memcachedClient = null;
	private static boolean enabled = true;
	/**
	 * tokyoTyrantServer
	 */
	protected static AbleskyXMemcachedClient tokyoTyrantMemcachedClient = null;
	private static boolean enabledTokyoTyrant = true;

	/**
	 * 每一个服务器重试的次数，当成功时，执行0操作
	 */
	public static Map<String, AtomicInteger> try_times = new HashMap<String, AtomicInteger>();

	/**
	 * 公共变量
	 */
	private static final String ZH = "_zh";
	private static final String EN = "_en";

	public static final String SERVER_MEMCACHED = "memcached";
	public static final String SERVER_TTSERVER = "tokyotyrant";

	public static final Log log = LogFactory.getLog(XmemcachedUtil.class.getName());

	/**
	 * 初始化
	 */
	public static AbleskyXMemcachedClient getMemcachedClient(String servers, int serverCnt, String server, String[] weights) {
		try {
			// use text protocol by default
			AbleskyXMemcachedClientBuilder builder;
			
			if (weights.length == 1 || weights.length != serverCnt) {
				builder = new AbleskyXMemcachedClientBuilder(AddrUtil.getAddresses(servers));
			} else {
				int[] weight = new int[weights.length];
				for (int i = 0; i < weights.length; i++) {
					weight[i] = Integer.parseInt(weights[i]);
				}
				builder = new AbleskyXMemcachedClientBuilder(AddrUtil.getAddresses(servers), weight);
			}
			if (server.equalsIgnoreCase(SERVER_TTSERVER)) {
				builder.setTranscoder(new TokyoTyrantTranscoder());
			} else {
				builder.setTranscoder(new SerializingTranscoder());
			}
			builder.setSessionLocator(new KetamaMemcachedSessionLocator());
			builder.setCommandFactory(new TextCommandFactory());
			builder.setConnectionPoolSize(Integer.valueOf(AbleskyImConfig.CONNECTION_POOLSIZE));
			return builder.build();
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	static {
		String[] serverArray;
		String[] weights;
		/**
		 * memcachedServer
		 */
		if (AbleskyImConfig.MEMCACHED_SERVER == null || AbleskyImConfig.MEMCACHED_SERVER.equals("")) {
			enabled = false;
		} else {
			serverArray = AbleskyImConfig.MEMCACHED_SERVER.split(","); // 服务器列表
			weights = AbleskyImConfig.MEMCACHED_WEIGHT.split(",");
			StringBuffer servers = new StringBuffer();
			for (String server : serverArray) {
				servers.append(server + " ");
				try_times.put("/" + server, new AtomicInteger(0));// 初始化失败次数
			}
			memcachedClient = getMemcachedClient(servers.toString(), serverArray.length, SERVER_MEMCACHED, weights);
			memcachedClient.setOpTimeout(5000L);// 操作超时
		}
		/**
		 * tokyotyrantServer
		 */
		if (AbleskyImConfig.TOKYO_TYRANT_SERVER == null || AbleskyImConfig.TOKYO_TYRANT_SERVER.equals("")) {
			enabledTokyoTyrant = false;
		} else {
			serverArray = AbleskyImConfig.TOKYO_TYRANT_SERVER.split(","); // 服务器列表
			weights = AbleskyImConfig.TTSERVER_WEIGHT.split(",");
			StringBuffer tokyoTyrantServers = new StringBuffer();
			for (String server : serverArray) {
				tokyoTyrantServers.append(server + " ");
				try_times.put("/" + server, new AtomicInteger(0));// 初始化失败次数
			}
			tokyoTyrantMemcachedClient = getMemcachedClient(tokyoTyrantServers.toString(), serverArray.length, SERVER_TTSERVER, weights);
			tokyoTyrantMemcachedClient.setOpTimeout(5000L);// 操作超时
		}

	}

	/**
	 * 单态模式
	 */
	private static XmemcachedUtil instance = null;

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new XmemcachedUtil();
		}
	}

	public static XmemcachedUtil getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	/**
	 * 判断是否开启
	 * 
	 * @return
	 */
	public static boolean isStarted() {
		if (enabled == false || memcachedClient == null || memcachedClient.isShutdown() == true) {
			return false;
		}
		return true;
	}

	public static boolean isStartedForTTServer() {
		if (enabledTokyoTyrant == false || tokyoTyrantMemcachedClient == null || tokyoTyrantMemcachedClient.isShutdown() == true) {
			return false;
		}
		return true;
	}

	/**
	 * 存储指令
	 * 
	 * @param key
	 *            存储的key名称
	 * @param value
	 *            实际存储的数据，可以是任意的java可序列化类型
	 * @param expire
	 *            时间（单位毫秒）
	 * @param locale
	 *            国际化 *
	 * @param useTokyoTyrant
	 *            服务器类型选择
	 * @return
	 */
	public boolean set(String key, Object value, long expire) {
		return this.set(key, value, expire, null);
	}

	public boolean set(String key, Object value, long expire, Locale locale) {
		return this.set(key, value, expire, locale, false);
	}

	public boolean set(String key, Object value, long expire, Locale locale, boolean useTokyoTyrant) {
		if (value == null || value.equals("")) {
			return false;
		}
		if (locale == null) {
			locale = Locale.CHINA;
		}
		if (locale.equals(Locale.CHINA)) {
			key = key + ZH;
		} else {
			key = key + EN;
		}
		InetSocketAddress address = null;
		boolean ret = false;
		int exp = new Long(expire / 1000).intValue();
		try {
			if (useTokyoTyrant) {
				if (enabledTokyoTyrant == false) {
					return false;
				}
				address = tokyoTyrantMemcachedClient.findServerByKey(key);
				ret = tokyoTyrantMemcachedClient.set(key, exp, value);
			} else {
				if (enabled == false) {
					return false;
				}
				address = memcachedClient.findServerByKey(key);
				ret = memcachedClient.set(key, exp, value);
			}

		} catch (TimeoutException e) {
			log.error("Set Command is failed! Error is TimeoutException, message is " + e.getMessage() + " and key is " + key);
			if (address != null) {
				this.addAndcheckIfRemoveServer(address);
			}
		} catch (InterruptedException e) {
			log.error("Set Command is failed! Error is InterruptedException, message is " + e.getMessage() + " and key is " + key);
		} catch (MemcachedException e) {
			log.error("Set Command is failed! Error is MemcachedException, message is " + e.getMessage() + " and key is " + key);
		}
		if (address != null) {
			initTryTimes(address);
		}
		return ret;
	}

	/**
	 * 安全存储数据
	 * 
	 * @param key
	 *            存储的key名称
	 * @param value
	 *            实际存储的数据，可以是任意的java可序列化类型
	 * @param expire
	 *            时间（单位毫秒）
	 * @param locale
	 *            国际化
	 * @param useTokyoTyrant
	 *            是否使用TokyoTyrant
	 * @return
	 */
	public boolean cas(String key, Object value, long expire, Long cas, Locale locale, boolean useTokyoTyrant) {
		if (value == null || value.equals("")) {
			return false;
		}
		if (locale == null) {
			locale = Locale.CHINA;
		}
		if (locale.equals(Locale.CHINA)) {
			key = key + ZH;
		} else {
			key = key + EN;
		}
		InetSocketAddress address = memcachedClient.findServerByKey(key);
		boolean ret = false;
		int exp = new Long(expire / 1000).intValue();
		try {
			if (useTokyoTyrant) {
				if (enabledTokyoTyrant == false) {
					return false;
				}
				ret = tokyoTyrantMemcachedClient.cas(key, exp, value, cas);
			} else {
				if (enabled == false) {
					return false;
				}
				ret = memcachedClient.cas(key, exp, value, cas);
			}

		} catch (TimeoutException e) {
			log.error("Cas Command is failed! Error is TimeoutException, message is " + e.getMessage() + " and key is " + key);
			if (address != null) {
				this.addAndcheckIfRemoveServer(address);
			}
		} catch (InterruptedException e) {
			log.error("Cas Command is failed! Error is InterruptedException, message is " + e.getMessage() + " and key is " + key);
		} catch (MemcachedException e) {
			log.error("Cas Command is failed! Error is MemcachedException, message is " + e.getMessage() + " and key is " + key);
		}
		if (address != null) {
			initTryTimes(address);
		}
		return ret;
	}

	/**
	 * 获取指令
	 * 
	 * @param key
	 *            获取的key名称
	 * @param locale
	 *            国际化
	 * @param useTokyoTyrant
	 *            服务器选择
	 * @return
	 */
	public Object get(String key) {
		return this.get(key, null);
	}

	public Object get(String key, Locale locale) {
		return this.get(key, locale, false);
	}

	public Object get(String key, Locale locale, boolean useTokyoTyrant) {
		if (locale == null) {
			locale = Locale.CHINA;
		}
		if (locale.equals(Locale.CHINA)) {
			key = key + ZH;
		} else {
			key = key + EN;
		}
		InetSocketAddress address = null;
		Object object = null;
		try {
			if (useTokyoTyrant) {
				if (enabledTokyoTyrant == false) {
					return null;
				}
				address = tokyoTyrantMemcachedClient.findServerByKey(key);
				object = tokyoTyrantMemcachedClient.get(key);
			} else {
				if (enabled == false) {
					return null;
				}
				address = memcachedClient.findServerByKey(key);
				object = memcachedClient.get(key);
			}

		} catch (TimeoutException e) {
			log.error("Get Command is failed! Error is TimeoutException, message is " + e.getMessage() + " and key is " + key);
			if (address != null) {
				this.addAndcheckIfRemoveServer(address);
			}
		} catch (InterruptedException e) {
			log.error("Get Command is failed! Error is InterruptedException, message is " + e.getMessage() + " and key is " + key);
		} catch (MemcachedException e) {
			log.error("Get Command is failed! Error is MemcachedException, message is " + e.getMessage() + " and key is " + key);
		}
		if (address != null) {
			initTryTimes(address);
		}
		return object;
	}

	/**
	 * 安全方式获取存储的数据
	 * 
	 * @param key
	 *            存储的key名称
	 * @param locale
	 *            国际化
	 * @return
	 */
	public GetsResponse<Object> gets(String key, Locale locale, boolean useTokyoTyrant) {
		if (locale == null) {
			locale = Locale.CHINA;
		}
		if (locale.equals(Locale.CHINA)) {
			key = key + ZH;
		} else {
			key = key + EN;
		}
		InetSocketAddress address = memcachedClient.findServerByKey(key);
		GetsResponse<Object> object = null;
		try {
			if (useTokyoTyrant) {
				if (enabledTokyoTyrant == false) {
					return null;
				}
				object = tokyoTyrantMemcachedClient.gets(key);
			} else {
				if (enabled == false) {
					return null;
				}
				object = memcachedClient.gets(key);
			}

		} catch (TimeoutException e) {
			log.error("Gets Command is failed! Error is TimeoutException, message is " + e.getMessage() + " and key is " + key);
			if (address != null) {
				this.addAndcheckIfRemoveServer(address);
			}
		} catch (InterruptedException e) {
			log.error("Gets Command is failed! Error is TimeoutException, message is " + e.getMessage() + " and key is " + key);
			e.printStackTrace();
		} catch (MemcachedException e) {
			log.error("Gets Command is failed! Error is TimeoutException, message is " + e.getMessage() + " and key is " + key);
			e.printStackTrace();
		}
		if (address != null) {
			initTryTimes(address);
		}
		return object;
	}

	/**
	 * 判断key是否存在
	 */
	public boolean keyExists(String key) {
		return this.keyExists(key, null);
	}

	public boolean keyExists(String key, Locale locale) {
		return this.keyExists(key, locale, false);
	}

	public boolean keyExists(String key, Locale locale, boolean useTokyoTyrant) {
		return this.get(key, locale, useTokyoTyrant) != null;
	}

	/**
	 * 删除指令
	 */
	public boolean delete(String key) {
		return this.delete(key, null);
	}

	public boolean delete(String key, Locale locale) {
		return this.delete(key, locale, false);
	}

	public boolean delete(String key, Locale locale, boolean useTokyoTyrant) {
		if (locale == null) {
			locale = Locale.CHINA;
		}
		if (locale.equals(Locale.CHINA)) {
			key = key + ZH;
		} else {
			key = key + EN;
		}
		boolean ret = false;
		InetSocketAddress address = null;
		try {
			if (useTokyoTyrant) {
				if (enabledTokyoTyrant == false) {
					return false;
				}
				address = tokyoTyrantMemcachedClient.findServerByKey(key);
				ret = tokyoTyrantMemcachedClient.delete(key);
			} else {
				if (enabled == false) {
					return false;
				}
				address = memcachedClient.findServerByKey(key);
				ret = memcachedClient.delete(key);
			}
		} catch (TimeoutException e) {
			log.error("Delete Command is failed! Error is TimeoutException, message is " + e.getMessage() + " and key is " + key);
			if (address != null) {
				this.addAndcheckIfRemoveServer(address);
			}
		} catch (InterruptedException e) {
			log.error("Delete Command is failed! Error is InterruptedException, message is " + e.getMessage() + " and key is " + key);
		} catch (MemcachedException e) {
			log.error("Delete Command is failed! Error is MemcachedException, message is " + e.getMessage() + " and key is " + key);
		}
		if (address != null) {
			initTryTimes(address);
		}
		return ret;
	}

	/**
	 * 清空缓存
	 */
	public void flushAll() {
		try {
			memcachedClient.flushAll();
			tokyoTyrantMemcachedClient.flushAll();
		} catch (TimeoutException e) {
			log.error("FlushAll Command is failed! Error is TimeoutException, message is " + e.getMessage());
		} catch (InterruptedException e) {
			log.error("FlushAll Command is failed! Error is InterruptedException, message is " + e.getMessage());
		} catch (MemcachedException e) {
			log.error("FlushAll Command is failed! Error is MemcachedException, message is " + e.getMessage());
		}
	}

	/**
	 * 添加服务器
	 * 
	 * @param inetSocketAddress
	 */
	public void addServer(InetSocketAddress inetSocketAddress) {
		this.addServer(inetSocketAddress, Boolean.FALSE);
	}

	public void addServer(InetSocketAddress inetSocketAddress, boolean useTokyoTyrant) {
		try {
			if (useTokyoTyrant) {
				if (enabledTokyoTyrant == false) {
					return;
				}
				tokyoTyrantMemcachedClient.addServer(inetSocketAddress);
			} else {
				if (enabled == false) {
					return;
				}
				memcachedClient.addServer(inetSocketAddress);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 删除服务器
	 * 
	 * @param inetSocketAddress
	 */
	public void deleteServer(String hostList) {
		this.deleteServer(hostList, Boolean.FALSE);
	}

	public void deleteServer(String hostList, boolean useTokyoTyrant) {
		try {
			if (useTokyoTyrant) {
				if (enabledTokyoTyrant == false) {
					return;
				}
				tokyoTyrantMemcachedClient.removeServer(hostList);
			} else {
				if (enabled == false) {
					return;
				}
				memcachedClient.removeServer(hostList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取Memcached服务器列表
	 * 
	 * @return
	 */
	public Collection<InetSocketAddress> getAvaliableServers() {
		Collection<InetSocketAddress> addressCollection = new ArrayList<InetSocketAddress>();
		try {
			addressCollection = memcachedClient.getAvailableServers();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addressCollection;
	}

	/**
	 * 获取TTServer服务器列表
	 * 
	 * @return
	 */
	public Collection<InetSocketAddress> getAvaliableServersOfTTSERVER() {
		Collection<InetSocketAddress> addressCollection = new ArrayList<InetSocketAddress>();
		try {
			if (enabledTokyoTyrant == false) {
				return addressCollection;
			}
			addressCollection = tokyoTyrantMemcachedClient.getAvailableServers();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addressCollection;
	}

	/**
	 * 获取服务器状态信息
	 * 
	 * @return
	 * @throws MemcachedException
	 * @throws InterruptedException
	 * @throws TimeoutException
	 */
	public Map<InetSocketAddress, Map<String, String>> getStats() throws MemcachedException, InterruptedException, TimeoutException {
		Map<InetSocketAddress, Map<String, String>> param = new HashMap<InetSocketAddress, Map<String, String>>();
		try {
			param = memcachedClient.getStats();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return param;
	}
	
	public Map<InetSocketAddress, Map<String, String>> getStatsForTTServer() throws MemcachedException, InterruptedException, TimeoutException {
		Map<InetSocketAddress, Map<String, String>> param = new HashMap<InetSocketAddress, Map<String, String>>();
		try {
			param = tokyoTyrantMemcachedClient.getStats();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return param;
	}

	/**
	 * 确认是否删除该服务器
	 */
	private void addAndcheckIfRemoveServer(InetSocketAddress server) {
		if (server == null) {
			return;
		}
		String host = server.getAddress().toString() + ":" + server.getPort();
		AtomicInteger try_time = try_times.get(host);
		int result = try_time.addAndGet(1);
		synchronized (try_times) {
			if (result > Integer.valueOf(AbleskyImConfig.MAX_TRY_TIMES)) {
				memcachedClient.removeServer(host);
			}
		}
	}

	/**
	 * 清0操作 try_times
	 * 
	 * @param args
	 */
	private void initTryTimes(InetSocketAddress server) {
		if (server == null) {
			return;
		}
		String host = server.getAddress().toString() + ":" + server.getPort();
		AtomicInteger try_time = try_times.get(host);
		if (try_time == null) {
			return;
		} else if (try_time.get() == 0) {
			return;
		} else {
			try_time.set(0);
			return;
		}
	}

	/**
	 * 自增,自减,set,get指令 提供原子操作Auto
	 * 
	 * @param args
	 */

	/**
	 * @deprecated 并发环境下该方法不适宜使用
	 */
	public boolean atomSet(String key, Long value, Long expire) {
		return this.set(key, value.toString(), expire);
	}

	public Long atomGet(String key) {
		Object object = this.get(key);
		if (object == null) {
			return 0l;
		}
		return Long.valueOf((String) this.get(key));
	}

	public Long atomIncr(String key, Long dec) {
		return this.atomIncr(key, null, dec, 0l, false);
	}

	public Long atomIncr(String key, Long dec, Long def) {
		return this.atomIncr(key, null, dec, def, false);
	}

	public Long atomIncr(String key, Long dec, Long def, boolean useTokyoTyrant) {
		return this.atomIncr(key, null, dec, def, useTokyoTyrant);
	}

	public Long atomIncr(String key, Locale locale, Long dec, Long def, boolean useTokyoTyrant) {
		if (locale == null) {
			locale = Locale.CHINA;
		}
		if (locale.equals(Locale.CHINA)) {
			key = key + ZH;
		} else {
			key = key + EN;
		}
		if (dec < 0 || def < 0) {
			return -1l;
		}
		InetSocketAddress address = null;
		long ret = 0;
		try {
			if (useTokyoTyrant) {
				if (enabledTokyoTyrant == false) {
					return -1l;
				}
				address = tokyoTyrantMemcachedClient.findServerByKey(key);
				ret = tokyoTyrantMemcachedClient.incr(key, dec);
			} else {
				if (enabled == false) {
					return -1l;
				}
				address = memcachedClient.findServerByKey(key);
				ret = memcachedClient.incr(key, dec);
			}
		} catch (TimeoutException e) {
			log.error("Increase Command is failed! Error is TimeoutException, message is " + e.getMessage() + " and key is " + key);
			if (address != null) {
				this.addAndcheckIfRemoveServer(address);
			}
		} catch (InterruptedException e) {
			log.error("Increase Command is failed! Error is InterruptedException, message is " + e.getMessage() + " and key is " + key);
		} catch (MemcachedException e) {
			log.error("Increase Command is failed! Error is MemcachedException, message is " + e.getMessage() + " and key is " + key);
		}

		return ret;
	}

	public Long atomIncr(String key, long expire, Locale locale, Long dec, Long def, boolean useTokyoTyrant) {
		if (locale == null) {
			locale = Locale.CHINA;
		}
		if (locale.equals(Locale.CHINA)) {
			key = key + ZH;
		} else {
			key = key + EN;
		}
		if (dec < 0 || def < 0) {
			return -1l;
		}
		InetSocketAddress address = null;
		long ret = 0;
		int exp = new Long(expire / 1000).intValue();
		try {
			if (useTokyoTyrant) {
				if (enabledTokyoTyrant == false) {
					return -1l;
				}
				address = tokyoTyrantMemcachedClient.findServerByKey(key);
				ret = tokyoTyrantMemcachedClient.incr(key, dec, def, tokyoTyrantMemcachedClient.getOpTimeout(), exp);
			} else {
				if (enabled == false) {
					return -1l;
				}
				address = memcachedClient.findServerByKey(key);
				ret = memcachedClient.incr(key, dec, def, tokyoTyrantMemcachedClient.getOpTimeout(), exp);
			}
		} catch (TimeoutException e) {
			log.error("Increase Command is failed! Error is TimeoutException, message is " + e.getMessage() + " and key is " + key);
			if (address != null) {
				this.addAndcheckIfRemoveServer(address);
			}
		} catch (InterruptedException e) {
			log.error("Increase Command is failed! Error is InterruptedException, message is " + e.getMessage() + " and key is " + key);
		} catch (MemcachedException e) {
			log.error("Increase Command is failed! Error is MemcachedException, message is " + e.getMessage() + " and key is " + key);
		}

		return ret;
	}

	public Long atomDecr(String key, Long dec) {
		return this.atomDecr(key, null, dec, 0l, false);
	}

	public Long atomDecr(String key, Long dec, Long def) {
		return this.atomDecr(key, null, dec, def, false);
	}

	public Long atomDecr(String key, Long dec, Long def, boolean useTokyoTyrant) {
		return this.atomDecr(key, null, dec, def, useTokyoTyrant);
	}

	public Long atomDecr(String key, Locale locale, Long dec, Long def, boolean useTokyoTyrant) {
		if (locale == null) {
			locale = Locale.CHINA;
		}
		if (locale.equals(Locale.CHINA)) {
			key = key + ZH;
		} else {
			key = key + EN;
		}
		if (dec < 0 || def < 0) {
			return -1l;
		}
		InetSocketAddress address = null;
		long ret = 0;
		try {
			if (useTokyoTyrant) {
				if (enabledTokyoTyrant == false) {
					return -1l;
				}
				address = tokyoTyrantMemcachedClient.findServerByKey(key);
				ret = tokyoTyrantMemcachedClient.decr(key, dec);
			} else {
				if (enabled == false) {
					return -1l;
				}
				address = memcachedClient.findServerByKey(key);
				ret = memcachedClient.decr(key, dec);
			}
		} catch (TimeoutException e) {
			log.error("Decrease Command is failed! Error is MemcachedException, message is " + e.getMessage() + " and key is " + key);
			if (address != null) {
				this.addAndcheckIfRemoveServer(address);
			}
		} catch (InterruptedException e) {
			log.error("Decrease Command is failed! Error is MemcachedException, message is " + e.getMessage() + " and key is " + key);
		} catch (MemcachedException e) {
			log.error("Decrease Command is failed! Error is MemcachedException, message is " + e.getMessage() + " and key is " + key);
		}

		return ret;
	}

	public Long atomDecr(String key, long expire, Locale locale, Long dec, Long def, boolean useTokyoTyrant) {
		if (locale == null) {
			locale = Locale.CHINA;
		}
		if (locale.equals(Locale.CHINA)) {
			key = key + ZH;
		} else {
			key = key + EN;
		}
		if (dec < 0 || def < 0) {
			return -1l;
		}
		InetSocketAddress address = null;
		long ret = 0;
		int exp = new Long(expire / 1000).intValue();
		try {
			if (useTokyoTyrant) {
				if (enabledTokyoTyrant == false) {
					return -1l;
				}
				address = tokyoTyrantMemcachedClient.findServerByKey(key);
				ret = tokyoTyrantMemcachedClient.decr(key, dec, def, tokyoTyrantMemcachedClient.getOpTimeout(), exp);
			} else {
				if (enabled == false) {
					return -1l;
				}
				address = memcachedClient.findServerByKey(key);
				ret = memcachedClient.decr(key, dec, def, tokyoTyrantMemcachedClient.getOpTimeout(), exp);
			}
		} catch (TimeoutException e) {
			log.error("Decrease Command is failed! Error is MemcachedException, message is " + e.getMessage() + " and key is " + key);
			if (address != null) {
				this.addAndcheckIfRemoveServer(address);
			}
		} catch (InterruptedException e) {
			log.error("Decrease Command is failed! Error is MemcachedException, message is " + e.getMessage() + " and key is " + key);
		} catch (MemcachedException e) {
			log.error("Decrease Command is failed! Error is MemcachedException, message is " + e.getMessage() + " and key is " + key);
		}

		return ret;
	}

	public static void main(String[] args) throws Exception {
		XmemcachedUtil cache = new XmemcachedUtil();
		cache.getAvaliableServers();
		// System.out.println(cache.atomIncr("badsa", 2l, 0l));
		// System.out.println(cache.atomIncr("adfe", 13l));
		// System.out.println(cache.atomDecr("adfe", 3l));
		// System.out.println(cache.atomIncr("adfe", 0l));
		// System.out.println(cache.get("badsa"));
		// System.out.println(cache.atomDecr("bads", 0l));
		// System.out.println(cache.atomIncr("bads", -1l));
		// System.out.println(cache.atomDecr("bads", -2l));
		// System.out.println(cache.atomDecr("bads", 342l));
		// System.out.println(cache.atomIncr("bads", 2l, 0l));
		// System.out.println(cache.atomDecr("bads", 4l));

	}
}
