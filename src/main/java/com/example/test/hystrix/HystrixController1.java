package com.example.test.hystrix;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hystrix1")
@DefaultProperties(defaultFallback = "defaultFail")
public class HystrixController1 {

    /**
     * 例子1
     * http://localhost:9001/hystrix1/test1
     * @return
     */
    @HystrixCommand(fallbackMethod = "failback1")
    @GetMapping("/test1")
    public String test1() {
        System.out.println("调用test1接口...发生异常");
        throw new RuntimeException();
    }

    private String failback1() {
        System.out.println("--->调用是failback1方法");
        return "failback1";
    }

    /**
     * 例子2
     * @return
     */
    @HystrixCommand(fallbackMethod = "failback2")
    @GetMapping("/test2")
    public String test2() {
        System.out.println("调用test2接口...发生异常");
        throw new RuntimeException();
    }

    @HystrixCommand(fallbackMethod = "failback3")
    private String failback2() {
        System.out.println("--->调用failback2接口...发生异常");
        throw new RuntimeException();
    }

    @HystrixCommand
    private String failback3() {
        System.out.println("--->调用failback3接口...发生异常");
        throw new RuntimeException();
    }

    private String defaultFail() {
        System.out.println("--->调用default fail方法");
        return "default fail";
    }

    /**
     * 例子3
     */
    @HystrixCommand(fallbackMethod = "getMsgFallback")
    @GetMapping("/test3")
    public Object getMsg() {
        System.out.println("--->调用getMsg方法");
        throw new RuntimeException();
    }

    public Object getMsgFallback() {
        return "getMsg 的 Fallback 内容！";
    }

}
