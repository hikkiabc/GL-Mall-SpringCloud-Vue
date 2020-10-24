<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item label="Ware">
        <el-select style="width:160px;" v-model="dataForm.wareId" placeholder="" clearable>
          <el-option :label="w.name" :value="w.id" v-for="w in wareList" :key="w.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="ProductComb ID">
        <el-input v-model="dataForm.skuId" placeholder="" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()">Search</el-button>
        <el-button v-if="isAuth('ware:waresku:save')" type="primary" @click="addOrUpdateHandle()">New</el-button>
        <el-button v-if="isAuth('ware:waresku:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">Delete</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="dataList" border v-loading="dataListLoading" @selection-change="selectionChangeHandle" style="width: 100%;">
      <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
      <el-table-column prop="id" header-align="center" align="center" label="id"></el-table-column>
      <el-table-column prop="productCombId" header-align="center" align="center" label="productCombId"></el-table-column>
      <el-table-column prop="wareId" header-align="center" align="center" label="WareId"></el-table-column>
      <el-table-column prop="stock" header-align="center" align="center" label="Stocks"></el-table-column>
      <el-table-column prop="productCombName" header-align="center" align="center" label="productCombName"></el-table-column>
      <el-table-column prop="stockLock" header-align="center" align="center" label="Lock"></el-table-column>
      <el-table-column fixed="right" header-align="center" align="center" width="150" label="Action">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">Edit</el-button>
          <el-button type="text" size="small" @click="deleteHandle(scope.row.id)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="sizeChangeHandle" @current-change="currentChangeHandle" :current-page="pageIndex" :page-sizes="[10, 20, 50, 100]" :page-size="pageSize" :total="totalPage" layout="total, sizes, prev, pager, next, jumper"></el-pagination>

    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>
  </div>
</template>

<script>
import AddOrUpdate from "./combination-add-or-update";
export default {
  data() {
    return {
      wareList: [],
      dataForm: {
        wareId: "",
        productCombId: ""
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false
    };
  },
  components: {
    AddOrUpdate
  },
  activated() {
    // console.log("接收到", this.$route.query.skuId);
    if (this.$route.query.productCombId) {
      this.dataForm.productCombId = this.$route.query.productCombId;
    }
    this.getWares();
    this.getDataList();
  },
  methods: {
    getWares() {
      this.$http({
        url: this.$http.adornUrl("/ware/0"),
        method: "get",
        params: this.$http.adornParams({
          pageNum: 1,
          pageSize: 500
        })
      }).then(({ data }) => {
        this.wareList = data.data.content;
      });
    },
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/ware/product_comb"),
        method: "get",
        params: this.$http.adornParams({
          pageNum: this.pageIndex,
          pageSize: this.pageSize,
          productCombId: this.dataForm.productCombId,
          wareId: this.dataForm.wareId
        })
      }).then(({ data }) => {

        if (data && data.code === 0) {
          this.dataList = data.data.content;
          this.totalPage = data.data.totalElements;
        } else {
          this.dataList = [];
          this.totalPage = 0;
        }
        this.dataListLoading = false;
      });
    },
    // 每页数
    sizeChangeHandle(val) {
      this.pageSize = val;
      this.pageIndex = 1;
      this.getDataList();
    },
    // 当前页
    currentChangeHandle(val) {
      this.pageIndex = val;
      this.getDataList();
    },
    // 多选
    selectionChangeHandle(val) {
      this.dataListSelections = val;
    },
    // 新增 / 修改
    addOrUpdateHandle(id) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id);
      });
    },
    // 删除
    deleteHandle(id) {
      var ids = id
        ? [id]
        : this.dataListSelections.map(item => {
          return item.id;
        });
      this.$confirm(
        `确定对[id=${ids.join(",")}]进行[${id ? "删除" : "批量删除"}]操作?`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
        this.$http({
          url: this.$http.adornUrl("/ware/waresku/delete"),
          method: "post",
          data: this.$http.adornData(ids, false)
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.getDataList();
              }
            });
          } else {
            this.$message.error(data.msg);
          }
        });
      });
    }
  }
};
</script>