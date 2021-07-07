package com.wzw.gulishop.search.service;

import com.wzw.common.to.es.SkuEsModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @auther Kevin
 * @ClassName ProductSaveService
 * @Date 2021.06.23 22:03
 */

@Service
public interface ProductSaveService {
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
