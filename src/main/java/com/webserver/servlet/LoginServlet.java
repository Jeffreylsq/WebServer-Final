package com.webserver.servlet;

import java.io.File;
import java.io.RandomAccessFile;

import com.webserver.http.HttpReponse;
import com.webserver.http.HttpRequest;

public class LoginServlet extends HttpServlet {
	public void service(HttpRequest request, HttpReponse response) {

		System.out.println("RegServlet: 开始登录业务");

		String name = request.getParameter("name");
		String pass = request.getParameter("password");
		System.out.println("登录用户用户名：" + name + ", 密码：" + pass);

		boolean flag = false;
		try(RandomAccessFile raf = new RandomAccessFile("user.txt","r");) {

			for(int i = 0 ; i < raf.length()/100; i ++) {
				raf.seek(i*100);
				byte[]data = new byte[32];
				raf.read(data);
				String userName = new String(data,"utf-8").trim();
				raf.read(data);
				raf.read(data);
				String passcode = new String(data,"utf-8").trim();

				if(name.equals(userName) && pass.equals(passcode)) {
					System.out.println("用户登录成功");
					flag = true;
					break;
				}else {
					flag = false;

				}
			}

			if(flag) {
				response.setEntity(new File("./webapps/myweb/login_success.html"));
			}else{
				response.setEntity(new File("./webapps/myweb/login_fail.html"));
			}




		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
