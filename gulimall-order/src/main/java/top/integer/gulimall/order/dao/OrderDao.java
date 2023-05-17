package top.integer.gulimall.order.dao;

import top.integer.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author songxiaoxu
 * @email songxiaoxu2002@gmail.com
 * @date 2023-05-15 15:50:58
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
