package top.integer.gulimall.order.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.integer.gulimall.order.entity.PaymentInfoEntity;
import top.integer.gulimall.order.enums.OrderStatusEnum;
import top.integer.gulimall.order.service.OrderService;
import top.integer.gulimall.order.service.PaymentInfoService;
import top.integer.gulimall.order.utils.AlipayTemplate;
import top.integer.gulimall.order.vo.PayAsyncVo;
import top.integer.gulimall.order.vo.PayVo;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
public class PayWebController {
    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentInfoService paymentInfoService;

    @ResponseBody
    @GetMapping("/payOrder")
    public String payOrder(String orderSn) throws AlipayApiException {
        PayVo pay = orderService.getOrderPay(orderSn);

        //        System.out.println("result = " + result);
        return alipayTemplate.pay(pay);
    }

    @PostMapping("/payed/notify")
    @ResponseBody
    public String payedNotify(PayAsyncVo payAsyncVo, HttpServletRequest request) throws AlipayApiException, UnsupportedEncodingException {
        if (!checkSignature(request)) {
            return "fail";
        }
        System.out.println("payAsyncVo = " + payAsyncVo);
        PaymentInfoEntity payment = new PaymentInfoEntity();
        payment.setAlipayTradeNo(payAsyncVo.getTrade_no());
        payment.setOrderSn(payAsyncVo.getOut_trade_no());
        payment.setPaymentStatus(payAsyncVo.getTrade_status());
        paymentInfoService.save(payment);

        if ("TRADE_SUCCESS".equals(payAsyncVo.getTrade_status())) {
            this.orderService.updateOrderPayedStatus(payAsyncVo.getOut_trade_no(), OrderStatusEnum.PAYED.getCode());
        }
        return "success";
    }

    public boolean checkSignature(HttpServletRequest request) throws UnsupportedEncodingException, AlipayApiException {
//获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
//            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        return AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(), alipayTemplate.getCharset(), alipayTemplate.getSign_type());
    }
}
