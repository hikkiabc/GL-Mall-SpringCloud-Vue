<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="box-card">
          <el-tabs tab-position="left" style="width:98%">
            <el-tab-pane :label="group.name" v-for="(group,gidx) in dataResp.attrGroups" :key="group.attrGroupId">
              <el-form ref="form" :model="dataResp">
                <el-form-item :label="attr.name" v-for="(attr,aidx) in group.attributeList" :key="attr.id">
                  <!-- <el-input v-model="dataResp.baseAttrs[gidx][aidx].attrId" type="hidden" v-show="false"></el-input> -->
                  <el-select v-model="dataResp.baseAttrs[gidx][aidx].value" :multiple="attr.valueType == 1" filterable allow-create clearable default-first-option placeholder="">
                    <el-option v-for="(val,vidx) in attr.value.split(',')" :key="vidx" :label="val" :value="val"></el-option>
                  </el-select>
                  <el-checkbox v-model="dataResp.baseAttrs[gidx][aidx].fastDisplay" true-label="1" false-label="0">Fast Display</el-checkbox>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
          <div style="margin:auto">
            <el-button type="success" style="float:right" @click="submitSpuAttrs">Submit</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  components: {},
  props: {},
  data() {
    return {
      productId: "",
      categoryId: "",
      dataResp: {
        attrGroups: [],
        baseAttrs: []
      },
      spuAttrsMap: {}
    };
  },
  computed: {},
  methods: {
    clearData() {
      this.dataResp.attrGroups = [];
      this.dataResp.baseAttrs = [];
      this.spuAttrsMap = {};
    },
    getSpuBaseAttrs() {
      this.$http({
        url: this.$http.adornUrl(`/product/attr/product-basic-attr/${this.productId}`),
        method: "get"
      }).then(({ data }) => {
        data.data.forEach(item => {
          this.spuAttrsMap["" + item.id] = item;
        });
      });
    },
    getQueryParams() {
      this.productId = this.$route.query.productId;
      this.categoryId = this.$route.query.categoryId;
    },
    showBaseAttrs() {
      let _this = this;
      this.$http({
        url: this.$http.adornUrl(
          `/product/attr/attrgroup-with-attr/${this.categoryId}`
        ),
        method: "get",
        params: this.$http.adornParams({})
      }).then(({ data }) => {
        data.data.forEach(item => {
          let attrArray = [];
          item.attributeList.forEach(attr => {
            let v = "";
            if (_this.spuAttrsMap["" + attr.id]) {
              if (attr.valueType == 1) {
                v = _this.spuAttrsMap["" + attr.id].value.split(",");
              }
              else {
                v = _this.spuAttrsMap["" + attr.id].value
              }
              // if (v.length == 1) {
              //   v = v[0] + "";
              // }
            }
            attrArray.push({
              id: attr.id,
              name: attr.name,
              value: v,
              fastDisplay: _this.spuAttrsMap["" + attr.id]
                ? _this.spuAttrsMap["" + attr.id].fastDisplay
                : attr.fastDisplay
            });
          });
          this.dataResp.baseAttrs.push(attrArray);
        });
        console.log(this.dataResp.baseAttrs);
        this.dataResp.attrGroups = data.data;
      });
    },
    submitSpuAttrs() {
      let submitData = [];
      this.dataResp.baseAttrs.forEach(item => {
        item.forEach(attr => {
          let val = "";
          if (attr.value instanceof Array) {
            val = attr.value.join(",");
          } else {
            val = attr.value;
          }
          if (val != "") {
            submitData.push({
              id: attr.id,
              name: attr.name,
              value: val,
              fastDisplay: attr.fastDisplay
            });
          }
        });
      });
      this.$confirm("Submit?", "Alert", {
        confirmButtonText: "Yes",
        cancelButtonText: "Cancel",
        type: "warning"
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl(`/product/attr/product-basic-attr/${this.productId}`),
            method: "post",
            data: this.$http.adornData(submitData, false)
          }).then(({ data }) => {
            this.$message({
              type: "success",
              message: "success!"
            });
          });
        })
        .catch((e) => {
          this.$message({
            type: "info",
            message: "Cancel" + e
          });
        });
    }
  },
  created() { },
  activated() {
    this.clearData();
    this.getQueryParams();
    if (this.productId && this.categoryId) {
      this.showBaseAttrs();
      this.getSpuBaseAttrs();
    }
  }
};
</script>
<style scoped>
</style>
