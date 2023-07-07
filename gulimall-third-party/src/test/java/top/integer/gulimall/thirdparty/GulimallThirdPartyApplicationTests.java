package top.integer.gulimall.thirdparty;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.integer.gulimall.thirdparty.controller.SmsController;

import java.io.File;

@SpringBootTest
class GulimallThirdPartyApplicationTests {

    @Autowired
    private OSS oss;
    @Autowired
    private SmsController smsController;

}
