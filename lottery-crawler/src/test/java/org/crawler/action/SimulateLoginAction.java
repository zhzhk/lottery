package org.crawler.action;

import org.crawler.service.GetDatasService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.Assert;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:crawler-application.xml"})
public class SimulateLoginAction {
	@Autowired
	private GetDatasService getDatasService;
	//模拟登录获取uid
	@SuppressWarnings("deprecation")
	@Test
	public void LoginAction(){
		String str = getDatasService.updateUid();
		Assert.assertEquals("更新失败", "更新成功", str);
	}
}
