package org.crawler.action;

import java.io.IOException;
import java.text.ParseException;

import org.apache.http.client.ClientProtocolException;
import org.crawler.action.CrawlerAction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:application.xml"})
public class CrawlerActionTest {
	@Autowired
	private CrawlerAction crawelAction;

	public void setCrawelUtil(CrawlerAction crawelAction) {
		this.crawelAction = crawelAction;
	}

	@Test
	public void test() throws ClientProtocolException, IOException{
		//ApplicationContext ctx = new ClassPathXmlApplicationContext("application.xml");
//		UrlData urlData = urlDao.getUrlData();
//		System.out.println(urlData.getWeb_site());
		crawelAction.LoginAction();
	}
	
	@Test
	public void testCrawelData() throws ClientProtocolException, IOException, ParseException{
		crawelAction.CrawelData();
	}
	
	@Test
	public void testCrawelRbData() throws ClientProtocolException, IOException, ParseException{
		crawelAction.CrawelRbData();
	}
}
