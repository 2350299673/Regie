package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.entity.Category;
import com.reggie.entity.Dish;
import com.reggie.entity.DishFlavor;
import com.reggie.service.CategoryService;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;
    /**
     * 新增菜品及口味
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.error(dishDto.toString());

        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息的分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name!=null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");//第三个参数是忽略哪个属性不拷贝
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类ID
            //根据ID查询分类对象
            Category category = categoryService.getById(categoryId);
//            保险起见可以用if判断做处理看是否拿到数据 否则可能会报空指针
//            if (category!=null){
//                String categoryName = category.getName();
//                dishDto.setCategoryName(categoryName);
//            }
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据ID查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.error(dishDto.toString());

        dishService.updateWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }


    /**
     * 批量停售
     */
    @PostMapping("/status/0")
    public R<String> stopSelling(@RequestParam List<Long> ids){
        ids.stream().map((item)->{
            //查询出来id
            String id = item.toString();
            //根据ID查询数据库，查询该用户的所有信息
            Dish dish = dishService.getById(id);
            //更改该用户的状态
            dish.setStatus(0);
            //将更改的数据重新保存到数据库中，是更新数据（update）不是保存数据（save）
            dishService.updateById(dish);
            return item;
        }).collect(Collectors.toList());
        return R.success("停售成功");
    }

    /**
     * 批量起售
     */
    @PostMapping("/status/1")
    public R<String> startSelling(@RequestParam List<Long> ids){
        ids.stream().map((item)->{
            //获取用户Id
            String id = item.toString();
            //根据ID查询数据库，查询用户所有信息
            Dish dish = dishService.getById(id);
            //更改用户状态
            dish.setStatus(1);
            //将更新后的数据重新保存到数据库中，是更新数据（update）不是保存数据（save）
            dishService.updateById(dish);
            return item;
        }).collect(Collectors.toList());
        return R.success("起售成功");
    }


    /**
     * 删除菜品信息
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        dishService.removeByIds(ids);
        return R.success("删除成功");
    }
}
