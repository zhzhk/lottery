package org.web.controllertest;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.web.Application;
import org.web.service.GetMatchesDatasService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@Transactional
@SpringBootTest(classes = Application.class)
//@JsonTest
public class TestIndexController {
	@Autowired
	private GetMatchesDatasService getMatchesDatasService;
	@Test
	public void test(){
		Map<String, Object> webFootballMatchesDatas = new HashMap<String, Object>();
		HttpServletRequest req = null;
        webFootballMatchesDatas = getMatchesDatasService.getMatchesDatas(req, null, null, null);
        System.out.println(webFootballMatchesDatas);
	}
}
