<template>
  <el-cascader placeholder='select' v-model="newCateIds" :options="categorys" :props="{ 
    expandTrigger: 'hover',value:'id',label:'name',children:'children'}"></el-cascader>
</template>
<script>
export default {
  name: "",
  props: {
    categoryIds: {
      type: Array,
      default() {
        return []
      }
    }
  },
  activated() {
    // this.newCateIds = []
  },
  created() {
    this.getCategorys()
  },
  data() {
    return {
      categorys: [],
      newCateIds: []
    };
  },
  methods: {
    init() {
      this.newCateIds = []
    },
    async getCategorys() {
      const data = await this.$http({
        url: this.$http.adornUrl("/product/category"),
        method: "get",
      });
      this.categorys = data.data.data;
      // console.log(this.categorys);
    },
  },
  watch: {
    newCateIds(v) {
      this.$emit('update:categoryIds', v)
      PubSub.publish('cateChange', v)
    },
    categoryIds(v) {
      this.newCateIds = v
    }
  },
}
</script>
<style lang="scss" scoped>
</style>