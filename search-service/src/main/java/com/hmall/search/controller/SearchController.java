package com.hmall.search.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmall.common.domain.PageDTO;
import com.hmall.search.domain.po.ItemDoc;
import com.hmall.search.domain.query.ItemPageQuery;
import com.hmall.search.service.SearchService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "ES搜索相关接口")
@RestController
@RequestMapping("/es_search")
@RequiredArgsConstructor
public class SearchController {
    @Autowired
    private final SearchService searchService;

    @PostMapping("/search")
    public PageDTO<ItemDoc> search(ItemPageQuery itemPageQuery) {
        return searchService.search(itemPageQuery);
    }

    @GetMapping("/autocomplete")
    public List<String> autocomplete(@RequestParam String keyword, @RequestParam Integer size) {
        return searchService.autocomplete(keyword, size);
    }

}
