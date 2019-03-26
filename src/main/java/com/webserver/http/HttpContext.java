package com.webserver.http;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Http协议相关定义内容
 * 
 * 
 * 设计这个类的目的是将所有Http协议定义的内容都放在这里 ，这样我们无论哪个类需要用到
 * Http协议的东西时，都可以来这里找到
 * @author soft01
 *
 */
public class HttpContext {
	
	/*
	 * Content-Type与资源类型的映射
	 * key:资源类型（后缀名）
	 * value:Content-Type对应的值 --------------这个值是规定好的 上千个
	 */
	
	
	private static final Map<String,String> MIME_MAPPING = new HashMap<>();
	
	static{
		//初始化
		initMimeMapping();
		
	}
	
	/*
	 * 初始化MIME_MAPPING
	 */
	private static void initMimeMapping() {

		/*
		 * 通过解析conf/web.xml文件，将所有类型初始化出来
		 * 1.创建SAXReader 并读取conf目录下的web.xml文件
		 * 2.将根元素下所有名为：<mime-mapping>的子标签获取出来
		 * 3.遍历所有的<mine-mapping>标签，并将其子标签：
		 * <extension>中间的文本作为key
		 * <mime-type>中间的文本作为value
		 * 保存到MIME_MAPPING这个map中完成初始化
		 */
		try {
			
			  SAXReader reader = new SAXReader();
			  Document doc =  reader.read( new File("conf/web.xml"));
			  Element root = doc.getRootElement();
			  List<Element> list = root.elements("mime-mapping");
			  
			  for(Element e: list) {
				  
				  MIME_MAPPING.put(e.elementText("extension"), e.elementText("mime-type"));
				  
			  }

		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * 根据资源类型名获取对应的Content-Type值
	 * ext 资源类型名， 如（png,html,css)
	 */
	public static String getMimeType(String ext) {
		
		return MIME_MAPPING.get(ext);
	}
	
	
	
	
	public static void main(String[] args) {
		String line = HttpContext.getMimeType("png");
		String name = "sadhgashk.png";
		String[]data = name.split("\\.");
		line = HttpContext.getMimeType(data[data.length-1]);
		System.out.println(line);
		System.out.println(MIME_MAPPING.size());
	}
	
}
