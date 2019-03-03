package com.steven.test;


import com.steven.util.JDBC2List;

public class JDBC2ListTest {

	public static void main(String[] args) {
		System.out.println(JDBC2List.query("select * from t_trail limit 0,20").toString());
		
		/*double d = 11212454500147574512.02185;
		System.out.println(new DecimalFormat("#0.00").format(d));
		System.out.println(String.format("%.2f", d));*/
		
		/*String re = "user_id_name_in";
		while(re.indexOf("_") != -1){
			int index = re.indexOf("_");
			re = re.replaceFirst("_", "");
			
			char tmp = re.charAt(index);
			char[] res = re.toCharArray();
			res[index] = tmp>='a'&&tmp<='z' ? (char)(tmp-32) : tmp ;
			re = String.valueOf(res);
		}
		System.out.println(re);*/
		
		
	}
}
