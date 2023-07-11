package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.entity.Category;
import com.reggie.entity.Dish;
import com.reggie.entity.Setmeal;
import com.reggie.mapper.CategoryMapper;
import com.reggie.service.CategoryService;
import com.reggie.service.DishService;
import com.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryMapper categoryMapper;


    /**
     * 根据Id之前进行分类，删除之前要进行判断
     *
     * @param id
     */
    @Override
    public void remove(Long id) {

        //查询当前套餐是否存在菜品，如果已经关联，则抛出一个异常(意思就是不让删除，给客户反馈一下)
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据ID查询Dish表，查看有没有ID符合
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);

        int dishCount = dishService.count(dishLambdaQueryWrapper);//select count(*) from dish;
        if (dishCount > 0) {
            //已经关联菜品，抛出一个异常
            throw new CustomException("当前分类已经关联菜品，不能删除");
        }

        //查询当前分类是否存在套餐，如果已经关联，则抛出一个异常(意思就是不让删除，给客户反馈一下)
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);
        if(setmealCount > 0){
            //已经关联菜品，抛出一个异常
            throw new CustomException("当前分类已经关联套餐，不能删除");
        }


        //没有分类和套餐,可以直接删除
//        categoryMapper.deleteById(id);

        //super():调用父类中的无参构造方法
        //super(参数)：调用父类中的有参构造方法
        //super.XXX():调用父类中的方法
        super.removeById(id);
    }
}
