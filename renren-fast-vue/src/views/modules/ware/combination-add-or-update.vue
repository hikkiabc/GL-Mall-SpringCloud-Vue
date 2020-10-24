<template>
  <el-dialog :title="!dataForm.id ? 'New' : 'Edit'" :close-on-click-modal="false" :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="150px">
      <el-form-item label="ProductComb ID" prop="productCombId">
        <el-input v-model="dataForm.productCombId" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="Ware" prop="wareId">
        <el-select v-model="dataForm.wareId" placeholder="" clearable>
          <el-option :label="w.name" :value="w.id" v-for="w in wareList" :key="w.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="Stock" prop="stock">
        <el-input v-model="dataForm.stock" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="ProductCombName" prop="productCombName">
        <el-input v-model="dataForm.productCombName" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="Lock" prop="stockLocked">
        <el-input v-model="dataForm.stockLock" placeholder=""></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">Cancel</el-button>
      <el-button type="primary" @click="dataFormSubmit()">Submit</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  data() {
    return {
      visible: false,
      wareList: [],
      dataForm: {
        id: 0,
        productCombId: "",
        wareId: "",
        stock: 0,
        productCombName: "",
        stockLock: 0
      },
      dataRule: {
        productCombId: [{ required: true, message: "sku_id不能为空", trigger: "blur" }],
        wareId: [
          { required: true, message: "仓库id不能为空", trigger: "blur" }
        ],
        stock: [{ required: true, message: "库存数不能为空", trigger: "blur" }],
        productCombName: [
          { required: true, message: "sku_name不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getWares();
  },
  methods: {
    getWares() {
      this.$http({
        url: this.$http.adornUrl("/ware/0"),
        method: "get",
        params: this.$http.adornParams({
          pageSize: 500,
          pageNum: 1
        })
      }).then(({ data }) => {
        this.wareList = data.data.content;
      });
    },
    init(id) {
      this.dataForm.id = id || 0;
      this.visible = true;
      this.$nextTick(() => {
        this.$refs["dataForm"].resetFields();
        console.log(1);
        if (this.dataForm.id) {
          this.$http({
            url: this.$http.adornUrl(`/ware/product_comb/${this.dataForm.id}`),
            method: "get",
            params: this.$http.adornParams()
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.dataForm.skuId = data.wareSku.skuId;
              this.dataForm.wareId = data.wareSku.wareId;
              this.dataForm.stock = data.wareSku.stock;
              this.dataForm.skuName = data.wareSku.skuName;
              this.dataForm.stockLocked = data.wareSku.stockLocked;
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
              `/ware/product_comb`
            ),
            method: "post",
            data: this.$http.adornData(
              this.dataForm
            )
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
  }
};
</script>