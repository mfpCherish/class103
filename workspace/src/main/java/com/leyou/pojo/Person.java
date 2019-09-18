package com.leyou.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "person01",type = "docs",shards = 3,replicas = 1)
public class Person implements Serializable {
    //使用REST的API的时候如下
    /*private String id;
    private String name;
    //职业
    private String career;
    //作品
    private String work;
    //账户
    private Double account;
    //描述
    private String des;*/


    //使用SDE的时候如下

    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Keyword)
    private String name;
    //职业
    @Field(type = FieldType.Keyword,index = true,store = true)
    private String career;
    //作品
    @Field(type = FieldType.Keyword,index = true,store = true)
    private String work;
    //账户
    @Field(type = FieldType.Double,index = true,store = true)
    private Double account;
    //描述
    @Field(type = FieldType.Text,analyzer = "ik_max_word",index = true,store = true)
    private String des;

}
