package top.integer.gulimall.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.integer.gulimall.order.vo.MemberAddressVo;

import java.util.List;

@Component
@FeignClient("gulimall-member")
public interface MemberAddressFeign {
    @GetMapping("/member/memberreceiveaddress/address")
    List<MemberAddressVo> getMemberAddress(@RequestParam("memberId") Long memberId);
}
