package com.atmb.test;

import com.atmb.mapper.IUserMapper;
import com.atmb.pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class CacheTest {

    private IUserMapper userMapper;
    private SqlSession sqlSession;
    private SqlSessionFactory sqlSessionFactory;

    @Before
    public void before() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        sqlSession = sqlSessionFactory.openSession();
        userMapper = sqlSession.getMapper(IUserMapper.class);

    }

    @Test
    public void firstLevelCache() {
        // 第一次查询id为1的用户
        User user1 = userMapper.findUserById(1);

        // 第二次查询id为1的用户
        User user2 = userMapper.findUserById(1);

        //结果只会打印一次Preparing: select * from user where id = ? 说明第二次没有发出查询
        System.out.println(user1 == user2);//结果为true
        //更新用户
        //User user = new User();
        //user.setId(1);
        //user.setUsername("tom");
        //userMapper.updateUser(user);
        //sqlSession.commit();
        //sqlSession.clearCache();
    }

    @Test
    public void firstLevelCacheUpdate() {
        // 第一次查询id为1的用户
        User user1 = userMapper.findUserById(1);
        System.out.println(user1);
        //更新用户
        User user = new User();
        user.setId(1);
        user.setUsername("tom");
        userMapper.updateUser(user);
        sqlSession.commit();


        // 第二次查询id为1的用户
        User user2 = userMapper.findUserById(1);
        System.out.println(user2);
        //结果打印两次Preparing: select * from user where id = ?
        System.out.println(user1 == user2);//结果为false

    }

    @Test
    public void firstLevelCacheClearCache() {
        // 第一次查询id为1的用户
        User user1 = userMapper.findUserById(1);
        System.out.println(user1);
        //清空一级缓存
        sqlSession.clearCache();
        // 第二次查询id为1的用户
        User user2 = userMapper.findUserById(1);
        System.out.println(user2);
        //结果打印两次Preparing: select * from user where id = ?
        System.out.println(user1 == user2);//结果为false

    }


    @Test
    public void SecondLevelCache() {
        SqlSession sqlSession1 = sqlSessionFactory.openSession();
        SqlSession sqlSession2 = sqlSessionFactory.openSession();
        SqlSession sqlSession3 = sqlSessionFactory.openSession();

        IUserMapper mapper1 = sqlSession1.getMapper(IUserMapper.class);
        IUserMapper mapper2 = sqlSession2.getMapper(IUserMapper.class);
        IUserMapper mapper3 = sqlSession3.getMapper(IUserMapper.class);

        User user1 = mapper1.findUserById(1);
        sqlSession1.clearCache(); //清空一级缓存


        //User user = new User();
        //user.setId(1);
        //user.setUsername("lisi");
        //mapper3.updateUser(user);
        //sqlSession3.commit();

        User user2 = mapper2.findUserById(1);

        System.out.println(user1 == user2);
    }

    @Test
    public void testTwoCache() {
        //根据 sqlSessionFactory 产⽣ session
        SqlSession sqlSession1 = sqlSessionFactory.openSession();
        SqlSession sqlSession2 = sqlSessionFactory.openSession();
        SqlSession sqlSession3 = sqlSessionFactory.openSession();
        System.out.println(sqlSession1 == sqlSession2);
        String statement = "com.lagou.pojo.UserMapper.selectUserByUserld";
        IUserMapper userMapper1 = sqlSession1.getMapper(IUserMapper.class);
        IUserMapper userMapper2 = sqlSession2.getMapper(IUserMapper.class);
        IUserMapper userMapper3 = sqlSession3.getMapper(IUserMapper.class);
        //第⼀次查询，发出sql语句，并将查询的结果放⼊缓存中
        User user1 = userMapper1.findUserById(1);
        System.out.println(user1);
        sqlSession1.close(); //第⼀次查询完后关闭sqlSession
        //执⾏更新操作，commit()
        user1.setUsername("bbb");
        userMapper3.updateUser(user1);
        sqlSession3.commit();
        //第⼆次查询，由于上次更新操作，缓存数据已经清空(防⽌数据脏读)，这⾥必须再次发出sql语
        User u2 = userMapper2.findUserById(1);
        System.out.println(u2);
        sqlSession2.close();
    }
}
