package TO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SecKillOrderTo {
    private String orderSn;
    private String userId;
    private Integer num;
    private BigDecimal seckillPrice;
    private String promotionSessionId;
    private String skuId;

}
