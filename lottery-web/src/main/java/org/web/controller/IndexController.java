package org.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.crawler.entity.GameData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web.service.FtMatchesDatasService;

@RestController
@RequestMapping("/")
public class IndexController {
	@Autowired
	private FtMatchesDatasService ftMatchesDatasService;
	
	@GetMapping(value = "index")
    public List<GameData> index() {
        List<GameData> gameDatas = new ArrayList<GameData>();
        gameDatas = ftMatchesDatasService.getMatchesDatas();
        return gameDatas;
    }
}
