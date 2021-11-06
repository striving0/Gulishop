package com.wzw.gulishop.product.web;

import com.wzw.gulishop.product.entity.CategoryEntity;
import com.wzw.gulishop.product.service.CategoryService;
import com.wzw.gulishop.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @auther Kevin
 * @ClassName IndexController
 * @Date 2021.06.30 0:35
 */

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;


    @Autowired
    RedissonClient redisson;

    @Autowired
    StringRedisTemplate redisTemplate;


    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {


        // 1、TODO 1、查出所有的一级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();

        //视图解析器进行拼接
        // classpath:/templates/ +返回+ .html
        model.addAttribute("categorys", categoryEntities);
        return "index";
    }


    //index/catalog.json
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        Map<String, List<Catelog2Vo>> catelogJson = categoryService.getCatelogJson();
        return catelogJson;
    }


    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        //1、获取一把锁，只要所的名字一样，就是同一把锁
        RLock lock = redisson.getLock("my-lock");
        //2、加锁
        lock.lock();//阻塞式等待默认加的锁都是30秒
        //1）锁的自动续期，如果业务超长，运行期间自动给锁续上新的30秒，不用担心业务超长锁自动过期
        //2）加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，所自动在30秒后自动删除


        //lock.lock(10, TimeUnit.SECONDS);//十秒自动解锁 自动解锁时间一定要大于业务执行时间
        //问题：lock.lock(10, TimeUnit.SECONDS),在锁时间到了以后不会自动续期
        //1、如果我们传递了锁的超时时间，就发送给redis执行脚本，进行占锁，默认超时间就是我们设置的时间
        //2、如果我们位指定时间，就使用30*1000【lockWatchdogTimeout看门狗时间】;
        //  只要占锁成功，就会启动一个定时任务【重新给锁设置时间，新的过期时间就是看门狗默认时间】
        //  internalLockLeaseTime【看门狗时间】 / 3，10秒后开时续期
        try {
            System.out.println("加锁成功，执行业务...." + Thread.currentThread().getId());

            Thread.sleep(30000);
        } catch (Exception e) {

        } finally {
            //3、解锁 当解锁代码没有执行的时候，redisson会不会出现死锁
            lock.unlock();
            System.out.println("释放锁...." + Thread.currentThread().getId());
        }

        return "hello";
    }


    //保证一定能读到最新数据，写锁是排他锁(互斥锁，独享)，读锁是共享锁
    //写锁没有释放读锁就必须等待
    //读+读相当于无锁
    //读+写有读书哦写锁也需要等待
    //只要有写的存在都必须等待
    @GetMapping("/write")
    @ResponseBody
    public String writeValue() {


        RReadWriteLock lock = redisson.getReadWriteLock("rw-lock");
        String s = "";
        RLock rLock = lock.writeLock();
        try {
            //1、该数据加写锁，读数据加读锁
            rLock.lock();
            System.out.println("写锁成功......."+Thread.currentThread().getId());
            s = UUID.randomUUID().toString();
            Thread.sleep(30000);
            redisTemplate.opsForValue().set("writeValue", s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
            System.out.println("写锁释放......."+Thread.currentThread().getId());
        }
        return s;
    }


    @GetMapping("/read")
    @ResponseBody
    public String readValue() {

        RReadWriteLock lock = redisson.getReadWriteLock("rw-lock");
        String ss = "";
        RLock rLock = lock.readLock();
        rLock.lock();
        try {
            System.out.println("读锁成功......."+Thread.currentThread().getId());
            ss = redisTemplate.opsForValue().get("writeValue");
            Thread.sleep(30000);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
            System.out.println("读锁释放......."+Thread.currentThread().getId());
        }
        return ss;
    }


    /**
     *车库停车
     * 3车位
     * 信号量可以做分布式限流
     */
    @GetMapping("/park")
    @ResponseBody
    public String park() throws InterruptedException {

        RSemaphore park = redisson.getSemaphore("park");
        //park.acquire();//获取一个信号、获取一个值、占一个车位
        final boolean b = park.tryAcquire();
        return "ok=>"+b;

    }


    @GetMapping("/go")
    @ResponseBody
    public String go() throws InterruptedException {

        RSemaphore park = redisson.getSemaphore("park");
        park.release();//释放一个车位

        return "ok";

    }


    /**
     *
     * 放假锁门
     * 1班没人了
     * 5个班全部走完了可以锁门
     */
    @GetMapping("/lockDoor")
    @ResponseBody
    public String lockDoor() throws InterruptedException {

        final RCountDownLatch door = redisson.getCountDownLatch("door");
        door.trySetCount(5);
        door.await();//等待闭锁完成
        return "放假了....";
    }

    @GetMapping("/gogo/{id}")
    @ResponseBody
    public String gogo(@PathVariable("id") Long id){

        final RCountDownLatch door = redisson.getCountDownLatch("door");
        door.countDown();;//技术减一
        return id+"班的人都走了";
    }

}
