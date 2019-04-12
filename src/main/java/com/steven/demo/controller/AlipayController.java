package com.steven.demo.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.steven.util.AlipayUtil;

/**
 * 
 * @Description 支付宝支付接口
 * @author Steven
 * @date 2019年3月28日 上午10:01:17
 */
@Controller
@RequestMapping("/alipay")
public class AlipayController {

	/**
	 * 
	 * @Descript alipay.trade.precreate(统一收单线下交易预创建)
	 * @return
	 */
	@RequestMapping("/pay")
	@ResponseBody
	public Map<String,Object> payMoney(String money){
		System.out.println("请求支付宝预支付");
		//获取预支付信息
		Map<String, Object> result = AlipayUtil.prePay(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase(), money);
		return result;
	}
	
	
	/**
	 * 
	 * @Descript alipay.trade.query(统一收单线下交易查询)
	 * @param orderID 订单号
	 * @return
	 */
	@RequestMapping("/query")
	@ResponseBody
	public Map<String,Object> query(String orderID){ 
		System.out.println("请求支付宝订单查询");
		Map<String, Object> result = AlipayUtil.queryPay(orderID);
		/*if(result != null && "SUCCESS".equals(result.get("result"))){
			if("TRADE_SUCCESS".equals(result.get("status")) || "TRADE_FINISHED".equals(result.get("status"))){
				return result;
			}
		}*/
		
		return result;
	}
	
	
	/**
	 * 
	 * @Descript alipay.trade.wap.pay（手机网站支付接口2.0）
	 * @param money 付款金额
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/h5")
	public Map<String,Object> h5Pay(String money){
		System.out.println("请求支付宝H5支付");
		return AlipayUtil.h5Pay(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase(), money);
	}
	
	
	/**
	 * 
	 * @Descript alipay.trade.query(统一收单线下交易查询)
	 * @param orderID 订单编号
	 * @return
	 */
	@RequestMapping("/queryStatus")
	@ResponseBody
	public Map<String,Object> queryStatus(String orderID){
		//等待请求10次
		int count = 0;
		
		while(true){
			//获取
			Map<String, Object> result = AlipayUtil.queryPay(orderID);
			result.remove("aaa");
			System.out.println(result.toString());
			if(result != null && "SUCCESS".equals(result.get("result")) && "TRADE_SUCCESS".equals(result.get("status")) || "TRADE_FINISHED".equals(result.get("status"))){
				System.out.println("支付成功！");
				result.remove("status");
				return result;
			}else{
				System.out.println("未支付！");
				try {
					Thread.sleep(3000);
					count++;
					if(count>=40 || (result != null && result.containsKey("trade_status") && "TRADE_CLOSED".equals(result.get("status")))){
						//result.remove("status");
						//return result;
						/**
						 * 超时支付
						 */
						Map<String, Object> cancelResult = AlipayUtil.cancelPay(orderID);
						System.out.println("超时支付----"+cancelResult.toString());
						if(cancelResult != null && "SUCCESS".equals(cancelResult.get("result"))){
							result.remove("status");
							return result;
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 
	 * @Descript 支付宝服务器异步通知商户服务器里指定的页面
	 * @param request
	 * @return
	 */
	@RequestMapping("/notifyurl")
	@ResponseBody
	public String notifyUrl(HttpServletRequest request){
		System.out.println("AlipayController.notifyUrl()");
		
		try {
			InputStream is = request.getInputStream();
			int len = 0;
			byte[] buf = new byte[1024];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((len = is.read(buf)) != -1) {
				baos.write(buf);
			}
			byte[] resultByte = baos.toByteArray();
			
			
			System.out.println(URLDecoder.decode(new String(resultByte)));
			System.out.println(URLDecoder.decode(baos.toString("utf-8")));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "notify_url.html";
	}
	
	
	/**
	 * 
	 * @Descript 支付宝服务器异步通知商户服务器里指定的页面
	 * @param request
	 * @return
	 */
	@RequestMapping("/returnurl")
	@ResponseBody
	public String returnUrl(HttpServletRequest request){
		System.out.println("AlipayController.returnUrl()");
		
		try {
			InputStream is = request.getInputStream();
			int len = 0;
			byte[] buf = new byte[1024];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((len = is.read(buf)) != -1) {
				baos.write(len);
			}
			byte[] resultByte = baos.toByteArray();
			System.out.println(new String(resultByte,"UTF-8"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "return_url.html";
	}
}
