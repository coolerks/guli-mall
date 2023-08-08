package top.integer.gulimall.search.entry;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@ToString
@Document(indexName = "bank")
public class Bank {
    private Integer accountNumber;
    private String firstname;
    private String address;
    private Integer balance;
    private String gender;
    private String city;
    private String employer;
    private String state;
    private Integer age;
    private String email;
    private String lastname;
}
