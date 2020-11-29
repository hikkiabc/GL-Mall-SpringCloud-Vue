package TO;

import lombok.Data;

@Data
public class WareOrderTaskDetailTo {
    private String id;
    private String taskId;
    private Integer status;
    private String skuId;
    private Integer count;
    private String wareId;
}
