package com.hmall.search.service.impl;

import com.hmall.common.domain.PageDTO;
import com.hmall.search.domain.po.ItemDoc;
import com.hmall.search.domain.query.ItemPageQuery;
import com.hmall.search.service.SearchService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class SearchServiceImpl implements SearchService {
    private static final Integer ADWeight = 5;
    private final RestHighLevelClient client;

    @Autowired
    public SearchServiceImpl(RestHighLevelClient client) {
        this.client = client;
    }

    // 查询所有商品
    @Override
    public PageDTO<ItemDoc> search(ItemPageQuery itemPageQuery) {
        Integer page = itemPageQuery.getPageNo();
        Integer size = itemPageQuery.getPageSize();
        page = page == null ? 1 : page;
        size = size == null ? 5 : size;
        // 1.创建 request 对象
        SearchRequest request = new SearchRequest("items");
        request.source().highlighter(SearchSourceBuilder.highlight().field("name"));

        // 2.计算分页参数
        int from = (page - 1) * size;

        // 3.构建查询条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 添加关键字匹配（name 字段）
        if (itemPageQuery.getKey() != null && !itemPageQuery.getKey().isEmpty()) {
            boolQuery.must(QueryBuilders.matchQuery("name", itemPageQuery.getKey())
                    .analyzer("ik_max_word"));
        }

        // 添加分类过滤
        if (itemPageQuery.getCategory() != null && !itemPageQuery.getCategory().isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("category", itemPageQuery.getCategory()));
        }

        // 添加品牌过滤
        if (itemPageQuery.getBrand() != null && !itemPageQuery.getBrand().isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("brand", itemPageQuery.getBrand()));
        }

        // 添加价格区间过滤
        if (itemPageQuery.getMinPrice() != null || itemPageQuery.getMaxPrice() != null) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("price");
            if (itemPageQuery.getMinPrice() != null) {
                rangeQuery.gte(itemPageQuery.getMinPrice()); // 大于等于
            }
            if (itemPageQuery.getMaxPrice() != null) {
                rangeQuery.lte(itemPageQuery.getMaxPrice()); // 小于等于
            }
            boolQuery.filter(rangeQuery);
        }

        if (itemPageQuery.getIsAD()) {
            FunctionScoreQueryBuilder scoreQueryBuilder = QueryBuilders.functionScoreQuery(
                    // 原始查询，相关性算分
                    boolQuery,
                    // function score的数组
                    new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                            // 其中的一个function score元素
                            new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                    // 过滤条件
                                    QueryBuilders.termQuery("isAD", true),
                                    // 算分函数
                                    ScoreFunctionBuilders.weightFactorFunction(ADWeight))
                    });
            request.source().query(scoreQueryBuilder);
        }

        // 4.配置 request 参数
        request.source()
                .query(boolQuery) // 设置查询条件
                .from(from)       // 设置分页起始
                .size(size);      // 设置分页大小

        // 5.发送请求
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return SearchService.parseResponseResult(response, page, size);
        } catch (IOException e) {
            log.error("查询商品失败", e);
            return PageDTO.empty(0L, 0L); // 返回空的分页结果
        }
    }

    @Override
    public List<String> autocomplete(String keyword, Integer size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        System.out.println(keyword);
        SearchRequest request = new SearchRequest("items");
        request.source()
                .query(QueryBuilders.matchQuery("name", keyword))
                .size(size);

        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return Arrays.stream(response.getHits().getHits())
                    .map(hit -> hit.getSourceAsMap().get("name").toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("自动补全失败", e);
            return Collections.emptyList();
        }
    }

}
