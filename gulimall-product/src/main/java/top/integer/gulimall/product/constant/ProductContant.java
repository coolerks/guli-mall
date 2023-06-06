package top.integer.gulimall.product.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ProductContant {
    @AllArgsConstructor
    @Getter
    public enum AttrEnum {
        /**
         *
         */
        ATTR_TYPE_BASE(1, "基本属性"), ATTR_TYPE_SALT(0, "销售属性");

        int code;
        String msg;
    }

    @AllArgsConstructor
    @Getter
    public enum StatusEnum {
        /**
         *
         */
        NEW_SPU(0, "新建"),
        SPU_UP(1, "上架"),
        SPU_DOWN(2, "下架");

        int code;
        String msg;
    }
}
