package com.steven.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import com.alibaba.fastjson.JSON;

/**
 * 
 * @Description 百度地图工具类
 * @author Steven
 * @date 2019年3月22日 下午6:20:13
 */
public class BaiduMapUtil {

	private static String GeoCoderServerWEB = "http://api.map.baidu.com/geocoder/v2/?callback=renderReverse&location=<LATLNG>&output=json&pois=0&extensions_poi=null&latest_admin=1&ak=ECESM1AMSAP0RC9A6dn1BGFTBQwMe48C";
	private static String GeoCoderServerLBS = "http://api.map.baidu.com/cloudrgc/v1?location=<LATLNG>&geotable_id=135675&coord_type=bd09ll&ak=ECESM1AMSAP0RC9A6dn1BGFTBQwMe48C";
	
	/**
	 * 
	 * @Descript WEB 服务API    将经纬度数据逆地址解析成地理信息
	 * @param latlng 经纬度
	 * @return
	 */
	public static Map<String,Object> latLng2AddressWEB(String latlng){
	
		if(latlng == null || latlng.isEmpty()){
			return null;
		}
		
		String apiurl = GeoCoderServerWEB.replace("<LATLNG>", latlng);
		String data = request(apiurl);
		
		System.out.println("获取到的地理数据：\n"+data);
		data = data.replace("renderReverse&&renderReverse(", "");
		data = data.substring(0,data.length()-1);
		
		return json2Map(data);
	}
	
	
	/**
	 * 
	 * @Descript LBS.云服务(基站定位)		将经纬度数据逆地址解析成地理信息
	 * @param latlng
	 * @return
	 */
	public static Map<String,Object> latLng2AddressLBS(String latlng){
		
		if(latlng == null || latlng.isEmpty()){
			return null;
		}
		
		String apiurl = GeoCoderServerLBS.replace("<LATLNG>", latlng);
		String data = request(apiurl);
		
		return json2Map(data);
	}
	
	
	/**
	 * 
	 * @Descript 网络请求接口
	 * @param apiurl 接口地址
	 * @return
	 */
	public static String request(String apiurl) {
		try {
			URL url = new URL(apiurl);

			// 请求微信服务器
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(5000);
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			// 设置文件类型:
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//			建立连接
			conn.connect();
//			byte[] postData = str.getBytes();
//			// 设置文件长度
//			conn.setRequestProperty("Content-Length", String.valueOf(postData.length));
//			OutputStream os = conn.getOutputStream();
//			os.write(postData);
//			os.flush();
//			os.close();

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
				
				System.out.println("请求返回数据：\n"+data);
				
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
		System.out.println("JSON转换后的地理信息：\n"+map);
		
		return map;
	}
	
	public static void main(String[] args) {
//		latLng2AddressWEB("45.714179,126.613923");
		latLng2AddressLBS("45.714179,126.613923");
	}
}


