package com.hmall.data.service;


import com.hmall.data.domain.po.ElasticsearchOperation;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ESService {

    void batchProcessOperation(ElasticsearchOperation operation);
}
