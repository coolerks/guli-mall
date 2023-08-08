package top.integer.gulimall.search.entry;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.util.List;

@Document(indexName = "test-father")
@Data
@ToString
public class Father {
    @Id
    private String id;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String name;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String content;
    @Field(type = FieldType.Keyword)
    private BigDecimal num;
    @Field(type = FieldType.Nested)
    private List<Attrs> list;
}
