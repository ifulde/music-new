package com.example.musicai.service;

import com.example.musicai.dto.RecommendData;
import com.example.musicai.dto.RecommendRequest;
import com.example.musicai.dto.RecommendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class RecommendService {
    private static final Logger logger = LoggerFactory.getLogger(RecommendService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${recommend.api.url:http://10.45.17.84:80/api/v1/recommend}")
    private String recommendApiUrl;

    public List<Long> getRecommendations(Object requestData) {
        try {
            logger.info("调用推荐服务，URL: {}", recommendApiUrl);
            logger.info("请求数据: {}", requestData);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> entity = new HttpEntity<>(requestData, headers);

            ResponseEntity<RecommendResponse> response = restTemplate.exchange(
                    recommendApiUrl,
                    HttpMethod.POST,
                    entity,
                    RecommendResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                RecommendResponse recommendResponse = response.getBody();
                if (recommendResponse.getCode() == 200 && recommendResponse.getData() != null) {
                    RecommendData data = recommendResponse.getData();
                    List<Long> topKItem = data.getTopKItem();
                    logger.info("推荐服务返回 {} 首歌曲", topKItem != null ? topKItem.size() : 0);
                    return topKItem;
                } else {
                    logger.warn("推荐服务返回错误: code={}, msg={}", recommendResponse.getCode(), recommendResponse.getMsg());
                }
            } else {
                logger.warn("推荐服务返回异常状态码: {}", response.getStatusCode());
            }

            return null;

        } catch (Exception e) {
            logger.error("调用推荐服务失败", e);
            return null;
        }
    }
}
