package com.webserver.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/*
 * 响应对象
 * 该类的每一个实例用于表示服务端发送给客户端的Http响应内容
 */
public class HttpReponse {

	/*
	 * 状态行相关信息定义
	 */
	//状态代码  默认值200
	private int statusCode = 200;
	//状态描述 默认对应200的描述内容“OK”
	private String statusReason = "OK";

	/*
	 * 响应头相关信息定义
	 */
	//key 响应头名字， value：响应头对应的值
	private Map<String,String> headers = new HashMap<>();

	/*
	 * 响应正文相关信息定义
	 */

	//响应正文实体文件
	private File entity;
	private Socket socket;
	private OutputStream out;

	/*
	 * 实例化请求对象的同时将Socket传入， 以便当前响应的对象通过他来获取输出流给对
	 * 给对应客户端发送相应内容
	 */

	public HttpReponse(Socket socket) {

		try {

			this.socket = socket;
			this.out = socket.getOutputStream();

		}catch(Exception e) {
			e.printStackTrace();
		}

	}


	/*
	 * 用于将当前响应对象的内容以一个标准的HTTP响应格式发送给客户端
	 * 
	 */

	public void flush() {

		//顺序发送响应的三部分
		sendStatusLine();
		sendHeader();
		sendContent();

	}

	/*
	 * 状态行
	 */
	private void sendStatusLine() {

		try {
			String line = "HTTP/1.1"+ " "+ statusCode + " " + statusReason;
			out.write(line.getBytes("ISO8859-1"));
			out.write(13);
			out.write(10);
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	/*
	 * 响应头
	 */
	private void sendHeader() {

		//将资源以一个HTTP响应发送给客户端
		/*
		 * HTTP/1.1 200 OK（CRLF）
		 * Content-Type:text/html(CRLF)
		 * Content-Length:213(CRLF)(CRLF)
		 * 100110101010...
		 * 
		 * HTTP/1.1 200 OK(CRLF)
          Content-Type:text/html(CRLF)
          Content-Length:224586(CRLF)(CRLF)
		 */
		try {

			/*
			 * 通过遍历headers，将所有需要发送给客户端的响应头进行发送
			 */
			Set<Entry<String,String>> entrySet = headers.entrySet();

			for(Entry<String,String> header: entrySet) {
				String name = header.getKey();
				String value = header.getValue();
				String line = name+ ": " + value;


				out.write(line.getBytes("ISO8859-1"));
				out.write(13);
				out.write(10);
			}


			//单独发送CRLF表示响应头部分发送完毕
			out.write(13);
			out.write(10);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 响应正文
	 */
	private void sendContent() {

		if(entity != null) {

			try (FileInputStream fis = new FileInputStream(entity);){

				byte[] data = new byte[1024*10];
				int d = -1;
				while((d = fis.read(data))!= -1) {
					out.write(data,0,d);
				}
			}  catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public File getEntity() {
		return entity;
	}


	/*
	 * 将给定的实体文件设置到response中
	 * 设置的同时会自动根据该文件添加对应的两个响应头：
	 * Context-Type 与 Context-Length
	 */

	public void setEntity(File entity) {
		this.entity = entity;


		//根据请求资源的是实际类型，设置Content-Type头
		String fileName = entity.getName();
		String [] arr = fileName.split("\\.");
		String line = HttpContext.getMimeType(arr[arr.length-1]);   //调用新创建的get方法

		this.putHeader("Content-Type", line);
		this.putHeader("Content-Length", entity.length()+"");
	}


	public int getStatusCode() {
		return statusCode;
	}


	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}


	public String getStatusReason() {
		return statusReason;
	}


	public void setStatusReason(String statusReason) {
		this.statusReason = statusReason;
	}

	/*
	 * 将给定的消息头设置到HttpResponse中
	 * name:消息头的名字
	 * value：消息头的值
	 */
	public void putHeader(String name, String value) {
		this.headers.put(name, value);
	}

	public String getHeader(String name) {
		return this.headers.get(name);
	}



}
