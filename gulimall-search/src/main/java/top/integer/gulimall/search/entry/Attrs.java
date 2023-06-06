package top.integer.gulimall.search.entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Attrs {
    @Field(type = FieldType.Long)
    private Long attrId;
    @Field(type = FieldType.Keyword, index = false, docValues = false)
    private String attrName;
    @Field(type = FieldType.Keyword)
    private String attrValue;
}
