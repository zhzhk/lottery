package org.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.web.entity.WebFootballMatchesData;
import org.web.service.FtMatchesDatasService;

//@Controller
@RestController
@RequestMapping("/")
public class IndexController {
	@Autowired
	private FtMatchesDatasService ftMatchesDatasService;
	
	//首页默认展示足球今日赛事数据
	@GetMapping(value = "index")
    public List<WebFootballMatchesData> index(HttpServletRequest req,WebFootballMatchesData webFootballMatchesData) {
		int addr = req.getRemotePort();
		System.out.println(addr);
        List<WebFootballMatchesData> webFootballMatchesDatas = new ArrayList<WebFootballMatchesData>();
        webFootballMatchesDatas = ftMatchesDatasService.getTodayMatchesDatas(webFootballMatchesData);
        return webFootballMatchesDatas;
    }
}
