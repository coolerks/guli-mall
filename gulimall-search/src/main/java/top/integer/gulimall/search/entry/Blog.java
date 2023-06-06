package top.integer.gulimall.search.entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "my-blog")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Blog {
    @Id
    private Long id;
    private String title;
    private String content;
}
