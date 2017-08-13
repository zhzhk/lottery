package org.web.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web.entity.PageEntity;
import org.web.entity.WebSportData;
import org.web.mapper.WebFootballMatchesDataMapper;
import org.web.service.GetMatchesDatasService;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class GetMatchesDatasServiceImpl implements GetMatchesDatasService {
	@Autowired
	private WebFootballMatchesDataMapper webFootballMatchesDataMapper;
	
	public Map<String, Object> getMatchesDatas(HttpServletRequest req,String type,String subtype,String pageNum) {
		int pageStart;
		if(pageNum == null){
			pageStart = 1;
		}else{
			pageStart = Integer.parseInt(pageNum);
		}
		
		if(type == null){
			type = "FT";
		}
		
		if(subtype == null){
			subtype = "r";
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		PageHelper.startPage(pageStart, PageEntity.rows);
		Example example = new Example(WebSportData.class);
		Criteria criteria = example.createCriteria();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateText  = simpleDateFormat.format(new Date().getTime()- 12*3600*1000);
		criteria.andEqualTo("m_date", dateText);
		criteria.andEqualTo("type", type);
		if("r".equals(subtype))
			criteria.andEqualTo("r_show", "1");
		else
			criteria.andEqualTo("rb_show", "1");
		List<WebSportData> gameList = webFootballMatchesDataMapper.selectByExample(example);
		map.put("gameList", gameList);
		PageInfo<WebSportData> page = new PageInfo<WebSportData>(gameList);
		System.out.println("---------111------------"+req.getParameter("subtype"));
		System.out.println("----------------------"+page.getTotal());
		System.out.println("----------------------"+page.getPageNum());
		System.out.println("----------------------"+page.getPageSize());
		System.out.println("----------------------"+page.getStartRow());
		System.out.println("----------------------"+page.getEndRow());
		System.out.println("----------------------"+page.getPages());
		
		System.out.println("----------------------"+page.getNavigateFirstPage());
		System.out.println("----------------------"+page.getNavigateLastPage());
		System.out.println("----------------------"+page.isIsFirstPage());
		System.out.println("----------------------"+page.isIsLastPage());
		System.out.println("----------------------"+page.isHasPreviousPage());
		System.out.println("----------------------"+page.isHasNextPage());
		map.put("totalNum",page.getTotal());
		map.put("totalPage",page.getPages());
		return map;
	}

}
