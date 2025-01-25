package com.hmall.search.service;

import cn.hutool.json.JSONUtil;
import com.hmall.common.domain.PageDTO;
import com.hmall.search.domain.po.ItemDoc;
import com.hmall.search.domain.query.ItemPageQuery;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface SearchService {
    public static PageDTO<ItemDoc> parseResponseResult(SearchResponse response, int page, int size) {
        SearchHits searchHits = response.getHits();
        // 4.1.总条数
        long total = searchHits.getTotalHits().value;
        long totalPages = (long) Math.ceil((double) total / size);

        // 4.2.命中的数据
        List<ItemDoc> itemDocs = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            // 4.2.1.获取 source 结果
            String json = hit.getSourceAsString();
            // 4.2.2.转为 ItemDoc
            ItemDoc doc = JSONUtil.toBean(json, ItemDoc.class);

            // 4.3.处理高亮结果
            Map<String, HighlightField> hfs = hit.getHighlightFields();
            if (hfs != null && !hfs.isEmpty()) {
                // 4.3.1.根据高亮字段名获取高亮结果
                HighlightField hf = hfs.get("name");
                if (hf != null) {
                    // 4.3.2.获取高亮结果，覆盖非高亮结果
                    String hfName = hf.getFragments()[0].string();
                    doc.setName(hfName);
                }
            }
            // 添加到结果列表
            itemDocs.add(doc);
        }
        // 构建并返回分页结果
        return new PageDTO<>(total, totalPages, itemDocs);
    }

    public PageDTO<ItemDoc>search(ItemPageQuery itemPageQuery);

    public List<String> autocomplete(String keyword,Integer size);

}
