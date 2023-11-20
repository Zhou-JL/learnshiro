package com.zhoujl.learnshiro.eneity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 资源表
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2022-02-14 14:00:00
 * @see: com.zhoujl.demo.rpcservice.entity
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("permission")
public class Permission implements Serializable {


	private String id;


	private String name;


	private String perCode;

}