package com.es7.demoes7.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "news_abnp")
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("新闻测试代码")
public class EsNews {
    @ApiModelProperty("主键")
    @JsonProperty(value = "Uuid")
    private String Uuid;
    @ApiModelProperty("新闻类别")
    @JsonProperty(value = "Type")
    private String Type;
    @ApiModelProperty("标题")
    @JsonProperty(value = "Title")
    private String Title;
    @ApiModelProperty("html代码")
    @JsonProperty(value = "HtmlCode")
    private String HtmlCode;
    @ApiModelProperty("内容")
    @JsonProperty(value = "Content")
    private String Content;
    @ApiModelProperty("分页实体")
    private Integer formNo;
    private Integer formSize;

}
