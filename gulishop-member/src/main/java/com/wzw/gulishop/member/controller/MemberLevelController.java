package com.wzw.gulishop.member.controller;

import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.R;
import com.wzw.gulishop.member.entity.MemberLevelEntity;
import com.wzw.gulishop.member.service.MemberLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;


/**
 * 会员等级
 *
 * @author Kevin
 * @email 2642779862@qq.com
 * @date 2021-01-18 11:57:33
 */
@RestController
@RequestMapping("member/memberlevel" )
public class MemberLevelController {
    @Autowired
    private MemberLevelService memberLevelService;

    /**
     * 列表
     */
    @RequestMapping("/list" )
//@RequiresPermissions("member:memberlevel:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberLevelService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}" )
    //@RequiresPermissions("member:memberlevel:info" )
    public R info(@PathVariable("id" ) Long id) {
            MemberLevelEntity memberLevel = memberLevelService.getById(id);

        return R.ok().put("memberLevel", memberLevel);
    }

    /**
     * 保存
     */
    @RequestMapping("/save" )
    //@RequiresPermissions("member:memberlevel:save")
    public R save(@RequestBody MemberLevelEntity memberLevel) {
            memberLevelService.save(memberLevel);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update" )
    //@RequiresPermissions("member:memberlevel:update")
    public R update(@RequestBody MemberLevelEntity memberLevel) {
            memberLevelService.updateById(memberLevel);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete" )
    //@RequiresPermissions("member:memberlevel:delete")
    public R delete(@RequestBody Long[] ids) {
            memberLevelService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
