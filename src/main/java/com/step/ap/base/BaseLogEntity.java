package com.step.ap.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 实体父类
 */
@Getter
@Setter
public class BaseLogEntity extends BaseEntity {

    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
