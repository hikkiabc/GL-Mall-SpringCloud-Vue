package com.glmall.glproduct.dao;

import com.glmall.glproduct.beans.ProductCombination;
import com.glmall.glproduct.beans.vo.SkuItemSaleAttrVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface ProductCombinationMapper extends JpaRepository<ProductCombination,String>, JpaSpecificationExecutor< ProductCombination> {

    List<ProductCombination> getProductCombinationByProductId(String id);

    @Query(value="SELECT GROUP_CONCAT(product_combination.id)  skuIds, product_comb_attribute.id attrId," +
            " product_comb_attribute.`name` attrName, product_comb_attribute.`value` attrValue FROM" +
            " `product_combination` LEFT JOIN product_comb_attribute on" +
            " product_comb_attribute.product_comb_id=product_combination.id" +
            "  where product_combination.product_id=?1 GROUP BY" +
            " product_comb_attribute.id, product_comb_attribute.`name`,product_comb_attribute.`value` ",
            nativeQuery=true)
    List<Map<String,Object>> findProductCombSaleAttrByProductId(String productId);
}
