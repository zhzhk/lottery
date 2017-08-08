package org.web.controllertest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.web.Application;
import org.web.entity.WebFootballMatchesData;
import org.web.service.FtMatchesDatasService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@Transactional
@SpringBootTest(classes = Application.class)
//@JsonTest
public class TestIndexController {
	@Autowired
	private FtMatchesDatasService ftMatchesDatasService;
	@Test
	public void test(){
		List<WebFootballMatchesData> webFootballMatchesDatas = new ArrayList<WebFootballMatchesData>();
        webFootballMatchesDatas = ftMatchesDatasService.getMatchesDatas();
        System.out.println(webFootballMatchesDatas);
	}
}
