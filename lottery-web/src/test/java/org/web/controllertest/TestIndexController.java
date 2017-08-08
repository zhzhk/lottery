package org.web.controllertest;

import java.util.ArrayList;
import java.util.List;

import org.crawler.entity.GameData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.web.service.FtMatchesDatasService;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"org.web.controller","org.web.service"})
@MapperScan(basePackages = "org.web.mapper")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:web-application.xml"})
public class TestIndexController {
	@Autowired
	private FtMatchesDatasService ftMatchesDatasService;
	@Test
	public void test(){
		List<GameData> gameDatas = new ArrayList<GameData>();
        gameDatas = ftMatchesDatasService.getMatchesDatas();
	}
}
