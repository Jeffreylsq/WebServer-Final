package com.webserver.servlet;

import com.webserver.http.HttpReponse;
import com.webserver.http.HttpRequest;

public abstract class HttpServlet {
	
	public abstract void service(HttpRequest request, HttpReponse response);
	
	
}
