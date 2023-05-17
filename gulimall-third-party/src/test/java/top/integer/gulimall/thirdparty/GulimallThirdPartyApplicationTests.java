package top.integer.gulimall.thirdparty;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
class GulimallThirdPartyApplicationTests {

    @Autowired
    private OSS oss;

    @Test
    void contextLoads() {
//        PutObjectResult putObjectResult = oss.putObject("gulimall-gulimall-gulimall-gulimall-gulimall", "博客2.drawio (2).png", new File("D:\\Download\\博客2.drawio (2).png"));
//        System.out.println("putObjectResult = " + putObjectResult);
    }

}
