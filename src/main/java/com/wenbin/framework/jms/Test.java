package com.wenbin.framework.jms;

public class Test {

	
	public static void main(String[] args) {
		 String a="D:\\test\\thumbjpg";
		 a = a.replaceAll("\\\\", "\\\\\\\\")  ;
         System.out.println(a);  
         System.out.println( a.split("\\\\\\\\").length);  

		
		
	       	 String s = "E:\\jbx\\x9\\io9";  
	         String ss;  
	         System.out.println( s);  

	         //��·��s�е�'\'��Ϊ'\\',Ϊ�λ�Ҫ��8��'\'������Ϊ4�������ˡ�  
	         s = s.replaceAll("\\\\", "\\\\\\\\")  ;
	         System.out.println( s);  
	         System.out.println( s.split("\\\\\\\\").length);  
	        // System.out.println( ss);  
		//a="D\\test\\thumbjpg";
//		System.out.println(a.split("\\\\").length);
//		System.out.println(b.split("[\\]").length);
//		System.out.println(a.split("[\\]").length);
//		a=a.replaceAll("\\/","[\\]");
//		String [] tmp=a.split("\\/");
//		System.out.println(tmp.length);
	}
}
