<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item label="Status">
        <el-select style="width:120px;" v-model="dataForm.status" placeholder="" clearable>
          <el-option label="New" :value="0"></el-option>
          <el-option label="Allocated" :value="1"></el-option>
          <el-option label="Assigned" :value="2"></el-option>
          <el-option label="Complete" :value="3"></el-option>
          <el-option label="Fail" :value="4"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="Keyword">
        <el-input style="width:120px;" v-model="dataForm.key" placeholder="" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()">Search</el-button>
        <el-button v-if="isAuth('ware:purchase:save')" type="primary" @click="addOrUpdateHandle()">New</el-button>
        <el-button v-if="isAuth('ware:purchase:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">Delete</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="dataList" border v-loading="dataListLoading" @selection-change="selectionChangeHandle" style="width: 100%;">
      <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
      <el-table-column prop="id" header-align="center" align="center" label="Purchase ID"></el-table-column>
      <el-table-column prop="assigneeId" header-align="center" align="center" label="Assignee ID"></el-table-column>
      <el-table-column prop="assigneeName" header-align="center" align="center" label="Assignee"></el-table-column>
      <el-table-column prop="contact" header-align="center" align="center" label="Contact"></el-table-column>
      <el-table-column prop="priority" header-align="center" align="center" label="Priority"></el-table-column>
      <el-table-column prop="status" header-align="center" align="center" label="Status">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status == 0">New</el-tag>
          <el-tag type="info" v-if="scope.row.status == 1">Allocated</el-tag>
          <el-tag type="warning" v-if="scope.row.status == 2">Assigned</el-tag>
          <el-tag type="success" v-if="scope.row.status == 3">Complete</el-tag>
          <el-tag type="danger" v-if="scope.row.status == 4">Fail</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="wareId" header-align="center" align="center" label="Ware ID"></el-table-column>
      <el-table-column prop="amount" header-align="center" align="center" label="Total Price"></el-table-column>
      <el-table-column prop="createTime" header-align="center" align="center" label="Created"></el-table-column>
      <el-table-column prop="updateTime" header-align="center" align="center" label="Updated"></el-table-column>
      <el-table-column fixed="right" header-align="center" align="center" width="150" label="Action">
        <template slot-scope="scope">
          <el-button type="text" size="small" v-if="scope.row.status==0||scope.row.status==1" @click="opendrawer(scope.row)">Assign</el-button>
          <el-button type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">Edit</el-button>
          <el-button type="text" size="small" @click="deleteHandle(scope.row.id)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="sizeChangeHandle" @current-change="currentChangeHandle" :current-page="pageIndex" :page-sizes="[10, 20, 50, 100]" :page-size="pageSize" :total="totalPage" layout="total, sizes, prev, pager, next, jumper"></el-pagination>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>
    <el-dialog title="Assign" :visible.sync="caigoudialogVisible" width="30%">
      <el-select v-model="userId" filterable placeholder="">
        <el-option v-for="item in userList" :key="item.userId" :label="item.username" :value="item.userId"></el-option>
      </el-select>
      <span slot="footer" class="dialog-footer">
        <el-button @click="caigoudialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="assignUser">Submit</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import AddOrUpdate from "./purchase-add-or-update";
export default {
  data() {
    return {
      currentRow: {},
      dataForm: {
        key: "",
        status: ""
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      caigoudialogVisible: false,
      userId: "",
      userList: []
    };
  },
  components: {
    AddOrUpdate
  },
  activated() {
    this.getDataList();
  },
  created() {

  },
  methods: {
    opendrawer(row) {
      this.getUserList();
      this.currentRow = row;
      this.userId = ''
      this.caigoudialogVisible = true;
    },
    assignUser() {
      let _this = this;
      let user = {};
      this.userList.forEach(item => {
        if (item.userId == _this.userId) {
          user = item;
        }
      });
      this.caigoudialogVisible = false;
      this.$http({
        url: this.$http.adornUrl(
          `/ware/purchase`
        ),
        method: "post",
        data: this.$http.adornData({
          id: this.currentRow.id || undefined,
          assigneeId: user.userId,
          assigneeName: user.username,
          contact: user.mobile,
          status: 1
        })
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.$message({
            message: "success",
            type: "success",
            duration: 1500
          });

          this.userId = "";
          this.getDataList();
        } else {
          this.$message.error(data.msg);
        }
      });
    },
    getUserList() {
      this.$http({
        url: this.$http.adornUrl("/sys/user/list"),
        method: "get",
        params: this.$http.adornParams({
          page: 1,
          limit: 500
        })
      }).then(({ data }) => {
        console.log(data);
        this.userList = data.page.list;
      });
    },
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/ware/purchase/0"),
        method: "get",
        params: this.$http.adornParams({
          pageNum: this.pageIndex,
          pageSize: this.pageSize,
          key: this.dataForm.key,
          status: this.dataForm.status,
        })
      }).then(({ data }) => {
        console.log(data);
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
          url: this.$http.adornUrl("/ware/purchase/delete"),
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