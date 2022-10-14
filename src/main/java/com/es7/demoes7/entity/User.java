package com.es7.demoes7.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "jacob_index")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String Name;
    private Integer age;
//    old没啥用
}
