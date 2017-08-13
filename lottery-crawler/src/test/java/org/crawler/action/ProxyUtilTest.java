package org.crawler.action;

import org.crawler.service.GetProxyIpService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:crawler-application.xml"})
public class ProxyUtilTest {
	@Autowired
	private GetProxyIpService getProxyIpService;
	@Test
	public void testGetFreeProxy(){
		int records = getProxyIpService.saveProxyIpDatas();
		System.out.println(records);
	}
}
