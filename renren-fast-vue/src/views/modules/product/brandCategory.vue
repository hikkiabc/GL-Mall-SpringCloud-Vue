<template>
  <el-dialog title="收货地址" :visible.sync="dialogTableVisible">
    <el-popover placement="right" width="400" trigger="click" v-model="showCategoryCasecade">
      <CategoryCascade ref='brandCategoryDialog' :categoryIds.sync='categoryIds'></CategoryCascade>
      <div style="text-align: right; margin: 0">
        <el-button size="mini" type="text" @click="showCategoryCasecade=false">Cancel</el-button>
        <el-button type="primary" size="mini" @click="addCategoryRelation">Submit</el-button>
      </div>
      <el-button @click="resetAddBrandCategoryDialog" slot="reference">Add new category</el-button>
    </el-popover>
    <div class="brand_category_table">
      <div v-if="brand_category_list.length==0">No relations</div>
      <el-table border v-else :data="brand_category_list">
        <el-table-column type='index' label="#"></el-table-column>
        <el-table-column property="brandId" label="Brand ID"></el-table-column>
        <el-table-column property="brandName" label="Brand Name"></el-table-column>
        <el-table-column property="categoryId" label="Category ID"></el-table-column>
        <el-table-column property="categoryName" label="Category Name" width='150px'></el-table-column>
        <el-table-column label="Action">
          <template slot-scope="scope">
            <el-button size="mini" type="danger" @click="deleteCategoryBrandRelation(scope.row.id)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </el-dialog>
</template>
<script>
import CategoryCascade from '../../common/category-cascade'
export default {
  name: "",
  components: { CategoryCascade },
  data() {
    return {
      dialogTableVisible: false,
      brandId: null,
      brand_category_list: [],
      categoryIds: [],
      showCategoryCasecade: false
    };
  },
  methods: {
    async deleteCategoryBrandRelation(id) {
      const data = await this.$http({
        url: this.$http.adornUrl('/product/brand_category'),
        method: 'delete',
        data: this.$http.adornData({
          id
        }, false)
      })
      console.log(data);
      this.getBrandCategory()
    },
    resetAddBrandCategoryDialog() {
      this.$refs.brandCategoryDialog.init()
    },
    async addCategoryRelation() {
      // console.log(this.categoryIds);
      const data = await this.$http({
        url: this.$http.adornUrl('/product/brand_category'),
        method: 'post',
        data: this.$http.adornData({
          brandId: this.brandId, categoryId: this.categoryIds[this.categoryIds.length - 1]
        })
      })
      this.getBrandCategory()
    },
    async init(brandId) {
      this.dialogTableVisible = true
      this.brandId = brandId
      this.getBrandCategory()
    },
    async getBrandCategory() {
      const data = await this.$http({
        url: this.$http.adornUrl(
          `/product/brand_category`
        ),
        method: "get",
        params: this.$http.adornParams({
          brandId: this.brandId
        })
      })
      // console.log(data);
      this.brand_category_list = data.data.data
    }
  },
}
</script>
<style lang="scss" scoped>
.brand_category_table {
  margin-top: 50px;
  padding-left: 30px;
}
</style>