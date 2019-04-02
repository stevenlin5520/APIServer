package com.steven.util;

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
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 
 * @Description 微信支付相关工具类
 * @author Steven
 * @date 2019年3月22日 下午3:36:16
 */
public class WxPayUtil {

	// 微信服务后台获取预支付交易单的地址
	private static final String WXSERVER = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	// 小程序APPID
	private static final String APPID = "wxd9564b8666be8516";
	// 商户号
	private static final String MCHID = "1524932721";
	// 商户私钥
	private static final String KEY = "4F0FC23BAA304EEDB63D23DDC8C49F72";
/*	// 小程序APPID
	private static final String APPID = "wxd9564b8666be8516";
	// 商户号
	private static final String MCHID = "1524932721";
	// 商户私钥
	private static final String KEY = "4F0FC23BAA304EEDB63D23DDC8C49F72";
*/	
	
	/**
	 * 
	 * @Descript 通过发起请求到微信支付服务后台生成预支付交易单
	 * @param postMap 请求参数      比如  {"body":"测试支付","total_fee":125,"openid":"oJgER5QnqUfbXPaV1_Y828cyvo70","payType":0}
	 * @param request HttpServletRequest
	 * @return JSON数据
	 */
	public static Map<String, String> execute(Map<String, String> postMap, HttpServletRequest request){
		
		Map<String, String> obj = new HashMap<>();
		
		// 小程序ID String(32) 微信分配的小程序ID
		obj.put("appid", APPID);
		// 商户号 String(32) 微信支付分配的商户号
		obj.put("mch_id", MCHID);
		// 随机字符串 String(32)
		obj.put("nonce_str", UUID.randomUUID().toString().replace("-", ""));
		// 商品描述 String(128)
		try {
			obj.put("body", String.valueOf(postMap.get("body").getBytes("UTF-8")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 商户订单号 String(32) 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*且在同一个商户号下唯一
		obj.put("out_trade_no", UUID.randomUUID().toString().replace("-", ""));
		// 标价金额 int 订单总金额，单位为分
		obj.put("total_fee", postMap.get("total_fee"));
		//TODO 没有数据应该返回提醒
		
		// 终端IP String(64) 支持IPV4和IPV6两种格式的IP地址。调用微信支付API的机器IP
		obj.put("spbill_create_ip", getIpAddress(request));
		// 通知地址 String(256) 异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数
		obj.put("notify_url", "http://"+getIpAddress(request)+":8080/APIServer/login/login");
		// 交易类型 String(16)
		// JSAPI--JSAPI支付（或小程序支付）、NATIVE--Native支付、APP--app支付，MWEB--H5支付
		if("1".equals(postMap.get("payType"))){
			obj.put("trade_type", "NATIVE");
		}else if("2".equals(postMap.get("payType"))){
			obj.put("trade_type", "APP");
		}else if("3".equals(postMap.get("payType"))){
			obj.put("trade_type", "MWEB");
		}else {
			obj.put("trade_type", "JSAPI");
		}
		// 用户标识 String(128) trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识
		if(!"1".equals(postMap.get("payType")) && !"2".equals(postMap.get("payType")) && !"3".equals(postMap.get("payType"))){
			obj.put("openid", postMap.get("openid"));
		}

		
		
		/**
		 * 签名生成的通用步骤如下：
			第一步，设所有发送或者接收到的数据为集合M，将集合M内非空参数值的参数按照参数名ASCII码从小到大排序（字典序），使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串stringA。
			
			特别注意以下重要规则：
			
			    ◆ 参数名ASCII码从小到大排序（字典序）；
			    ◆ 如果参数的值为空不参与签名；
			    ◆ 参数名区分大小写；
			    ◆ 验证调用返回或微信主动通知签名时，传送的sign参数不参与签名，将生成的签名与该sign值作校验。
			    ◆ 微信接口可能增加字段，验证签名时必须支持增加的扩展字段
			
			第二步，在stringA最后拼接上key得到stringSignTemp字符串，并对stringSignTemp进行MD5运算，再将得到的字符串所有字符转换为大写，得到sign值signValue。
			
			◆ key设置路径：微信商户平台(pay.weixin.qq.com)-->账户设置-->API安全-->密钥设置
		 */
		String stringA = firstSign(obj);
		String stringSignTemp = stringA + "&key=" + KEY;
		String signValue = MD5Encryption(stringSignTemp);
		
		// 签名 String(32) 通过签名算法计算得出的签名值
		obj.put("sign", signValue);

		// 将数据转换成XML格式的String
		String requestData = Map2XMLStr(obj);
		// 发起请求获取预支付交易单
		String result = request(requestData);
		
		return XMLStr2Map(result);
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
				System.out.println("成功获取微信服务器返回的数据:\n" + data);

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
		System.out.println("map转换XML结果：\n" + result);
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

		System.err.println("XML转换Map结果：\n" + result.toString());
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
	
	
}
