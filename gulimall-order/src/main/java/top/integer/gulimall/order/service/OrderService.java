package top.integer.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.integer.common.utils.PageUtils;
import top.integer.gulimall.order.entity.OrderEntity;
import top.integer.gulimall.order.vo.OrderConfirmVo;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author songxiaoxu
 * @email songxiaoxu2002@gmail.com
 * @date 2023-05-15 15:50:58
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

}

