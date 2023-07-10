package top.integer.gulimall.member.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

@TableName("ums_member_oauth2")
@Data
@ToString
public class MemberOauth2 {
    private Long memberId;
    private String uid;
}
