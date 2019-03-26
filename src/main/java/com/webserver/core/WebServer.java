package com.webserver.core;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WebServer主类
 * @author soft01
 *
 */
public class WebServer {

	private ServerSocket server;
	
	private ExecutorService threadPool;
	public WebServer() {
		
		try {
			server = new ServerSocket(12000);
			threadPool = Executors.newFixedThreadPool(50);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		
		try {
			
			/*
			 *暂时不启用允许多个客户端多次连接 
			 */
			while(true) {
				
			    System.out.println("等待客户端连接....");
				Socket socket = server.accept();
				System.out.println("一个客户端连接....");
				//启动线程处理客户端交互
				
				ClientHandler handler = new ClientHandler(socket);
				//将任务交给线程池
				threadPool.execute(handler);
				
			}
			
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args) {
		
		WebServer server = new WebServer();
		server.start();
	}
	
	
}
