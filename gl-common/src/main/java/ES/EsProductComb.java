package ES;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class EsProductComb {

    private BigDecimal price;
    private Boolean hasStock;
    private String productCombId;
    private String productId;
    private String productCombImg;
    private String productCombTitle;
    private String categoryId;
    private String categoryName;
    private String brandId;
    private String brandName;
    private String brandImg;
    private Integer saleCount;
    private Long hotScore=0L;
    private List<Attr> attrList;

    @Data
    public static class Attr{
        private String name;
        private String id;
        private String value;
    }
}
