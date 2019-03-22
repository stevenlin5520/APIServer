package com.steven.demo.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.steven.util.WxPayUtil;

/**
 * 
 * @Description 微信支付接口
 * @author Steven
 * @date 2019年3月22日 下午3:41:04
 */
@Controller
@RequestMapping("/wxpay")
public class WxPayController {

	/**
	 * 
	 * @Descript 通过发起请求到微信支付服务后台生成预支付交易单
	 * @param postMap 请求参数  {"body":"测试支付","total_fee":1,"openid":"openidopenid","payType":1}
	 * 				body				商品描述
	 * 				total_fee			支付价格（单位分）
	 * 				openid				openid（微信公众号、小程序需要传入)
	 * 				payType				支付类型：1	NATIVE支付	;	2	APP支付	;	3	MWEB（H5支付） ;	4	JASPI（公众号、小程序支付）
	 * @param request HttpServletRequest
	 * @return JSON数据
	 * @Remind  报错   "return_msg": "商户号该产品权限未开通，请前往商户平台>产品中心检查后重试"    ；  表示未开通相应的支付类型
	 */
	@ResponseBody
	@RequestMapping("/prepay")
	public static Map<String, String> prePay(@RequestBody Map<String, String> postMap, HttpServletRequest request) {
		
		Map<String, String> result = new HashMap<>(16);
		if(!postMap.containsKey("body") || postMap.get("body") == null || postMap.get("body").isEmpty()){
			result.put("return_code", "FAIL");
			result.put("return_msg", "商品描述有错，请检查商品描述！");
			return result;
		}
		if(!postMap.containsKey("total_fee") || postMap.get("total_fee") == null || postMap.get("total_fee").isEmpty()){
			result.put("return_code", "FAIL");
			result.put("return_msg", "支付价格有错，请检查支付价格！");
			return result;
		}
		if(!postMap.containsKey("payType") || postMap.get("payType") == null || postMap.get("payType").isEmpty()){
			result.put("return_code", "FAIL");
			result.put("return_msg", "支付类型有错，请检查支付类型！");
			return result;
		}
		if(!"1".equals(postMap.get("payType")) && !"2".equals(postMap.get("payType")) && !"3".equals(postMap.get("payType"))){
			if(!postMap.containsKey("openid") || postMap.get("openid") == null || postMap.get("openid").isEmpty()){
				result.put("return_code", "FAIL");
				result.put("return_msg", "OPENID有错，请检查OPENID！");
				return result;
			}
		}
		
		result = WxPayUtil.execute(postMap, request);
		
		return result;
	}
}
