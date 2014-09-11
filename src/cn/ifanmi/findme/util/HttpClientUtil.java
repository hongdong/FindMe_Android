package cn.ifanmi.findme.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.graphics.drawable.Drawable;

public class HttpClientUtil {

	private static HttpClient httpClient;
	private static final int DEFAULT_HOST_CONNECTIONS = 30; 
	private static final int DEFAULT_MAX_CONNECTIONS = 30; 
	private static final int DEFAULT_SOCKET_TIMEOUT = 5000;
	private static final String SCHEME_NAME = "http";
	public static final String BASE_URL = "http://114.215.115.33/";
//	public static final String BASE_URL = "http://192.168.1.11:8080/FindMeServer/";
	
	/*
	 * 私有的，空的，构造函数。因为用httpclient有很多好处。
	 * 其实我这里是工具类了，其它地方根本不会去new，所以加不加其实无所谓
	 */
	private HttpClientUtil() {
		
	}
	
	/*
	 * 有时其它地方需要直接用到httpClient这个对象，
	 * 而不是下面的静态方法
	 */
	public static synchronized HttpClient getHttpClient() {
		if (null == httpClient) {
			final HttpParams httpParams = new BasicHttpParams();   
			ConnManagerParams.setTimeout(httpParams, 3000);    
	        HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);  
	        HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);   
			ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(DEFAULT_HOST_CONNECTIONS));     
	        ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);  
	        SchemeRegistry schemeRegistry = new SchemeRegistry();    
	        schemeRegistry.register(new Scheme(SCHEME_NAME, PlainSocketFactory.getSocketFactory(), 80));    
	        ClientConnectionManager manager = new ThreadSafeClientConnManager(httpParams, schemeRegistry);    
	        httpClient = new DefaultHttpClient(manager, httpParams);  
		}
		return httpClient;
	}
	

	public static String getRequest(final String url) throws Exception {
		FutureTask<String> task = new FutureTask<String>(new Callable<String>() {
			@Override
			public String call() throws Exception {
				HttpGet get = new HttpGet(url);							//创建HttpGet对象用于GET请求
				HttpClient httpClient = getHttpClient();
				HttpResponse response = httpClient.execute(get);		//发送GET请求
				if (200 == response.getStatusLine().getStatusCode()) {	//code为200表示成功返回
					String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
					return result;										//response.getEntity()可获取服务器返回的字符串
				}
				return null;
			}
		});
		new Thread(task).start();										//FutureTask是一个异步线程
		return task.get();
	}
	
	/*用POST命令发送请求的URL，外加一个Map参数返回服务器相应的字符串*/
	public static String postRequest(final String url, final Map<String, String>rawParams) throws Exception {
		FutureTask<String> task = new FutureTask<String>(new Callable<String>() {
			@Override
			public String call() throws Exception {
				HttpPost post = new HttpPost(url);						//创建HttpPost对象用于POST请求
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				for (String key : rawParams.keySet()) {					//参数比较多的话可以封装起来
					params.add(new BasicNameValuePair(key, rawParams.get(key)));
				}
				post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				HttpClient httpClient = getHttpClient();
				HttpResponse response = httpClient.execute(post);
				if (200 == response.getStatusLine().getStatusCode()) {
					String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
					return result;
				}
				return null;
			}
		});
		new Thread(task).start();
		return task.get();
	}
	
	/**
	 * 获取图片
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static Drawable getDrawable(final String url) throws Exception {
		FutureTask<Drawable> task = new FutureTask<Drawable>(new Callable<Drawable>() {
			public Drawable call() throws Exception {
				HttpGet get = new HttpGet(url);					//创建HttpGet对象用于GET请求
				HttpClient httpClient = getHttpClient();
				HttpResponse response = httpClient.execute(get);		//发送GET请求
				if (200 == response.getStatusLine().getStatusCode()) {
					InputStream is = response.getEntity().getContent();
					return Drawable.createFromStream(is, url);
				}
				return null;
			}
		});
		new Thread(task).start();
		return task.get();
	}	
	
}
