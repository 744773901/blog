package com.zjj.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author 知白守黑
 * @date 2022/11/16 23:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaxwellDataDTO {

    /**
     * 变更数据所属的数据库
     */
    private String database;

    /**
     * 变更数据所属的表
     */
    private String table;

    /**
     * 数据变更类型
     */
    private String type;

    /**
     * 对于insert类型，表示插入的数据
     * 对于update类型，标识修改之后的数据
     * 对于delete类型，表示删除的数据
     */
    private Map<String, Object> data;

    /**
     * 数据变更发生的时间
     */
    private Integer ts;

    /**
     * 事务id
     */
    private Integer xid;


    /**
     * 事务提交标志，可用于重新组装事务
     */
    private Boolean commit;


}
