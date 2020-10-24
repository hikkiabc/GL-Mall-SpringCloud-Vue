<template>
  <el-dialog :title="!dataForm.id ? 'Add' : 'Edit'" :close-on-click-modal="false" :visible.sync="visible" @closed="dialogClose">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
      <!--       @keyup.enter.native="dataFormSubmit()" -->
      <el-form-item label="Attri Name" prop="name">
        <el-input v-model="dataForm.name" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="Attri Type" prop="type">
        <el-select v-model="dataForm.type" placeholder="Select">
          <el-option label="Product Attribute" value="1"></el-option>
          <el-option label="Sale Attribute" value="0"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="Value Type" prop="valueType">
        <el-switch v-model="dataForm.valueType" active-text="Multiple" inactive-text="Single" active-color="#13ce66" inactive-color="#ff4949" inactive-value="0" active-value="1"></el-switch>
      </el-form-item>
      <el-form-item label="Value" prop="value">
        <!-- <el-input v-model="dataForm.valueSelect"></el-input> -->
        <el-select v-model="dataForm.value" multiple filterable allow-create placeholder=""></el-select>
      </el-form-item>
      <el-form-item label="Attri Icon" prop="icon">
        <el-input v-model="dataForm.icon" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="Attri Category" prop="categoryId">
        <category-cascader :categoryIds.sync="categoryIds"></category-cascader>
      </el-form-item>
      <el-form-item label="Attri Group" prop="groupId" v-if="dataForm.type == 1">
        <el-select ref="groupSelect" v-model="dataForm.groupId" placeholder="">
          <el-option v-for="item in attrGroups" :key="item.id" :label="item.name" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="searchType" prop="searchType" v-if="dataForm.type == 1">
        <el-switch v-model="dataForm.searchType" active-color="#13ce66" inactive-color="#ff4949" active-value="1" inactive-value="0"></el-switch>
      </el-form-item>
      <el-form-item label="Fast Display" prop="fastDisplay" v-if="dataForm.type == 1">
        <el-switch v-model="dataForm.fastDisplay" active-color="#13ce66" inactive-color="#ff4949" active-value="1" inactive-value="0"></el-switch>
      </el-form-item>
      <el-form-item label="Attri Status" prop="status">
        <el-switch v-model="dataForm.status" active-color="#13ce66" inactive-color="#ff4949" active-value="1" inactive-value="0"></el-switch>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">Cancel</el-button>
      <el-button type="primary" @click="dataFormSubmit()">Submit</el-button>
    </span>
  </el-dialog>
</template>

<script>
import CategoryCascader from "../../common/category-cascade";
export default {
  data() {
    return {
      visible: false,
      dataForm: {
        id: null,
        name: "",
        searchType: 0,
        valueType: 0,
        icon: "",
        value: "",
        type: '',
        status: 1,
        categoryId: "",
        groupId: "",
        fastDisplay: 0
      },
      categoryIds: [],
      attrGroups: [],
      dataRule: {
        name: [
          { required: true, message: "属性名不能为空", trigger: "blur" }
        ],
        searchType: [
          {
            required: true,
            message: "是否需要检索不能为空",
            trigger: "blur"
          }
        ],
        valueType: [
          {
            required: true,
            message: "值类型不能为空",
            trigger: "blur"
          }
        ],
        icon: [
          { required: true, message: "属性图标不能为空", trigger: "blur" }
        ],
        type: [
          {
            required: true,
            message: "属性类型不能为空",
            trigger: "blur"
          }
        ],
        status: [
          {
            required: true,
            message: "启用状态不能为空",
            trigger: "blur"
          }
        ],
        categoryId: [
          {
            required: true,
            message: "需要选择正确的三级分类数据",
            trigger: "blur"
          }
        ],
        fastDisplay: [
          {
            required: true,
            message: "快速展示不能为空",
            trigger: "blur"
          }
        ]
      }
    };
  },
  props: {
    type: {
      type: Number,
      default: 1
    }
  },
  watch: {
    categoryIds(path) {
      this.attrGroups = [];

      this.dataForm.categoryId = path[path.length - 1];
      if (path && path.length == 3) {
        this.$http({
          url: this.$http.adornUrl(
            `/product/attr/group/${path[path.length - 1]}`
          ),
          method: "get",
          params: this.$http.adornParams({ page: 1, limit: 10000000 })
        }).then(({ data }) => {
          // console.log(data);
          if (data && data.code === 0) {
            this.attrGroups = data.data.content;
            this.dataForm.groupId = this.attrGroups[0] ? this.attrGroups[0].id : null
            this.attrGroups.unshift('')
            console.log(this.attrGroups);
          } else {
            this.$message.error(data.msg);
          }
        });
      } else if (path.length == 0) {
        this.dataForm.cateId = "";
      } else {
        this.$message.error("请选择正确的分类");
        this.dataForm.cateId = "";
      }
    },
    getType(v, v1) {
      // this.type = this.dataForm.type
      // console.log(this.dataForm.type);
      // this.$emit('update:type', this.dataForm.type)
    }
  },
  computed: {
    getType() {
      return this.dataForm.type
    }
  },
  components: { CategoryCascader },
  methods: {
    init(id) {
      this.dataForm.id = id || null;
      this.dataForm.type = this.type + '';
      this.visible = true;
      this.$nextTick(() => {
        this.$refs["dataForm"].resetFields();
        if (this.dataForm.id) {
          this.$http({
            url: this.$http.adornUrl(
              `/product/attr/attribute/0`
            ),
            method: "get",
            params: this.$http.adornParams({ id, type: this.type })
          }).then(({ data }) => {
            // console.log(data);
            if (data && data.code === 0) {
              this.dataForm.id = data.data.content[0].id
              this.dataForm.name = data.data.content[0].name;
              this.dataForm.searchType = data.data.content[0].searchType;
              this.dataForm.valueType = data.data.content[0].valueType;
              this.dataForm.icon = data.data.content[0].icon;
              this.dataForm.value = data.data.content[0].value.split(',')
              this.dataForm.type = data.data.content[0].type
              this.dataForm.status = data.data.content[0].status
              this.dataForm.categoryId = data.data.content[0].categoryId
              this.dataForm.fastDisplay = data.data.content[0].fastDisplay
              this.categoryIds = data.data.content[0].categoryIds
              this.$nextTick(() => {
                this.dataForm.groupId = data.data.content[0].groupId
              });
            }
          });
        }
      });
    },
    // 表单提交
    dataFormSubmit() {
      this.$refs["dataForm"].validate(valid => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(
              `/product/attr/attribute`
            ),
            method: "post",
            data: this.$http.adornData({
              ...this.dataForm, value: this.dataForm.value.join(',')
            })
          }).then(({ data }) => {
            // console.log(data);
            if (data && data.code === 0) {
              this.$message({
                message: "Success",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.visible = false;
                  this.$emit("refreshDataList");
                }
              });
            } else {
              this.$message.error(data.msg);
            }
          });
        }
      });
    },
    //dialogClose
    dialogClose() {
      this.categoryIds = [];
    }
  }
};
</script>
