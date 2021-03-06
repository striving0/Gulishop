package com.wzw.gulishop.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.constant.ProductConstant;
import com.wzw.common.to.SkuHasStockVo;
import com.wzw.common.to.SkuReductionTo;
import com.wzw.common.to.es.SkuEsModel;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;
import com.wzw.common.utils.R;
import com.wzw.gulishop.coupon.to.SpuBoundTo;
import com.wzw.gulishop.product.dao.SpuInfoDao;
import com.wzw.gulishop.product.entity.*;
import com.wzw.gulishop.product.feign.CouponFeignService;
import com.wzw.gulishop.product.feign.SearchFeignService;
import com.wzw.gulishop.product.feign.WareFeignService;
import com.wzw.gulishop.product.service.*;
import com.wzw.gulishop.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {


    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService imagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService attrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }


    /**
     * TODO?????????????????????
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        //1.??????spu???????????????pms_spu_info
        final SpuInfoEntity infoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(infoEntity);

        //2.??????psu???????????????;pms_spu_info_desc
        final List<String> decript = vo.getDecript();
        final SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(infoEntity.getId());
        descEntity.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDesc(descEntity);

        //3.??????spu????????????;pms_spu_images
        List<String> images = vo.getImages();
        imagesService.saveImages(infoEntity.getId(), images);
        //4.??????spu???????????????pms_product_attr_vatue
        final List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        final List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            final ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());
            final AttrEntity id = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(id.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(infoEntity.getId());
            return valueEntity;
        }).collect(Collectors.toList());
        attrValueService.saveProductAttr(collect);
        //5.??????spu??????????????????gulimall_sms->sms_spu_bounds
        final Bounds bounds = vo.getBounds();
        final SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(infoEntity.getId());
        final R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if (r.getCode() != 0) {
            log.error("????????????spu??????????????????");
        }
        //6.????????????spu???????????????sku??????;
        final List<Skus> skus = vo.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach(item -> {
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                final SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(infoEntity.getBrandId());
                skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(infoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                //6.1sku??????????????????pms_spu_images

                skuInfoService.saveSkuInfo(skuInfoEntity);

                final Long skuId = skuInfoEntity.getSkuId();

                final List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                    final SkuImagesEntity skuImagesEntity = new SkuImagesEntity();

                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity -> {
                    //??????true?????????????????????false????????????
                    return StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                //6.2sku??????????????????pms_sku_images
                skuImagesService.saveBatch(imagesEntities);
                //TODO ??????????????????????????????
                final List<Attr> attr = item.getAttr();
                final List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    final SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);
                    return attrValueEntity;
                }).collect(Collectors.toList());

                //6.3sku????????????????????????pms_sku_sale_attr_value

                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                //6.4sku?????????????????????;gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price

                final SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal(0)) == 1) {
                    final R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r1.getCode() != 0) {
                        log.error("????????????sku??????????????????");
                    }
                }
            });
        }
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity infoEntity) {

        this.baseMapper.insert(infoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("id",key).or().like("spu_name",key);
            });
        }


        // status=1 and (id=1 or spu_name like xxx)
        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status",status);
        }

        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }

        /**
         * status: 2
         * key:
         * brandId: 9
         * catelogId: 225
         */

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {

        //1????????????spuID???????????????sku?????????????????????
        List<SkuInfoEntity> skus = skuInfoService.getSkuBySpuId(spuId);
        System.out.println("!@#$%^&*()="+skus);
        List<Long> skuIdList = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());


        //TODO 4.????????????sku????????????????????????????????????
        List<ProductAttrValueEntity> baseAttrs = attrValueService.baseAttrlistforspu(spuId);
        List<Long> attrIds = baseAttrs.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrIds);
        System.out.println("qwertyuiop="+searchAttrIds);

        Set<Long> idSet = new HashSet<>(searchAttrIds);
        List<SkuEsModel.Attrs> attrsList = baseAttrs.stream().filter(item -> {
            return idSet.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, attrs1);
            return attrs1;
        }).collect(Collectors.toList());

        Map<Long, Boolean> stockMap=null;
        //TODO 1.???????????????????????????????????????????????????
        try {
            R r = wareFeignService.getSkuHasStock(skuIdList);
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {
            };
            stockMap= r.getData(typeReference).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));
            System.out.println("qwweeeu456462==="+stockMap);
        }catch (Exception e){
            log.error("????????????????????????????????????{}",e);
        }

        //2.????????????sku??????
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> upProducts = skus.stream().map(sku -> {
            //?????????????????????
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, esModel);
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());
            //hasStock,hostScore
            //??????????????????
            if (finalStockMap ==null){
                esModel.setHasStock(true);
            }else {
                esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }
            //TODO 2.???????????????0
            esModel.setHotScore(0L);
            //TODO 3.????????????????????????????????????
            BrandEntity brand = brandService.getById(esModel.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setBrandImg(brand.getLogo());
            CategoryEntity category = categoryService.getById(esModel.getCatalogId());
            esModel.setCatalogName(category.getName());
            //??????????????????
            esModel.setAttrs(attrsList);
            return esModel;
        }).collect(Collectors.toList());
        // TODO 5.??????????????????es????????????
        R r = searchFeignService.productStatusUp(upProducts);
        if(r.getCode()==0){
            //??????????????????
            // TODO 6???????????????spu?????????
            baseMapper.updateSpuStatus("spuId", ProductConstant.StatusEnum.SPU_UP.getCode());
        }else {
            //??????????????????
            //TODO 7?????????????????????????????????;????????????
        }

    }
}
