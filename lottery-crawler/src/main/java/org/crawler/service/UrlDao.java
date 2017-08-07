package org.crawler.service;

import java.util.List;

import org.crawler.entity.GameData;
import org.crawler.entity.UrlData;
import org.springframework.stereotype.Service;

@Service
public interface UrlDao {
	public UrlData getUrlData(String wtype);
	public void updateUid(String uid, String wtype);
	public List<UrlData> getDataUrls();
	public int saveGameDatas(List<GameData> gameDatas);
}
