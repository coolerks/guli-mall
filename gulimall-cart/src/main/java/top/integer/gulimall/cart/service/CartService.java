package top.integer.gulimall.cart.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.integer.common.utils.R;
import top.integer.gulimall.cart.feign.SkuFeign;
import top.integer.gulimall.cart.interceptor.CartInterceptor;
import top.integer.gulimall.cart.vo.CartItemVo;
import top.integer.gulimall.cart.vo.CartVo;
import top.integer.gulimall.cart.vo.SkuInfoVo;
import top.integer.gulimall.cart.vo.UserInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class CartService {
    @Autowired
    private ThreadPoolExecutor executor;
    @Autowired
    private RedisTemplate<Object, Object> template;
    @Autowired
    private SkuFeign skuFeign;
    public static final String CART_PREFIX = "gulimall:cart:";

    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<Object, Object, Object> cartOps = getCartOps();

        CartItemVo cartItemVo = new CartItemVo();


        if (cartOps.get(String.valueOf(skuId)) != null) {
            CartItemVo cartItemVo1 = (CartItemVo) cartOps.get(String.valueOf(skuId));
            if (cartItemVo1 != null) {
                BeanUtils.copyProperties(cartItemVo1, cartItemVo);
            }
            cartItemVo.setCount(cartItemVo.getCount() + num);
        } else {
            CompletableFuture<Void> getSkuInfo = CompletableFuture.runAsync(() -> {
                R skuInfo = skuFeign.getSkuInfo(skuId);
                SkuInfoVo skuInfoVo = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                cartItemVo.setCheck(true);
                cartItemVo.setCount(num);
                cartItemVo.setSkuId(skuInfoVo.getSkuId());
                cartItemVo.setPrice(skuInfoVo.getPrice());
                cartItemVo.setImage(skuInfoVo.getSkuDefaultImg());
                cartItemVo.setTitle(skuInfoVo.getSkuTitle());
            }, executor);
            CompletableFuture<Void> getAttrValues = CompletableFuture.runAsync(() -> {
                R attrValues = skuFeign.getAttrValues(skuId);
                List<String> attrs = attrValues.getData(new TypeReference<List<String>>() {
                });
                cartItemVo.setSkuAttr(attrs);
            }, executor);
            CompletableFuture.allOf(getSkuInfo, getAttrValues).get();
        }

        cartOps.put(String.valueOf(skuId), cartItemVo);
        return cartItemVo;
    }

    private BoundHashOperations<Object, Object, Object> getCartOps() {
        UserInfo userInfo = CartInterceptor.userInfo.get();


        String cartKey = null;
        if (userInfo.getUserId() != null) {
            cartKey = String.valueOf(userInfo.getUserId());
        } else {
            cartKey = userInfo.getUserKey();
        }
        return getCartOps(cartKey);
    }

    public BoundHashOperations<Object, Object, Object> getCartOps(String key) {
        return template.boundHashOps(CART_PREFIX + key);
    }

    public CartItemVo getSkuInfo(Long skuId) {
        BoundHashOperations<Object, Object, Object> cartOps = getCartOps();
        Object o = cartOps.get(String.valueOf(skuId));
        return (CartItemVo) o;
    }

    public CartVo getCart() {
        UserInfo userInfo = CartInterceptor.userInfo.get();
        BoundHashOperations<Object, Object, Object> cartOps = getCartOps();
        CartVo cartVo = new CartVo();

        List<CartItemVo> tempUserCartItems = getCartItems(userInfo.getUserKey());


        if (userInfo.getUserId() != null) {
            ArrayList<CartItemVo> items = new ArrayList<>();
            cartVo.setItems(items);
            if (!tempUserCartItems.isEmpty()) {
                tempUserCartItems.forEach(it -> {
                    try {
                        addToCart(it.getSkuId(), it.getCount());
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            List<CartItemVo> loginUserCartItems = getCartItems(String.valueOf(userInfo.getUserId()));
            cartVo.setItems(loginUserCartItems);
            template.delete(CART_PREFIX + userInfo.getUserKey());
        } else {
            cartVo.setItems(tempUserCartItems);
        }
        return cartVo;
    }

    public List<CartItemVo> getCartItems(String key) {
        if (key == null) {
            return Collections.emptyList();
        }
        BoundHashOperations<Object, Object, Object> ops = getCartOps(key);
        List<Object> values = ops.values();
        List<CartItemVo> cartItemVos = Collections.emptyList();
        if (values != null) {
            cartItemVos = values.stream().map(it -> (CartItemVo) it).toList();
        }
        return cartItemVos;
    }

    public void checkOrUncheck(Long skuId, Boolean isChecked) {
        BoundHashOperations<Object, Object, Object> cartOps = getCartOps();
        CartItemVo cartItemVo = (CartItemVo) cartOps.get(String.valueOf(skuId));
        if (cartItemVo != null) {
            cartItemVo.setCheck(Boolean.TRUE.equals(isChecked));
            cartOps.put(String.valueOf(skuId), cartItemVo);
        }
    }

    public void countItem(Long skuId, Long count) {
        BoundHashOperations<Object, Object, Object> cartOps = getCartOps();
        CartItemVo cartItemVo = (CartItemVo) cartOps.get(String.valueOf(skuId));
        if (cartItemVo != null) {
            cartItemVo.setCount(count.intValue());
            cartOps.put(String.valueOf(skuId), cartItemVo);
        }
    }

    public void deleteItem(Long skuId) {
        BoundHashOperations<Object, Object, Object> cartOps = getCartOps();
        cartOps.delete(String.valueOf(skuId));
    }

    public List<CartItemVo> currentUserCartItems(Long memberId) {
        BoundHashOperations<Object, Object, Object> cartOps = getCartOps(String.valueOf(memberId));
        return Optional.ofNullable(cartOps.values())
                .orElse(Collections.emptyList())
                .stream()
                .map(it -> (CartItemVo) it)
                .filter(CartItemVo::getCheck)
                .toList();
    }
}

