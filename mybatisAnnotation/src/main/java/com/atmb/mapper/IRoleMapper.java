package com.atmb.mapper;

import com.atmb.pojo.Role;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface IRoleMapper {


    @Select("select * from sys_role r,sys_user_role ur where r.id = ur.roleid and ur.userid = #{uid}")
    public List<Role> findRoleByUid(Integer uid);
}
