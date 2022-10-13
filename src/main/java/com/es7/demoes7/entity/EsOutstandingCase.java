package com.es7.demoes7.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "my_1012")
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("优秀案例模型")

public class EsOutstandingCase {

    //    资料标题
    @ApiModelProperty("资料标题")

    private String esDataTitle;
    //    资料标题id 在文件实体中 对应 businessUuid
    @ApiModelProperty("资料标题id")

    private String esReferenceTableUuid;

    //    资料分类id =====对应 pdictid 作为父id查询 下一级包含的类
    @ApiModelProperty("资料分类id")

    private String esDataSortId;
    //资料分类NAME
    @ApiModelProperty("资料分类NAME")
    private String esDataSortName;
    //    字典名称
    @ApiModelProperty("字典名称")
    private String esDictname;
    //    树id  和字典名称所在的类的 主键uuid一样
    @ApiModelProperty("树id")
    private String esTypeId;
    //    文件名
    @ApiModelProperty("文件名")
    private String esFileName;
    //    文件id
    @ApiModelProperty("文件id")
    private String esAttachmentUuid;
    //文件内容
    @ApiModelProperty("文件内容")
    private String esContent;

    @ApiModelProperty("分页实体")
    private Integer formNo;
    private Integer formSize;

    @ApiModelProperty("文件内容")
    private Byte[] esBlob;
}
