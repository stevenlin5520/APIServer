package com.steven.demo.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/pay")
public class PayController {

	// 微信服务后台获取预支付交易单的地址
	private static final String WXSERVER = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	// 小程序APPID
	private static final String APPID = "你的小程序APPID";
	// 商户号
	private static final String MCHID = "微信商户号MCHID";
	// 商户私钥
	private static final String KEY = "商户号私钥KEY";
	// 用户的openid
	private static final String OPENID = "oJgER5bXxly4yRVT8o8IV9bZAkF8";

	/**
	 * 通过发起请求到微信支付服务后台生成预支付交易单
	 * @param postMap 小程序发送的数据 {"body":"测试支付","total_fee":1,"openid":"openidopenid"}
	 * @param request 客户端请求
	 */
	@ResponseBody
	@RequestMapping("/prepay")
	public static Map<String, String> prePay(Map<String, String> postMap, HttpServletRequest request) {
		/**
		 * 获取小程序的数据
		 */
		
		Map<String, String> obj = new HashMap<>();
		// 小程序ID String(32) 微信分配的小程序ID
		obj.put("appid", APPID);
		// 商户号 String(32) 微信支付分配的商户号
		obj.put("mch_id", MCHID);
		// 随机字符串 String(32)
		obj.put("nonce_str", UUID.randomUUID().toString().replace("-", ""));
		// 商品描述 String(128)
		try {
			obj.put("body", postMap.get("body")!=null && postMap.get("body").length()>0 ? String.valueOf(postMap.get("body").getBytes("UTF-8")) : "TestPay");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 商户订单号 String(32) 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*且在同一个商户号下唯一
		obj.put("out_trade_no", UUID.randomUUID().toString().replace("-", ""));
		// 标价金额 int 订单总金额，单位为分
		obj.put("total_fee", postMap.get("total_fee")!=null && postMap.get("total_fee").length()>0 ? postMap.get("total_fee") : "1");
		// 终端IP String(64) 支持IPV4和IPV6两种格式的IP地址。调用微信支付API的机器IP
		obj.put("spbill_create_ip", getIpAddress(request));
		// 通知地址 String(256) 异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数
		obj.put("notify_url", "http://"+getIpAddress(request)+":8080/APIServer/login/login");
		// 交易类型 String(16)
		// JSAPI--JSAPI支付（或小程序支付）、NATIVE--Native支付、APP--app支付，MWEB--H5支付
		obj.put("trade_type", "JSAPI");
		// 用户标识 String(128) trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识
		obj.put("openid", postMap.get("openid")!=null && postMap.get("openid").length()>0 ? postMap.get("openid") : OPENID);

		// 对传送数据进行签名(但不包括sign)
		String stringA = firstSign(obj);
		String stringSignTemp = stringA + "&key=" + KEY;
		String sign = MD5Encryption(stringSignTemp);
		// 签名 String(32) 通过签名算法计算得出的签名值
		obj.put("sign", sign);

		// 将数据转换成XML格式的String
		String requestData = Map2XMLStr(obj);
		// 发起请求获取预支付交易单
		String result = request(requestData);
		
		return XMLStr2Map(result);
	}

	/**
	 * 预支付的签名
	 * @param obj 待签名数据
	 * @return 签名后数据
	 */
	public static String firstSign(Map<String, String> obj) {
		String result = "";

		List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(obj.entrySet());
		System.out.println("排序前：" + list.toString());
		// 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
		Collections.sort(list, new Comparator<Map.Entry<String, String>>() {

			@Override
			public int compare(Entry<String, String> paramT1, Entry<String, String> paramT2) {
				return paramT1.getKey().toString().compareTo(paramT2.getKey());
			}
		});

		System.err.println("排序后：" + list.toString());

		// 拼接成需要的URL字符串
		for (Entry<String, String> entry : list) {
			result += "&" + entry.toString();
		}
		result = result.substring(1);

		System.out.println("字段排序拼接后：" + result);
		return result;
	}

	/**
	 * MD5加密
	 * @param str 待加密数据
	 * @return 加密后数据
	 */
	public static String MD5Encryption(String str) {
		String result = "";

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			// 计算md5函数
			md.update(str.getBytes());
			// digest()最后确定返回md5 hash值，返回值为8位字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
			// BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
			// 一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方）
			result = new BigInteger(1, md.digest()).toString(16).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		System.err.println("加密后数据(签名)：" + result);
		return result;
	}

	/**
	 * @Description 获取客户端公网IP地址
	 * @param request
	 * @return 公网IP地址
	 */
	public static String getIpAddress(HttpServletRequest request) {
		// 避免反向代理不能获取真实地址, 取X-Forwarded-For中第一个非unknown的有效IP字符串
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		System.err.println("客户端IP地址：" + ip);
		return ip;
	}

	/**
	 * Map转XML字符串
	 * @param obj 待转换的Map
	 * @return 转换后的XML格式的Map数据
	 */
	public static String Map2XMLStr(Map<String, String> obj) {
		String result = "<xml>";

		Set<String> keys = obj.keySet();
		for (String key : keys) {
			result += "<" + key + ">" + obj.get(key) + "</" + key + ">";
		}

		result += "</xml>";
		System.out.println("转换结果：" + result);
		return result;
	}

	/**
	 * XML字符串转成Map
	 * @param xmlStr 待转换的XML字符串
	 * @return 转化后的Map对象
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> XMLStr2Map(String xmlStr) {
		// 返回的结果数据
		Map<String, String> result = new HashMap<>();

		// 创建SAXReader的对象reader
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(new ByteArrayInputStream(xmlStr.getBytes("UTF-8")));
			// 获取根节点
			Element rootEle = document.getRootElement();
			// 获取迭代器
			Iterator<Element> iterator = rootEle.elementIterator();
			// 遍历迭代器，获取根节点中的信息
			Element tmp = null;
			while (iterator.hasNext()) {
				tmp = iterator.next();
				result.put(tmp.getName(), tmp.getText());
				tmp = null;
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		System.err.println("转换结果：" + result.toString());
		return result;
	}

	/**
	 * 发起网络请求获取预交易订单号
	 * @param str 转换成XML格式的String数据
	 * @return 微信服务器返回的数据
	 */
	public static String request(String str) {
		try {
			URL url = new URL(WXSERVER);

			// 请求微信服务器
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(5000);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			// 设置文件类型:
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			byte[] postData = str.getBytes();
			// 设置文件长度
			conn.setRequestProperty("Content-Length", String.valueOf(postData.length));
			OutputStream os = conn.getOutputStream();
			os.write(postData);
			os.flush();
			os.close();

			if (conn.getResponseCode() == 200) {
				// 用getInputStream()方法获得服务器返回的输入流
				InputStream in = conn.getInputStream();

				// 获取服务器返回数据,将数据转成字符串
				ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
				byte[] buff = new byte[100];
				int rc = 0;
				while ((rc = in.read(buff, 0, 100)) > 0) {
					swapStream.write(buff, 0, rc);
				}
				byte[] byteData = swapStream.toByteArray();

				String data = new String(byteData, "UTF-8");
				System.out.println("成功获取微信服务器返回的数据" + data);

				in.close();
				return data;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

	public static void main(String[] args) {
		/*
		 * // XML格式的String转Map String xmlStr =
		 * "<xml><name>黎明</name><age>25</age><address>哈尔滨</address></xml>";
		 * System.out.println(XMLStr2Map(xmlStr));
		 */

		/*
		 * // Map转XMl格式的String Map<String,String> obj = new HashMap<>();
		 * obj.put("name", "大王"); obj.put("age", "18"); obj.put("address",
		 * "北京"); System.out.println(Map2XMLStr(obj));
		 */

		// 获取预支付交易单号
		// request("023YqlOW0vNSM22z0FNW07mkOW0YqlOA");
		System.out.println(UUID.randomUUID().toString().toUpperCase().replace("-", ""));
	}
}
