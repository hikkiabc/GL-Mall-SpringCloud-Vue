<template>
  <div>
    <el-row>
      <el-col :span="24">
        <el-steps :active="step" finish-status="success">
          <el-step title="Basic Info"></el-step>
          <el-step title="Basic Attri"></el-step>
          <el-step title="Sale Attri"></el-step>
          <el-step title="SKU"></el-step>
          <el-step title="Finish"></el-step>
        </el-steps>
      </el-col>
      <el-col :span="24" v-show="step==0">
        <el-card class="box-card" style="width:80%;margin:20px auto">
          <!-- <el-form ref="spuBaseForm" :model="spu" label-width="120px" 
          :rules="spuBaseInfoRules"
          > -->
          <el-form ref="spuBaseForm" :model="spu" label-width="120px">
            <el-form-item label="Product Name" prop="spuName">
              <el-input v-model="spu.spuName"></el-input>
            </el-form-item>
            <el-form-item label="Desc" prop="spuDescription">
              <el-input v-model="spu.spuDescription"></el-input>
            </el-form-item>
            <el-form-item label="Category" prop="categoryId">
              <category-cascader :categoryIds.sync='categoryIds'></category-cascader>
            </el-form-item>
            <el-form-item label="Brand" prop="brandId">
              <brand-select></brand-select>
            </el-form-item>
            <el-form-item label="Weight(Kg)" prop="weight">
              <el-input-number v-model.number="spu.weight" :min="0" :precision="3" :step="0.1"></el-input-number>
            </el-form-item>
            <el-form-item label="Score" prop="bounds">
              <label>Gold</label>
              <el-input-number style="width:130px" placeholder="Gold" v-model="spu.bounds.buyBound" :min="0" controls-position="right"></el-input-number>
              <label style="margin-left:15px">Growth</label>
              <el-input-number style="width:130px" placeholder="Growth" v-model="spu.bounds.growBound" :min="0" controls-position="right">
                <template slot="prepend">Growth</template>
              </el-input-number>
            </el-form-item>
            <el-form-item label="Intro" prop="description">
              <multi-upload v-model="spu.descriptionImgs"></multi-upload>
            </el-form-item>

            <el-form-item label="Gallary" prop="images">
              <multi-upload v-model="spu.images"></multi-upload>
            </el-form-item>
            <el-form-item>
              <el-button type="success" @click="collectSpuBaseInfo">Next</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="24" v-show="step==1">
        <el-card class="box-card" style="width:80%;margin:20px auto">
          <el-tabs tab-position="left" style="width:98%">
            <el-tab-pane :label="group.name" v-for="(group,gidx) in dataResp.attrGroups" :key="group.id">
              <!-- 遍历属性,每个tab-pane对应一个表单，每个属性是一个表单项  spu.baseAttrs[0] = [{attrId:xx,val:}]-->
              <el-form label-width="50px" ref="form" :model="spu">
                <el-form-item :label="attr.name" v-for="(attr,aidx) in group.attributeList" :key="attr.id">
                  <!-- <el-input v-model="dataResp.baseAttrs[gidx][aidx].attrId" type="hidden" v-show="false"></el-input> -->
                  <el-select v-model="dataResp.baseAttrs[gidx][aidx].value" :multiple="attr.type == 1" filterable allow-create default-first-option placeholder="Select Value">
                    <el-option v-for="(val,vidx) in attr.value.split(',')" :key="vidx" :label="val" :value="val"></el-option>
                  </el-select>
                  <el-checkbox v-model="dataResp.baseAttrs[gidx][aidx].fastDisplay" :true-label="1" :false-label="0">Fast Display</el-checkbox>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
          <div style="margin:auto">
            <el-button type="primary" @click="step = 0">Last</el-button>
            <el-button type="success" @click="generateSaleAttrs">Next</el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="24" v-show="step==2">
        <el-card class="box-card" style="width:80%;margin:20px auto">
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <span>Select Sale Attr</span>
              <el-form ref="saleform" :model="spu">
                <el-form-item :label="attr.name" v-for="(attr,aidx) in dataResp.saleAttrs" :key="attr.id">
                  <!-- <el-input v-model="dataResp.tempSaleAttrs[aidx].attrId" type="hidden" v-show="false"></el-input> -->
                  <el-checkbox-group v-model="dataResp.tempSaleAttrs[aidx].value">
                    <el-checkbox :v-if="dataResp.saleAttrs[aidx].value != ''" :label="val" v-for="val in dataResp.saleAttrs[aidx].value.split(',')" :key="val"></el-checkbox>
                    <div style="margin-left:20px;display:inline">
                      <el-button v-show="!inputVisible[aidx].view" class="button-new-tag" size="mini" @click="showInput(aidx)">Add</el-button>
                      <el-input v-show="inputVisible[aidx].view" v-model="inputValue[aidx].val" :ref="'saveTagInput'+aidx" size="mini" style="width:150px" @keyup.enter.native="handleInputConfirm(aidx)" @blur="handleInputConfirm(aidx)"></el-input>
                    </div>
                  </el-checkbox-group>
                </el-form-item>
              </el-form>
            </div>
            <el-button type="primary" @click="step = 1">Last</el-button>
            <el-button type="success" @click="generateSkus">Next</el-button>
            <!-- <el-button type="success" @click="test">Next</el-button> -->
          </el-card>
        </el-card>
      </el-col>
      <el-col :span="24" v-show="step==3">
        <el-card class="box-card" style="width:80%;margin:20px auto">
          <el-table :data="spu.skus" style="width: 100%">
            <el-table-column label="Attri Combination">
              <el-table-column :label="item.name" v-for="(item,index) in dataResp.tableAttrColumn" :key="item.id">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.attr[index].value }}</span>
                </template>
              </el-table-column>
            </el-table-column>
            <el-table-column label="Product Name" prop="name">
              <template slot-scope="scope">
                <el-input v-model="scope.row.name"></el-input>
              </template>
            </el-table-column>
            <el-table-column label="Title" prop="skuTitle">
              <template slot-scope="scope">
                <el-input v-model="scope.row.title"></el-input>
              </template>
            </el-table-column>
            <el-table-column label="SubTitle" prop="skuSubtitle">
              <template slot-scope="scope">
                <el-input v-model="scope.row.subTitle"></el-input>
              </template>
            </el-table-column>
            <el-table-column label="Price" prop="price">
              <template slot-scope="scope">
                <el-input v-model="scope.row.price"></el-input>
              </template>
            </el-table-column>
            <el-table-column type="expand">
              <template slot-scope="scope">
                <el-row>
                  <el-col :span="24">
                    <label style="display:block;float:left">Select Or Add</label>
                    <multi-upload style="float:left;margin-left:10px;" :showFile="false" :listType="'text'" v-model="uploadImages"></multi-upload>
                  </el-col>
                  <el-col :span="24">
                    <el-divider></el-divider>
                  </el-col>
                  <el-col :span="24">
                    <el-card style="width:170px;float:left;margin-left:15px;margin-top:15px;" :body-style="{ padding: '0px' }" v-for="(img,index) in spu.images" :key="index">
                      <img :src="img" style="width:160px;height:120px" />
                      <div style="padding: 14px;">
                        <el-row>
                          <el-col :span="12">
                            <el-checkbox @change='test' v-model="scope.row.images[index].imgUrl" :true-label="img" false-label></el-checkbox>
                          </el-col>
                          <el-col :span="12">
                            <el-tag v-if="scope.row.images[index].defaultImg == 1">
                              <input type="radio" checked :name="scope.row.name" @change="checkDefaultImg(scope.row,index,img)" />Default
                            </el-tag>
                            <el-tag v-else>
                              <input type="radio" :name="scope.row.name" @change="checkDefaultImg(scope.row,index,img)" />Default
                            </el-tag>
                          </el-col>
                        </el-row>
                      </div>
                    </el-card>
                  </el-col>
                </el-row>
                <!-- 折扣，满减，会员价 -->
                <el-form :model="scope.row">
                  <el-row>
                    <el-col :span="24">
                      <el-form-item label="Discount">
                        <label>Reach</label>
                        <el-input-number style="width:160px" :min="0" controls-position="right" v-model="scope.row.fullCount"></el-input-number>
                        <label>Units</label>

                        <label style="margin-left:15px;">Has</label>
                        <el-input-number style="width:160px" v-model="scope.row.discount" :precision="2" :max="1" :min="0" :step="0.01" controls-position="right"></el-input-number>
                        <label>Off</label>
                        <el-checkbox v-model="scope.row.countStatus" :true-label="1" :false-label="0">Multi</el-checkbox>
                      </el-form-item>
                    </el-col>
                    <el-col :span="24">
                      <el-form-item label="Reduce">
                        <label>Reach</label>
                        <el-input-number style="width:160px" v-model="scope.row.fullPrice" :step="100" :min="0" controls-position="right"></el-input-number>
                        <label>Dollors</label>
                        <label style="margin-left:15px;">Reduce</label>
                        <el-input-number style="width:160px" v-model="scope.row.reducePrice" :step="10" :min="0" controls-position="right"></el-input-number>
                        <label>Dollors</label>
                        <el-checkbox v-model="scope.row.priceStatus" :true-label="1" :false-label="0">Multi</el-checkbox>
                      </el-form-item>
                    </el-col>

                    <el-col :span="24">
                      <el-form-item label="Member Price" v-if="scope.row.memberPrice.length>0">
                        <br />
                        <!--   @change="handlePriceChange(scope,mpidx,$event)" -->
                        <el-form-item v-for="(mp,mpidx) in scope.row.memberPrice" :key="mp.id">
                          {{mp.name}}
                          <el-input-number style="width:160px" v-model="scope.row.memberPrice[mpidx].price" :precision="2" :min="0" controls-position="right"></el-input-number>
                        </el-form-item>
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-form>
              </template>
            </el-table-column>
          </el-table>
          <el-button type="primary" @click="step = 2">Last</el-button>
          <el-button type="success" @click="submitSkus">Next</el-button>
        </el-card>
      </el-col>
      <el-col :span="24" v-show="step==4">
        <el-card class="box-card" style="width:80%;margin:20px auto">
          <h1>Success</h1>
          <el-button type="primary" @click="addAgian">Add New</el-button>
          <el-button type="primary" @click="step=5">Finish</el-button>
        </el-card>
      </el-col>
      <el-col :span="24" v-show="step==5">
        <el-card class="box-card" style="width:80%;margin:20px auto">
          <h1>Finish</h1>
          <el-button type="primary">View Product</el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import CategoryCascader from "../../common/category-cascade";
import BrandSelect from "../../common/brand-select";
import MultiUpload from "@/components//multi-upload";

export default {
  //import引入的组件需要注入到对象中才能使用
  components: { CategoryCascader, BrandSelect, MultiUpload },
  props: {},
  data() {
    return {
      categoryIds: [],
      catPathSub: null,
      brandIdSub: null,
      uploadDialogVisible: false,
      uploadImages: [],
      step: 0,
      //spu_name  spu_description  catalog_id  brand_id  weight  publish_status
      spu: {
        //要提交的数据
        spuName: "",
        spuDescription: "",
        categoryId: null,
        brandId: "",
        weight: "",
        publishStatus: 0,
        descriptionImgs: [], //商品详情
        images: [], //商品图集，最后sku也可以新增
        bounds: {
          //积分
          buyBound: 0,
          growBound: 0
        },
        baseAttrs: [], //基本属性
        skus: [] //所有sku信息
      },
      spuBaseInfoRules: {
        spuName: [
          { required: true, message: "请输入商品名字", trigger: "blur" }
        ],
        spuDescription: [
          { required: true, message: "请编写一个简单描述", trigger: "blur" }
        ],
        categoryId: [
          { required: true, message: "请选择一个分类", trigger: "blur" }
        ],
        brandId: [
          { required: true, message: "请选择一个品牌", trigger: "blur" }
        ],
        description: [
          { required: true, message: "请上传商品详情图集", trigger: "blur" }
        ],
        images: [
          { required: true, message: "请上传商品图片集", trigger: "blur" }
        ],
        weight: [
          {
            type: "number",
            required: true,
            message: "请填写正确的重量值",
            trigger: "blur"
          }
        ]
      },
      dataResp: {
        //后台返回的所有数据
        attrGroups: [],
        baseAttrs: [],
        saleAttrs: [],
        tempSaleAttrs: [],
        tableAttrColumn: [],
        memberLevels: [],
        steped: [false, false, false, false, false]
      },
      inputVisible: [],
      inputValue: []
    };
  },
  //计算属性 类似于data概念
  computed: {},
  //监控data中的数据变化
  watch: {
    uploadImages(val) {
      //扩展每个skus里面的imgs选项
      let imgArr = Array.from(new Set(this.spu.images.concat(val)));

      //{imgUrl:"",defaultImg:0} 由于concat每次迭代上次，有很多重复。所以我们必须得到上次+这次的总长

      this.spu.skus.forEach((item, index) => {
        let len = imgArr.length - this.spu.skus[index].images.length; //还差这么多
        if (len > 0) {
          let imgs = new Array(len);
          imgs = imgs.fill({ imgUrl: "", defaultImg: 0 });
          this.spu.skus[index].images = item.images.concat(imgs);
        }
      });

      this.spu.images = imgArr; //去重
      console.log("this.spu.skus", this.spu.skus);
    },
  },

  //方法集合
  methods: {
    test() {
      this.categoryIds = [1]
    },
    addAgian() {
      this.step = 0;
      this.resetSpuData();
    },
    resetSpuData() {
      this.categoryIds = []
      this.spu = {
        spuName: "",
        spuDescription: "",
        categoryId: null,
        brandId: "",
        weight: "",
        publishStatus: 0,
        descriptionImgs: [],
        images: [],
        bounds: {
          buyBound: 0,
          growBound: 0
        },
        baseAttrs: [],
        skus: []
      };
    },
    handlePriceChange(scope, mpidx, e) {
      this.spu.skus[scope.$index].memberPrice[mpidx].price = e;
    },
    // 查询所有会员信息
    getMemberLevels() {
      this.$http({
        url: this.$http.adornUrl("/member/memberlevel/0"),
        method: "get",
        params: this.$http.adornParams({
          pageNum: 1,
          pageSize: 500
        })
      })
        .then(({ data }) => {
          this.dataResp.memberLevels = data.data.content;
        })
        .catch(e => {
          console.log(e);
        });
    },
    showInput(idx) {
      console.log("``````", this.view);
      this.inputVisible[idx].view = true;
      // this.$refs['saveTagInput'+idx].$refs.input.focus();
    },
    checkDefaultImg(row, index, img) {
      console.log("默认图片", row, index);
      //这个图片被选中了，
      row.images[index].imgUrl = img; //默认选中
      row.images[index].defaultImg = 1; //修改标志位;
      //修改其他人的标志位
      row.images.forEach((item, idx) => {
        if (idx != index) {
          row.images[idx].defaultImg = 0;
        }
      });
    },
    handleInputConfirm(idx) {
      let inputValue = this.inputValue[idx].val;
      if (inputValue) {
        // this.dynamicTags.push(inputValue);
        if (this.dataResp.saleAttrs[idx].value == "") {
          this.dataResp.saleAttrs[idx].value = inputValue;
        } else {
          this.dataResp.saleAttrs[idx].value += "," + inputValue;
        }
      }
      this.inputVisible[idx].view = false;
      this.inputValue[idx].val = "";
    },
    collectSpuBaseInfo() {
      //spuBaseForm
      // console.log(this.spu);
      this.$refs.spuBaseForm.validate(valid => {
        if (valid) {
          this.step = 1;
          this.showBaseAttrs();
        } else {
          return false;
        }
      });
    },
    generateSaleAttrs() {
      //把页面绑定的所有attr处理到spu里面,这一步都要做
      this.spu.baseAttrs = [];
      // console.log(this.dataResp.baseAttrs.flat(Infinity));
      this.dataResp.baseAttrs.forEach(item => {
        item.forEach(attr => {
          // console.log("笛卡尔生成组合：" , attr);
          let { id, value, fastDisplay } = attr;
          //跳过没有录入值的属性
          if (value != "") {
            if (value instanceof Array) {
              //多个值用;隔开
              value = value.join(",");
            }
            this.spu.baseAttrs.push({ id, value, fastDisplay });
          }
        });
      });
      /////////////////////////////
      //  or use flat()
      //  this.spu.baseAttrs= this.dataResp.baseAttrs.flat(Infinity)
      this.step = 2;
      this.getShowSaleAttr();
    },
    generateSkus() {
      this.step = 3;

      //根据笛卡尔积运算进行生成sku
      let selectValues = [];
      this.dataResp.tableAttrColumn = [];
      this.dataResp.tempSaleAttrs.forEach(item => {
        if (item.value.length > 0) {
          selectValues.push(item.value);
          this.dataResp.tableAttrColumn.push(item);
        }
      });

      let descartes = this.descartes(selectValues);
      //[["黑色","6GB","移动"],["黑色","6GB","联通"],["黑色","8GB","移动"],["黑色","8GB","联通"],
      //["白色","6GB","移动"],["白色","6GB","联通"],["白色","8GB","移动"],["白色","8GB","联通"],
      //["蓝色","6GB","移动"],["蓝色","6GB","联通"],["蓝色","8GB","移动"],["蓝色","8GB","联通"]]
      // console.log("笛卡尔生成的组合", JSON.stringify(descartes));
      //有多少descartes就有多少sku
      let skus = [];

      descartes.forEach((descar, descaridx) => {
        let attrArray = []; //sku属性组
        descar.forEach((de, index) => {
          //构造saleAttr信息
          let saleAttrItem = {
            id: this.dataResp.tableAttrColumn[index].id,
            name: this.dataResp.tableAttrColumn[index].name,
            value: de
          };
          attrArray.push(saleAttrItem);
        });
        //先初始化几个images，后面的上传还要加
        let imgs = [];
        console.log(this.spu.images);
        this.spu.images.forEach((img, idx) => {
          imgs.push({ imgUrl: "", defaultImg: 0 });
        });
        //会员价，也必须在循环里面生成，否则会导致数据绑定问题
        let memberPrices = [];
        if (this.dataResp.memberLevels.length > 0) {
          for (let i = 0; i < this.dataResp.memberLevels.length; i++) {
            console.log(this.dataResp.memberLevels[i]);
            if (this.dataResp.memberLevels[i].privilegeMemberPrice == 1) {
              memberPrices.push({
                id: this.dataResp.memberLevels[i].id,
                name: this.dataResp.memberLevels[i].name,
                price: 0
              });
            }
          }
        }
        // console.log(memberPrices);
        //;descaridx，判断如果之前有就用之前的值;
        let res = this.hasAndReturnSku(this.spu.skus, descar);
        if (res === null) {
          skus.push({
            attr: attrArray,
            name: this.spu.spuName + " " + descar.join(" "),
            price: 0,
            title: this.spu.spuName + " " + descar.join(" "),
            subTitle: "",
            images: imgs,
            descar: descar,
            fullCount: 0,
            discount: 0,
            countStatus: 0,
            fullPrice: 0.0,
            reducePrice: 0.0,
            priceStatus: 0,
            memberPrice: new Array().concat(memberPrices)
          });
          console.log(skus);
        } else {
          skus.push(res);
        }
      });
      this.spu.skus = skus;
      // console.log("结果!!!", this.spu.skus, this.dataResp.tableAttrColumn);
    },
    //判断如果包含之前的sku的descar组合，就返回这个sku的详细信息；
    hasAndReturnSku(skus, descar) {
      let res = null;
      if (skus.length > 0) {
        for (let i = 0; i < skus.length; i++) {
          if (skus[i].descar.join(" ") == descar.join(" ")) {
            res = skus[i];
          }
        }
      }
      return res;
    },
    getShowSaleAttr() {
      //获取当前分类可以使用的销售属性
      this.dataResp.tempSaleAttrs = []
      this.dataResp.saleAttrs = []
      if (/* !this.dataResp.steped[1] */true) {
        this.$http({
          url: this.$http.adornUrl(
            `/product/attr/attribute/${this.spu.categoryId}`
          ),
          method: "get",
          params: this.$http.adornParams({
            pageNum: 1,
            pageSize: 500,
            type: '0'
          })
        }).then(({ data }) => {
          this.dataResp.saleAttrs = data.data.content;
          data.data.content.forEach(item => {
            this.dataResp.tempSaleAttrs.push({
              id: item.id,
              value: [],
              name: item.name
            });
            this.inputVisible.push({ view: false });
            this.inputValue.push({ val: "" });
          });
          this.dataResp.steped[1] = true;
        });
      }
    },
    showBaseAttrs() {
      this.dataResp.baseAttrs = []
      this.dataResp.attrGroups = []
      if (!this.dataResp.steped[0]) {
        this.$http({
          url: this.$http.adornUrl(
            `/product/attr/attrgroup-with-attr/${this.spu.categoryId}/`
          ),
          method: "get",
          params: this.$http.adornParams({})
        })
          .then(({ data }) => {
            //先对表单的baseAttrs进行初始化
            // console.log(data);
            data.data.forEach(item => {
              // 输出基本信息
              let attrArray = [];
              // 显示基本属性
              if (item.attributeList != null && item.attributeList.length > 0) {
                item.attributeList.forEach(attr => {
                  attrArray.push({
                    id: attr.id,
                    value: "",
                    fastDisplay: attr.fastDisplay
                  });
                });
              }
              this.dataResp.baseAttrs.push(attrArray);
            });
            this.dataResp.steped[0] = 0;
            this.dataResp.attrGroups = data.data;
          })
          .catch(e => {
            console.log(e);
          });
      }
    },

    submitSkus() {
      console.log("~~~~~", JSON.stringify(this.spu));
      this.$confirm("Submit Product? Will take a few moment", "Warning:", {
        confirmButtonText: "Yes",
        cancelButtonText: "Cancel",
        type: "warning"
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/product"),
            method: "post",
            data: this.$http.adornData({
              name: this.spu.spuName,
              description: this.spu.spuDescription,
              categoryId: this.spu.categoryId,
              brandId: this.spu.brandId,
              weight: this.spu.weight,
              publishStatus: this.spu.publishStatus,
              productDescImgs: this.spu.descriptionImgs,
              productImg: this.spu.images,
              productBound: this.spu.bounds,
              product_attributes: this.spu.baseAttrs,
              productCombVOList: this.spu.skus
            }, false)
          }).then(({ data }) => {
            console.log(data);
            if (data.code == 0) {
              this.$message({
                type: "success",
                message: "Success!"
              });
              this.step = 4;
            } else {
              this.$message({
                type: "error",
                message: "Fail: " + data.msg + "】"
              });
            }
          });
        })
        .catch(e => {
          // console.log("已取消");
          this.$message({
            type: "info",
            message: "Cancel "
          });
        });
    },
    //笛卡尔积运算
    descartes(list) {
      //parent上一级索引;count指针计数
      // console.log(list);
      var point = {};

      var result = [];
      var pIndex = null;
      var tempCount = 0;
      var temp = [];
      //根据参数列生成指针对象
      for (var index in list) {
        if (typeof list[index] == "object") {
          point[index] = { parent: pIndex, count: 0 };
          pIndex = index;
        }
      }
      //单维度数据结构直接返回
      if (pIndex == null) {
        return list;
      }

      //动态生成笛卡尔积
      while (true) {
        for (var index in list) {
          tempCount = point[index]["count"];
          temp.push(list[index][tempCount]);
        }

        //压入结果数组
        result.push(temp);
        temp = [];
        //检查指针最大值问题
        while (true) {
          if (point[index]["count"] + 1 >= list[index].length) {
            point[index]["count"] = 0;
            pIndex = point[index]["parent"];
            if (pIndex == null) {
              return result;
            }

            //赋值parent进行再次检查
            index = pIndex;
          } else {
            point[index]["count"]++;
            break;
          }
        }
      }
    }
  },
  //生命周期 - 创建完成（可以访问当前this实例）
  created() { },
  //生命周期 - 挂载完成（可以访问DOM元素）
  mounted() {
    // 当用户点击三级分类id时 准备给品牌查询
    this.catPathSub = PubSub.subscribe("cateChange", (msg, val) => {
      this.spu.categoryId = val[val.length - 1];
    });
    this.brandIdSub = PubSub.subscribe("brandIdChange", (msg, val) => {
      this.spu.brandId = val;
    });
    this.getMemberLevels();
  },
  beforeCreate() { }, //生命周期 - 创建之前
  beforeMount() { }, //生命周期 - 挂载之前
  beforeUpdate() { }, //生命周期 - 更新之前
  updated() { }, //生命周期 - 更新之后
  beforeDestroy() {
    PubSub.unsubscribe(this.catPathSub);
    PubSub.unsubscribe(this.brandIdSub);
  }, //生命周期 - 销毁之前
  destroyed() { }, //生命周期 - 销毁完成
  activated() { } //如果页面有keep-alive缓存功能，这个函数会触发
};
</script>
<style scoped>
</style>