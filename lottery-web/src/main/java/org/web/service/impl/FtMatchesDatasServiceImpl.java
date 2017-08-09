package org.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web.entity.WebFootballMatchesData;
import org.web.mapper.WebFootballMatchesDataMapper;
import org.web.service.FtMatchesDatasService;

import com.github.pagehelper.PageHelper;

import tk.mybatis.mapper.entity.Example;

@Service
public class FtMatchesDatasServiceImpl implements FtMatchesDatasService {
	@Autowired
	private WebFootballMatchesDataMapper webFootballMatchesDataMapper;
	
	public List<WebFootballMatchesData> getTodayMatchesDatas(WebFootballMatchesData webFootballMatchesData) {
		if(webFootballMatchesData.getPage() != null && webFootballMatchesData.getRows() != null){
			PageHelper.startPage(webFootballMatchesData.getPage(), webFootballMatchesData.getRows());
		}
		Example example = new Example(WebFootballMatchesData.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("MB_Team_cn", "高车士打");
		List<WebFootballMatchesData> gameList = webFootballMatchesDataMapper.selectByExample(example);
		return gameList;
	}
}