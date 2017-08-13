package org.crawler.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.crawler.entity.ProxyIpData;
import org.crawler.service.GetProxyIpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.crawler.util.ProxyUtil;

@Repository
@Transactional
public class GetProxyIpServiceImpl implements GetProxyIpService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public int saveProxyIpDatas(){
		//娓呯┖浠ｇ悊琛�
		deleteProxyDatas();
		//鎶撳彇浠ｇ悊鏁版嵁
		List<ProxyIpData> proxyIpDatas = ProxyUtil.getFreeProxy(0);
		return batchSaveProxyDatas(proxyIpDatas);
	}
	
	public int batchSaveProxyDatas(List<ProxyIpData> proxyIpDatas) {
		List<Object[]> obj = new ArrayList<Object[]>();
		String sql = "insert into web_proxy_ip(`ip`,`port`,`location`,`type`,`createtime`) values(?,?,?,?,?)";
		for(ProxyIpData proxyIpData : proxyIpDatas){
			Object[] values = new Object[]{
					proxyIpData.getIp(),
					proxyIpData.getPort(),
					proxyIpData.getLocation(),
					proxyIpData.getType(),
					proxyIpData.getCreatetime()
			};
			obj.add(values);
		}
		int[] insertCounts = jdbcTemplate.batchUpdate(sql,obj);
		return insertCounts.length;
	}

	public void deleteProxyDatas(){
		int rowCount = jdbcTemplate.queryForObject("select count(*) from web_proxy_ip",Integer.class);
		if(rowCount>0){
			jdbcTemplate.update("delete from web_proxy_ip");
		}
		
	}
}
