<template>
  <div>
    <el-tree ref="tree" node-key="id" :data="data" :props="defaultProps" :expand-on-click-node="false" @node-click="handleNodeClick" :default-expanded-keys="expandIds">
      <!-- <span class="custom-tree-node" slot-scope="{ node, data }">
      <span>{{ node.label }}</span>
    </span> -->

    </el-tree>
    <!-- <el-cascader placeholder='category' v-model="test" :options="data" :props="props"></el-cascader> -->
  </div>
</template>
<script>
export default {
  name: "",
  created() {
    this.getAllCategories()
  },
  props: {
    testId: {
      type: String,
      // default
    }
  },
  data() {
    return {
      lastActiveNode: null,
      test: [],
      props: {
        expandTrigger: 'hover',
        value: "id",
        label: "name",
        children: "children"
      },
      data: [],
      defaultProps: {
        children: "children",
        label: "name",
      },
      expandIds: []
    };
  },
  watch: {
    testId: {
      handler(v) {
        let node = this.$refs.tree.getNode(v)
        let eles = document.querySelectorAll('.el-tree-node__label')
        // console.log(eles);
      }
    }
  },
  methods: {
    handleNodeClick(data, node, comp) {
      if (this.lastActiveNode) {
        this.lastActiveNode.$el.querySelectorAll('.el-tree-node__content')[0].style.color = '#606266'
      }
      comp.$el.querySelectorAll('.el-tree-node__content')[0].style.color = 'red'
      this.lastActiveNode = comp
      this.$emit('nodeClick', data, node)
    },
    async getAllCategories() {
      const data = await this.$http({
        url: this.$http.adornUrl("/product/category"),
        method: "get",
      });
      this.data = data.data.data;
      // console.log(data);
    },
  },
}
</script>
<style lang="scss" scoped>
</style>