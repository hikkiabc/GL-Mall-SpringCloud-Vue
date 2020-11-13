package com.glmall.glproduct.controller;

//import com.aliyun.oss.OSSClient;
import com.glmall.glproduct.beans.*;
import com.glmall.glproduct.beans.vo.ProductVO;
import com.glmall.glproduct.service.CategoryService;
import com.glmall.glproduct.service.ProductBrandService;
import com.glmall.glproduct.service.ProductService;
import com.glmall.utils.R;
import com.glmall.utils.SnowFlakeWorker;
import com.glmall.utils.validate.AddValidate;
import com.glmall.utils.validate.UpdateValidate;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
//@CrossOrigin
@RequestMapping("/product")
public class ProductController {
@Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductBrandService productBrandService;

    @GetMapping("/category")
    public R getCategoryWithChildren(){
        List<ProductCategory> categoryList=categoryService.getCategoryWithChildren();
        return R.ok().put("data",categoryList);
    }
    @DeleteMapping("/category")
    public R deleteCategory(@RequestBody Map map){
//        System.out.println(map);
        List<Map> categoryList= (List) map.get("categoryIds");
        List<String> ids = categoryList.stream().map(i->(String)i.get("id")).collect(Collectors.toList());
        System.out.println(ids);
        Long res=categoryService.deleteAll(ids);
        return R.ok().put("data",res);
    }
    @PostMapping("/category")
    public R saveNewCategory(@RequestBody List<ProductCategory> productCategoryList){
        List<ProductCategory> resList=new ArrayList<>();
        for (ProductCategory productCategory : productCategoryList) {
            ProductCategory productCategory1= categoryService.save(productCategory);
            resList.add(productCategory1);
        }

            return R.ok().put("data",resList);

    }
    @GetMapping("/category/{id}")
    public R findCategoryById(@PathVariable String id){
      ProductCategory productCategory=  categoryService.findCategoryById(id);
        return R.ok().put("data",productCategory);
    }
    @GetMapping("/brand")
    public R findAllBrand(@RequestParam Map map){
        String id = (String) map.get("id");
        if (!StringUtils.isBlank(id)){
          ProductBrand productBrand=  productBrandService.findById(id);
          return R.ok().put("data",productBrand);
        }else{
            Page<ProductBrand> productBrandList= productBrandService.findAllBrand(map);
            return R.ok().put("data",productBrandList);
        }
    }

    @GetMapping("/brand-by-brandIds")
    public R findBrandByBrandIdList(@RequestParam("brandIds") List<String> brandIds){
      List<ProductBrand> productBrandList=  productBrandService.findBrandByBrandIdList(brandIds);
      return R.ok().put("data",productBrandList);
    }
    @PostMapping("/brand")
    public R addOrUpdateBrand(@Validated({AddValidate.class, UpdateValidate.class}) @RequestBody ProductBrand productBrand/*, BindingResult result*/){
//        if (result.hasErrors()){
//            Map errMap=new HashMap();
//            result.getFieldErrors().forEach(e->{
//                errMap.put(e.getField(),e.getDefaultMessage());
//            });
//            return R.error().put("data",errMap);
//        }

       ProductBrand productBrand1= productBrandService.save(productBrand);
       return R.ok().put("data",productBrand1);
    }
    @GetMapping("/brand_category")
    public R getBrandCategoryRelations(@RequestParam(value = "brandId") String id){
//        System.out.println(id);
       List<BrandCategory> brandCategoryList= productBrandService.getBrandCategoryRelations(id);
       return R.ok().put("data",brandCategoryList);
    }
    @PostMapping("/brand_category")
    public R saveOrUpdateBrandCategoryRelations(@RequestBody BrandCategory brandCategory){
//        System.out.println(brandCategory);
      BrandCategory brandCategory1=  productBrandService.saveOrUpdateBrandCategoryRelations(brandCategory);
      if (null==brandCategory1) return R.ok().put("data","duplicated");
      return R.ok().put("data",brandCategory1);
    }
    @DeleteMapping("brand_category")
    public R deleteBrandCategory(@RequestBody BrandCategory brandCategory){

        try {
            productBrandService.deleteBrandCategory(brandCategory);
            return R.ok().put("date","ok");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("delete fails");
        }
    }
    @PostMapping("")
    public R saveProduct(@RequestBody ProductVO productVO){
        System.out.println(productVO);
      Map productVO1=  productService.saveProduct(productVO);
      return R.ok().put("data",productVO1);
    }
    @GetMapping("")
    public R getAllProduct(@RequestParam Map map){
      Page<ProductEntity> productEntityPage=  productService.getAllProduct(map);
      return R.ok().put("data",productEntityPage);
    }
    @PostMapping("{id}")
    public R updateProductById(@PathVariable String id){
       Integer productEntity= productService.updateProductById(id);
       return R.ok().put("data",productEntity);
    }
    @GetMapping("/combination")
    public R getAllCombination(@RequestParam Map map){
      Page<ProductCombination > productCombinationPage=  productService.getAllCombination(map);
      return R.ok().put("data",productCombinationPage);
    }

}
