package com.wzw.gulishop.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;




//2级分类VO
/**
 * @auther Kevin
 * @ClassName Catelog2Vo
 * @Date 2021.07.03 20:57
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catelog2Vo {
    private String catelog1Id;//1级分类id
    private List<Catelog3Vo> catalog3List;
    private String id;
    private String name;


    //三级分类VO
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Catelog3Vo{
        private String catelog2Id;
        private String id;
        private String name;
    }


}
