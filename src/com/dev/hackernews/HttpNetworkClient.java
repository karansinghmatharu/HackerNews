package com.dev.hackernews;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class HttpNetworkClient {
	private static HttpNetworkClient clientInstance;
	private HttpClient httpClient;
	
	public synchronized static HttpNetworkClient getInstance()
	{
		if(clientInstance == null)
		{
			clientInstance = new HttpNetworkClient();
		}

		return clientInstance;
	}
	
	public HttpNetworkClient()
	{
		final HttpParams httpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, 1000*20); // Timeout set as 10 secs
	    httpClient = new DefaultHttpClient(httpParams);
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
	
	
	
	
}