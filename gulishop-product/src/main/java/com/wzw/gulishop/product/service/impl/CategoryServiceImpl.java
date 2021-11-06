package com.wzw.gulishop.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;
import com.wzw.gulishop.product.dao.CategoryDao;
import com.wzw.gulishop.product.entity.CategoryEntity;
import com.wzw.gulishop.product.service.CategoryBrandRelationService;
import com.wzw.gulishop.product.service.CategoryService;
import com.wzw.gulishop.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Transactional
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redisson;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {

        //1.查出所有分类
        //selectList(null);为null表示查询所有
        List<CategoryEntity> entities = baseMapper.selectList(null);
        //2.组装成父子的树形结构
        //2.1）、找到所有的一级分类
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map((menu) -> {
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        // TODO 1、检查当前删除的菜单是否被别的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();

        List<Long> parentPath = findParentPath(catelogId, paths);

        Collections.reverse(parentPath);

        return parentPath.toArray(new Long[parentPath.size()]);
    }


    /**
     * 级联更新所有关联的数据
     * @CacheEvict 缓存失效模式
     */
    @CacheEvict(value = "category",key = "'getLevel1Categorys'")
    @Transactional
    @Override
    public void updateCaseCade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());

    }

    //每一个缓存的数据都要指定放到哪个名字的缓存，【缓存的分区】
    @Cacheable(value = {"category"},key = "#root.method.name",sync = true) //代表当前方法的结果需要缓存，如果缓存中有方法不调用，如果缓存中没有就调用方法，最后将结果放回缓存中
    @Override
    public List<CategoryEntity> getLevel1Categorys() {

        System.out.println("getLevel1Categorys..........");
        Long l = System.currentTimeMillis();
        //1查出所有一级分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));

        return categoryEntities;
    }

    @Cacheable(value = "category" , key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        System.out.println("查询了数据库..........");
        /**
         *
         * 1.将数据库多次查询变为一次
         */
        /**
         * 1、空结果缓存，解决缓存击穿
         * 2、设置过期时间(添加随机值)，解决缓存雪崩
         * 3、加锁，解决缓存击穿
         */
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        //1查出所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        //2封装数据
        Map<String, List<Catelog2Vo>> parent_id = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1每一个一级分类，拿到这个一级的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            //2封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //3找当前二级分类的三级分类分装成VO
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            //2分装成指定格式
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        return parent_id;
    }

    //TODO 产生堆外内存溢出 OutOfDirectMemoryError
    //1)、springboot2.0以后默认使用lettuce作为操作redis的客户端。他使用netty进行网通信
    //2)、lettuce的bug导致netty的堆外内存溢出 -Xmx300m:netty如果没有指定堆外内存就默认-Xmx:300m
    //3)、可以通过-Dio.netty.moxDirectMemory进行设置
    //解决方案，不能使用-Dio.netty.moxDirectMemory只去调大堆内存
    //升级lettuce客户端。2)、切换使用jedis
//    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson2() {
        //给缓存中方的JSON字符串，拿出JSON字符串，还要逆转为可用的对象类型【序列化与反序列化】

        //1.加入缓存,缓存中存的数据是JSON字符串
        //JSON是跨语言跨平台的兼容
        String catalogJson = redisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)) {
            System.out.println("缓存不命中...将要查询数据库");
            //2.缓存中没有就查询数据库
            final Map<String, List<Catelog2Vo>> catelogJsonFromDb = getCatelogJsonFromDbWithRedisLock();
//            //3.查到的数据再放到缓存，将对象转为JSON放在缓存中
//            final String s = JSON.toJSONString(catelogJsonFromDb);
//            //redisTemplate.opsForValue().set("catalogJson", s);
//            redisTemplate.opsForValue().set("catalogJson", s, 1, TimeUnit.DAYS);
            return catelogJsonFromDb;
        }
        System.out.println("缓存命中...直接返回");
        //转为我们注指定的对象
        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });

        return result;
    }


    /**
     * 缓存里的数据如何和数据库里的保持一致
     * 缓存数据一致性
     * 1）、双写模式
     * 2）、失效模式
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithRedissonLock() {
        //占分布式锁，去Redis占坑
        //锁的粒度越细就越快
        RLock lock = redisson.getLock("catelogJson-lock");
        lock.lock();
        Map<String, List<Catelog2Vo>> dataFromDb;
        try {
            dataFromDb = getDataFromDb();
        } finally {
            lock.unlock();
        }

        return dataFromDb;

    }


    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithRedisLock() {
        //占分布式锁，去Redis占坑
        final String uuid = UUID.randomUUID().toString();
        final Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("获取分布式锁成功......");
            //加锁成功执行业务
            //2、设置过期时间必须和加锁是同步的和原子的
            Map<String, List<Catelog2Vo>> dataFromDb;
            try {
                dataFromDb = getDataFromDb();
            } finally {
                String script = "if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                //删除锁
                final Long lock1 = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                        Arrays.asList("lock"), uuid);
            }
            //redisTemplate.delete("lock");
            //删除锁之前获取值对比，对比成功功删锁也得是原子操作 lua脚本解锁
//            final String lockValue = redisTemplate.opsForValue().get("lock");
//            if (uuid.equals(lockValue)){
//                //删除我自己的锁
//                redisTemplate.expire("lock", 30,TimeUnit.SECONDS);
//            }

            return dataFromDb;
        } else {
            //加锁失败重试
            //休眠100ms重试
            System.out.println("获取分布式锁失败....等待重试....");
            try {
                Thread.sleep(200);
            } catch (Exception e) {

            }
            return getCatelogJsonFromDbWithRedisLock();//自旋的方式
        }
    }

    private Map<String, List<Catelog2Vo>> getDataFromDb() {
        String catalogJson = redisTemplate.opsForValue().get("catalogJson");
        if (!StringUtils.isEmpty(catalogJson)) {
            //如果缓存不为空直接返回
            //转为我们注指定的对象
            Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            return result;
        }
        System.out.println("查询了数据库..........");
        /**
         *
         * 1.将数据库多次查询变为一次
         */
        /**
         * 1、空结果缓存，解决缓存击穿
         * 2、设置过期时间(添加随机值)，解决缓存雪崩
         * 3、加锁，解决缓存击穿
         */
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        //1查出所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        //2封装数据
        Map<String, List<Catelog2Vo>> parent_id = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1每一个一级分类，拿到这个一级的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            //2封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //3找当前二级分类的三级分类分装成VO
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            //2分装成指定格式
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));

        //3.查到的数据再放到缓存，将对象转为JSON放在缓存中
        final String s = JSON.toJSONString(parent_id);
        //redisTemplate.opsForValue().set("catalogJson", s);
        redisTemplate.opsForValue().set("catalogJson", s, 1, TimeUnit.DAYS);
        return parent_id;
    }


    //从数据库查询并分装分类数据
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithLocalLock() {

        //1只要一把锁就能锁住需要这个锁的线程
        //1、synchronized(this):springboot所有的组件容器中都是单例的
        // TODO synchronized,JUC(lock)都是本地锁,在分布式情况下，想要锁住所有就必须使用分布式锁
        synchronized (this) {
            //得到锁以后，我们再去缓存中确定一次，如果没有才需要继续查询
            return getDataFromDb();
        }

    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
        return collect;

        //return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //1.收集当前节点ID
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }


    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid().equals(root.getCatId());
        }).map(categoryEntity -> {
            //1.找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            // 2. 菜单的排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());


        return children;
    }


}
