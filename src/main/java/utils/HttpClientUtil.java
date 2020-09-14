package utils;

import com.alibaba.fastjson.JSONObject;

import jdk.nashorn.internal.scripts.JO;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StreamUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 *
 */
public class HttpClientUtil {


	public static void main(String[] args) {

		System.out.println(httpClient("http://127.0.0.1:8080/test/lottery?key=lotterycount1"));

	}


	public static String httpClientUtilByJSON(String url,String requestBody){
		url = StringUtils.trim(url);
		ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
			@Override
			public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
				HeaderElementIterator it = new BasicHeaderElementIterator
						(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
				while (it.hasNext()) {
					HeaderElement he = it.nextElement();
					String param = he.getName();
					String value = he.getValue();
					if (value != null && param.equalsIgnoreCase
							("timeout")) {
						return Long.parseLong(value) * 1000;
					}
				}
				return 60 * 1000;//如果没有约定，则默认定义时长为60s
			}
		};
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(500);
		connectionManager.setDefaultMaxPerRoute(50);//例如默认每路由最高50并发，具体依据业务来定
		CloseableHttpClient httpclient = HttpClients.custom()
				.setConnectionManager(connectionManager)
				.setKeepAliveStrategy(myStrategy)
				.setDefaultRequestConfig(RequestConfig.custom().setStaleConnectionCheckEnabled(true).build())
				.build();
		HttpPost httpPost = null;
		String result=null;
		try {
			httpPost = new HttpPost(url);
			httpPost.addHeader("Content-Type", "application/json;charset=utf-8");
			httpPost.setEntity(new StringEntity(requestBody,"UTF-8"));

			CloseableHttpResponse httpResponse = httpclient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			InputStream responseBodyAsStream = entity.getContent();

			result = IOUtils.toString(responseBodyAsStream, "UTF-8");

//			byte[] buf = new byte[2048];
//			StringBuffer sb = new StringBuffer();
//			try {
//				for(int len=0;(len=responseBodyAsStream.read(buf))!=-1;){
//					sb.append(new String(buf, 0, len,"UTF-8"));
//				}
//			} catch (Exception e) {
//				System.out.println("报错啦。。。。。。。。。");
//			}
//
			return result;
		} catch (Exception e) {
			if(httpPost!=null){
				httpPost.abort();
			}
			e.printStackTrace();
		} finally{
			try {
				if(httpclient!=null){
					httpclient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

    public static String post(String url, String requestBody) {
        url = StringUtils.trim(url);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = null;
        String result = null;
        try {
            httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
            httpPost.setEntity(new StringEntity(requestBody, Charset.forName("UTF-8")));
            CloseableHttpResponse httpResponse = httpclient.execute(httpPost);
            HttpEntity entity = httpResponse.getEntity();
            InputStream responseBodyAsStream = entity.getContent();

            result = IOUtils.toString(responseBodyAsStream, "UTF-8");

            return result;
        } catch (Exception e) {
            if (httpPost != null) {
                httpPost.abort();
            }
            e.printStackTrace();
        } finally {
            try {
                if (httpclient != null) {
                    httpclient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }


//    public static String post(String url, String requestBody) {
//        url = StringUtils.trim(url);
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        HttpPost httpPost = null;
//        String result = null;
//        try {
//            httpPost = new HttpPost(url);
//            httpPost.setHeader("content-type", "application/json; charset=UTF-8");
//            httpPost.setEntity(new StringEntity(requestBody));
//            CloseableHttpResponse httpResponse = httpclient.execute(httpPost);
//            HttpEntity entity = httpResponse.getEntity();
//            InputStream responseBodyAsStream = entity.getContent();
//
//            result = IOUtils.toString(responseBodyAsStream, "UTF-8");
//
//            return result;
//        } catch (Exception e) {
//            if (httpPost != null) {
//                httpPost.abort();
//            }
//            e.printStackTrace();
//        } finally {
//            try {
//                if (httpclient != null) {
//                    httpclient.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return "";
//    }


	public static String httpClientUtil(String url,String requestBody){
		url = StringUtils.trim(url);
	//	CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = null;
		HttpClient httpclient = null;
		String result=null;
		try {

			BasicHttpClientConnectionManager connManager = getSSLManager();


			httpPost = new HttpPost(url);
			httpPost.addHeader("Content ", "text/xml,charset=UTF-8");
			httpPost.setEntity(new StringEntity(requestBody));
			httpclient = HttpClientBuilder.create()
					.setConnectionManager(connManager)
					.build();

			HttpResponse httpResponse = httpclient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			InputStream responseBodyAsStream = entity.getContent();
			
			result = IOUtils.toString(responseBodyAsStream, "UTF-8");
			
			return result;
		} catch (Exception e) {
			if(httpPost!=null){
				httpPost.abort();
			}
			e.printStackTrace();
		} finally{
//			try {
//				if(httpclient!=null){
//					httpclient.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
		return "";
	}

	public static String httpClientUtil2(String url,String requestBody){
		url = StringUtils.trim(url);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = null;
		String result=null;
		try {
			httpPost = new HttpPost(url);
			httpPost.addHeader("Content ", "text/xml,charset=UTF-8");
			httpPost.setEntity(new StringEntity(requestBody,"UTF-8"));

			CloseableHttpResponse httpResponse = httpclient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			InputStream responseBodyAsStream = entity.getContent();

			result = IOUtils.toString(responseBodyAsStream, "UTF-8");

//			byte[] buf = new byte[2048];
//			StringBuffer sb = new StringBuffer();
//			try {
//				for(int len=0;(len=responseBodyAsStream.read(buf))!=-1;){
//					sb.append(new String(buf, 0, len,"UTF-8"));
//				}
//			} catch (Exception e) {
//				System.out.println("报错啦。。。。。。。。。");
//			}
//
			return result;
		} catch (Exception e) {
			if(httpPost!=null){
				httpPost.abort();
			}
			e.printStackTrace();
		} finally{
			try {
				if(httpclient!=null){
					httpclient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	public static String httpClientUtil3(String url,Map<String,String> parmas){
		url = StringUtils.trim(url);
		//	CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = null;
		HttpClient httpclient = null;
		String result=null;
		try {

			BasicHttpClientConnectionManager connManager = getSSLManager();

			List<NameValuePair> paramPairs = new ArrayList<>();
			Set<String> keySet = parmas.keySet();
			for (String key : keySet) {
				Object val = parmas.get(key);
				if(val != null)
					paramPairs.add(new BasicNameValuePair(key, val.toString()));
			}
			UrlEncodedFormEntity reqEntity;

			httpPost = new HttpPost(url);
			httpPost.addHeader("Content ", "text/xml,charset=UTF-8");
			reqEntity = new UrlEncodedFormEntity(paramPairs, "UTF-8");
			httpPost.setEntity(reqEntity);
			httpclient = HttpClientBuilder.create()
					.setConnectionManager(connManager)
					.build();

			HttpResponse httpResponse = httpclient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			InputStream responseBodyAsStream = entity.getContent();

			result = IOUtils.toString(responseBodyAsStream, "UTF-8");

			return result;
		} catch (Exception e) {
			if(httpPost!=null){
				httpPost.abort();
			}
			e.printStackTrace();
		} finally{
//			try {
//				if(httpclient!=null){
//					httpclient.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
		return "";
	}

	private static BasicHttpClientConnectionManager getSSLManager() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		//使用 loadTrustMaterial() 方法实现一个信任策略，信任所有证书
		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
			// 信任所有
			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				return true;
			}
		}).build();

		//NoopHostnameVerifier类:  作为主机名验证工具，实质上关闭了主机名验证，它接受任何
		//有效的SSL会话并匹配到目标主机。
		HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);

		BasicHttpClientConnectionManager connManager = new BasicHttpClientConnectionManager(
				RegistryBuilder.<ConnectionSocketFactory>create()
						.register("http", PlainConnectionSocketFactory.getSocketFactory())
						//                      .register("https", SSLConnectionSocketFactory.getSocketFactory())
						.register("https", sslsf)
						.build(),
				null,
				null,
				null
		);
		return connManager;
	}


	public static String doPost(String url, Map<String, String> param, RequestConfig requestConfig) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
			if(requestConfig!=null){
				httpPost.setConfig(requestConfig);
			}
			// 创建参数列表
			if (param != null) {
				List<NameValuePair> paramList = new ArrayList<>();
				for (String key : param.keySet()) {
					paramList.add(new BasicNameValuePair(key, (String) param.get(key)));
				}
				// 模拟表单
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "utf-8");
				httpPost.setEntity(entity);
			}
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(response!=null){
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return resultString;
	}
	public static String httpString(String url,Map<String, String> params){
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
			nameValuePairs.add(nameValuePair);
		}
		return httpString(url, nameValuePairs);
	}
	
	public static String httpString(String url,List<NameValuePair> nameValuePairs){
		url = StringUtils.trim(url);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = null;
		try {
			//url = URLEncoder.encode(url, "UTF-8"); 
			httpPost = new HttpPost(url);
			httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
			
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			CloseableHttpResponse httpResponse = httpclient.execute(httpPost);
			
			HttpEntity entity = httpResponse.getEntity();
			
			
			InputStream responseBodyAsStream = entity.getContent();
			byte[] buf = new byte[1024];
			StringBuffer sb = new StringBuffer();
			try {
				for(int len=0;(len=responseBodyAsStream.read(buf))!=-1;){
					sb.append(new String(buf, 0, len,"utf-8"));
				}
			} catch (Exception e) {
				System.out.println("报错啦。。。。。。。。。");
			} finally{
				httpResponse.close();
			}
			return sb.toString();
		} catch (Exception e) {
			if(httpPost!=null){
				httpPost.abort();
			}
			e.printStackTrace();
		} finally{
			try {
				if(httpclient!=null){
					httpclient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static InputStream httpInputStream(String url,Map<String, String> params){
		url = StringUtils.trim(url);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
			nameValuePairs.add(nameValuePair);
		}
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = null;
		try {
			//url = URLEncoder.encode(url, "UTF-8"); 
			httpPost = new HttpPost(url);
			httpPost.addHeader("Content ", "text/xml,charset=utf-8");
			
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			CloseableHttpResponse httpResponse = httpclient.execute(httpPost);
			
			HttpEntity entity = httpResponse.getEntity();
			
			
			InputStream responseBodyAsStream = entity.getContent();
			return responseBodyAsStream;
		} catch (Exception e) {
			if(httpPost!=null){
				httpPost.abort();
			}
			e.printStackTrace();
		} finally{
			try {
				if(httpclient!=null){
					httpclient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public  static String httpClient(String url){
		url = StringUtils.trim(url);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost(url);
			httpPost.addHeader("Content", "text/xml,charset=utf-8");
			
			CloseableHttpResponse httpResponse = httpclient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			InputStream responseBodyAsStream = entity.getContent();
			byte[] buf = new byte[1024];
			StringBuffer sb = new StringBuffer();
			try {
				for(int len=0;(len=responseBodyAsStream.read(buf))!=-1;){
					sb.append(new String(buf, 0, len,"utf-8"));
				}
			} catch (Exception e) {
				System.out.println("报错啦。。。。。。。。。");
			}
			
			return sb.toString();
		} catch (Exception e) {
			if(httpPost!=null){
				httpPost.abort();
			}
			e.printStackTrace();
		} finally{
			try {
				if(httpclient!=null){
					httpclient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
	
	public  static String httpGet(String url){
		url = StringUtils.trim(url);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet(url);
			httpGet.addHeader("Content-Type", "text/xml,charset=utf-8");
			
			CloseableHttpResponse httpResponse = httpclient.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			InputStream responseBodyAsStream = entity.getContent();
			StringBuffer sb = new StringBuffer();
			String line = null;
			BufferedReader br = null;
			try {
				InputStreamReader isr = new InputStreamReader(responseBodyAsStream, "gbk");
				br = new BufferedReader(isr);
				while((line = br.readLine())!=null){
					sb.append(line);
				}
			} catch (Exception e) {
				System.out.println("报错啦。。。。。。。。。");
			}finally{
				br.close();
			}
			
			return sb.toString();
		} catch (Exception e) {
			if(httpGet!=null){
				httpGet.abort();
			}
			e.printStackTrace();
		} finally{
			try {
				if(httpclient!=null){
					httpclient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
	
	
	

	/**
	 * 发送get请求
	 * 
	 * @throws Exception
	 */
	public static String get(String url) {

		String result = "";
		InputStream in = null;
		try {
			// 打开和URL之间的连接
			HttpURLConnection conn = (HttpURLConnection) new URL(url)
					.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestMethod("GET");
			// 建立实际的连接
			conn.connect();
			// 定义输入流来读取URL的响应
			in = conn.getInputStream();
			result = StreamUtils.copyToString(in, Charset.forName("utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}




}
