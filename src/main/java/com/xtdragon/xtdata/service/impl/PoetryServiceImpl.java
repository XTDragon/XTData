package com.xtdragon.xtdata.service.impl;

import com.xtdragon.xtdata.service.PoetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PoetryServiceImpl implements PoetryService {

    @Autowired
    RestTemplate restTemplate;

    @Override
    public String getPoetry() {
        String url="https://github.com/chinese-poetry/chinese-poetry/blob/master/json/poet.song.0.json";
        //依赖于GitHub项目https://github.com/NateScarlet/holiday-cn
        HttpEntity entity = restTemplate.exchange(url, HttpMethod.GET,null, String.class);
        return entity.getBody().toString();
    }
}
