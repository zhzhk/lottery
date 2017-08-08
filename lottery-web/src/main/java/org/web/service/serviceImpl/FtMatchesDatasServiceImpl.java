package org.web.service.serviceImpl;

import java.util.List;

import org.crawler.entity.GameData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web.mapper.GameDataMapper;
import org.web.service.FtMatchesDatasService;

@Service
public class FtMatchesDatasServiceImpl implements FtMatchesDatasService {
	@Autowired
	private GameDataMapper gameDataMapper;

	public List<GameData> getMatchesDatas() {
		System.out.println(gameDataMapper);
		List<GameData> gameList = gameDataMapper.selectAll();		
		return gameList;
	}
}
