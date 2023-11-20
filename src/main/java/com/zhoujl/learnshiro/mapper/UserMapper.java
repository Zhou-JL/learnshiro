package com.zhoujl.learnshiro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhoujl.learnshiro.eneity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2023-11-16 10:15
 * @see: com.zhoujl.learnshiro.mapper
 * @Version: 1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT NAME FROM role WHERE id IN (SELECT role_id FROM user_role WHERE user_id=(SELECT id FROM USER WHERE username=#{principal}))")
    List<String> getUserRoleInfoMapper(@Param("principal") String principal);
}
