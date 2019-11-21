package com.example.test.hystrix;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 *  HystrixController3
 *
 * maxQueueSize：作业队列的最大值，默认值为 -1，设置为此值时，队列会使用 SynchronousQueue，此时其 size 为0。
 * Hystrix 不会向队列内存放作业。如果此值设置为一个正的 int 型，队列会使用一个固定 size 的 LinkedBlockingQueue。
 * 此时在核心线程池内的线程都在忙碌时，会将作业暂时存放在此队列内，但超出此队列的请求依然会被拒绝。
 *
 * queueSizeRejectionThreshold：由于 maxQueueSize 值在线程池被创建后就固定了大小，
 * 如果需要动态修改队列长度的话可以设置此值，
 * 即使队列未满，队列内作业达到此值时同样会拒绝请求。
 * 此值默认是 5，所以有时候只设置了 maxQueueSize 也不会起作用。
 */
@RestController
@RequestMapping("/hystrix3")
@DefaultProperties(defaultFallback = "defaultFail")
public class HystrixController3 {

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500")
    })
    @GetMapping("/test1")
    public String test1() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(1000);
        return "test1";
    }

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1500"),
            // 滑动统计的桶数量
            /**
             * 设置一个rolling window被划分的数量，若numBuckets＝10，rolling window＝10000，
             *那么一个bucket的时间即1秒。必须符合rolling window % numberBuckets == 0。默认1
             */
            @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "10"),
            // 设置滑动窗口的统计时间。熔断器使用这个时间
            /** 设置统计的时间窗口值的，毫秒值。
             circuit break 的打开会根据1个rolling window的统计来计算。
             若rolling window被设为10000毫秒，则rolling window会被分成n个buckets，
             每个bucket包含success，failure，timeout，rejection的次数的统计信息。默认10000
             **/
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000")},
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "15"),
                    /**
                     * BlockingQueue的最大队列数，当设为-1，会使用SynchronousQueue，值为正时使用LinkedBlcokingQueue。
                     */
                    @HystrixProperty(name = "maxQueueSize", value = "15"),
                    /**
                     * 设置存活时间，单位分钟。如果coreSize小于maximumSize，那么该属性控制一个线程从实用完成到被释放的时间.
                     */
                    @HystrixProperty(name = "keepAliveTimeMinutes", value = "2"),
                    /**
                     * 设置队列拒绝的阈值,即使maxQueueSize还没有达到
                     */
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "15"),
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "10"),
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000")
            })
    @GetMapping("/test2")
    public String test2() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(1000);
        return "test2";
    }

    private String defaultFail() {
        System.out.println("default fail");
        return "default fail";
    }
}
