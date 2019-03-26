package com.webserver.servlet;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ShowAllUserDemo {

	public static void main(String[] args)throws IOException {

		 try {
			RandomAccessFile raf = new RandomAccessFile("user.txt","r");
			for(int i = 0 ; i < raf.length();i++) {
				byte[]data = new byte[32];
				raf.read(data);
				String name = new String(data,"utf-8").trim();
				raf.read(data);
				String nick = new String(data,"utf-8").trim();
				raf.read(data);
				String pass = new String(data,"utf-8").trim();
				int age = raf.readInt();
				
				System.out.println("Name: " + name + " Nickname: " + nick + " Password: " + pass + " Age: " + age);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
