<template>
  <div>
    <el-upload :action="dataObj.host" list-type="picture" :multiple="false" :data='dataObj' :show-file-list="showFileList" :file-list="fileList" :before-upload="beforeUpload" :on-remove="handleRemove" :on-success="handleUploadSuccess" :on-preview="handlePreview">
      <el-button size="small" type="primary">upload</el-button>
      <div slot="tip" class="el-upload__tip"></div>
    </el-upload>
    <el-dialog :visible.sync="dialogVisible">
      <img width="100%" :src="fileList[0].url" alt />
    </el-dialog>
    <!-- <div>{{fileList[0].url}}</div> -->
    <!-- <div @click="test">test</div> -->
  </div>
</template>
<script>
import { policy } from "./policy.js";
import { getUUID } from "@/utils";
export default {
  name: "SingleUpload",
  props: {
    value: String
  },

  computed: {
    imageUrl() {
      return this.value;
    },
    imageName() {
      if (this.value != null && this.value !== "") {
        return this.value.substr(this.value.lastIndexOf("/") + 1);
      } else {
        return null;
      }
    },
    fileList() {
      return [
        {
          name: this.imageName,
          url: this.imageUrl
        }
      ];
    },
    showFileList: {
      get: function () {
        return (
          this.value !== null && this.value !== "" && this.value !== undefined
        );
      },
      set: function (newValue) { }
    }
  },
  data() {
    return {
      dataObj: {
        success_action_status: '',
        policy: "",
        signature: "",
        key: "",
        ossaccessKeyId: "",
        dir: "",
        host: ""
        // callback:'',
      },
      dialogVisible: false
    };
  },
  methods: {
    emitInput(val) {
      this.$emit("input", val);
    },
    handleRemove(file, fileList) {
      this.emitInput("");
    },
    handlePreview(file) {
      this.dialogVisible = true;
    },
    beforeUpload(file) {
      let _self = this;
      return new Promise((resolve, reject) => {
        policy()
          .then(response => {
            _self.dataObj.success_action_status = '200';
            _self.dataObj.policy = response.data.policy;
            _self.dataObj.signature = response.data.signature;
            _self.dataObj.ossaccessKeyId = response.data.accessid;
            _self.dataObj.key = response.data.dir + getUUID() + "_${filename}";
            _self.dataObj.dir = response.data.dir;
            _self.dataObj.host = response.data.host;
            // console.log("响应的数据", _self.dataObj);
            resolve(true);
          })
          .catch(err => {
            reject(false);
          });
      });
    },
    test() {

      // this.fileList.pop()
      console.log(this.value);
    },
    handleUploadSuccess(res, file) {
      this.showFileList = true;
      // this.fileList.push({
      //   name: file.name,
      //   url:
      //     this.dataObj.host +
      //     "/" +
      //     this.dataObj.key.replace("${filename}", file.name)
      // });
      // console.log(this.fileList);
      this.emitInput(this.dataObj.host +
        "/" +
        this.dataObj.key.replace("${filename}", file.name))
    }
  },
  // watch: {
  //   value(v) {
  //     console.log(v);
  //   }
  // },
};
</script>
<style>
</style>
