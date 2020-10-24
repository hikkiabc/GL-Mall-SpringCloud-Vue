<template>
  <el-dialog title="Attr-Group-Relation" :visible.sync="dialogTableVisible">
    <el-dialog append-to-body title="Add New" width="60%" :visible.sync="addNewVisible">
      <el-input v-model="key" placeholder="Attr Name" style='width:200px;margin-right:30px' clearable></el-input>
      <el-button size='small' @click="getDataList">Search</el-button>
      <el-table :data="attributeList" @selection-change="handleAddAttriSelectionChange">
        <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
        <el-table-column property="id" label="ID" width=""></el-table-column>
        <el-table-column property="name" label="Attr Name" width=""></el-table-column>
        <el-table-column property="icon" label="Icon" width=""></el-table-column>
        <el-table-column label="Value">
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
      </el-table>
      <div class="inner-submit-btn-container" style='margin:20px '>
        <el-button size='small ' @click="addNewVisible=false">Cancel</el-button>
        <el-button size='small ' @click="addRelationSubmit">Submit</el-button>
      </div>

      <el-pagination @size-change="sizeChangeHandle" @current-change="currentChangeHandle" :current-page="pageIndex" :page-sizes="[1, 2, 5]" :page-size="pageSize" :total="totalPage" layout="total, sizes, prev, pager, next, jumper"></el-pagination>
    </el-dialog>
    <el-button type="" size='small ' @click="addNewClick">New Attribute</el-button>
    <el-button type="danger" size='small ' @click="deleteHandle()">Delete Selected</el-button>
    <el-table :data="data" @selection-change="handleSelectionChange">
      <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
      <el-table-column property="id" label="ID" width="150"></el-table-column>
      <el-table-column property="name" label="Attr Name" width="200"></el-table-column>
      <el-table-column label="Value">
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
      <el-table-column fixed="right" header-align="center" align="center" label="Action">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="deleteHandle(scope.row.id)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>
<script>
export default {
  name: "",
  data() {
    return {
      attributeList: [],
      pageSize: 5,
      pageIndex: 1,
      key: null,
      addNewVisible: false,
      dialogTableVisible: false,
      data: [],
      totalPage: 0,
      groupId: null,
      attribute_attributeAttrGroupToAdd: [],
      attribute_attributeAttrGroup: [],

    };
  },
  methods: {
    currentChangeHandle(val) {
      this.pageIndex = val;
      this.getDataList();
    },
    addNewClick() {
      this.getDataList()
      this.addNewVisible = true
      // this.$nextTick(() => {
      //   this.getDataList()
      // })

    },
    async getDataList() {
      const data = await this.$http({
        url: this.$http.adornUrl(
          `/product/attr/attribute/unrealated`
        ),
        method: "get",
        params: this.$http.adornParams({ key: this.key, pageSize: this.pageSize, pageNum: this.pageIndex, groupId: this.groupId })
      })
      // console.log(data);
      this.attributeList = data.data.data.content
      this.totalPage = data.data.data.totalElements
    },
    sizeChangeHandle(val) {
      this.pageSize = val;
      this.pageIndex = 1;
      this.getDataList();
    },
    handleAddAttriSelectionChange(v) {
      this.attribute_attributeAttrGroupToAdd = v.map(e => {
        return { attributeId: e.id, attributeGroupId: this.groupId }
      })
    },
    handleSelectionChange(v) {
      this.attribute_attributeAttrGroup = v.map(e => ({ attributeId: e.id, attributeGroupId: this.groupId }))
    },
    async addRelationSubmit() {
      const data = await this.$http({
        url: this.$http.adornUrl(
          `/product/attr/attribute_attributeGroup`
        ),
        method: "post",
        data: this.$http.adornData(this.attribute_attributeAttrGroupToAdd, false)
      })
      this.getAttri_attrGroupList(this.groupId)
      this.addNewVisible = false
    },
    async deleteHandle(id) {
      const data = await this.$http({
        url: this.$http.adornUrl(
          `/product/attr/attribute_attributeGroup`
        ),
        method: "delete",
        data: this.$http.adornData(!id ? this.attribute_attributeAttrGroup : [{ attributeId: id, attributeGroupId: this.groupId }], false)
      })
      if (data.data.code == 0) {
        this.dialogTableVisible = false
        this.$emit('refreshDataList')
      }
    },
    async getAttri_attrGroupList(groupId) {
      const data = await this.$http({
        url: this.$http.adornUrl(
          `/product/attr/attribute/0`
        ),
        method: "get",
        params: this.$http.adornParams({ groupId, pageSize: 10000, pageNum: 1 })
      })
      this.data = data.data.data.content
    },
    init(groupId) {
      this.groupId = groupId
      this.dialogTableVisible = true
      this.$nextTick(async () => {
        this.getAttri_attrGroupList(groupId)
      })

    }
  },
}
</script>
<style  scoped>
.inner-submit-btn-container {
  display: flex;
  justify-content: flex-end;
}
.inner-submit-btn-container > button:nth-child(2) {
  background-color: rgb(37, 209, 180);
  color: bisque;
}
</style>