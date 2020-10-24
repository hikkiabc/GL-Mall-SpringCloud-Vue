<template>
  <el-dialog :title="!dataForm.id ? 'New' : 'Edit'" :close-on-click-modal="false" :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="120px">
      <el-form-item label="Priority" prop="priority">
        <el-input v-model="dataForm.priority" placeholder=""></el-input>
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
        assigneeId: '',
        assigneeName: '',
        phone: '',
        priority: '',
        status: 0,
        wareId: '',
        amount: '',
        createTime: '',
        updateTime: ''
      },
      dataRule: {
        assigneeId: [
          { required: true, message: '采购人id不能为空', trigger: 'blur' }
        ],
        assigneeName: [
          { required: true, message: '采购人名不能为空', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '联系方式不能为空', trigger: 'blur' }
        ],
        priority: [
          { required: true, message: '优先级不能为空', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '状态不能为空', trigger: 'blur' }
        ],
        wareId: [
          { required: true, message: '仓库id不能为空', trigger: 'blur' }
        ],
        amount: [
          { required: true, message: '总金额不能为空', trigger: 'blur' }
        ],
        createTime: [
          { required: true, message: '创建日期不能为空', trigger: 'blur' }
        ],
        updateTime: [
          { required: true, message: '更新日期不能为空', trigger: 'blur' }
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
            url: this.$http.adornUrl(`/ware/purchase/${this.dataForm.id}`),
            method: 'get',
            params: this.$http.adornParams({
              pageNum: 1,
              pageSize: 1
            })
          }).then(({ data }) => {
            console.log(data);
            if (data && data.code === 0) {
              this.dataForm.assigneeId = data.data.content[0].assigneeId
              this.dataForm.assigneeName = data.data.content[0].assigneeName
              this.dataForm.contact = data.data.content[0].contact
              this.dataForm.priority = data.data.content[0].priority
              this.dataForm.status = data.data.content[0].status
              this.dataForm.wareId = data.data.content[0].wareId
              this.dataForm.totalPrice = data.data.content[0].totalPrice
              this.dataForm.createTime = data.data.content[0].createTime
              this.dataForm.updateTime = data.data.content[0].updateTime
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
            url: this.$http.adornUrl(`/ware/purchase`),
            method: 'post',
            data: this.$http.adornData({
              'id': this.dataForm.id || undefined,
              'assigneeId': this.dataForm.assigneeId,
              'assigneeName': this.dataForm.assigneeName,
              'contact': this.dataForm.contact,
              'priority': this.dataForm.priority,
              'status': this.dataForm.status,
              'wareId': this.dataForm.wareId,
              'totalPrice': this.dataForm.totalPrice,
              'createTime': this.dataForm.createTime,
              'updateTime': this.dataForm.updateTime
            })
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({
                message: 'success',
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