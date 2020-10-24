<template>
  <el-dialog :title="!dataForm.id ? 'Add' : 'Edit'" :close-on-click-modal="false" :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="130px">
      <el-form-item label="Brand Name" prop="name">
        <el-input v-model="dataForm.name" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="Show Status" prop="showStatus">
        <el-switch @change='showStatusChange' active-value=1 inactive-value=0 v-model="dataForm.showStatus" active-color="#13ce66" inactive-color="#ff4949">
        </el-switch>
      </el-form-item>

      <el-form-item label="Sort" prop="sort">
        <el-input v-model.number="dataForm.sort" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="Logo" prop="logo">
        <Singleupload v-model="dataForm.logo"></Singleupload>
      </el-form-item>

    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">Cancel</el-button>
      <el-button type="primary" @click="dataFormSubmit()">Submit</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { isEmail, isMobile } from '@/utils/validate'
import Singleupload from '@/components/single-upload'
export default {
  components: {
    Singleupload
  },
  data() {
    var validatePassword = (rule, value, callback) => {
      if (!this.dataForm.id && !/\S/.test(value)) {
        callback(new Error('密码不能为空'))
      } else {
        callback()
      }
    }
    var validateComfirmPassword = (rule, value, callback) => {
      if (!this.dataForm.id && !/\S/.test(value)) {
        callback(new Error('确认密码不能为空'))
      } else if (this.dataForm.password !== value) {
        callback(new Error('确认密码与密码输入不一致'))
      } else {
        callback()
      }
    }
    var validateEmail = (rule, value, callback) => {
      if (!isEmail(value)) {
        callback(new Error('邮箱格式错误'))
      } else {
        callback()
      }
    }
    var validateMobile = (rule, value, callback) => {
      if (!isMobile(value)) {
        callback(new Error('手机号格式错误'))
      } else {
        callback()
      }
    }
    return {
      visible: false,
      roleList: [],
      dataForm: {
        logo: '',
        id: '',
        sort: null,
        showStatus: '',
        name: ''

      },
      dataRule: {
        // name: [
        //   { required: true, message: '用户名不能为空', trigger: 'blur' }
        // ],
        name: [
          {
            validator: (rule, value, callback) => {
              if (!/^[a-zA-Z0-9]*$/.test(value)) {
                callback(new Error('Must be letters'))
              } else {
                callback()
              }
            }, trigger: 'blur'
          }
        ],
        comfirmPassword: [
          { validator: validateComfirmPassword, trigger: 'blur' }
        ],
        sort: [

          {
            validator: (rule, value, callback) => {
              if (!Number.isInteger(value)) {
                callback(new Error('Must be Number'))
              } else {
                callback()
              }
            }, trigger: 'blur'
          }
        ],
        mobile: [
          { required: true, message: '手机号不能为空', trigger: 'blur' },
          { validator: validateMobile, trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    showStatusChange() {
    },
    init(id) {
      this.visible = true
      this.dataForm.id = id || ''
      this.$http({
        url: this.$http.adornUrl('/product/brand/'),
        method: 'get',
        params: this.$http.adornParams({
          id
        })
      }).then(({ data }) => {

        this.dataForm.name = data.data.name
        this.dataForm.sort = data.data.sort * 1
        this.dataForm.showStatus = data.data.showStatus
        this.dataForm.logo = data.data.logo
        // console.log(this.dataForm.logo);
      })
      // .then(() => {
      //   this.visible = true
      //   this.$nextTick(() => {
      //     this.$refs['dataForm'].resetFields()
      //   })
      // }).then(() => {
      //   if (this.dataForm.id) {
      //     this.$http({
      //       url: this.$http.adornUrl(`/sys/user/info/${this.dataForm.id}`),
      //       method: 'get',
      //       params: this.$http.adornParams()
      //     }).then(({ data }) => {
      //       if (data && data.code === 0) {
      //         this.dataForm.userName = data.user.username
      //         this.dataForm.salt = data.user.salt
      //         this.dataForm.email = data.user.email
      //         this.dataForm.mobile = data.user.mobile
      //         this.dataForm.roleIdList = data.user.roleIdList
      //         this.dataForm.status = data.user.status
      //       }
      //     })
      //   }
      // })
    },
    // 表单提交
    dataFormSubmit() {
      this.$refs['dataForm'].validate(async (valid) => {

        if (valid) {
          const data = await this.$http({
            url: this.$http.adornUrl(`/product/brand`),
            method: 'post',
            data: this.$http.adornData(
              this.dataForm
              , false
            )
          })
          this.visible = false
          this.$emit('refreshDataList')
          // console.log(data);
        }
      })

    }
  }
}
</script>
