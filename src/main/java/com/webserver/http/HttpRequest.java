 package com.webserver.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求对象
 * 该类类的每一个实例用于表示一个Http请求内容
 * 
 * 一个Http请求包含三部分：
 * 请求行，消息头，消息正文
 * @author soft01
 *
 */
public class HttpRequest  {

	/*
	 *请求行相关信息定义 
	 */

	//请求方式
	private String method;
	//抽象资源路径
	private String url;
	//请求使用的HTTP协议版本
	private String protocol;
	//消息头相关信息定义
	private Map<String,String> headers = new HashMap<>();

	//url中的请求部分，？左侧内容
	private String requestURI;
	//url中参数部分，？右侧内容
	private String queryString;
	//每一组参数 key:参数名 value：参数值
	private Map<String,String> parameters = new HashMap<>();

	private Socket socket;
	private InputStream in;

	/**
	 * 构造方法，用来初始化请求对象
	 */

	public HttpRequest(Socket socket)throws EmptyRequestException {

		try {
			   this.socket = socket;
			this.in = socket.getInputStream();
			/*
			 * 1：解析请求行
			 * 2：解析消息头
			 * 3：解析消息正文
			 */

			parseRequestLine();
			parseHeaders();
			parseContent();
		}catch(EmptyRequestException e) {
		  throw e;
			
		}catch (Exception e) {
		

			e.printStackTrace();
		}

	}

	/**
	 * 解析请求行
	 */
	private void parseRequestLine() throws EmptyRequestException{

		System.out.println("HttpRequest: 解析请求行");

		try {

			/*
			 * 通过Socket获取的输入流读取客户端发送过来的
			 * 请求中的第一行字符串，这一行应当就是请求行的内容了，
			 * 读取后再将请求行内容按照空格进行拆分，这时应当可以拆分
			 * 出三项内容：他们是： method,url,protocol
			 * 我们再将他们设置到对应的属性上即完成解析
			 */

			String str = readLine();
			System.out.println("请求行：" + str);

			/*
			 * 如果请求行内容是一个空字符串，则说明本次请求是空请求
			 */
			if("".equals(str)) {
				
				throw new EmptyRequestException();
			}
			
			

			String [] arr = str.split("\\s");
			this.method = arr[0];
			this.url = arr[1];
			parseURL();//进一步解析url
			this.protocol = arr[2];
			System.out.println("Method: " + method);
			System.out.println("url:" + url);
			System.out.println("protocol:" + protocol );

		}catch(EmptyRequestException e) {
		   //若是空请求则抛出
			throw e;
		}catch(Exception e) {
		
             e.printStackTrace();
		}

		System.out.println("HttpRequest: 请求行解析完毕");
	}


	/*
	 * 进一步解析请求行中的url部分
	 * 因为一个url可能有两种情况：含有参数，不含有参数
	 * 如果含有参数要对参数进行解析
	 */
	private void parseURL() {
		System.out.println("HttpRequest: 进一步解析url");
		/*
		 * url有两种情况：
		 * 1：不含有参数，如：/myweb/index.html
		 * 对于这种情况，直接将url的值设置到属性requestURI上即可
		 * 
		 * 2：含有参数，如：/myweb/reg?username=xx&&password=xx.....
		 * 对于这种情况，我们首先将url按照“？”拆分成两部分
		 * 第一部分为请求部分，赋值给requestURI
		 * 第二部分为参数部分，赋值给queryString
		 * 然后再对参数部分进行进一步拆分：
		 * 首先按照&拆分出每一组参数，然后每一组参数再按照“=”拆分成两部分，分别是参数名和参数值，再将他们以key，
		 * value存入到parameter这个Map类型属性完成解析工作
		 */

		if(!url.contains("?")) {

			this.requestURI = url;
		}else {
			
			String[] data = url.split("[?]");
			requestURI = data[0];
			if(data.length > 1) {
				queryString = data[1];
				//对参数部分转码，将所有%xx内容还原成对应字符
				try {
					this.queryString = URLDecoder.decode(this.queryString,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				
				
                //进一步拆分
				 data = queryString.split("[&]");

				for(int i = 0 ; i < data.length ;i++) {
                     //每个参数再按照“=”拆分
					String[] data2 = data[i].split("[=]");
                    if(data2.length >1) {
					parameters.put(data2[0], data2[1]);
                    }else{
                    	parameters.put(data2[0], null);
                    }
                    
				}
			} 
		}

		System.out.println("requestURI: " + requestURI);
		System.out.println("queryString: " + queryString);
		System.out.println("parameter: " + parameters);
		System.out.println("HttpRequest: 解析url完毕");

	}


	/*
	 * 解析消息头
	 */
	private void parseHeaders() {

		System.out.println("HttpRequest: 解析消息头");

		/*
		 * parseRequestLine方法已经通过输入流将请求中的请求行内容读取完毕，那么到parseHeader这个方法时
		 * 再通过输入流读取得内容应当就是消息头部分了， 
		 * 
		 * 解析思路：
		 * 顺序读取若干行字符串，每一行都是一个消息头
		 * 将消息头按照：冒号拆分成两部分，分别是消息头的名字和对应的值
		 * 并将每个消息头的名字叫key， 消息头的值作为value 
		 * 并保存到headers这个Map中即可完成解析工作
		 * 
		 * 如果读取亿行字符串时返回的是一个空字符串，即：""则说明了本次单独读取到了CRLF,那么就可以停止读取工作了
		 * （消息头部分读取完了）
		 */

		while(true) {

			try {
				String line = readLine();

				if("".equals(line)) {
					break;
				}

				String [] data = line.split(": ");
				headers.put(data[0],data[1]);


			} catch (IOException e) {

				e.printStackTrace();
			}

		}

		System.out.println(headers);

		System.out.println("HttpRequest: 消息头解析完毕");

	}



	/*
	 * 解析消息正文
	 */
	private void parseContent() {

	}

	/*
	 * 通过对应客户端的输入流，读取一行客户端发送过来的字符串，一行是以（CRLF）作为结束的
	 */

	private String readLine()throws IOException {
		StringBuilder builder = new StringBuilder();
		//c1 表示上次读到的字符， c2表示本次读到的字符
		int c1 = -1, c2 = -1;

		while((c2 = in.read())!= -1) {

			if(c1 == 13 && c2 == 10) {
				break;
			}
			builder.append((char)c2);
			c1 = c2;
		}

		return builder.toString().trim();


	}

	public String getMethod() {
		return method;
	}

	public String getUrl() {
		return url;
	}

	public String getProtocol() {
		return protocol;
	}


	public String getRequestURI() {
		return requestURI;
	}

	public String getQueryString() {
		return queryString;
	}
	
	/*
	 * 获取给定参数对应的值
	 */
	public String getParameter(String name) {
		
		return this.parameters.get(name);
		
	}


}
