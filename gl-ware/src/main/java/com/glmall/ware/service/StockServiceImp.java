package com.glmall.ware.service;

import TO.WareOrderTaskDetailTo;
import TO.WareOrderTaskTo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.glmall.utils.BeanUtil;
import com.glmall.utils.R;
import com.glmall.ware.beans.WareOrderTask;
import com.glmall.ware.beans.WareOrderTaskDetail;
import com.glmall.ware.feign.OrderFeign;
import com.glmall.ware.mapper.WareOrderTaskDetailMapper;
import com.glmall.ware.mapper.WareOrderTaskMapper;
import com.glmall.ware.mapper.WareProductCombMapper;
import com.glmall.ware.vo.LockOrderStockVo;
import com.glmall.ware.vo.OrderItemVo;
import com.glmall.ware.vo.OrderVo;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import exception.NoStockException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class StockServiceImp implements StockService {
    @Autowired
    WareProductCombMapper wareProductCombMapper;
    @Autowired
    WareOrderTaskMapper wareOrderTaskMapper;
    @Autowired
    WareOrderTaskDetailMapper wareOrderTaskDetailMapper;
    @Autowired
    OrderFeign orderFeign;
    @Autowired
    RabbitTemplate rabbitTemplate;


    @Override
    public Boolean lockOrderItemStock(LockOrderStockVo lockOrderStockVos) {
        List<OrderItemVo> orderItemList = lockOrderStockVos.getOrderItemList();
        WareOrderTask wareOrderTask = new WareOrderTask();
        wareOrderTask.setOrderSn(lockOrderStockVos.getOrderSn());
        WareOrderTask wareOrderTaskSaved = wareOrderTaskMapper.save(wareOrderTask);
        for (OrderItemVo orderItemVo : orderItemList) {
            List<String> wareIdsHaveStockBySkuId = wareProductCombMapper.findWareIdsHaveStockBySkuId(orderItemVo.getSkuId());
            if (wareIdsHaveStockBySkuId == null || wareIdsHaveStockBySkuId.size() == 0) {
                throw new NoStockException(orderItemVo.getSkuId());
            }
            Boolean lockSuccess = false;
            for (String wareId : wareIdsHaveStockBySkuId) {
                Integer lockRes = wareProductCombMapper.lockBySkuIdAndCountAndWareId(
                        orderItemVo.getSkuId(), wareId, orderItemVo.getSkuQuantity());
                if (lockRes == 1) {
                    lockSuccess = true;
                    WareOrderTaskDetail wareOrderTaskDetail = new WareOrderTaskDetail();
                    wareOrderTaskDetail.setCount(orderItemVo.getSkuQuantity());
                    wareOrderTaskDetail.setSkuId(orderItemVo.getSkuId());
                    wareOrderTaskDetail.setWareId(wareId);
                    wareOrderTaskDetail.setStatus(0);
                    wareOrderTaskDetail.setTaskId(wareOrderTaskSaved.getId());
                    wareOrderTaskDetailMapper.save(wareOrderTaskDetail);
                    WareOrderTaskTo wareOrderTaskTo = new WareOrderTaskTo();
                    wareOrderTaskTo.setWareOrderTaskId(wareOrderTaskSaved.getId());
                    WareOrderTaskDetailTo wareOrderTaskDetailTo = new WareOrderTaskDetailTo();
                    BeanUtils.copyProperties(wareOrderTaskDetail, wareOrderTaskDetailTo);
                    wareOrderTaskTo.setWareOrderTaskDetailTo(wareOrderTaskDetailTo);
                    rabbitTemplate.convertAndSend("stock.event.exchange", "stock.locked", wareOrderTaskTo);
                    break;
                }
            }
            if (!lockSuccess) {
                throw new NoStockException(orderItemVo.getSkuId());
            }
        }

        return true;
    }

    @Override
    public void unLockStock(WareOrderTaskTo wareOrderTaskTo) throws IOException {
        WareOrderTaskDetail wareOrderTaskDetail = wareOrderTaskDetailMapper
                .findById_1(wareOrderTaskTo.getWareOrderTaskDetailTo().getId());
        if (null != wareOrderTaskDetail) {
            WareOrderTask wareOrderTask = wareOrderTaskMapper.findById(wareOrderTaskTo.getWareOrderTaskId()).get();
            R orderBySn = orderFeign.getOrderBySn(wareOrderTask.getOrderSn());
            if ((Integer) orderBySn.get("code") == 0) {
                OrderVo orderVo = orderBySn.getData(new TypeReference<>() {
                });
                if (orderVo == null || orderVo.getStatus() == 4) {
                    if (wareOrderTaskDetail.getStatus() == 0) {
                        //0=locked 1=unlocked 2=reduced
                        WareOrderTaskDetailTo wareOrderTaskDetailTo = wareOrderTaskTo.getWareOrderTaskDetailTo();
                        Integer unlock = wareProductCombMapper
                                .unLockStock(wareOrderTaskDetailTo.getSkuId(), wareOrderTaskDetailTo.getWareId(),
                                        wareOrderTaskDetailTo.getCount().toString());
                        wareOrderTaskDetail.setStatus(1);
                        wareOrderTaskDetailMapper.save(wareOrderTaskDetail);
                    }
                }
            } else {
                throw new RuntimeException("order_feign getOrderSn failed");
            }
        }

    }

    @Override
    public void unLockStock(OrderVo orderVo) {
        WareOrderTask byOrderSn = wareOrderTaskMapper.findByOrderSn(orderVo.getOrderSn());
        Specification<WareOrderTaskDetail> specification = new Specification<>() {
            @Override
            public Predicate toPredicate(Root<WareOrderTaskDetail> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate taskId = criteriaBuilder.equal(root.get("taskId"), byOrderSn.getId());
                Predicate status = criteriaBuilder.equal(root.get("status"), 0);
                return criteriaBuilder.and(taskId, status);
            }
        };
        List<WareOrderTaskDetail> wareOrderTaskDetailList = wareOrderTaskDetailMapper.findAll(specification);
        for (WareOrderTaskDetail wareOrderTaskDetail : wareOrderTaskDetailList) {
            wareProductCombMapper.unLockStock(wareOrderTaskDetail.getSkuId(), wareOrderTaskDetail.getWareId(),
                    wareOrderTaskDetail.getCount().toString());
            wareOrderTaskDetail.setStatus(1);
            wareOrderTaskDetailMapper.save(wareOrderTaskDetail);
        }

    }
}
