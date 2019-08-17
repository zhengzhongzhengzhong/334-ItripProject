package cn.itrip.dao.itripUser;

import cn.itrip.pojo.*;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface ItripUserMapper {

    public ItripUser getUserE(@Param(value = "uname")String name);

	public ItripUser iflogin(@Param(value = "uname")String name,@Param(value = "passwd")String wd);

	public ItripUser getItripUserById(@Param(value = "id") Long id)throws Exception;

	public List<ItripUser>	getItripUserListByMap(Map<String, Object> param)throws Exception;

	public Integer getItripUserCountByMap(Map<String, Object> param)throws Exception;

	public Integer insertItripUser(ItripUser itripUser)throws Exception;

	public Integer updateItripUser(ItripUser itripUser)throws Exception;

	public Integer UpdateByid(@Param("id")String Ucode)throws Exception;

	public Integer deleteItripUserById(@Param(value = "id") Long id)throws Exception;

}
