package com.webserver.servlet;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;

import com.webserver.http.HttpReponse;
import com.webserver.http.HttpRequest;

public class HttpChange extends HttpServlet{
	public void service(HttpRequest request, HttpReponse response) {

		System.out.println("RegServlet: 开始修改密码");
		String name = request.getParameter("oldname");
		String oldPassword = request.getParameter("oldPassword");
		String newPassword = request.getParameter("newPassword");
		boolean flag = true;

		try(RandomAccessFile raf = new RandomAccessFile("user.txt","rw");) {


			for(int i = 0 ; i < raf.length()/100 ;i++) {
				raf.seek(i*100);
				byte[] data = new byte[32];
				raf.read(data);
				String oldNam = new String(data,"UTF-8").trim();
				raf.read(data);
				raf.read(data);
				String oldPass = new String(data,"UTF-8").trim();
				if(name.equals(oldNam) && oldPassword.equals(oldPass)) {
					flag = true;
					System.out.println("找到该用户"); 
					raf.seek(i*100+64);
					byte[]arr = newPassword.getBytes("UTF-8");
					arr = Arrays.copyOf(arr, 32);
					raf.write(arr);
					break;

				}else {
					flag = false;

				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		if(flag) {

			response.setEntity(new File("./webapps/myweb/change_success.html"));

		}else {
			response.setEntity(new File("./webapps/myweb/change_fail.html"));
		}


	}
}
