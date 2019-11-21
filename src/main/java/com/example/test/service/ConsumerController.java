package com.example.test.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Jack
 *
 * @HystrixCommand 加到服务的接口方法上，可以对接口限流。
 * 下面的代码，给服务的 /hello接口 加了hystrix线程隔离，并且限制并发为5。
 * 当接口熔断或者降级时，会走降级方法，降级方法将异常信息返回，并且返回状态码 503
 *
 * 实例默认值：通过代码为实例定义的默认值。 通过代码的方式为实例设置属性值来覆盖默认的全局配置。
 */
@RestController
public class ConsumerController {

    @Autowired
    HelloService helloService;

    @RequestMapping(value = "/ribbon-consumer", method = RequestMethod.GET)
    public String helloConsumer() {
        return helloService.hello();
    }

    @HystrixCommand(
            commandKey = "helloCommand",//缺省为方法名
            threadPoolKey = "helloPool",//缺省为类名
            fallbackMethod = "fallbackMethod",//指定降级方法，在熔断和异常时会走降级方法
            commandProperties = {
                    //超时时间
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
            },
            threadPoolProperties = {
                    //并发，缺省为10
                    @HystrixProperty(name = "coreSize", value = "5")
            }
    )
    @RequestMapping(
            value = "/hello",
            method = RequestMethod.GET
    )
    public String sayHello(HttpServletResponse httpServletResponse) {
        return "Hello World!：00000";
    }


    /**
     * 例子2
     */
    //对controller层的接口做hystrix线程池隔离，可以起到限流的作用
    @HystrixCommand(
            commandKey = "helloCommand2",//缺省为方法名
            threadPoolKey = "helloPool2",//缺省为类名
            fallbackMethod = "fallbackMethod2",//指定降级方法，在熔断和异常时会走降级方法
            commandProperties = {
                    //超时时间
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
            },
            threadPoolProperties = {
                    //并发，缺省为10
                    @HystrixProperty(name = "coreSize", value = "5")
            }
    )
    @RequestMapping(
            value = "/hello2",
            method = RequestMethod.GET
    )
    public String sayHello2(HttpServletResponse httpServletResponse){
        return "Hello World!：00000";
    }

    /**
     *  降级方法，状态码返回503
     *  注意，降级方法的返回类型与形参要与原方法相同，可以多一个Throwable参数放到最后，用来获取异常信息
     */
    public String fallbackMethod2(HttpServletResponse httpServletResponse,Throwable e) {
        System.out.println("断路器已触发，并作相应的业务处理...");
        httpServletResponse.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        return e.getMessage();
    }

}
