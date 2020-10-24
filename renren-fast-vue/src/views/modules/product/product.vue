<template>
  <div>
    <el-row>
      <el-col :span="24">
        <el-form :inline="true" :model="dataForm">
          <el-form-item label="Category">
            <category-cascader :categoryIds.sync="categoryIds"></category-cascader>
          </el-form-item>
          <el-form-item label="Brand">
            <brand-select style="width:160px"></brand-select>
          </el-form-item>
          <el-form-item label="Status">
            <el-select style="width:160px" placeholder="select" v-model="dataForm.status" clearable>
              <el-option label="New" :value="0"></el-option>
              <el-option label="List" :value="1"></el-option>
              <el-option label="UnList" :value="2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="Key word">
            <el-input style="width:160px" v-model="dataForm.key" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchSpuInfo">Search</el-button>
          </el-form-item>
        </el-form>
      </el-col>
      <el-col :span="24">
        <spuinfo :catId="catId"></spuinfo>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import CategoryCascader from "../../common/category-cascade";
import BrandSelect from "../../common/brand-select";
import Spuinfo from "./product-info";
export default {

  components: { CategoryCascader, Spuinfo, BrandSelect },
  props: {},
  data() {

    return {
      catId: 0,
      categoryIds: [],
      dataForm: {
        status: "",
        key: "",
        brandId: null,
        categoryId: null
      },
      catPathSub: null,
      brandIdSub: null
    };
  },
  //计算属性 类似于data概念
  computed: {},
  //监控data中的数据变化
  watch: {},
  //方法集合
  methods: {
    searchSpuInfo() {
      this.PubSub.publish("dataForm", this.dataForm);
    }
  },
  //生命周期 - 创建完成（可以访问当前this实例）
  created() { },
  //生命周期 - 挂载完成（可以访问DOM元素）
  mounted() {
    this.catPathSub = PubSub.subscribe("cateChange", (msg, val) => {
      this.dataForm.categoryId = val[val.length - 1];
    });
    this.brandIdSub = PubSub.subscribe("brandIdChange", (msg, val) => {
      this.dataForm.brandId = val;
    });
  },
  beforeCreate() { }, //生命周期 - 创建之前
  beforeMount() { }, //生命周期 - 挂载之前
  beforeUpdate() { }, //生命周期 - 更新之前
  updated() { }, //生命周期 - 更新之后
  beforeDestroy() {
    PubSub.unsubscribe(this.catPathSub);
    PubSub.unsubscribe(this.brandIdSub);
  }, //生命周期 - 销毁之前
  destroyed() { }, //生命周期 - 销毁完成
  activated() { } //如果页面有keep-alive缓存功能，这个函数会触发
};
</script>
<style scoped>
</style>
