<template>
  <div class="mod-config">
    <el-table :data="dataList" border v-loading="dataListLoading" @selection-change="selectionChangeHandle" style="width: 100%;">
      <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
      <el-table-column prop="id" header-align="center" align="center" label="id"></el-table-column>
      <el-table-column prop="name" header-align="center" align="center" label="Name"></el-table-column>
      <el-table-column prop="description" header-align="center" align="center" label="Description"></el-table-column>
      <el-table-column prop="categoryId" header-align="center" align="center" label="Category"></el-table-column>
      <el-table-column prop="brandId" header-align="center" align="center" label="Brand"></el-table-column>
      <el-table-column prop="weight" header-align="center" align="center" label="Weight(kg)"></el-table-column>
      <el-table-column prop="publishStatus" header-align="center" align="center" label="Status">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.publishStatus == 0">New</el-tag>
          <el-tag v-if="scope.row.publishStatus == 1">List</el-tag>
          <el-tag v-if="scope.row.publishStatus == 2">UnList</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" header-align="center" align="center" label="Created"></el-table-column>
      <el-table-column prop="updateTime" header-align="center" align="center" label="Updated"></el-table-column>
      <el-table-column fixed="right" header-align="center" align="center" width="150" label="Action">
        <template slot-scope="scope">
          <el-button v-if="scope.row.publishStatus == 0" type="text" size="small" @click="productUp(scope.row.id)">List</el-button>
          <el-button type="text" size="small" @click="attrUpdateShow(scope.row)">Attribute</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination @size-change="sizeChangeHandle" @current-change="currentChangeHandle" :current-page="pageIndex" :page-sizes="[10, 20, 50, 100]" :page-size="pageSize" :total="totalPage" layout="total, sizes, prev, pager, next, jumper"></el-pagination>
  </div>
</template>

<script>
export default {
  data() {
    return {
      dataSub: null,
      dataForm: {},
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false
    };
  },
  props: {
    catId: {
      type: Number,
      default: 0
    }
  },
  components: {},
  activated() {
    this.getDataList();
  },
  methods: {
    productUp(id) {
      this.$http({
        url: this.$http.adornUrl("/product/" + id),
        method: "post"
      }).then(({ data }) => {

        if (data && data.code === 0) {
          this.$message({
            message: "Success",
            type: "success",
            duration: 1500,
            onClose: () => {
              this.getDataList();
            }
          });
        } else {
          this.$message.error(data.msg);
        }
      }).catch(e => {
        console.log(e);
      });
    },
    // 跳转到规格
    attrUpdateShow(row) {
      // console.log(row);
      this.$router.push({
        path: "/product-attrupdate",
        query: { productId: row.id, categoryId: row.categoryId }
      });
    },
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true;
      let param = {};
      Object.assign(param, this.dataForm, {
        pageNum: this.pageIndex,
        pageSize: this.pageSize
      });
      this.$http({
        url: this.$http.adornUrl("/product"),
        method: "get",
        params: this.$http.adornParams(param)
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.dataList = data.data.content;
          this.totalPage = data.data.numberOfElements;
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
    addOrUpdateHandle(id) { }
  },
  mounted() {
    this.dataSub = PubSub.subscribe("dataForm", (msg, val) => {
      // console.log("查询条件", val);
      this.dataForm = val;
      this.getDataList();
    });
  },
  beforeDestroy() {
    PubSub.unsubscribe(this.dataSub);
  }
};
</script>
