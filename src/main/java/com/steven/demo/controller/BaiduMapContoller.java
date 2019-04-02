package com.steven.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @Description 百度地图接口服务
 * @author Steven
 * @date 2019年3月22日 下午6:12:43
 */
@RequestMapping("/baidumap")
@Controller
public class BaiduMapContoller {

	@RequestMapping("/latLng2Address")
	public String latLng2Address(){
		System.out.println("BaiduMapContoller.latLng2Address()");
		
		return "index";
	}
}
