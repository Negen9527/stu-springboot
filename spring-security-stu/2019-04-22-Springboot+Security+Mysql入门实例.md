### 用SpringBoot + Security + Mysql + Jpa 完成一个简单的登录验证

#### 1、新建springboot项目并引入依赖
pom.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.4.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.negen</groupId>
    <artifactId>spring-security-stu</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>war</packaging>
    <name>spring-security-stu</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <!--<scope>runtime</scope>-->
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

#### 2、配置 application.yaml
application.yaml
```
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/db_springboot?useUnicode=true&characterEncoding=utf-8&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: 123456

  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update
```
#### 3、创建实体类 UserRole (用户角色)
UserRole.java
```
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * 用户角色表
 */
@Setter
@Getter
@ToString
@Entity
@Table(name = "user_role")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column(name = "role_name")
    String roleName;
}
```
#### 4、创建实体类 User (用户),实现 security 里的 UserDetails 接口，重写各个方法
User.java
```
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Setter
@Getter
@ToString
@Entity
@Table(name = "user")
public class User implements UserDetails {
    static Logger log = LoggerFactory.getLogger(User.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column(name = "user_name")
    String userName;
    @Column(name = "user_pass")
    String userPass;
    @ManyToMany(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    List<UserRole> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        List<UserRole> roles = this.getRoles();
        for (UserRole userRole:roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(userRole.getRoleName()));
        }
        log.info("grantedAuthorities===>" + grantedAuthorities.toString());
        return grantedAuthorities;
    }
    @Override
    public String getPassword() {
        return this.userPass;
    }
    @Override
    public String getUsername() {
        return this.userName;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
```
#### 5、创建 UserRepository 接口
UserRpository.java
```
import com.negen.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String userName);
}
```
#### 6、创建 UserService
UserService.java
```
import com.negen.model.User;
import com.negen.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserService implements UserDetailsService {
    static Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        log.info("param--userName===>" + userName);
        User user = userRepository.findByUserName(userName);
        if(user == null){
            log.info("user===>" + user);
            throw new UsernameNotFoundException("用户名不存在");
        }
        log.info("userName===>" + user.getUsername());
        log.info("userPass===>" + user.getPassword());
        return user;
    }
}
```

#### 7、创建 WebSecurityConfig
WebSecurityConfig.java
```
import com.negen.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    UserDetailsService userService(){
        return new UserService();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService());
    }
    //已弃用
    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated().and()
                .formLogin()
                .loginPage("/login")
                .failureUrl("/login?error")
                .permitAll().and()
                .logout()
                .permitAll();

    }
}
```

#### 8、配置 WebMvcConfig ，形成登录地址映射(注册登录地址)
WebMvcConfig.java
```
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }
}
```

#### 9、创建 IndexController 用于登录成功后的跳转
```
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    @RequestMapping("/")
    public String index(){
        System.out.println("in======>index");
        return "index";
    }
}
```
#### 10、编写登录界面
login.html
```
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <title>登录</title>
</head>
<body>
<form action="/login" th:action="@{/login}" method="post" name="form" role="form">
    <table>
        <tr>
            <td>username：</td>
            <td><input type="text" name="username" placeholder="请输入用户名"/></td>
        </tr>

        <tr>
            <td>userpass：</td>
            <td><input type="password" name="password" placeholder="请输入密码"/></td>
        </tr>
    </table>
    <input type="submit" value="登录">
</form>
</body>
</html>
```
#### 11、编写index.html
```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>首页</title>
</head>
<body>
登录成功<br>
这是首页
</body>
</html>
```

#### 12、测试
创建数据库 db_springboot  
运行项目，进入数据库，向 user 表中随便添加一条数据  
浏览器访问localhost:8080 被拦截到登录页面，输入刚才写入数据库的用户账号密码登录

#### 登录表单有坑
![image.png](https://upload-images.jianshu.io/upload_images/16432686-a4f1629a42ead76e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

+ 表单 action 属性必须为 post
+ 账号输入框的 name 属性必须为 username
+ 密码输入框的 name 属性必须为 password  

具体原因可以看看我们实现的接口 UserDetails 的源码
![image.png](https://upload-images.jianshu.io/upload_images/16432686-3d083ece38d3578f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
