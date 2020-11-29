package com.glmall.ware.mapper;

import TO.ProductCombStockTO;
import com.glmall.ware.beans.ProductCombWare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WareProductCombMapper extends JpaRepository<ProductCombWare, String>, JpaSpecificationExecutor<ProductCombWare> {
    @Query(value = "select sum(stock-stock_lock) from product_comb_ware where product_comb_id = ?1", nativeQuery = true)
    Long getProductCombHasStockByProductCombId(String productCombId);

    @Query(value = "select ware_id from product_comb_ware where product_comb_id=?1 " +
            "and stock-stock_lock>0", nativeQuery = true)
    List<String> findWareIdsHaveStockBySkuId(String id);

    @Modifying
    @Query(value = "UPDATE product_comb_ware set stock_lock=stock_lock+?3 where" +
            " product_comb_id=?1 and ware_id=?2 and stock-stock_lock>=?3", nativeQuery = true)
    Integer lockBySkuIdAndCountAndWareId(String skuId, String wareId, Integer skuQuantity);

    @Modifying
    @Query(value="update product_comb_ware set stock_lock=stock_lock+?3 where product_comb_id=?1 and" +
            " ware_id =?2",nativeQuery=true)
    Integer unLockStock(String skuId, String wareId, String count);
}
