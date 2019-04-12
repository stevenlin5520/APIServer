package com.steven.util;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeCancelRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;

/**
 * 
 * @Description 支付宝支付工具类
 * @author Steven
 * @date 2019年3月28日 上午10:03:08
 */
public class AlipayUtil {

	final static private String SERVER = "https://openapi.alipaydev.com/gateway.do";
	final static private String APPID = "2016092700607081";
	final static private String PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCysSiFx4/+axCYsEC7gLZBrrR1+ta2WJVpR7AvD38fBso7X4Ss/6rZaRFnviEIPOD9KXQpZcYJdV0V6BvIWEok0FIWwtkCxRBmSzGa2QBsa39SKTbfOggJNictmfmxH4dquKvfJNJ8VKiMnrwITXMQImKiceBX6Cl/UlXDpM86TtR3OJow5zTNMhNeA7oScgam5k0+k6VsCM7ITi4VxIpLZCR1qmcHXw7T/0YCIbj6nYZLP0cV0fpUErKYEIyVtm43ndHYAkLPB6XStJhrDmfu9jm3IiVptFtm6Np8Bm3KVhVXgowLCAUKvc8U/+EUN6mqKcPuPxTXl5Y6iBZriw8zAgMBAAECggEAev2c2peE8KRYg/+sTmJFD+hjpWDFMDTAgfoptfqAkKZnw333H+vJeGLs6+7dZYIhiZ5iqau3JwN00oMzvC2ourX4ClqKa3Gf3AidV+tMc1N6Ct77OaIDrfNr++MjDVqaJkZ9ZdU+Wv0aFc+btEs6izxfj1e6BkdxaIUPKt3hFEvPRA4TVcmbAz6aZ4lVTmzOreZCsZ+dnA8zp7kJ65zrjimt79Ao9OmYnAB+FDdR2T21AFhxjpkvChw5SDq3e8HBTq+XixhAKiG7dqu0ifucfC481JYFnCJm7F6tOm6qxMFjq9xyoJnAm3v7PwROGAYN117zdD6bJhAg+gb5gf3vwQKBgQDbXo0tR7Ye3kRp8laN5G+qCIRPlD0flOITMpxW0GrdQX8+Au3h+fLpht+kAI3sEeoTSSuRUzGYBEwx5XxBpscCkX5xDZQlt4oXdKvhglSS8qqvdpOVI/d6LltZg2R7RaMnOdaPZ9Uue6CN64WliWZpIMnFPJbkmc35p2EYH0KBiwKBgQDQh8LVbWuPISgbknldK4XXE1snjK1hqCH9ZrjkQJEjrf5BuIHyVE2sF1al/14OJhCCy+Ge4g1LYzYyCSlCP1utBodnilies4ywTZpqGg3yuaEYOzqI0HD5BMM0mTh4qYq5X8yju/DLOWRFTJ8MI/sOkD+4e+8/CeWhVG31pLUN+QKBgQDSdzOhtsSPdxZSQ5QxTHKpwsQWiZXfH/uLCaqfTBTLNJS8zRioDyO0U5drhLClRka6vB/VrOjO/YiQ8qi1YCpdV3PlSW7ShPYzLNffYyh7V+DUuUI8+6XrDmDMT9huwNAYyVAGhEsu7qOKpeWyfVHJGREcb9Yhe0L3/FOWTh+DowKBgA9rWn7JnoBw5TRUumCnTThAGCdugYuNESZZqCZIl/LZdMmLSSCQF0Y4W5Hetie5rNLfHInREUaXyXQdtU1OvgqnRu/jDPZ/NFH9CwP5dTqTe1P9XTCsqQ3gykZjZgeWEcnvSYR56y47yixNpB0TtAa5p9yOXXx90NsDETZuvWyRAoGAJBJK8PaOSyDU2P/AtLxpTy5rirPnxCRfnCfuq7BxSHnILeBHclEYAu4JvIYvKDdwFSJLhh1rIvhd2EeA+vcobk7ECoMhKW43jmo0J9zYYQ1L2M62SuZjT5CBW5jh5ddc7d8aqrhrou094EsGgSyU1LIpw6Em4xM3hPI0RrBGCbs=";
	final static private String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzC7TX76QJ901q+FgvYfjXCZc4wY+WcAFvNmQlHFfK6T2lJdDwRkWPxg1LM/7kek5F0/RwpZdWPPKWMV6kDNxC8mGyxoOTcfOWkYGKSVi9gBNwSwA9uOpvooIcrEqmmYrmw+SVzPt9f9l4gQZxBNUPus7+4jgGMzDr0ij+gSV8KZpSz+Sb/yCHK6fDL9EPVv7ETo8lyacXIn333+xu30tT89ytMAVDL1Q7EHdJzZMBSAii9vD5PU0/fNSgI9Eqo3Pa4fST+kkPZq37JQtDLIk5wQS1hKvdEGqkZiIXVZkBSI0xzK12PyEkcLGI97Y0WprJcqiCn2PURlnMoc9BzY7nwIDAQAB";
	
	//安丽
//	final static private String SERVER = "https://openapi.alipaydev.com/gateway.do";
//	final static private String APPID = "2016092700604784";
//	final static private String PRIVATE_KEY = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQComzFuizaB3KJ5kXxqQi/NnN/TTVmWdQpfvZGLkVNrUfwjXI4/gAKAUbxks+1zR/mD62pUaY4rwnVQRGKMPI1K/k6vjmvYMMJkznhkFTO+QomQ4kE41tqsuyd3VaNx8nzb6ReIwZjnFt86QLRV/L3WkgHB2Ppz5/K34OMS1LRmPUwAeyaC3fb+h1t72NHtVM2+le0dVh17deGDCEGJB87UoR3tqIganZdQcFOCgBFnzMB6v0FX1QMEZqne349AaCA5UaRvQxWTALsWyaKaJY8yMv1XUu/qoCINGSxw1dkPJtnnjYC2v+k6RxJR9atLb2tJ0eTrr8muhLxGxyL3vNOnAgMBAAECggEARpY6WqpDlph1GOoUqTOuaalMd10BraCwSCbIASNFRj/bjD5aezEvWuNdONevCD1Qs67iJBbQ43cUwBbxHv7WtFfFP3AjrC21pJhgBJ5vvC6FAzgF0DL696eevTIILFM1fKeYJ7PNPKnxqKSgGwOlwmf7/ljFebLYhazLw/1yzP4cZF76nKgUjlz/H2OkqiKwb8ChEbNveSbv7+xgT/On4253dr/QUCt/wz3WyBKMmN7f50CitSdd2rLe2VW8Cf6lcDFeRsjaYfse9WOtdSNObePQZ5UGs5g8G0qdI/pCKodBP0f61lCSSeMsUjATD/JxLuy43b6LqSAcoo1OLrcEeQKBgQDZ7wsYk6WB6mH/0xBcGsOv6nA1eL2+NTnM5g52mJY3ld+p4JZnRMHeUpxlLC6bsjsOewXo8rg7pJMMQulmikg93b1b7CJIgfcKMCtr3631p8dJEoCfsFw++odYdHH6frhcp7zEG76IWb5WrntYVp9A+2/+Yy7q8eFQncC5hVpQ4wKBgQDGDnU0e2Uey7VXXRsXITNi+2QCW7XRRyBn0HLyVrcasSJxyoOWDd7u1Kph2WO2mUZV0nKS9tqF6UHz4HXck8z6nlJlfcCoihFmB0cuPUhTiq4A88XpGft8QVYn3HxLkvJnzYihOia9xrjDpmwtPgsTnG7ChcjN3tYuNoNGUUKBbQKBgQCib9oFZ4dlXh3NYLRA8nIJHOSl9mj27RU39AGzw+ezZrJfHFb+gfE+lLSddGEJC7+Y+3lcOA255uDzsqvlEgzmj/AkI80p3jUUEvNKZF51S6jufiwA2qLnDjTzaVfLLcicxymbwlIxrzJ/yW2GWoo5FTBUm/jMhZ07SVns7chzoQKBgQCTovFhht4xH571GdV432njzguV6nFVKJUSbgNRfKyPfB11/4UpVPuqwuKjgB5hXk5c6Tw5K0Rn8CIWvmp9W7u/0kcPmrcDq7AegyxfXpVMmSLuWMld4t1hmX60xhntaAPEtySzMprwenL+fU9ERmyRWNoDrSCrvgGgf0hKIYbW3QKBgQDU8o8ZEL6y0bFZ4B3FmfdtnYvNUiK9NTF9832BMbkRkBvqDmrZgkgMeAsWw/il3l0kCe9XocA1y+CROPZCbbAKPm2siglBe1Ygu0puJ1l2VHY/7VCb3PyvOwqv+fwjQwGTJcAwFspwArX5s9enW6ptg9azfvqnDgoxZ9B25pQuzw==";
//	final static private String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwjXqOw70aL9T4bgbItPj6rr+aWJDWbBtXr8VaW8eDUzjvReOEsTf2b3LQIVtvZqIThjiTXNGLx8MZ3Ny5kulipYwsaqyYS8clX2doPoishkhOR18kV3+e9LXETZZxwhmOgEC25r06XqDDWNC1tTn5k/Gnzn9IpnwxL6lDVqdSlwf1zvqVHlGRRWiKHcYTIqeJhHC8QsywRiReekCXHSPorqHYG1LLb2MmxFAC/Z6xd25c7XLW9qj0Kf+xei2+J7uWQR3eKKsZ+HdJmW/ZguvreQP9ffO5uHSqbJD+voTDGFB1342+Wo2WbnLt/4yj2UQOdbSe0LybIgqonqhd6tefQIDAQAB";
	
//	final static private String SERVER = "https://openapi.alipay.com/gateway.do";
//	final static private String APPID = "2019011162895179";
//	final static private String PRIVATE_KEY = "MIIEugIBADANBgkqhkiG9w0BAQEFAASCBKQwggSgAgEAAoIBAQDN2+et2UVm7c5VXc+aIdsk1Kbpl5KW/jYg3RDCPkYueOjxYcqyKBbllAw6asdR7NbeXA+6ezpZzojQQsnpTuipRahpMAymMZi3pOUxRbzGapbuYQDvkU4b99OYHlMA8MN364cRByeeh3aT5v0O1MedXc5Gb4e31jmDRmNNmsCUEj1NB69F8hDCHJch0wLSTbs9c5ie4kIzuWNfsscPuMzs4hSkvZrW5qA2O+CCciZBr8IKQ2c5w+5oQTeCU997+HV+XQoEMtogRtiw+agZuFAeBADKEA7fb0v7RQ9TUc55mUQV5wn6ACg6UIUXuv51axjP1U03IqClaZ1S6QvoVPAbAgMBAAECggEATWDNUuGKXsggeKIMeMklEFEGdGNO7IadXfj6Zaj2xF0aYjD5+cFr6dueRQ8B4N9/XJGKL/RF2J/zeSTBtHq+pXHTfcZb2tRlrRHEPKMT9Bj4pp27tlEN5SFgXZt4Y7AcKSVU3aes0r/kaG+4yyxb4+DwujUN9KIHeDqq9O6oWH9dtyr3DrlieElFK3t19rl0xlLyEWnS9SxiVZU1RioHWrGg9LGeEOZf7iKeGMP7gKo/FTNO/UC2A1DuHAO7hCIIMKU3xpI0c9z5NAQPjiucZhGdajthpJEGClEmKBt2IRFVycF5EBvF3QwqvtQANTM1YV7D45DLj96ZABFKsec3IQKBgQD0JcDisZzN5P/b6/U9dVTo1/S7Lh8Ik7zaQKpu14cfTX8F0iz+jexv/qqHYuOfI+tA8xMOk6o957g+mgMdWcusfkI+g4Ee6zNcegt3hfxX9zh5unhyVWFzpW7NuZz7H8GC2bOBG3qpdWYhryPpOA0VJxB26xU5r8tpxwO/z7ku6QKBgQDX2k4niOBWUjv/GtcVjAiaMrOh2SRzOoZB5NIRZKisWOWw1SguryXzkC+uLdBenyxTCdILyGByYKm2qH0TRtfWIQh6x1xsBLzjBi2bZ+iroIyZH8EOgAIjH75uuxcCEYozJHksnLWxuUasNJ8oRADlCbne5flAIKPIhXMUZxHsYwKBgFTjp5xqyy2f2QKbmWqkGEDZjcNjdvDhhYGRiHD/yYCtzmO0v2ZG7JXIQ3zNen2D3LF5kv+3gjMCFcLWhAy5gyLXx6q6yLN6biJSYRhO4v2m/OD+YBDI9RhjxhIL3rRGR1962HokltLC+h7uKZtWnN3k9kmS1Y94O07tHrWbwTS5AoGAW3uqpBbe/bbctsIpZnhzLOdQOshNKokjXcodPkHrOyByOQAeh6qz6waxLqE8Lt5Hauo+m7/RIKjXRRMj74eW8qoNbfwQr7dSVuXHbpYbhqIE9OEAcaTPQxOxIj0pAzdr20EaGk5Qs4JS8albyzUHLAtzO4U8nnE2+UWWAI+uiQECf1rBY6bn1aMlMhxLDVeGnZq+bDPX2oJfpBDnvxAiYOnwtybolC5aUALhHptMPEcDHJhSrgRtAOGYGmRGTMUzmiMO2pzjw15i3RKupXrn22YB0Cr8LWpIV0iHFWyLgUUbvF9OCpH5MOTZRPCjriVv1kn3LTfZvvq/yPvPho0Doo8=";
//	final static private String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzdvnrdlFZu3OVV3PmiHbJNSm6ZeSlv42IN0Qwj5GLnjo8WHKsigW5ZQMOmrHUezW3lwPuns6Wc6I0ELJ6U7oqUWoaTAMpjGYt6TlMUW8xmqW7mEA75FOG/fTmB5TAPDDd+uHEQcnnod2k+b9DtTHnV3ORm+Ht9Y5g0ZjTZrAlBI9TQevRfIQwhyXIdMC0k27PXOYnuJCM7ljX7LHD7jM7OIUpL2a1uagNjvggnImQa/CCkNnOcPuaEE3glPfe/h1fl0KBDLaIEbYsPmoGbhQHgQAyhAO329L+0UPU1HOeZlEFecJ+gAoOlCFF7r+dWsYz9VNNyKgpWmdUukL6FTwGwIDAQAB";
	
	/**
	 * 
	 * @Descript alipay.trade.precreate(统一收单线下交易预创建)   
	 * @Descript 收银员通过收银台或商户后台调用支付宝接口，生成二维码后，展示给用户，由用户扫描二维码完成订单支付
	 * @param orderID 订单号
	 * @param money	支付金额,分（最多到小数点后两位）
	 * @return
	 */
	public static Map<String,Object> prePay(String orderID,String money){
		//返回数据
		Map<String, Object> result = new HashMap<>(16);
		
		AlipayClient alipayClient = new DefaultAlipayClient(SERVER,APPID,PRIVATE_KEY,"json","utf-8",PUBLIC_KEY,"RSA2");
		AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
		//同步回调	支付成功后，支付宝将此地址发往浏览器客户端，重定向到此该地址
		//request.setReturnUrl("http://231q021j08.imwork.net:30711/APIServer/alipay/returnurl");
		//异步回调	支付完成后，支付宝异步回调此接口，在此做业务处理（注：此地址必须是公网可以访问到的）
		request.setNotifyUrl("http://231q021j08.imwork.net:30711/APIServer/alipay/notifyurl");
		request.setBizContent("{" +
				//商户订单号,64个字符以内、只能包含字母、数字、下划线；需保证在商户端不重复
				"\"out_trade_no\":\""+orderID+"\"," +
				//【可选】   卖家支付宝用户ID。 如果该值为空，则默认为商户签约账号对应的支付宝用户ID
				//"\"seller_id\":\""+seller_id+"\"," +
				//订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000] 如果同时传入了【打折金额】，【不可打折金额】，【订单总金额】三者，则必须满足如下条件：【订单总金额】=【打折金额】+【不可打折金额】
				"\"total_amount\":"+money+"," +
				//【可选】	可打折金额. 参与优惠计算的金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000] 如果该值未传入，但传入了【订单总金额】，【不可打折金额】则该值默认为【订单总金额】-【不可打折金额】
				//"\"discountable_amount\":8.88," +
				//订单标题
				"\"subject\":\"Iphone6 16G\"," +
				 //【可选】	对交易或商品的描述
				"\"body\":\"Iphone6 16G商品主体\"," +
				 //【可选】	该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m
				"\"timeout_express\":\"2m\"," +
				 //【可选】	该笔订单允许的最晚付款时间，逾期将关闭交易，从生成二维码开始计时。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
				"\"qr_code_timeout_express\":\"2m\"" +
				"  }");
		try {
			AlipayTradePrecreateResponse response = alipayClient.execute(request);
			if(response.isSuccess()){
				System.out.println("成功获取到预支付数据！"+response.getBody());
				if(response.getCode() != null && "10000".equals(response.getCode())){
					result.put("result", "SUCCESS");
					//获取支付宝网关返回的二维码url地址
					result.put("qrcode", response.getQrCode());
					result.put("orderID", response.getOutTradeNo());
				}else{
					result.put("result", "FAIL");
					result.put("message", "没有获取到支付宝二维码数据！");
				}
			} else {
				System.out.println(response.getSubMsg());
				result.put("result", "FAIL");
				result.put("message", response.getSubMsg());
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
			result.clear();
			result.put("result", "FAIL");
			result.put("message", "获取预支付数据出错!");
		} 
		
		return result;
	}
	
	/**
	 * 
	 * @Descript alipay.trade.query(统一收单线下交易查询)
	 * @Descript 该接口提供所有支付宝支付订单的查询，商户可以通过该接口主动查询订单状态，完成下一步的业务逻辑。 需要调用查询接口的情况： 当商户后台、网络、服务器等出现异常，商户系统最终未接收到支付通知； 调用支付接口后，返回系统错误或未知交易状态情况； 调用alipay.trade.pay，返回INPROCESS的状态； 调用alipay.trade.cancel之前，需确认支付状态；
	 * @return
	 */
	public static Map<String,Object> queryPay(String orderID){
		//返回数据
		Map<String, Object> result = new HashMap<>(16);
		
		AlipayClient alipayClient = new DefaultAlipayClient(SERVER,APPID,PRIVATE_KEY,"json","utf-8",PUBLIC_KEY,"RSA2");
		AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
		request.setBizContent("{"
				+ "\"out_trade_no\":\"" + orderID +"\""
				+ "}");
		
		try {
			AlipayTradeQueryResponse response = alipayClient.execute(request);
			System.out.println("成功获取到订单查询数据：\n"+response.getBody());
			if(response.isSuccess()){
				if(response.getCode() != null && "10000".equals(response.getCode())){
					result.put("result", "SUCCESS");
					result.put("orderID", response.getOutTradeNo());
					result.put("status", response.getTradeStatus());
				}else{
					result.put("result", "FAIL");
					result.put("message", "未查到订单或交易不成功！");
				}
			}else{
				System.out.println(response.getSubMsg());
				result.put("result", "FAIL");
				result.put("message", response.getSubMsg());
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
			result.clear();
			result.put("result", "FAIL");
			result.put("message", "获取订单查询数据出错!");
		}
		
		return result;
	}
	
	/**
	 * 
	 * @Descript alipay.trade.wap.pay（手机网站支付接口2.0）   
	 * @Descript 用户通过H5网页调用支付宝支付
	 * @param orderID 订单号
	 * @param money	支付金额,分（最多到小数点后两位）
	 * @return
	 */
	public static Map<String,Object> h5Pay(String orderID,String money){
		//返回数据
		Map<String, Object> result = new HashMap<>(16);
		
		AlipayClient alipayClient = new DefaultAlipayClient(SERVER,APPID,PRIVATE_KEY,"json","utf-8",PUBLIC_KEY,"RSA2");
		AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
		//同步回调	支付成功后，支付宝将此地址发往浏览器客户端，重定向到此该地址
		request.setReturnUrl("http://231q021j08.imwork.net:30711/APIServer/return_url.html");
		//异步回调	支付完成后，支付宝异步回调此接口，在此做业务处理（注：此地址必须是公网可以访问到的）
		request.setNotifyUrl("http://231q021j08.imwork.net:30711/APIServer/alipay/notifyurl");
		request.setBizContent("{" +
				//商户订单号,64个字符以内、只能包含字母、数字、下划线；需保证在商户端不重复
				"\"out_trade_no\":\""+orderID+"\"," +
				//【可选】   卖家支付宝用户ID。 如果该值为空，则默认为商户签约账号对应的支付宝用户ID
				//"\"seller_id\":\""+seller_id+"\"," +
				//订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000] 如果同时传入了【打折金额】，【不可打折金额】，【订单总金额】三者，则必须满足如下条件：【订单总金额】=【打折金额】+【不可打折金额】
				"\"total_amount\":"+money+"," +
				//【可选】	可打折金额. 参与优惠计算的金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000] 如果该值未传入，但传入了【订单总金额】，【不可打折金额】则该值默认为【订单总金额】-【不可打折金额】
				//"\"discountable_amount\":8.88," +
				//订单标题
				"\"subject\":\"Iphone6 16G\"," +
				 //【可选】	对交易或商品的描述
				"\"body\":\"Iphone6 16G商品主体\"," +
				 //【可选】	该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m
				"\"timeout_express\":\"2m\"," +
				 //【可选】	该笔订单允许的最晚付款时间，逾期将关闭交易，从生成二维码开始计时。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
				"\"qr_code_timeout_express\":\"2m\"" +
				"  }");
		try {
			AlipayTradeWapPayResponse response = alipayClient.pageExecute(request);
			String body = response.getBody();
			System.out.println("手机网站支付2.0接口获取到数据:\n"+body);
			if(body == null || body.isEmpty()){
				result.put("result", "FAIL");
				result.put("message", "H5支付失败");
			}else{
				result.put("result", "SUCCESS");
				result.put("orderID", orderID);
				result.put("Form", body);
				result.put("message", "成功获取到支付数据!");
			}
			
			return result;
		} catch (AlipayApiException e) {
			e.printStackTrace();
			result.clear();
			result.put("result", "FAIL");
			result.put("message", "获取支付数据出错!");
			return result;
		} 
		
	}
	
	
	/**
	 * 
	 * @Descript alipay.trade.app.pay(app支付接口2.0)
	 * @Descript 外部商户APP唤起快捷SDK创建订单并支付
	 * @param orderID 订单号
	 * @param money	支付金额,分（最多到小数点后两位）
	 * @return
	 */
	public static Map<String,Object> appPay(String orderID,String money){
		//返回结果
		Map<String,Object> result = new HashMap<>(16);
		
		AlipayClient alipayClient = new DefaultAlipayClient(SERVER,APPID,PRIVATE_KEY,"json","utf-8",PUBLIC_KEY,"RSA2");
		AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
		//异步回调	支付完成后，支付宝异步回调此接口，在此做业务处理（注：此地址必须是公网可以访问到的）
		request.setNotifyUrl("http://231q021j08.imwork.net:30711/APIServer/alipay/notifyurl");
		request.setBizContent("{" +
				//商户订单号,64个字符以内、只能包含字母、数字、下划线；需保证在商户端不重复
				"\"out_trade_no\":\""+ orderID +"\"," +
				//订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000] 如果同时传入了【打折金额】，【不可打折金额】，【订单总金额】三者，则必须满足如下条件：【订单总金额】=【打折金额】+【不可打折金额】
				"\"total_amount\":\""+ money +"\"," +
				//订单标题
				//订单标题
				"\"subject\":\"Iphone6 16G\"," +
				 //【可选】	对交易或商品的描述
				"\"body\":\"Iphone6 16G商品主体\"," +
				 //【可选】	该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m
				"\"timeout_express\":\"2m\"," +
				"  }");
		try{
			AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
			String body = response.getBody();
			System.out.println("App支付2.0接口获取到数据"+body);
			if(body == null || body.isEmpty()){
				result.put("result", "FAIL");
				result.put("message", "H5支付失败");
			}else{
				String body2 = URLDecoder.decode(body,"utf-8");
				String body3 = body2.replace("&quot;", "\"");
				result.put("result", "SUCCESS");
				result.put("orderID", orderID);
				result.put("Form", body3);
				result.put("message", "成功获取到支付数据!");
			}
			return result;
		}catch(Exception e){
			e.printStackTrace();
			result.clear();
			result.put("result", "FAIL");
			result.put("message", "获取支付数据出错!");
			return result;
		}
	}

	
	/**
	 * 
	 * @Descript alipay.trade.cancel(统一收单交易撤销接口)
	 * @Descript 支付交易返回失败或支付系统超时，调用该接口撤销交易。如果此订单用户支付失败，支付宝系统会将此订单关闭；如果用户支付成功，支付宝系统会将此订单资金退还给用户。 注意：只有发生支付系统超时或者支付结果未知时可调用撤销，其他正常支付的单如需实现相同功能请调用申请退款API。提交支付交易后调用【查询订单API】，没有明确的支付结果再调用【撤销订单API】
	 * @return
	 */
	public static Map<String,Object> cancelPay(String orderID){
		//返回数据
		Map<String, Object> result = new HashMap<>(16);
		
		AlipayClient alipayClient = new DefaultAlipayClient(SERVER,APPID,PRIVATE_KEY,"json","utf-8",PUBLIC_KEY,"RSA2");
		AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
		request.setBizContent("{"
				+ "\"out_trade_no\":\"" + orderID + "\""
				+ "}");		
		
		try {
			AlipayTradeCancelResponse response = alipayClient.execute(request);
			System.out.println("成功获取到订单撤销API数据!\n"+response.getBody());
			
			if(response.isSuccess()){
				if(response.getCode() != null && "10000".equals(response.getCode())){
					result.put("result", "SUCCESS");
					result.put("orderID", response.getOutTradeNo());
					result.put("message", "已成功撤销订单支付！");
					return result;
				}else{
					result.put("result", "FAIL");
					result.put("message", "订单支付撤销失败！");
					return result;
				}
			}else{
				System.out.println(response.getSubMsg());
				result.put("result", "FAIL");
				result.put("message", response.getSubMsg());
				return result;
			}
			
		} catch (AlipayApiException e) {
			result.clear();
			result.put("result", "FAIL");
			result.put("message", "撤销订单API异常！");
			e.printStackTrace();
			return result;
		}
				
	}
	
	
	/**
	 * 
	 * @Descript 将JSON格式的数据转换成Map数据
	 * @param data JSON字符串数据
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String,Object> json2Map(String data){
		//转换第一层map
		Map<String,Object> map = JSON.parseObject(data, Map.class);
		for (Object item : map.entrySet()) {
			if(((Map.Entry)item).getValue() != null && ((Map.Entry)item).getValue() != "" && ((Map.Entry)item).getValue().toString().contains("\"")){
				//转换第二层map
				Map map2 = JSON.parseObject(((Map.Entry)item).getValue().toString(), Map.class);
				
				for (Object item2 : map2.entrySet()) {
					if(((Map.Entry)item2).getValue() != null && ((Map.Entry)item2).getValue() != "" && ((Map.Entry)item2).getValue().toString().contains("\"")){
						//转换第三层map
						map2.put(((Map.Entry)item2).getKey().toString(), JSON.parseObject(((Map.Entry)item2).getValue().toString(), Map.class));
					}
				}
				
				map.put(((Map.Entry)item).getKey().toString(), map2);
			}
			
		}
		System.out.println("JSON转换后的数据：\n"+map);
		
		return map;
	}	
	
	
	public static void main(String[] args) {
		System.out.println(queryPay(201846+""));
	}
	
}
