package com.wzw.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @auther Kevin
 * @ClassName ListValueConstraintValidator
 * @Date 2021.04.22 21:01
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {


    private Set<Integer> set =new HashSet<>();

    //初始化方法
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] vals = constraintAnnotation.vals();
        for (int val:vals){
            set.add(val);
        }

    }


    //判断是校验成功
    /**
     * value 需要校验的值
     *
     *
     * */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return set.contains(value);
    }
}
