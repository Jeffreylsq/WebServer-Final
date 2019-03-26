package com.webserver.servlet;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;

import com.webserver.http.HttpReponse;
import com.webserver.http.HttpRequest;

/*
 * 处理注册业务
 */
public class RegServlet extends HttpServlet{

	public void service(HttpRequest request, HttpReponse response) {
		
		System.out.println("RegServlet: 开始注册用户业务 " );
		
		/*
		 * 1.通过request获取用户在注册页面上输入的注册信息
		 * 2.将该用户信息写入到文件user.dat中保存起来
		 * 3.响应客户端注册成功页面
		 */
		
		String username = request.getParameter("username");
		String nickname = request.getParameter("nickname");
		int age = (request.getParameter("age")!= null)?Integer.parseInt(request.getParameter("age")):(0);
		String password = request.getParameter("password");
		
		System.out.println(username + " ," + nickname + " , " + age + " ," + password);
		
		if(username == null) {
			response.setEntity(new File("./webapps/root/loginError.html"));
			return;
		}
		
		/*
		 * 2.将用户信息写入user.dat文件， 每个文件占用100字节
		 * 其中用户名 密码 昵称为字符串 各占32个字节
		 * 年龄是int值占四字节
		 */
		
		try {
			RandomAccessFile raf = new RandomAccessFile("user.txt","rw");
			raf.seek(raf.length());
			
			byte[]data = username.getBytes("UTF-8");
			data = Arrays.copyOf(data,32);
			raf.write(data);
			
			data = nickname.getBytes("UTF-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			data = password.getBytes("UTF-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			raf.writeInt(age);
			raf.close();
			
			
			
			
			//3.设置Response 响应注册成功页面
			
			response.setEntity(new File("./webapps/myweb/reg_success.html"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("RegServlet: 注册用户业务完毕 ");
		
		
	}
	
	
	
	
	
	
	
	
}
