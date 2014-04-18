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

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.metaparadigm.jsonrpc.JSONRPCResult;
import com.metaparadigm.jsonrpc.JSONSerializer;
import com.metaparadigm.jsonrpc.MarshallException;
import com.metaparadigm.jsonrpc.SerializerState;

public class JSONHelperUtil {
	public static final boolean KEEPALIVE = true;
	public static final Log log = LogFactory.getLog(JSONHelperUtil.class);
	public static JSONSerializer ser = new JSONSerializer();
	static {
		try {
			ser.registerDefaultSerializers();
			ser.setMarshallClassHints(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Object toJsonObject(Object dto) {
		SerializerState state = new SerializerState();
		Object o = null;
		try {
			o = ser.marshall(state, dto);
		} catch (MarshallException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return o;
	}

	public static String dtoToJSON(Object dto) {
		JSONRPCResult json_res = null;
		SerializerState state = new SerializerState();
		try {
			json_res = new JSONRPCResult(JSONRPCResult.CODE_SUCCESS, 1, ser.marshall(state, dto));
		} catch (MarshallException e) {
			e.printStackTrace();
		}
		String jsonStr = json_res.toString();
		return jsonStr;
	}

	public static void outputDTOToJSON(Object dto, HttpServletResponse response) {
		outputDTOToJSON(dto, null, response);
	}

	public static void outputDTOToJSON(Object dto, Map param, HttpServletResponse response) {
		outputDTOToJSON(dto, param, null, response);
	}

	public static void generateJsonByRemoteCall(boolean isSuccess, String message, String jsoncallback, HttpServletResponse response) {
		generateJsonByRemoteCall(isSuccess, message, jsoncallback, null, null, response);
	}

	public static void generateJsonByRemoteCall(boolean isSuccess, String message, String jsoncallback, String encode, HttpServletResponse response) {
		generateJsonByRemoteCall(isSuccess, message, jsoncallback, null, encode, response);
	}

	public static void generateJsonByRemoteCall(boolean isSuccess, String message, String jsoncallback, Map<String, Object> metaData, HttpServletResponse response) {
		generateJsonByRemoteCall(isSuccess, message, jsoncallback, metaData, null, response);
	}

	/**
	 * 安全隐私协议
	 */
	public static void addP3P(HttpServletResponse response) {
		response.addHeader("p3p", "CP=\"CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI DSP COR\"");
	}
	
	public static void generateJsonByRemoteCall(boolean isSuccess, String message, String jsoncallback, Map<String, Object> metaData, String encode, HttpServletResponse response) {
		/**
		 * 安全隐私协议
		 */
		addP3P(response);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("success", isSuccess);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (message != null) {
			try {
				jsonObject.put("message", message);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (metaData != null) {
			for (Object key : metaData.keySet()) {
				Object value = metaData.get(key);
				try {
					jsonObject.put(key.toString(), value);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		byte[] bout = null;
		StringBuffer sb = new StringBuffer();
		String contentType = "";

		if (jsoncallback != null) {

			sb.append(jsoncallback + "(");
			sb.append(jsonObject.toString());
			sb.append(");");

		} else {
			sb.append(jsonObject.toString());
		}

		try {
			contentType += jsoncallback != null ? "text/javascript;" : "text/plain;";

			if (encode == null || encode.equalsIgnoreCase("") || !Charset.isSupported(encode)) {
				bout = sb.toString().getBytes("UTF-8");
				contentType += "charset=utf-8";
			} else {
				bout = sb.toString().getBytes(encode);
				contentType += "charset=" + encode;
			}

			response.setContentType(contentType);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			OutputStream out = response.getOutputStream();
			out.write(bout);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void generateJsonByRemoteCall(boolean isSuccess, String jsoncallback, HttpServletResponse response, String encode) {
		/**
		 * 安全隐私协议
		 */
		addP3P(response);
		byte[] bout = null;
		StringBuffer sb = new StringBuffer();
		String contentType = "";

		if (jsoncallback != null) {
			sb.append(jsoncallback + "(");
			sb.append(isSuccess);
			sb.append(");");

		} else {
			sb.append(isSuccess);
		}
		try {
			contentType += jsoncallback != null ? "text/javascript;" : "text/plain;";

			if (encode == null || encode.equalsIgnoreCase("") || !Charset.isSupported(encode)) {
				bout = sb.toString().getBytes("UTF-8");
				contentType += "charset=utf-8";
			} else {
				bout = sb.toString().getBytes(encode);
				contentType += "charset=" + encode;
			}

			response.setContentType(contentType);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			OutputStream out = response.getOutputStream();
			out.write(bout);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void generateJsonByRemoteCall(Object dto, Map param, String jsoncallback, HttpServletResponse response) {
		generateJsonByRemoteCallWithEncode(dto, param, jsoncallback, null, response);
	}

	public static void generateJsonByRemoteCallWithEncode(Object dto, Map param, String jsoncallback, String encode, HttpServletResponse response) {
		/**
		 * 安全隐私协议
		 */
		addP3P(response);
		StringBuffer sb = new StringBuffer();
		String contentType = "";

		if (jsoncallback != null && jsoncallback != "") {
			sb.append(jsoncallback + "(");
		}

		String dtoJsonString = dtoToJSON(dto);
		if (param != null) {
			sb.append(marshalMetaData(param));
			sb.append(dtoJsonString.substring(1, dtoJsonString.length()));
		} else {
			sb.append(dtoJsonString);
		}

		if (jsoncallback != null && jsoncallback != "") {
			sb.append(");");
		}

		byte[] bout = null;
		try {
			contentType += jsoncallback != null ? "text/javascript;" : "text/plain;";

			if (encode == null || encode.equalsIgnoreCase("") || !Charset.isSupported(encode)) {
				bout = sb.toString().getBytes("UTF-8");
				contentType += "charset=utf-8";
			} else {
				bout = sb.toString().getBytes(encode);
				contentType += "charset=" + encode;
			}

			response.setContentType(contentType);

		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		// response.setContentType("text/plain;charset=utf-8");
		if (KEEPALIVE) {
			response.setIntHeader("Content-Length", bout.length);
		}
		try {
			OutputStream out = response.getOutputStream();
			out.write(bout);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void outputDTOToJSON(Object dto, Map param, String contentType, HttpServletResponse response) {
		/**
		 * 安全隐私协议
		 */
		addP3P(response);
		StringBuffer sb = new StringBuffer();
		if (param != null) {
			sb.append(marshalMetaData(param));
		}
		String dtoJsonString = dtoToJSON(dto);
		if (param != null) {
			sb.append(dtoJsonString.substring(1, dtoJsonString.length()));
		} else {
			sb.append(dtoJsonString);
		}
		byte[] bout = null;
		try {
			bout = sb.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		if (StringUtil.isNull(contentType)) {
			response.setContentType("text/plain;charset=utf-8");
		} else {
			response.setContentType(contentType);
		}
		if (KEEPALIVE) {
			response.setIntHeader("Content-Length", bout.length);
		}
		try {
			OutputStream out = response.getOutputStream();
			out.write(bout);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void outputMapToJSON(Map metaData, HttpServletResponse response) {
		/**
		 * 安全隐私协议
		 */
		addP3P(response);
		StringBuffer sb = new StringBuffer();
		sb.append(marshalMetaData2(metaData));
		sb.append("temp:1}");
		byte[] bout = null;
		try {
			bout = sb.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		response.setContentType("text/plain;charset=utf-8");
		if (KEEPALIVE) {
			response.setIntHeader("Content-Length", bout.length);
		}
		try {
			OutputStream out = response.getOutputStream();
			out.write(bout);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void outputString(String str, HttpServletResponse response) {
		/**
		 * 安全隐私协议
		 */
		addP3P(response);
		byte[] bout = null;
		try {
			bout = str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		response.setContentType("text/html;charset=utf-8");
		if (KEEPALIVE) {
			response.setIntHeader("Content-Length", bout.length);
		}
		try {
			OutputStream out = response.getOutputStream();
			out.write(bout);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void outputStringToJSON(String str, HttpServletResponse response) {
		/**
		 * 安全隐私协议
		 */
		addP3P(response);
		byte[] bout = null;
		try {
			bout = str.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		response.setContentType("text/html;charset=utf-8");
		if (KEEPALIVE) {
			response.setIntHeader("Content-Length", bout.length);
		}
		try {
			OutputStream out = response.getOutputStream();
			out.write(bout);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void outputStringToXML(StringBuffer str, HttpServletResponse response) {
		/**
		 * 安全隐私协议
		 */
		addP3P(response);
		byte[] bout = null;
		try {
			bout = str.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		response.setContentType("text/xml;charset=utf-8");
		if (KEEPALIVE) {
			response.setIntHeader("Content-Length", bout.length);
		}
		try {
			OutputStream out = response.getOutputStream();
			out.write(bout);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void outputExceptionToJSON(Throwable e, HttpServletResponse response) {
		/**
		 * 安全隐私协议
		 */
		addP3P(response);
		response.setContentType("text/plain;charset=utf-8");
		JSONRPCResult json_res = null;
		SerializerState state = new SerializerState();
		if (e instanceof InvocationTargetException)
			e = ((InvocationTargetException) e).getTargetException();
		json_res = new JSONRPCResult(JSONRPCResult.CODE_REMOTE_EXCEPTION, 1, e);
		byte[] bout = null;
		try {
			bout = json_res.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		boolean keepalive = true;
		if (keepalive) {
			response.setIntHeader("Content-Length", bout.length);
		}
		try {
			OutputStream out = response.getOutputStream();
			out.write(bout);
			out.flush();
			out.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static String marshalMetaData(Map map) {
		StringBuffer sb = new StringBuffer("{");
		for (Object key : map.keySet()) {
			Object value = map.get(key);
			if (value instanceof Integer) {
				sb.append("\"" + key.toString() + "\":" + value.toString() + ",");
			} else if (value instanceof Long) {
				sb.append("\"" + key.toString() + "\":" + value.toString() + ",");
			} else if (value instanceof String) {
				sb.append("\"" + key.toString() + "\":\"" + jsonFormat(value.toString()) + "\",");
			} else if (value instanceof Float) {
				sb.append("\"" + key.toString() + "\":" + value.toString() + ",");
			} else if (value instanceof Boolean) {
				sb.append("\"" + key.toString() + "\":" + value.toString() + ",");
			} else if (value instanceof BigDecimal) {
				sb.append("\"" + key.toString() + "\":" + value.toString() + ",");
			}
		}
		return sb.toString();
	}

	public static String marshalMetaData2(Map map) {
		StringBuffer sb = new StringBuffer("{");
		for (Object key : map.keySet()) {
			Object value = map.get(key);
			if (value instanceof Integer) {
				sb.append(key.toString() + ":" + value.toString() + ",");
			} else if (value instanceof Long) {
				sb.append(key.toString() + ":" + value.toString() + ",");
			} else if (value instanceof Float) {
				sb.append(key.toString() + ":" + value.toString() + ",");
			} else if (value instanceof String) {
				sb.append(key.toString() + ":\"" + jsonFormat(value.toString()) + "\",");
			} else if (value instanceof Boolean) {
				sb.append(key.toString() + ":" + value.toString() + ",");
			} else if (value instanceof BigDecimal) {
				sb.append(key.toString() + ":" + value.toString() + ",");
			} else if (value == null) {
				sb.append(key.toString() + ":" + null + ",");
			}
		}
		return sb.toString();
	}

	public static String jsonFormat(String s) {
		s = s.replaceAll("\\\\", "\\\\\\\\");
		s = s.replaceAll("\\\"", "\\\\\"");
		// s = s.replaceAll("\\/", "\\\\/");
		s = s.replaceAll("\\\n", "\\\\\\n");
		// s = s.replaceAll("\\:", "\\\\:");
		// s = s.replaceAll("\\{", "\\\\{");
		// s = s.replaceAll("\\}", "\\\\}");
		return s;
	}

	public static void outputOperationResultAsJSON(boolean isSuccess, String message, HttpServletResponse response) {
		outputOperationResultAsJSON(isSuccess, message, null, response);
	}

	public static void outputOperationResultAsJSON(boolean isSuccess, String message, Map<String, Object> metaData, HttpServletResponse response) {
		/**
		 * 安全隐私协议
		 */
		addP3P(response);
		response.setContentType("text/plain;charset=utf-8");
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("success", isSuccess);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (message != null)
			try {
				jsonObject.put("message", message);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		if (metaData != null) {
			for (Object key : metaData.keySet()) {
				Object value = metaData.get(key);
				try {
					jsonObject.put(key.toString(), value);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			OutputStream out = response.getOutputStream();
			out.write(jsonObject.toString().getBytes("UTF-8"));
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Upload client can not recieve contentType with "text/plain"
	 * 
	 * @param isSuccess
	 * @param message
	 * @param response
	 * @throws JSONException
	 */
	public static void outputOperationResultAsJSONSpecialForForm(boolean isSuccess, String message, Map<String, Object> metaData, HttpServletResponse response) {
		/**
		 * 安全隐私协议
		 */
		addP3P(response);
		response.setContentType("text/html");
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("success", isSuccess);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (message != null)
			try {
				jsonObject.put("message", message);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		if (metaData != null)
			for (Object key : metaData.keySet()) {
				Object value = metaData.get(key);
				try {
					jsonObject.put(key.toString(), value);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		try {
			OutputStream out = response.getOutputStream();
			out.write(jsonObject.toString().getBytes("UTF-8"));
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 多个dtoList或者dto（可以混用）
	 * 
	 * @author hdu
	 * @param map
	 *            --基本类型、dto、dtolist的map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String multiDtoListToJSON(Map<String, Object> map) {
		SerializerState state = new SerializerState();
		JSONObject jo = new JSONObject();
		try {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				jo.put(entry.getKey(), ser.marshall(state, entry.getValue()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jo.toString();
	}

	/**
	 * 多个dtoList或者dto（可以混用）
	 * 
	 * @author hdu
	 * @param objectMap
	 *            --基本类型、dto、dtolist的map
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public static void outputMultiDtoListToJSON(Map<String, Object> objectMap, HttpServletResponse response) {
		outputMultiDtoListToJSON(objectMap, null, response);
	}

	/**
	 * 多个dtoList或者dto（可以混用）
	 * 
	 * @author hdu
	 * @param objectMap
	 *            --基本类型、dto、dtolist的map
	 * @param contentType
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public static void outputMultiDtoListToJSON(Map<String, Object> objectMap, String contentType, HttpServletResponse response) {
		/**
		 * 安全隐私协议
		 */
		addP3P(response);
		StringBuffer sb = new StringBuffer();
		String dtoJsonString = multiDtoListToJSON(objectMap);
		sb.append(dtoJsonString);
		byte[] bout = null;
		try {
			bout = sb.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		if (StringUtil.isNull(contentType)) {
			response.setContentType("text/plain;charset=utf-8");
		} else {
			response.setContentType(contentType);
		}
		if (KEEPALIVE) {
			response.setIntHeader("Content-Length", bout.length);
		}
		try {
			OutputStream out = response.getOutputStream();
			out.write(bout);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void main(String args[]) {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList dto1 = new ArrayList();
		for (int i = 0; i < 10; i++)
			dto1.add(i);
		ArrayList dto2 = new ArrayList();
		for (int i = 0; i < 10; i++)
			dto2.add(i);
		map.put("duhao", dto1);
		map.put("hoveen", dto2);
		System.out.println(multiDtoListToJSON(map));
		// log.debug(new String(dtoToJSON(map)));
		// log.debug(marshalMetaData(map));
	}
}
