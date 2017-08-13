package org.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web.entity.Result;
import org.web.service.GetMatchesDatasService;
import org.web.util.ResultUtil;

//@Controller
@RestController
@RequestMapping("/")
public class IndexController {
	@Autowired
	private GetMatchesDatasService getMatchesDatasService;
	
	/**
	 * 首页默认展示足球今日赛事数据
	 * @param req
	 * @return
	 */
	@GetMapping(value = "index")
    public Result<Object> index(HttpServletRequest req) {
		int addr = req.getRemotePort();
		System.out.println(addr);
        Map<String, Object> webFootballMatchesDatas = new HashMap<String, Object>();
        webFootballMatchesDatas = getMatchesDatasService.getMatchesDatas(req,"","","");
        Result<Object> ret = ResultUtil.success(webFootballMatchesDatas);
        return ret;
    }
	
	/**
	 * 赛事导航菜单跳转
	 * @param type  --赛事类型，FT-足球  BK-篮球
	 * @param subtype    --rb-滚球   r-今日赛事
	 * @return
	 */
	@GetMapping(value = "matches/{type}/{subtype}/{pageStart}")
	public Result<Object> matchesNavigate(HttpServletRequest req,@PathVariable("type") String type,@PathVariable("subtype") String subtype,@PathVariable("pageStart") String pageStart){
		System.out.println("**************"+req.getParameter("subtype"));
		Map<String, Object> webFootballMatchesDatas = new HashMap<String, Object>();
        webFootballMatchesDatas = getMatchesDatasService.getMatchesDatas(req,type,subtype,pageStart);
        Result<Object> ret = ResultUtil.success(webFootballMatchesDatas);
        return ret;
	}
}
