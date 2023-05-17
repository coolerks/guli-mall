package top.integer.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;
import top.integer.common.valid.AddGroup;
import top.integer.common.valid.ListValue;
import top.integer.common.valid.UpdateGroup;
import top.integer.common.valid.UpdateStatusGroup;

import javax.validation.constraints.*;

/**
 * 品牌
 *
 * @author songxiaoxu
 * @email songxiaoxu2002@gmail.com
 * @date 2023-05-15 14:57:34
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	@NotNull(message = "更新时必须指定品牌id", groups = {UpdateGroup.class, UpdateStatusGroup.class})
	@Null(message = "添加商品是不允许指定品牌id", groups = AddGroup.class)
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "名称不能为空", groups = {UpdateGroup.class, AddGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@URL(message = "logo必须是一个链接", groups = {UpdateGroup.class, AddGroup.class})
	@NotBlank(message = "logo不能为空", groups = {UpdateGroup.class, AddGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
    @ListValue(value = {0, 1}, groups = {UpdateGroup.class, AddGroup.class, UpdateStatusGroup.class})
	@NotNull(message = "状态不能为空", groups = {UpdateGroup.class, AddGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@Pattern(regexp = "^[a-zA-Z]$", message = "首字母必须是字母且只有一个", groups = {UpdateGroup.class, AddGroup.class})
	@NotBlank(message = "首字母不能为空")
	private String firstLetter;
	/**
	 * 排序
	 */

	@Min(value = 0, message = "排序必须大于0", groups = {UpdateGroup.class, AddGroup.class})
	@NotNull(message = "排序不能为空", groups = {UpdateGroup.class, AddGroup.class})
	private Integer sort;

}
