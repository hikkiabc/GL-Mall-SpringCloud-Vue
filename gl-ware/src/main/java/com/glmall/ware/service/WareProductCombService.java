package com.glmall.ware.service;

import TO.ProductCombStockTO;
import com.glmall.ware.beans.ProductCombWare;
import com.glmall.ware.beans.PurchaseDetail;
import com.glmall.ware.beans.WareInfo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface WareProductCombService {
    Page<ProductCombWare> getWareProductComb(Map map);

    Page<WareInfo> getWareInfo(Map map, String id);

    WareInfo saveWareInfo(WareInfo wareInfo);

    Page<PurchaseDetail> getPurchaseDetail(Map map, String id);

    PurchaseDetail savePurchaseDetail(PurchaseDetail purchaseDetail);

    ProductCombWare saveWareProductComb(ProductCombWare productCombWare);

    List<ProductCombStockTO> getProductCombHasStockByProductCombIdList(List<String> idList);
}
