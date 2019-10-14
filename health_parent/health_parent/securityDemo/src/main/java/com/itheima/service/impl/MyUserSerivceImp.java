package com.itheima.service.impl;

import com.itheima.pojo.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

public class MyUserSerivceImp implements UserDetailsService {

    /**
     * 登陆认证时，security就会来调用这个方法
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 调用dao方法
        User user = findByUsername(username);
        if(null != user){
            // 密码校验 交给security
            //String username, 登陆用户名
            // String password, 密码 从数据库获取
            //Collection<? extends GrantedAuthority> authorities 用户的权限集合
            List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>();
            // 权限，这里可以是角色名也可以是权限名
            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");
            authorityList.add(authority);
            // security登陆用户信息, authorityList设置了用户的权限集合
            org.springframework.security.core.userdetails.User user1 = new org.springframework.security.core.userdetails.User(username, user.getPassword(), authorityList);
            return user1;
        }
        return null;
    }

    private User findByUsername(String username){
        User user = null;
        if("admin".equals(username)) {
            user = new User();
            user.setUsername("admin");
            user.setPassword("$2a$10$eVv1MOayZz76kzsXXfqbquoRJDJ9tlSG1yMBRvvGBa6dVdpm0grGe");
            user.setId(9527);
        }
        return user;
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("admin"));

        //$2a$10$0LnwGI3TM9jBdRhnwujtkun1Mlr5BHgB/vIno8xn.GHGQcgKeCaYS
        //$2a$10$lxb0uYgUWKSpMbTbbPGdL.xFasE79CIhYc99oJF.OeCsx6YOycA9.

        // 校验
        //rawPassword    没有加密 明文
        //encodedPassword  加密后的密文
        System.out.println(encoder.matches("1234", "$2a$10$0LnwGI3TM9jBdRhnwujtkun1Mlr5BHgB/vIno8xn.GHGQcgKeCaYS"));
        System.out.println(encoder.matches("1234", "$2a$10$lxb0uYgUWKSpMbTbbPGdL.xFasE79CIhYc99oJF.OeCsx6YOycA9."));

    }
}
