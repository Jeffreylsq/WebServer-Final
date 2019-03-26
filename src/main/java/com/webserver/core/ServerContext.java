package com.webserver.core;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.webserver.servlet.HttpChange;
import com.webserver.servlet.HttpServlet;
import com.webserver.servlet.LoginServlet;
import com.webserver.servlet.RegServlet;

/*
 * 服务端所有配置信息
 * 
 */
public class ServerContext {
	/*
	 * 所有Servlet
	 * 请求路径
	 * 具体处理对应业务的Servlet实例
	 */
	private static Map<String, HttpServlet> SERVLET_MAPPING = new HashMap<>();

	static {

		//初始化所有的Servlet
		initServletMapping();
	}

	private static void initServletMapping() {
		/*
		 * 加载conf/servlet.xml
		 * 将根标签下所有的servlet标签获取到
		 * 并将其path属性的值作为key
		 * 将className属性的值取出并利用反射实例化
		 * 对应的Servlet实例作为value
		 * 保存到SERVLET_MAPPING这个map中完成初始化
		 * 
		 * 注意：
		 * 利用反射加载每个Servlet并实例化后，返回的都是Object，但是这些Servlet都继承自HttpServlet
		 * 所以将他们造型成HttpServlet即可，然后以value形式存入Map
		 */


		try { 
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new File("conf/servlets.xml"));
			Element root = doc.getRootElement();
			List<Element> list = root.elements("servlet");
			
			
			for(Element e: list) {
				
				String key = e.attributeValue("path");
				String name = e.attributeValue("className");
				System.out.println(key + "  " + name);
				Class cls = Class.forName(name);
				HttpServlet servlet = (HttpServlet)cls.newInstance();
				SERVLET_MAPPING.put(key, servlet);	
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}
	/*
	 * 根据请求路径获取对应的Servlet实例
	 */
	public static HttpServlet getServlet(String path) {
		return SERVLET_MAPPING.get(path);
	}


	public static void main(String []args) {
		
		HttpServlet servlet = getServlet("/myweb/login");
		System.out.println(servlet);
	}




}
