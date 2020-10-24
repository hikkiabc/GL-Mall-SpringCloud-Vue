<template>
  <el-dialog :title="!dataForm.id ? 'Add' : 'New'" :close-on-click-modal="false" :visible.sync="visible" @closed="dialogClose">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="120px">
      <el-form-item label="Name" prop="name">
        <el-input v-model="dataForm.name" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="Sort" prop="sort">
        <el-input v-model="dataForm.sort" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="Description" prop="description">
        <el-input v-model="dataForm.description" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="Icon" prop="icon">
        <el-input v-model="dataForm.icon" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="Category" prop="cateIds">
        <el-cascader placeholder='category' v-model="dataForm.cateIds" :options="categorys" @change="handleChange" :props="props"></el-cascader>
        <!-- <category-cascader :catelogPath.sync="catelogPath"></category-cascader> -->
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">Cancel</el-button>
      <el-button type="primary" @click="dataFormSubmit()">Submit</el-button>
    </span>
  </el-dialog>
</template>

<script>
// import CategoryCascader from "../common/category-cascader";
export default {
  data() {
    return {
      props: {
        expandTrigger: 'hover',
        value: "id",
        label: "name",
        children: "children"
      },
      visible: false,
      categorys: [],
      catelogPath: [],
      dataForm: {
        id: '',
        name: "",
        sort: "",
        description: "",
        icon: "",
        cateId: null,
        cateIds: []
      },
      dataRule: {
        attrGroupName: [
          { required: true, message: "组名不能为空", trigger: "blur" }
        ],
        sort: [{ required: true, message: "排序不能为空", trigger: "blur" }],
        descript: [
          { required: true, message: "描述不能为空", trigger: "blur" }
        ],
        icon: [{ required: true, message: "组图标不能为空", trigger: "blur" }],
        catelogId: [
          { required: true, message: "所属分类id不能为空", trigger: "blur" }
        ]
      }
    };
  },
  // components: { CategoryCascader },
  methods: {
    dialogClose() {
      this.dataForm.cateIds = [];
    },
    async getCategorys() {
      const data = await this.$http({
        url: this.$http.adornUrl("/product/category"),
        method: "get",
      });
      this.categorys = data.data.data;
      // console.log(this.categorys);
    },
    init(id) {
      this.dataForm.id = id || 0;
      this.visible = true;
      this.$nextTick(() => {
        this.$refs["dataForm"].resetFields();
        if (this.dataForm.id) {
          this.$http({
            url: this.$http.adornUrl(
              `/product/attr/group/0`
            ),
            method: "get",
            params: this.$http.adornParams({
              id
            })
          }).then(({ data }) => {
            this.dataForm.name = data.data.content[0].name;
            this.dataForm.sort = data.data.content[0].sort;
            this.dataForm.description = data.data.content[0].description;
            this.dataForm.icon = data.data.content[0].icon;
            this.dataForm.cateIds = data.data.content[0].categoryIds;


          });
        }
      });
    },
    handleChange(v) {
      console.log(v);
    },
    dataFormSubmit() {
      this.$refs["dataForm"].validate(valid => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(
              `/product/attr/group`
            ),
            method: "post",
            data: this.$http.adornData({
              ...this.dataForm, cateId: this.dataForm.cateIds[this.dataForm.cateIds.length - 1]
            })
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({
                message: "操作成功",
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
    }
  },
  created() {
    this.getCategorys();
  }
};
</script>

