### 1.Interceptor介绍

**拦截器(Interceptor)同** Filter 过滤器一样，它俩都是面向切面编程——AOP 的具体实现（AOP切面编程只是一种编程思想而已）。

你可以使用 Interceptor 来执行某些任务，例如在 **Controller** 处理请求之前编写日志，添加或更新配置......

在 **Spring中**，当请求发送到 **Controller** 时，在被**Controller**处理之前，它必须经过 **Interceptors**（0或更多）。

**Spring Interceptor**是一个非常类似于**Servlet Filter** 的概念 。

### 2.过滤器和拦截器的区别

对于过滤器和拦截器的区别， [知乎@Kangol LI](https://www.zhihu.com/question/35225845/answer/61876681) 的回答很不错。

- 过滤器（Filter）：当你有一堆东西的时候，你只希望选择符合你要求的某一些东西。定义这些要求的工具，就是过滤器。
- 拦截器（Interceptor）：在一个流程正在进行的时候，你希望干预它的进展，甚至终止它进行，这是拦截器做的事情。

### 3.自定义 Interceptor

如果你需要自定义 **Interceptor** 的话必须实现 **org.springframework.web.servlet.HandlerInterceptor**接口或继承 **org.springframework.web.servlet.handler.HandlerInterceptorAdapter**类，并且需要重写下面下面3个方法：

```java
public boolean preHandle(HttpServletRequest request,
                         HttpServletResponse response,
                         Object handler)
 
 
public void postHandle(HttpServletRequest request,
                       HttpServletResponse response,
                       Object handler,
                       ModelAndView modelAndView)
 
 
public void afterCompletion(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler,
                            Exception ex)
```

注意： ***preHandle***方法返回 **true**或 **false**。如果返回 **true**，则意味着请求将继续到达 **Controller** 被处理。

![Interceptor示意图](https://my-blog-to-use.oss-cn-beijing.aliyuncs.com/2019-7/interceptor-spring.png)

每个请求可能会通过许多拦截器。下图说明了这一点。

![每个请求可能会通过许多拦截器](https://my-blog-to-use.oss-cn-beijing.aliyuncs.com/2019-7/interceptor-spring.png)

**`LogInterceptor`用于过滤所有请求**

```java
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class LogInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        long startTime = System.currentTimeMillis();
        System.out.println("\n-------- LogInterception.preHandle --- ");
        System.out.println("Request URL: " + request.getRequestURL());
        System.out.println("Start Time: " + System.currentTimeMillis());

        request.setAttribute("startTime", startTime);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, //
                           Object handler, ModelAndView modelAndView) throws Exception {

        System.out.println("\n-------- LogInterception.postHandle --- ");
        System.out.println("Request URL: " + request.getRequestURL());

        // You can add attributes in the modelAndView
        // and use that in the view page
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, //
                                Object handler, Exception ex) throws Exception {
        System.out.println("\n-------- LogInterception.afterCompletion --- ");

        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        System.out.println("Request URL: " + request.getRequestURL());
        System.out.println("End Time: " + endTime);

        System.out.println("Time Taken: " + (endTime - startTime));
    }

}
```

**`OldLoginInterceptor`**是一个拦截器，如果用户输入已经被废弃的链接   **“ / admin / oldLogin”**，它将重定向到新的 **“ / admin / login”。**

```java
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class OldLoginInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        System.out.println("\n-------- OldLoginInterceptor.preHandle --- ");
        System.out.println("Request URL: " + request.getRequestURL());
        System.out.println("Sorry! This URL is no longer used, Redirect to /admin/login");

        response.sendRedirect(request.getContextPath() + "/admin/login");
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, //
                           Object handler, ModelAndView modelAndView) throws Exception {

        // This code will never be run.
        System.out.println("\n-------- OldLoginInterceptor.postHandle --- ");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, //
                                Object handler, Exception ex) throws Exception {

        // This code will never be run.
        System.out.println("\n-------- QueryStringInterceptor.afterCompletion --- ");
    }

}
```

![](https://my-blog-to-use.oss-cn-beijing.aliyuncs.com/2019-7/OldLoginInterceptor.png)

![](https://my-blog-to-use.oss-cn-beijing.aliyuncs.com/2019-7/OldLoginInterceptor2.png)

**`AdminInterceptor`**

```java
package org.o7planning.sbinterceptor.interceptor;
 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
 
public class AdminInterceptor extends HandlerInterceptorAdapter {
 
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
 
        System.out.println("\n-------- AdminInterceptor.preHandle --- ");
        return true;
    }
 
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, //
            Object handler, ModelAndView modelAndView) throws Exception {
         
        System.out.println("\n-------- AdminInterceptor.postHandle --- ");
    }
 
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, //
            Object handler, Exception ex) throws Exception {
 
        System.out.println("\n-------- AdminInterceptor.afterCompletion --- ");
    }
 
}
```

**配置拦截器**

```java
import github.javaguide.springbootfilter.interceptor.AdminInterceptor;
import github.javaguide.springbootfilter.interceptor.LogInterceptor;
import github.javaguide.springbootfilter.interceptor.OldLoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // LogInterceptor apply to all URLs.
        registry.addInterceptor(new LogInterceptor());

        // Old Login url, no longer use.
        // Use OldURLInterceptor to redirect to a new URL.
        registry.addInterceptor(new OldLoginInterceptor())//
                .addPathPatterns("/admin/oldLogin");

        // This interceptor apply to URL like /admin/*
        // Exclude /admin/oldLogin
        registry.addInterceptor(new AdminInterceptor())//
                .addPathPatterns("/admin/*")//
                .excludePathPatterns("/admin/oldLogin");
    }

}

```

**自定义 Controller 验证拦截器**

```java
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class InterceptorTestController {

    @RequestMapping(value = { "/", "/test" })
    public String test(Model model) {

        System.out.println("\n-------- MainController.test --- ");

        System.out.println(" ** You are in Controller ** ");

        return "test";
    }

    // This path is no longer used.
    // It will be redirected by OldLoginInterceptor
    @Deprecated
    @RequestMapping(value = { "/admin/oldLogin" })
    public String oldLogin(Model model) {

        // Code here never run.
        return "oldLogin";
    }

    @RequestMapping(value = { "/admin/login" })
    public String login(Model model) {

        System.out.println("\n-------- MainController.login --- ");

        System.out.println(" ** You are in Controller ** ");

        return "login";
    }

}

```

**thymeleaf 模板引擎**

`test.html`

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
 
   <head>
      <meta charset="UTF-8" />
      <title>Spring Boot Mvc Interceptor example</title>
   </head>
 
   <body>
      <div style="border: 1px solid #ccc;padding: 5px;margin-bottom:10px;">
         <a th:href="@{/}">Home</a>
         &nbsp;&nbsp; | &nbsp;&nbsp;
         <a th:href="@{/admin/oldLogin}">/admin/oldLogin (OLD URL)</a>  
      </div>
 
      <h3>Spring Boot Mvc Interceptor</h3>
       
      <span style="color:blue;">Testing LogInterceptor</span>
      <br/><br/>
 
      See Log in Console..
 
   </body>
</html>
```

`login.html`

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
   <head>
      <meta charset="UTF-8" />
      <title>Spring Boot Mvc Interceptor example</title>
   </head>
   <body>
    
      <div style="border: 1px solid #ccc;padding: 5px;margin-bottom:10px;">
         <a th:href="@{/}">Home</a>
         &nbsp;&nbsp; | &nbsp;&nbsp;
         <a th:href="@{/admin/oldLogin}">/admin/oldLogin (OLD URL)</a>  
      </div>
       
      <h3>This is Login Page</h3>
       
      <span style="color:blue">Testing OldLoginInterceptor &amp; AdminInterceptor</span>
      <br/><br/>
      See more info in the Console.
       
   </body>
    
</html>
```

### 4.运行程序并测试效果

测试用户访问 http://localhost:8080/ 的时候， **LogInterceptor**记录相关信息（页面地址，访问时间），并计算 **Web服务器**处理请求的时间。另外，页面会被渲染成 `test.html`。

当用户访问 http://localhost:8080/admin/oldLogin 也就是旧的登录页面（不再使用）时， **OldLoginInterceptor**将请求重定向 http://localhost:8080/admin/login 页面会被渲染成正常的登录页面 `login.html`。

**注意看控制台打印出的信息。**

### 5.总结

首先介绍了 Interceptor 的一些概念，然后通过一个简单的小案例走了一遍自定义实现 Interceptor 的过程。

代办：

1. Filter 和 Interceptor 执行顺序分析；
2. Spring Boot 实现监听器；
3. Filter、Interceptor、Listener对比分析；

代码地址：https://github.com/Snailclimb/springboot-guide/tree/master/source-code/basis/springboot-filter-interceptor