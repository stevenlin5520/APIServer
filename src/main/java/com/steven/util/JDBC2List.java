package com.steven.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将JDBC查询到的数据转换成List<String,Object>>类型
 * @author Steven
 */
public class JDBC2List {
	
	/**
	 * 开启数据库连接
	 */
	private static Connection conn = null;
	
	static{
		if(conn == null){
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://106.13.45.179:3306/db_bike_mis", "remote", "PGZHc20190131Bm888");
			
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 连接数据库获取List数据
	 * @param querySql 查询语句
	 * @return 
	 */
	public static List<Map<String,Object>> query(String querySql) {
		//将结果集转换后的List数据结果
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		Statement sta = null;
		ResultSet rs = null;
		//存储临时的Map数据
		Map<String,Object> rowData = null;
		
		try {
			sta = conn.createStatement();
			rs = sta.executeQuery(querySql);
			//获得结果集结构信息,元数据
			ResultSetMetaData md = rs.getMetaData(); 
			//获得列数
			int columnCount = md.getColumnCount(); 
			//遍历结果集
			while (rs.next()) {
				rowData = rowData == null ? new HashMap<String,Object>() : rowData;
				for (int i = 1; i <= columnCount; i++) {
					//System.out.println(md.getColumnType(i)+"---"+md.getColumnTypeName(i)+"----"+rs.getObject(i));
					rowData.put(formatColumnName(md.getColumnName(i)), formatData(md.getColumnTypeName(i),rs.getObject(i)));
				}
				result.add(rowData);
				rowData = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				sta.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			closeConnection();
		}
		return result;
	}
	
	/**
	 * 关闭数据库连接
	 */
	public static void closeConnection(){
		if(conn != null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 数据格式化
	 * @param obj 待转换的数据
	 * @param type 数据类型（大写)
	 * @return 
	 */
	public static Object formatData(String type, Object obj){
		Object result = obj;
		switch (type) {
		case "INT":
			result = obj == null ? 0 : obj;
			break;
			
		//格式化价格
		case "DOUBLE":
			result = (Double)obj==0 ? 0 : new DecimalFormat("#0.00").format(obj);
			break;
		
		case "DATETIME":
			result = obj == null ? "" : obj.toString().substring(0,obj.toString().indexOf('.'));
			break;
			
		case "DATE":
			result = obj == null ? "" : obj;
			break;
			
		case "VARCHAR":
			result = obj == null ? "" : obj;
			break;
			
		case "BIGINT":
			result = obj == null ? 0 : obj;
			break;
			
		default:
			break;
		}
		
		return result;
	}
	
	/**
	 * 递归方式格式化数据库字段。将数据库字段_去掉，列名变成java规范的驼峰命名。如user_login_date变为useLoginDate
	 * @param columnName
	 * @return
	 */
	public static String formatColumnName(String columnName){
		if(columnName == null || columnName.length() <= 0){
			return "";
		}
		
		while(columnName.indexOf("_") != -1){
			int index = columnName.indexOf("_");
			columnName = columnName.replaceFirst("_", "");
			//需要变为大写的字符
			char tmp = columnName.charAt(index);
			char[] res = columnName.toCharArray();
			//将小写修改为大写
			res[index] = tmp>='a'&&tmp<='z' ? (char)(tmp-32) : tmp ;
			//返回格式化的数据给columnName
			columnName = String.valueOf(res);
		}
		return columnName;
	}
}
