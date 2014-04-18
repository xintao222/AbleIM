package com.ablesky.im.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class CommonDaoSupport extends HibernateDaoSupport {
	public static String DIR_DESC = "desc";
	public static String DIR_ASC = "asc";

	public int getTotalCount(Session session, String hql) throws DataAccessException {
		Integer amount = new Integer(0);
		int sqlFrom = hql.indexOf("from");
		int sqlOrderby = hql.indexOf("order by");
		String countStr = "";
		if (sqlOrderby > 0) {
			countStr = "select count(*) " + hql.substring(sqlFrom, sqlOrderby);
		} else {
			countStr = "select count(*) " + hql.substring(sqlFrom);
		}
		Query qry = null;
		try {
			qry = session.createQuery(countStr);
			List list = qry.list();
			if (!list.isEmpty()) {
				amount = new Integer(list.get(0).toString());
			} else {
				return 0;
			}
		} catch (HibernateException ex) {
			// throw new DataAccessException(ex);
		} finally {
			// session.clear();
		}
		return amount.intValue();
	}

	public int getTotalCount(Session session, String hql, Map<String, Object> param) throws DataAccessException {
		Integer amount = new Integer(0);
		int sqlFrom = hql.indexOf("from");
		int sqlOrderby = hql.indexOf("order by");
		String countStr = "";
		if (sqlOrderby > 0) {
			countStr = "select count(*) " + hql.substring(sqlFrom, sqlOrderby);
		} else {
			countStr = "select count(*) " + hql.substring(sqlFrom);
		}
		Query query = null;
		try {
			query = session.createQuery(countStr);
			for (String key : param.keySet()) {
				Object value = param.get(key);
				query.setParameter(key, value);
			}
			List list = query.list();
			if (!list.isEmpty()) {
				if (hql.indexOf("group by") != -1 || hql.indexOf("GROUP BY") != -1) {
					amount = list.size();
					// for(int i=0;i<list.size();i++){
					// amount += new Integer(list.get(i).toString()).intValue();
					// }
				} else {
					amount = new Integer(list.get(0).toString());
				}

			} else {
				return 0;
			}
		} catch (HibernateException ex) {
			// throw new DataAccessException(ex);
		} finally {
			// session.clear();
		}
		return amount.intValue();
	}

	/**
	 * 使用主键查询count
	 */
	public int getTotalCountByDirectHQL(Session session, String hql, Map<String, Object> param) throws DataAccessException {
		Integer amount = new Integer(0);
		Query query = null;
		try {
			query = session.createQuery(hql);
			for (String key : param.keySet()) {
				Object value = param.get(key);
				query.setParameter(key, value);
			}
			List list = query.list();
			if (!list.isEmpty()) {
				if (hql.indexOf("group by") != -1 || hql.indexOf("GROUP BY") != -1) {
					amount = list.size();
				} else {
					if (list.get(0) != null){
						amount = new Integer(list.get(0).toString());
					}
				}

			} else {
				return 0;
			}
		} catch (HibernateException ex) {
			ex.printStackTrace();
		} finally {
			// session.clear();
		}
		return amount.intValue();
	}

	public int getTotalCountBySQL(Session session, String hql, Map<String, Object> param) throws DataAccessException {
		Integer amount = new Integer(0);
		int sqlFrom = hql.indexOf("from");
		int sqlOrderby = hql.indexOf("order by");
		String countStr = "";
		if (sqlOrderby > 0) {
			countStr = "select count(*) " + hql.substring(sqlFrom, sqlOrderby);
		} else {
			countStr = "select count(*) " + hql.substring(sqlFrom);
		}
		Query query = null;
		try {
			query = session.createSQLQuery(countStr);
			if (param != null) {
				for (String key : param.keySet()) {
					Object value = param.get(key);
					query.setParameter(key, value);
				}
			}
			List list = query.list();
			if (!list.isEmpty()) {
				if (hql.indexOf("group by") != -1 || hql.indexOf("GROUP BY") != -1) {
					amount = list.size();
				} else {
					amount = new Integer(list.get(0).toString());
				}

			} else {
				return 0;
			}
		} catch (HibernateException ex) {
			// throw new DataAccessException(ex);
		} finally {
			// session.clear();
		}
		return amount.intValue();
	}

	/**
	 * 使用主键查询count
	 */
	public int getTotalCountByDirectSQL(Session session, String sql, boolean groupBy, Map<String, Object> param) throws DataAccessException {
		Integer amount = new Integer(0);
		Query query = null;
		try {
			query = session.createSQLQuery(sql);
			if (param != null) {
				for (String key : param.keySet()) {
					Object value = param.get(key);
					query.setParameter(key, value);
				}
			}

			List list = query.list();
			if (!list.isEmpty()) {
				if (groupBy) {
					amount = list.size();
				} else {
					amount = new Integer(list.get(0).toString());
				}

			} else {
				return 0;
			}
		} catch (HibernateException ex) {
			// throw new DataAccessException(ex);
		} finally {
			// session.clear();
		}
		return amount.intValue();
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getResult(Session session, final int start, final int limit, String hql) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> list = new ArrayList<Object>();
		int count;
		Query query = null;
		query = session.createQuery(hql);
		if (limit != 0) {
			query.setFirstResult(start);
			query.setMaxResults(limit);
		}
		list = query.list();

		count = getTotalCount(session, hql);
		map.put(CommonStaticConst.LIST, list);
		map.put(CommonStaticConst.TOTALCOUNT, count);
		return map;
	}

	public Map<String, Object> getResult(Session session, String hql) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> list = new ArrayList<Object>();
		int count;
		Query query = null;
		query = session.createQuery(hql);
		list = query.list();

		count = getTotalCount(session, hql);
		map.put(CommonStaticConst.LIST, list);
		map.put(CommonStaticConst.TOTALCOUNT, count);

		return map;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getResult(Session session, final int start, final int limit, String hql, Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> list = new ArrayList<Object>();
		int count;
		Query query = null;
		query = session.createQuery(hql);
		for (String key : param.keySet()) {
			Object value = param.get(key);
			query.setParameter(key, value);
		}
		if (limit != 0) {
			query.setFirstResult(start);
			query.setMaxResults(limit);
		}
		list = query.list();

		count = getTotalCount(session, hql, param);
		map.put(CommonStaticConst.LIST, list);
		map.put(CommonStaticConst.TOTALCOUNT, count);

		return map;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getLimitResult(Session session, final int start, final int limit, String hql, Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> list = new ArrayList<Object>();
		int count;
		Query query = null;
		query = session.createQuery(hql);
		for (String key : param.keySet()) {
			Object value = param.get(key);
			query.setParameter(key, value);
		}

		if (limit != 0) {
			query.setFirstResult(start);
			query.setMaxResults(limit);
		}
		list = query.list();

		count = getTotalCount(session, hql, param);
		map.put(CommonStaticConst.LIST, list);
		map.put(CommonStaticConst.TOTALCOUNT, count);

		return map;
	}
	
	public int getTotalCountBySqlQurey(Session session, String sql, Map<String, Object> param) throws DataAccessException {
		Integer amount = new Integer(0);
		int sqlFrom = sql.indexOf("from");
		int sqlOrderby = sql.indexOf("order by");
		String countStr = "";
		if (sqlOrderby > 0) {
			countStr = "select count(*) " + sql.substring(sqlFrom, sqlOrderby);
		} else {
			countStr = "select count(*) " + sql.substring(sqlFrom);
		}
		Query query = null;
		try {
			query = session.createSQLQuery(countStr);
			for (String key : param.keySet()) {
				Object value = param.get(key);
				query.setParameter(key, value);
			}
			if (!query.list().isEmpty()) {
				amount = new Integer(query.list().get(0).toString());
			} else {
				return 0;
			}
		} catch (HibernateException ex) {
			// throw new DataAccessException(ex);
		} finally {
			// session.clear();
		}
		return amount.intValue();
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getResultBySqlQurey(Session session, final int start, final int limit, String sql, Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> list = new ArrayList<Object>();
		int count;
		Query query = null;
		query = session.createSQLQuery(sql);
		for (String key : param.keySet()) {
			Object value = param.get(key);
			query.setParameter(key, value);
		}
		if (limit != 0) {
			query.setFirstResult(start);
			query.setMaxResults(limit);
		}
		list = query.list();

		count = getTotalCount(session, sql, param);
		map.put(CommonStaticConst.LIST, list);
		map.put(CommonStaticConst.TOTALCOUNT, count);

		return map;
	}

	@SuppressWarnings("unchecked")
	public List getResultListBySqlQurey(Session session, final int start, final int limit, String sql, Map<String, Object> param) {
		Query query = null;
		query = session.createSQLQuery(sql);
		for (String key : param.keySet()) {
			Object value = param.get(key);
			query.setParameter(key, value);
		}
		if (limit != 0) {
			query.setFirstResult(start);
			query.setMaxResults(limit);
		}
		return query.list();
	}
	
//	/**
//	 * 从hibernate的session获取JDBC连接，进行数据库查询操作
//	 * 这么做是因为hibernate对标准SQL的外连接支持不充分
//	 * @param session
//	 * @param sql
//	 * @param paramList
//	 * @return
//	 */
//	public List<List<Object>> getResultListByJDBC(Session session, String sql, List<Object> paramList){
//		
//		List<List<Object>> list = new ArrayList<List<Object>>();
//		Connection con = session.connection();  
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		try {
//			pstmt = con.prepareStatement(sql);
//			pstmt.setLong(1, (Long) paramList.get(0));
//			rs = pstmt.executeQuery();			
//			ResultSetMetaData md = rs.getMetaData();  
//			int columnCount = md.getColumnCount(); 					
//			while(rs.next()){
//				List<Object> rowList = new ArrayList<Object>();
//				for (int i = 1; i <= columnCount; i++) {  
//					rowList.add(rs.getObject(i));  
//				}   
//				list.add(rowList);  
//			}			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			try {
//				if(null != rs){
//					rs.close();
//				}
//				if(null != pstmt){
//					pstmt.close();
//				}
//				/*if(null != con){
//					con.close();
//				}*/
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return list.size() == 0 ? null : list;
//	}

	@SuppressWarnings("unchecked")
	public static List getResultList(Session session, final int start, final int limit, String hql, Map<String, Object> param) {
		Query query = null;
		query = session.createQuery(hql);
		for (String key : param.keySet()) {
			Object value = param.get(key);
			query.setParameter(key, value);
		}
		if (limit != 0) {
			query.setFirstResult(start);
			query.setMaxResults(limit);
		}
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public static List getResultList(Session session, final int start, final int limit, String hql) {
		Query query = null;
		query = session.createQuery(hql);
		if (limit != 0) {
			query.setFirstResult(start);
			query.setMaxResults(limit);
		}
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public static List getResultList(Session session, String hql, Map<String, Object> param) {
		Query query = null;
		query = session.createQuery(hql);
		for (String key : param.keySet()) {
			Object value = param.get(key);
			query.setParameter(key, value);
		}

		return query.list();
	}

	/**
	 * 批量更新
	 * 
	 * @param session
	 * @param hql
	 * @param param
	 * @return 影响的行数
	 */
	public static int excuteUpdate(Session session, String hql, Map<String, Object> param) {
		int temp = 0;
		try {
			Query query = null;
			query = session.createQuery(hql);
			for (String key : param.keySet()) {
				Object value = param.get(key);
				query.setParameter(key, value);
			}
			temp = query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// session.close();
		}
		return temp;
	}

	/**
	 * 执行sql
	 * 
	 * @param type
	 * @return
	 */
	public static void excuteSql(Session session, String sql, Map<String, Object> param) {
		try {
			Query query = null;
			query = session.createSQLQuery(sql);
			if (param != null) {
				for (String key : param.keySet()) {
					Object value = param.get(key);
					query.setParameter(key, value);
				}
			}
			query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// session.close();
		}
		return;
	}

	public static String Hql(int type) {
		String hql = "";
		switch (type) {
		case 1:
			// hql = "paid = 0";
			hql = "pricerange = '0'";
			break;
		case 2:
			// hql = "paid<=10 and paid>0";
			hql = "pricerange = 'Up to $10'";
			break;
		case 3:
			// hql = "paid<=20 and paid>10";
			hql = "pricerange = '$10-20";
			break;
		case 4:
			// hql = "paid<=50 and paid>20";
			hql = "pricerange = '$20-50'";
			break;
		case 5:
			// hql = "paid<=100 and paid>50";
			hql = "pricerange = '$50-100'";
			break;
		case 6:
			// hql = "paid>100";
			hql = "pricerange = 'More than 100'";
			break;
		}

		return hql;
	}

	public static String escapeSql(String str) {
		str = StringEscapeUtils.escapeSql(str);
		if (str.contains("_")) {
			str = str.replace("_", "\\_");
		}
		if (str.contains("%")) {
			str = str.replace("%", "\\%");
		}

		return str;
	}
	
	/**
	 * 根据SQL查询返回指定对象
	 * @param session
	 * @param objectClass 指定对象
	 * @param start
	 * @param limit
	 * @param sql
	 * @param param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getObjectBySqlQuery(Session session, Class objectClass, int start, int limit, String sql, Map<String, Object> param){
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> list = new ArrayList<Object>();
		int count;
		Query query = null;
		query = getSession().createSQLQuery(sql).addEntity(objectClass);
		for (String key : param.keySet()) {
			Object value = param.get(key);
			query.setParameter(key, value);
		}
		if (limit != 0) {
			query.setFirstResult(start);
			query.setMaxResults(limit);
		}
		list = query.list();

		count = this.getTotalCountByDirectSQL(session, sql, Boolean.TRUE, param);
		map.put(CommonStaticConst.LIST, list);
		map.put(CommonStaticConst.TOTALCOUNT, count);

		return map;
	}

}
