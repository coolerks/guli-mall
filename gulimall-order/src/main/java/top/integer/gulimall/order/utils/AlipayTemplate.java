package top.integer.gulimall.order.utils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import top.integer.gulimall.order.vo.PayVo;

//@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private String app_id = "9021000124651443";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC9F/J9ty7OxoBZiVGrSiEIMp1hPD8atFb2JFZlj6bOndOr7W1TMU6X305uJiKtvOSNBk/mOfnATobRxpVHXkiGV6RYvC4BZkarZB7vH6G+35imrXU1S/6iqgh6sB9jJwDqNc/qaM5PyibcJmI7SgqXddgMkUb744DSNBB80noSduYtmE6jia4vLDdf6zzDsx1Uk4wwzNIK7Qd42Cy74hF1IwpO24wN3qSg0QGpEpoLs9yOKJInKW4M5/zW3phaIGbd4JsY+dUKVihh054+1iLqhEu1iuo7ug7v6WgIhe+R8juVhJYwahJW10NRC1s1xXai+rkvGYlseqIjqqssjoOXAgMBAAECggEAb1czbHy5Lf3Jw27MzNwYORh8fK2ZxqKbddGNob2FDbH22gwyTEMDMP/G0arYzo4j0Tn/P+OxQF+8mLaiXWSRtbDTT4B2YlKTmAWbBVgNyDMmUjZehmRZ6fPOjFc6FAr6Se4nn7HYkTjJyuU7AAgn1mkILtY4AsdBqgQIFUdlo4qjOhpN4CisU5rtdvHToS3EM9INKMtPot5lEtJZo8yoFpVDEp7LqQKIY8Kt1QRZdi/LMV+psPV5Fo81Ty+xWpW6psob/hWE+M9UzeBN5Bwrg5VJQmVYiAoLxe0NS+47rh3oaLqWzmnflLgcQT17/DJxa6YNlufqvW1RpySgMR7CaQKBgQD+sYnvIaGE8IAKUrS/z4obGsHNxxOEW26K2KM/lOpAJ0b/xaR4rq9LBhMZlsxjRYIRb5zDM4+hZylzKL3p1fauKyHcW56+yHoy3CcquRax2s2tgU9C5jIPO+TwmbtbcklnmDuFn1Bm3dDUI1sJ9SkYRJ26uKbVr1+DpgudcJS4lQKBgQC+EENdoH/TIiodm8TuCzRB6JPV1rYA5Uadk1V9XIdzjqo3IXCrmpWroB8eEVFZa4uui6Zsc8TTysRKVgPA+hUAe0DObzk/GBUzLWTikreHnmERGBFPKKsPlB2Z8WsO2cJ1W4qgCLNOl7Wh1V8wQ3ecsc80ICEovZOZUrQ6KfmEewKBgEG4cD5LcFFMec2wH8Hq/NkXo3DE8O1TZoffdOiANZ4ORIzVZCZb9RGn5J/SwLTRB8oWAd6A3IwqgZCcr7y6IGg66wbggWr7ckTTeCvXDNsh+bbOyjQaEErMCrxm/uiO6hAYQSuuu2F/1VYYNXKxV0RS2FyJL0uucDgZTZ/3n/itAoGAOCqILdfrTlJvTbalmI75D20SmwPLXzJiDtjmT5iTcqO7S3V3XU7eYgx8hZ8qghlU+uzL7oq7pLbsEOegTlvY7v59ZUPbD/km8qu8f3RKBirYlnudSd5DxuPHZ6G/OHFDlPjLW4WWr2gHxd5PNe0KIOUuzUGdavOy9YFKP1H+KZcCgYAadrcS5j/BeZYo1ujzot0JfBGn16SgBp/QmK4hTRZzQ9GzWzTsxzNi34wiWRmeKZiy6JODFTaGMT1l/zS2jE8A39MpTrUdJzKhiWpecAIvKLfiZTnID6erQC8q1W3A0UErTvJoj9lOa6DYiYazshkgpRoZ5E+qVZdv5TE/Jf2ERw==";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlQxgDMl4bo3HpEch+Lz3ZFckqLPsvpNYWKJpV7sDzis1OL2QU3FJUBoRyxbIN4c5JHy/ihWxWe8WAElVdYlaieiTb8USrrSEbyXlPth8MgHQcWUSU3w+r5+U7BYoZJ4/x8PJwUG4n+3CRPpWk/fMk3phnSFsN1Zg4/vtS/xuUrYVCgNX/D0+QTTNcEc2Iss+3Uav8SQE+BYJhK8HLtwyjaabilepvMPqh0Oe02nPJ2YixbOHfH3o8wmGR45Dn9hYOzOSOK2s3Etx12T8mw6IAl6AznebSWv1S+wT+Y/woZtMnc8Iy4quHtzEVquFrqhE+qZlEnlWRMO0g1zQgWqiKwIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private String notify_url = " http://dtpq2m.natappfree.cc/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private String return_url = "http://order.gulimall.com/list.html";

    // 签名方式
    private String sign_type = "RSA2";

    // 字符编码格式
    private String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";

    public String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"timeout_express\":\"1m\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
//        System.out.println("支付宝的响应：" + result);

        return result;

    }
}
