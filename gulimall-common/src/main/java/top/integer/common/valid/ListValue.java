package top.integer.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自定义校验注解，至少需要提供以下属性：
 * <ul>
 *   <li>message - String 类型</li>
 *   <li>groups - Class&lt;?&gt;[] 类型</li>
 *   <li>payload - Class&lt;? extends Payload&gt;[] 类型</li>
 * </ul>
 * 还需要使用一些元注解用来标注：
 * <ul>
 *     <li>@Constraint 指定校验器，这个类需要实现{@link javax.validation.ConstraintValidator ConstraintValidator}接口</li>
 *     <li>@Target 目标</li>
 * </ul>
 * 还需要提供一个properties文件：ValidationMessages.properties，放在resource目录下
 * @author songxiaoxu
 */
@Constraint(validatedBy = {ListValueConstraintValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface ListValue {
    int[] value() default {};

    String message() default "{top.integer.gulimall.valid.ListValue.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
