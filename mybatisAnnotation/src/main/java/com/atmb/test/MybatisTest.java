package com.atmb.test;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.atmb.mapper.IOrderMapper;
import com.atmb.mapper.IUserMapper;
import com.atmb.mapper.UserMapper;
import com.atmb.pojo.Order;
import com.atmb.pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MybatisTest {

    private IUserMapper userMapper;
    private IOrderMapper orderMapper;

    @Before
    public void befor() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        userMapper = sqlSession.getMapper(IUserMapper.class);
        orderMapper = sqlSession.getMapper(IOrderMapper.class);
    }

    @Test
    public void addUser() {
        User user = new User();
        //user.setId(10);
        user.setUsername("测试数据");
        user.setBirthday("2021-3-30");
        user.setPassword("123456");
        userMapper.addUser(user);
    }

    @Test
    public void updateUser() {
        User user = new User();
        user.setId(10);
        user.setUsername("修改了测试数据");

        userMapper.updateUser(user);

    }

    @Test
    public void selectUser() {
        List<User> users = userMapper.selectUser();
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void deleteUser() {
        userMapper.deleteUser(10);
    }


    @Test
    public void oneToOne() {
        List<Order> orderAndUser = orderMapper.findOrderAndUser();
        for (Order order : orderAndUser) {
            System.out.println(order);
        }
    }

    @Test
    public void oneToMany() {
        List<User> all = userMapper.findAll();
        for (User user : all) {
            System.out.println(user);
        }

    }

    @Test
    public void ManyToMany() {
        List<User> all = userMapper.findAllUserAndRole();
        for (User user : all) {
            System.out.println(user);
        }

    }

    @Test
    public void pageHelperTest() {

        PageHelper.startPage(1, 1);
        List<User> users = userMapper.selectUser();
        for (User user : users) {
            System.out.println(user);
        }

        PageInfo<User> pageInfo = new PageInfo<>(users);
        System.out.println("总条数：" + pageInfo.getTotal());
        System.out.println("总页数：" + pageInfo.getPages());
        System.out.println("当前页：" + pageInfo.getPageNum());
        System.out.println("每页显示的条数：" + pageInfo.getPageSize());


    }

    @Test
    public void mapperTest() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user = new User();
        user.setId(1);
        User user1 = mapper.selectOne(user);
        System.out.println(user1);


        //2.example方法
        Example example = new Example(User.class);
        example.createCriteria().andEqualTo("id", 1);
        List<User> users = mapper.selectByExample(example);
        for (User user2 : users) {
            System.out.println(user2);
        }
    }
}



