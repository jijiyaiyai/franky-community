package com.franky.community.dao;

import com.franky.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {

//    @Insert({
//            "insert into login_ticket(user_id,ticket,status,expired) " +
//                    "values ( #{userId},#{ticket},#{status},#{expired})"
//    })
//    @Options(useGeneratedKeys = true, keyProperty = "id")

    //新增登录凭证
    int insertLoginTicket(LoginTicket loginTicket);

//    @Select({
//            "select id,user_id,ticket,status,expired ",
//            "from login_ticket where ticket=#{ticket}"
//    })

    //查找登录凭证
    LoginTicket selectByTicket(String ticket);

//    @Update({
//            "<script>",
//            "update login_ticket set status=#{status} where ticket=#{ticket} ",
////            "<if test=\"ticket!=null\"> ",
////            "and 1=1 ",
////            "</if>",
//            "</script>"
//    })

    // 更新登录凭证的状态
    int updateStatus(String ticket, int status);

}
