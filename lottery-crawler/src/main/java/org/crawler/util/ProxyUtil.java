package org.crawler.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.crawler.entity.ProxyIpData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ProxyUtil {
	public static List<ProxyIpData> getFreeProxy(int pageNum){
		List<ProxyIpData> proxyList = new ArrayList<ProxyIpData>();
		if(0 == pageNum){
			pageNum = 1;
		}
		String url = "http://www.kuaidaili.com/free/inha/"+pageNum+"/";
//		System.out.println(url);
		
		HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler(){

			@Override
			public boolean retryRequest(IOException exception, int exeCount, HttpContext context) {
				if(exeCount > 3){
					return false;
				}
				if(exception instanceof InterruptedIOException){
					//Timeout
					return false;
				}
				if(exception instanceof UnknownHostException){
					return false;
				}
				if(exception instanceof ConnectTimeoutException){
					//connection refused
					return false;
				}
				if(exception instanceof SSLException){
					return false;
				}
				HttpClientContext clientContext = HttpClientContext.adapt(context);
				HttpRequest request = clientContext.getRequest();
				boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
				if(idempotent){
					return true;
				}
				return false;
			}
			
		};
		
//		SimpleDateFormat simpleFormate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		Date date = new Date();
		
//		long dateTime = date.getTime();
//		long dateTime = simpleFormate.parse(date.getTime());
		
//		String dateStr = simpleFormate.format(date);
//		Date datef = new Date(dateStr);
		long daten = new Date().getTime();
		Timestamp timeStamp = new Timestamp(daten);
		CloseableHttpClient httpclient = HttpClients.custom().setRetryHandler(retryHandler).build();
		HttpGet httpget = new HttpGet(url);
		try {
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			System.out.println(response.getStatusLine());
			Document doc = Jsoup.parse(EntityUtils.toString(entity));
			Elements trs = doc.getElementsByTag("tbody").select("tr");
			for(Element elem : trs){
				String ip = elem.getElementsByAttributeValue("data-title", "IP").text();
				String port = elem.getElementsByAttributeValue("data-title", "PORT").text();
				String type = elem.getElementsByAttributeValue("data-title", "类型").text();
				String location = elem.getElementsByAttributeValue("data-title", "位置").text();
				System.out.println(ip+"**"+port+"**"+type+"**"+location);
				ProxyIpData proxyIpData = new ProxyIpData();
				proxyIpData.setIp(ip);
				proxyIpData.setPort(port);
				proxyIpData.setLocation(location);
				proxyIpData.setType(type);
				proxyIpData.setCreatetime(timeStamp);
				proxyList.add(proxyIpData);
			}

			if(pageNum < 10){
				pageNum = pageNum+1;
				getFreeProxy(pageNum);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return proxyList;
	}
}
