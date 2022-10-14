package com.es7.demoes7.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "boot")
public class boot {
    private long id;
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String name;
    //    old没啥用
}
