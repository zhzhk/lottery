package org.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web.entity.WebFootballMatchesData;
import org.web.mapper.WebFootballMatchesDataMapper;
import org.web.service.FtMatchesDatasService;

@Service
public class FtMatchesDatasServiceImpl implements FtMatchesDatasService {
	@Autowired
	private WebFootballMatchesDataMapper webFootballMatchesDataMapper;

	public List<WebFootballMatchesData> getMatchesDatas() {
		List<WebFootballMatchesData> gameList = webFootballMatchesDataMapper.selectAll();		
		return gameList;
	}
}
