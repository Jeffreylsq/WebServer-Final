package com.webserver.core;

import java.io.File;
import java.net.Socket;

import com.webserver.http.EmptyRequestException;
import com.webserver.http.HttpReponse;
import com.webserver.http.HttpRequest;
import com.webserver.servlet.HttpChange;
import com.webserver.servlet.HttpServlet;
import com.webserver.servlet.LoginServlet;
import com.webserver.servlet.RegServlet;

public class ClientHandler implements Runnable{

	private Socket socket;

	public ClientHandler(Socket socket) {
		this.socket = socket;

	}



	public void run() {

		try {

			//1.解析请求
			//实例化请求对象， 实例化过程也是解析请求的过程

			HttpRequest request = new HttpRequest(socket);
			
			//实例化响应对象
			HttpReponse response = new HttpReponse(socket);


			//2.处理请求
			//先通过request获取用户请求的资源的抽象路径
			String path = request.getRequestURI();
			//首先判断该请求路径是否请求一个业务
			/*
			 * 根据请求路径去ServletContext提取对应的Servlet
			 * 若返回值不是null，说明该请求是请求一个业务
			 * 那么就调用对应的Servlet的service方法
			 */
			
			
			
			HttpServlet servlet = ServerContext.getServlet(path);
			//重点 reg是和html action = reg 相同的
			if(servlet != null){
				//请求注册业务
			    servlet.service(request, response);
			    
			}else {

				//从webapps目录下根据该抽象路径寻找请求资源
				File file = new File("./webapps" + path);

				//判断用户请求的资源是否存在
				if(file.exists()) {
					System.out.println("该资源已找到！");

					//将要响应的资源设置在response中
					response.setEntity(file);


				}else {
					System.out.println("该资源不存在！");
					//响应404页面
					response.setEntity(new File("./webapps/root/404.html"));

					//设置状态代码与描述
					response.setStatusCode(404);
					response.setStatusReason("NOT FOUND");
				}
			}		
			//3.发送响应

			response.flush();

           /*
            * 这里如果单独捕获空请求异常
            * 如果ClientHandler在一开始实例化请求对象
            * HttpRequest时，该构造方法抛出了空请求异常，那么直接会跳到catch这里
            * 这样就等于忽略了ClientHandler后续应该有的处理请求和响应客户端的操作了
            * 
            * 
            */

		}catch(EmptyRequestException e) {
		
			System.out.println("空请求");
			
		}catch(Exception e) {
		
			e.printStackTrace();

		}finally {

			//处理完毕后与客户端断开释放资源
			try {
				socket.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}



	}

}