package TO;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProductCombTo {
    private String id;
    private String productId;
    private String name;
    private String categoryId;
    private String brandId;
    private String defaultImg;
    private BigDecimal price;
    private String title;
    private String subTitle;
    private Integer saleCount=0;
}
