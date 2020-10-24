<template>
  <el-dialog :title="!dataForm.id ? 'New' : 'Edit'" :close-on-click-modal="false" :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="120px">
      <el-form-item label="Name" prop="name">
        <el-input v-model="dataForm.name" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="Address" prop="address">
        <el-input v-model="dataForm.address" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="Area Code" prop="areaCode">
        <el-input v-model="dataForm.areaCode" placeholder=""></el-input>
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
      dataForm: {
        id: 0,
        name: '',
        address: '',
        areaCode: ''
      },
      dataRule: {
        name: [
          { required: true, message: 'not empty', trigger: 'blur' }
        ],
        address: [
          { required: true, message: 'not empty', trigger: 'blur' }
        ],
        areaCode: [
          { required: true, message: 'not empty', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    init(id) {
      this.dataForm.id = id || 0
      this.visible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.id) {
          this.$http({
            url: this.$http.adornUrl(`/ware/${this.dataForm.id}`),
            method: 'get',
            params: this.$http.adornParams({ pageNum: 1, pageSize: 1 })
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.dataForm.name = data.data.content[0].name
              this.dataForm.address = data.data.content[0].address
              this.dataForm.areaCode = data.data.content[0].areaCode
            }
          })
        }
      })
    },
    // 表单提交
    dataFormSubmit() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(`/ware/${this.dataForm.id}`),
            method: 'post',
            data: this.$http.adornData({
              'id': this.dataForm.id || undefined,
              'name': this.dataForm.name,
              'address': this.dataForm.address,
              'areaCode': this.dataForm.areaCode
            })
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.visible = false
                  this.$emit('refreshDataList')
                }
              })
            } else {
              this.$message.error(data.msg)
            }
          })
        }
      })
    }
  }
}
</script>
