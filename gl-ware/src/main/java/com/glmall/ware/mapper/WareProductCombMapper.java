package com.glmall.ware.mapper;

import TO.ProductCombStockTO;
import com.glmall.ware.beans.ProductCombWare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WareProductCombMapper extends JpaRepository<ProductCombWare,String>, JpaSpecificationExecutor<ProductCombWare> {
    @Query(value="select sum(stock-stock_lock) from product_comb_ware where product_comb_id = ?1",nativeQuery=true)
    Long getProductCombHasStockByProductCombId(String productCombId);
}
