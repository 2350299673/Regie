package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.DishDto;
import com.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    /**
     * 新增菜品，同时插入菜品对应得口味数据 ，需要操作两张表 dish,dish_flavor
     */
    public void saveWithFlavor(DishDto dishDto);

    /**
     * 根据Id查询菜品信息和对应的口味信息
     */
    public DishDto getByIdWithFlavor(Long id);

    /**
     * 更新菜品信息和对应的口味信息
     * @param dishDto
     */
    public void updateWithFlavor(DishDto dishDto);
}
