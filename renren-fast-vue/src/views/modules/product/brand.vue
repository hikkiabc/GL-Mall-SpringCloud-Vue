<template>
  <div class="mod-user">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.name" placeholder="Brand Name" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="getAllBrand()">Search</el-button>
        <el-button v-if="isAuth('sys:user:save')" type="primary" @click="addOrUpdateHandle()">New</el-button>
        <el-button v-if="isAuth('sys:user:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">Batch Delete</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="dataList" border v-loading="dataListLoading" @selection-change="selectionChangeHandle" style="width: 100%;">
      <el-table-column type="selection" header-align="center" align="center" width="50">
      </el-table-column>
      <el-table-column prop="id" header-align="center" align="center" width="180" label="ID">
      </el-table-column>
      <el-table-column prop="name" header-align="center" align="center" width="140" label="Brand Name">
      </el-table-column>
      <el-table-column prop="logo" header-align="center" align="center" width="100" label="Logo">
        <template slot-scope="scope">
          <img :src="scope.row.logo" width="50px" alt="">
        </template>
      </el-table-column>
      <el-table-column prop="showStatus" header-align="center" align="center" label="showStatus">
        <template slot-scope="scope">
          <el-switch @change='showStatusChange(scope.row)' active-value=1 inactive-value=0 v-model="scope.row.showStatus" active-color="#13ce66" inactive-color="#ff4949">
          </el-switch>
        </template>
      </el-table-column>
      <el-table-column fixed="right" header-align="center" align="center" width="250" label="操作">
        <template slot-scope="scope">
          <el-button v-if="isAuth('sys:user:update')" type="text" size="small" @click="showCategoryBrandRelation(scope.row.id)">Category</el-button>
          <el-button v-if="isAuth('sys:user:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">Edit</el-button>
          <el-button v-if="isAuth('sys:user:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="sizeChangeHandle" @current-change="currentChangeHandle" :current-page="pageNum" :page-sizes="[1, 2, 50, 100]" :page-size="pageSize" :total="totalPage" layout="total, sizes, prev, pager, next, jumper">
    </el-pagination>
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getAllBrand"></add-or-update>
    <BrandCategory ref='brandCategory' v-if="showCategoryBrandRelationView"></BrandCategory>
  </div>
</template>

<script>
import AddOrUpdate from './user-add-or-update'
import BrandCategory from './brandCategory'
export default {
  created() {

  },
  data() {
    return {
      dataForm: {
        name: ''
      },
      dataList: [],
      pageIndex: 1,
      pageNum: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      showCategoryBrandRelationView: false
    }
  },
  components: {
    AddOrUpdate, BrandCategory
  },
  activated() {
    // this.getDataList()
    this.getAllBrand()
  },
  methods: {
    showCategoryBrandRelation(id) {
      this.showCategoryBrandRelationView = true
      // this.$refs.brandCategory.init(id)
      this.$nextTick(() => {
        this.$refs.brandCategory.init(id)
      })


    },
    async showStatusChange(e) {
      const data = await this.$http({
        url: this.$http.adornUrl('/product/brand'),
        method: 'post',
        data: this.$http.adornData(e, false)
      })
      console.log(data);
    },
    async getAllBrand() {
      this.dataListLoading = true
      const data = await this.$http({
        url: this.$http.adornUrl("/product/brand"),
        method: "get",
        params: this.$http.adornParams({
          'pageNum': this.pageNum,
          'pageSize': this.pageSize,
          'key': this.dataForm.name,

        })
      });
      this.dataList = data.data.data.content
      // console.log(data);
      this.totalPage = data.data.data.totalPages
      this.dataListLoading = false
    },
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/sys/user/list'),
        method: 'get',
        params: this.$http.adornParams({
          'page': this.pageIndex,
          'limit': this.pageSize,
          'username': this.dataForm.userName
        })
      }).then(({ data }) => {
        // console.log(data);
        if (data && data.code === 0) {
          this.dataList = data.page.list
          this.totalPage = data.page.totalCount
        } else {
          this.dataList = []
          this.totalPage = 0
        }
        this.dataListLoading = false
      })
    },
    // 每页数
    sizeChangeHandle(val) {
      this.pageSize = val
      this.pageNum = 1
      this.getAllBrand()
    },
    // 当前页
    currentChangeHandle(val) {
      this.pageNum = val
      this.getAllBrand()
    },
    // 多选
    selectionChangeHandle(val) {
      this.dataListSelections = val
    },
    // 新增 / 修改
    addOrUpdateHandle(id) {
      this.addOrUpdateVisible = true
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id)
      })
    },
    // 删除
    deleteHandle(id) {
      var userIds = id ? [id] : this.dataListSelections.map(item => {
        return item.userId
      })
      this.$confirm(`确定对[id=${userIds.join(',')}]进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/sys/user/delete'),
          method: 'post',
          data: this.$http.adornData(userIds, false)
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({
              message: '操作成功',
              type: 'success',
              duration: 1500,
              onClose: () => {
                this.getDataList()
              }
            })
          } else {
            this.$message.error(data.msg)
          }
        })
      }).catch(() => { })
    }
  }
}
</script>
