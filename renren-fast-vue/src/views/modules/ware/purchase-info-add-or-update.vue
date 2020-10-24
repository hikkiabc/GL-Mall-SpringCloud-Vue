<template>
  <el-dialog :title="!dataForm.id ? 'New' : 'Edit'" :close-on-click-modal="false" :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="150px">
      <el-form-item label="ProductComb ID" prop="productCombId">
        <el-input v-model="dataForm.productCombId" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="Amount" prop="amount">
        <el-input v-model="dataForm.amount" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="Ware" prop="wareId">
        <el-select v-model="dataForm.wareId" placeholder="" clearable>
          <el-option :label="w.name" :value="w.id" v-for="w in wareList" :key="w.id"></el-option>
        </el-select>
      </el-form-item>
      <!-- [0新建，1已分配，2正在采购，3已完成，4采购失败] -->
      <!-- <el-form-item label="状态" prop="status">
        <el-select v-model="dataForm.status" placeholder="请选择状态" clearable>
          <el-option label="新建" :value="0"></el-option>
          <el-option label="已分配" :value="1"></el-option>
          <el-option label="正在采购" :value="2"></el-option>
          <el-option label="已完成" :value="3"></el-option>
          <el-option label="采购失败" :value="4"></el-option>
        </el-select>
      </el-form-item>-->
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
        purchaseId: "",
        productCombId: "",
        amount: "",
        productCombPrice: "",
        wareId: "",
        status: 0
      },
      dataRule: {
        productCombId: [
          { required: true, message: "empty", trigger: "blur" }
        ],
        amount: [
          { required: true, message: "empty", trigger: "blur" }
        ],
        wareId: [{ required: true, message: "empty", trigger: "blur" }]
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
          pageNum: 1,
          pageSize: 500
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
        if (this.dataForm.id) {
          this.$http({
            url: this.$http.adornUrl(
              `/ware/purchase-detail/${this.dataForm.id}`
            ),
            method: "get",
            params: this.$http.adornParams({ pageNum: 1, pageSize: 1 })
          }).then(({ data }) => {
            console.log(data);
            if (data && data.code === 0) {
              this.dataForm.purchaseId = data.data.content[0].purchaseId;
              this.dataForm.productCombId = data.data.content[0].productCombId;
              this.dataForm.amount = data.data.content[0].amount;
              this.dataForm.productCombPrice = data.data.content[0].productCombPrice;
              this.dataForm.wareId = data.data.content[0].wareId;
              this.dataForm.status = data.data.content[0].status;
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
              `/ware/purchase-detail`
            ),
            method: "post",
            data: this.$http.adornData({
              id: this.dataForm.id || undefined,
              purchaseId: this.dataForm.purchaseId,
              productCombId: this.dataForm.productCombId,
              amount: this.dataForm.amount,
              productCombPrice: this.dataForm.productCombPrice,
              wareId: this.dataForm.wareId,
              status: this.dataForm.status
            })
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({
                message: "success",
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
