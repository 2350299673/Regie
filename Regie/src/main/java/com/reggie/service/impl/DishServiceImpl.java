package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.dto.DishDto;
import com.reggie.entity.Dish;
import com.reggie.entity.DishFlavor;
import com.reggie.mapper.DishMapper;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {


    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * 新增菜品，同时保存对应的口味信息，Dish信息保存到Dish表,DishFlavor信息保存到DishFlavor表
     *
     * @param dishDto
     */
    @Override
    //多表联查，需要加入事务
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品信息到菜品表dish
        //继承了Dish，所以这里的this是调用父类dish里面的方法(MP包装的)
        this.save(dishDto);//方法调用者的地址值

        //DishDto表中口味信息封装的只有口味信息，没有菜品ID，所以要先获取菜品ID,并将ID注入到每一个菜品口味中
        Long dishId = dishDto.getId();//这里调用的是父类的属性

        List<DishFlavor> flavors = dishDto.getFlavors();
        //将ID注入到每一个菜品口味中
//        flavors.stream().map((item)->{
//            item.setDishId(dishId);
//            return item;
//        }).collect(Collectors.toList());

        //   变量类型    变量
        for (DishFlavor item :
            //  被遍历的集合或数组
                flavors) {
            item.setDishId(dishId);
        }

        //将菜品口味信息保存到菜品口味表
        dishFlavorService.saveBatch(flavors);
    }
}
