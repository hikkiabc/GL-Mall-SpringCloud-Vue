<template>
  <el-row :gutter="20">
    <el-col :span="3">
      <category @tree-node-click="treenodeclick"></category>
    </el-col>
    <el-col :span="20">
      <div class="mod-config">
        <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
          <el-form-item>
            <el-input v-model="dataForm.key" placeholder="Attribute Name" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button @click="getDataList()">Search</el-button>
            <el-button type="success" @click="getAllDataList()">Search All</el-button>
            <el-button v-if="isAuth('product:attr:save')" type="primary" @click="addOrUpdateHandle()">New</el-button>
            <el-button v-if="isAuth('product:attr:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">Delete Multiple</el-button>
          </el-form-item>
        </el-form>
        <el-table :data="dataList" border v-loading="dataListLoading" @selection-change="selectionChangeHandle" style="width: 100%;">
          <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
          <el-table-column prop="id" header-align="center" align="center" label="id"></el-table-column>
          <el-table-column prop="name" header-align="center" align="center" label="Attri Name"></el-table-column>
          <el-table-column v-if="type == 1" prop="searchType" header-align="center" align="center" label="SearchType">
            <template slot-scope="scope">
              <i class="el-icon-success" v-if="scope.row.searchType==1"></i>
              <i class="el-icon-error" v-else></i>
            </template>
          </el-table-column>
          <el-table-column prop="valueType" header-align="center" align="center" label="Value Type">
            <template slot-scope="scope">
              <el-tag type="success" v-if="scope.row.valueType==0">Single</el-tag>
              <el-tag v-else>Multiple</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="icon" header-align="center" align="center" label="Icon">
            <template slot-scope="scope">
              <!-- 自定义表格+自定义图片 -->
              <img :src="scope.row.logo" style="width: 60px; height: 60px" />
            </template>
          </el-table-column>
          <el-table-column prop="" header-align="center" align="center" label="Availible Values">
            <template slot-scope="scope">
              <el-tooltip placement="top">
                <div slot="content">
                  <span v-for="(i,index) in scope.row.value.split(',')" :key="index">
                    {{i}}
                    <br />
                  </span>
                </div>
                <el-tag>{{scope.row.value.split(",")[0]+" ..."}}</el-tag>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="enable" header-align="center" align="center" label="Status">
            <template slot-scope="scope">
              <i class="el-icon-success" v-if="scope.row.status==1"></i>
              <i class="el-icon-error" v-else></i>
            </template>
          </el-table-column>
          <el-table-column prop="categoryName" header-align="center" align="center" label="Category"></el-table-column>
          <el-table-column v-if="type == 1" prop="groupName" header-align="center" align="center" label="Group"></el-table-column>
          <el-table-column v-if="type == 1" prop="" header-align="center" align="center" label="Fast Display">
            <template slot-scope="scope">
              <i class="el-icon-success" v-if="scope.row.fastDisplay==1"></i>
              <i class="el-icon-error" v-else></i>
            </template>
          </el-table-column>
          <el-table-column fixed="right" header-align="center" align="center" width="100" label="Action">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">Edit</el-button>
              <el-button type="text" size="small" @click="deleteHandle(scope.row.id)">Delete</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination @size-change="sizeChangeHandle" @current-change="currentChangeHandle" :current-page="pageIndex" :page-sizes="[10, 20, 50, 100]" :page-size="pageSize" :total="totalPage" layout="total, sizes, prev, pager, next, jumper"></el-pagination>
        <!-- 弹窗, 新增 / 修改 -->
        <add-or-update :type.sync="type" v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>
      </div>
    </el-col>
  </el-row>
</template>

<script>
import Category from "../../common/category";
import AddOrUpdate from "./attr-add-or-update";
export default {

  components: { Category, AddOrUpdate },
  props: {
    type: {
      type: Number,
      default: 1
    }
  },
  data() {
    return {
      cateId: 0,
      // type: 1,
      dataForm: {
        key: ""
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
  activated() {
    this.getDataList();
  },
  watch: {
    type: {
      handler(v) {
        console.log(v);
      }
    }
  },
  methods: {
    treenodeclick(data, node, component) {
      if (node.level == 3) {
        this.cateId = data.id;
        this.getDataList();
      }
    },
    getAllDataList() {
      this.cateId = 0;
      this.getDataList();
    },
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true;
      let type = this.attrtype == 0 ? "sale" : "base";
      this.$http({
        url: this.$http.adornUrl(`/product/attr/attribute/${this.cateId}`),
        method: "get",
        params: this.$http.adornParams({
          pageNum: this.pageIndex,
          pageSize: this.pageSize,
          key: this.dataForm.key,
          type: this.type
        })
      })
        .then(({ data }) => {
          // console.log(data);
          if (data && data.code === 0) {
            this.dataList = data.data.content;
            this.totalPage = data.data.totalPages;
          } else {
            this.dataList = [];
            this.totalPage = 0;
          }
          this.dataListLoading = false;
        })
        .catch(() => { });
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
          return item.attrId;
        });
      this.$confirm(
        `确定对[id=${ids.join(",")}]进行[${id ? "删除" : "批量删除"}]操作?`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      )
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/product/attr/delete"),
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
        })
        .catch(() => { });
    }
  }
};
</script>
<style scoped>
</style>
