<template>
  <div>
    <el-button @click="deleteSelected" type="danger">Delete Selected</el-button>
    <el-tree ref="tree" show-checkbox node-key="id" :data="data" :props="defaultProps" :expand-on-click-node="false" @node-click="handleNodeClick" :default-expanded-keys="expandIds" draggable @node-drop="handleDrop" :allow-drop="allowDrop">
      <span class="custom-tree-node" slot-scope="{ node, data }">
        <span>{{ node.label }}</span>
        <span>
          <el-button v-if="node.level < 3" type="text" size="mini" @click="() => append(data)">
            Append
          </el-button>
          <el-button type="text" size="mini" @click="() => edit(data)">
            Eidt
          </el-button>
          <el-button v-if="node.childNodes.length < 1" type="text" size="mini" @click="() => remove(node, data)">
            Delete
          </el-button>
        </span>
      </span>
    </el-tree>

    <el-dialog title="Add a new Category" width="30%" :visible.sync="dialogVisible">
      <span></span>
      <el-form :model="newCategory" ref="numberValidateForm" label-width="50px" class="demo-ruleForm">
        <el-form-item label="Name">
          <el-input v-model="newCategory.name"></el-input>
        </el-form-item>
        <el-form-item label="Sort">
          <el-input v-model="newCategory.sort"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitAddCategory">Submit</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { log } from "util";

export default {
  name: "",
  created() {
    this.getAllCategories();
  },
  data() {
    return {
      maxCateLv: 0,
      changeCateList: [],
      newCategory: {
        name: "",
        sort: null,
        unit: null,
        pid: null,
        cateLevel: null,
        id: null,
      },
      dialogVisible: false,
      data: [],
      defaultProps: {
        children: "children",
        label: "name",
      },
      expandIds: [],
    };
  },
  methods: {
    async deleteSelected() {
      console.log(this.$refs.tree.getCheckedNodes(),);
      const data1 = await this.$http({
        url: this.$http.adornUrl("/product/category"),
        method: "delete",
        data: this.$http.adornData({
          categoryIds: this.$refs.tree.getCheckedNodes(),
          // categoryIds: this.$refs.tree.getCheckedNodes(),
        }),
      });
      this.getAllCategories()
      this.expandIds = this.$refs.tree.getCheckedNodes().map(i => i.pid)
    },
    async handleDrop(draggingNode, dropNode, dropType, ev) {
      // console.log("tree drop: ", draggingNode, dropNode, dropType);
      let siblings;
      let pid;
      let expandId;
      if (dropType === "inner") {
        expandId = dropNode.data.id;
        siblings = dropNode.childNodes;
        pid = dropNode.data.id;
      } else {
        expandId = dropNode.data.pid;
        siblings = dropNode.parent.childNodes;
        pid = dropNode.data.pid;
      }
      siblings.forEach((e, i) => {
        if (e.data.id == draggingNode.data.id) {
          if (draggingNode.data.cateLevel != e.level) {
            console.log("change children catelv");
            this.changeChildrenCateLvl(e);
          }
          this.changeCateList.push({
            id: e.data.id,
            sort: i,
            pid,
            cateLevel: e.level,
          });
        } else {
          this.changeCateList.push({ id: e.data.id, sort: i });
        }
      });
      const data1 = await this.$http({
        url: this.$http.adornUrl("/product/category"),
        method: "post",
        data: this.$http.adornData(this.changeCateList, false),
      });
      this.getAllCategories();
      this.changeCateList = [];
      this.expandIds = [expandId, draggingNode.data.pid];
    },
    changeChildrenCateLvl(node) {
      if (node.childNodes && node.childNodes.length > 0) {
        node.childNodes.forEach((e) => {
          this.changeCateList.push({ id: e.data.id, cateLevel: e.level });
          this.changeChildrenCateLvl(e);
        });
      }
    },
    allowDrop(draggingNode, dropNode, type) {
      // console.log(draggingNode, dropNode, type);
      this.maxCateLv = draggingNode.data.cateLevel;
      this.findMaxDeep(draggingNode.data);

      if (type === "inner") {
        return (
          this.maxCateLv -
          draggingNode.data.cateLevel + 1 +
          dropNode.data.cateLevel * 1 <= 4
        );
      } else {
        return (
          this.maxCateLv - draggingNode.data.cateLevel + 1 + (dropNode.data.cateLevel - 1) <= 4
        );
      }
    },
    findMaxDeep(node) {
      // console.log(node);
      if (node.children && node.children.length > 0) {
        for (let index = 0; index < node.children.length; index++) {
          if (node.children[index].cateLevel > this.maxCateLv) {
            this.maxCateLv = node.children[index].cateLevel;
          }
          this.findMaxDeep(node.children[index]);
        }
      }
    },
    handleNodeClick() { },
    async edit(data) {
      const data1 = await this.$http({
        url: this.$http.adornUrl("/product/category/" + data.id),
        method: "get",
        params: this.$http.adornParams({
          // id: data.id,
          // categoryIds: this.$refs.tree.getCheckedNodes(),
        }),
      });

      this.newCategory.pid = null;
      this.dialogVisible = true;
      this.newCategory.name = data1.data.data.name;
      this.newCategory.id = data.id;
      this.newCategory.cateLevel = null;
      this.newCategory.sort = data1.data.data.sort;
    },
    append(data) {
      this.newCategory.sort = null;
      this.newCategory.id = null;
      this.newCategory.name = "";
      this.dialogVisible = true;
      this.newCategory.pid = data.id;
      this.newCategory.cateLevel = data.cateLevel * 1 + 1;
      this.newCategory.sort = null;
    },
    async remove(node, data) {
      const data1 = await this.$http({
        url: this.$http.adornUrl("/product/category"),
        method: "delete",
        data: this.$http.adornData({
          categoryIds: [node.data],
          // categoryIds: this.$refs.tree.getCheckedNodes(),
        }),
      });
      this.getAllCategories();
      this.expandIds = [node.parent.data.id];
    },
    async getAllCategories() {
      const data = await this.$http({
        url: this.$http.adornUrl("/product/category"),
        method: "get",
      });
      this.data = data.data.data;
      // console.log(data);
    },
    async submitAddCategory() {
      this.dialogVisible = false;
      const data1 = await this.$http({
        url: this.$http.adornUrl("/product/category"),
        method: "post",
        data: this.$http.adornData([this.newCategory], false),
      });
      if (this.newCategory.id) this.expandIds = [this.newCategory.id];
      else this.expandIds = [this.newCategory.pid];

      this.getAllCategories();
    },
  },
};
</script>

<style lang="scss" scoped>
</style>
