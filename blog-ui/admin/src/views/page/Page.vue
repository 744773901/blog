<template>
  <el-card class="main-card">
    <div class="title">{{ $route.name }}</div>
    <div class="operation-container">
      <el-button @click="openDialog(null)" type="primary" size="small" icon="el-icon-plus">新建页面</el-button>
    </div>
    <!-- 相册列表 -->
    <el-row class="page-container" :gutter="12" v-loading="loading">
      <!-- 空状态 -->
      <el-empty v-if="pageList.length === 0" description="暂无页面" />
      <el-col v-for="item of pageList" :key="item.id" :md="6">
        <div class="page-item">
          <!-- 相册操作 -->
          <div class="page-operation">
            <el-dropdown @command="handleCommand">
              <i class="el-icon-more" style="color:#fff" />
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item :command="'update' + JSON.stringify(item)">
                  <i class="el-icon-edit" />编辑
                </el-dropdown-item>
                <el-dropdown-item :command="'delete' + item.id">
                  <i class="el-icon-delete" />删除
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
          <el-image fit="cover" class="page-cover" :src="item.pageCover" />
          <div class="page-name">{{ item.pageName }}</div>
        </div>
      </el-col>
    </el-row>
    <!-- 新增或修改页面对话框 -->
    <el-dialog :visible.sync="addOrEdit" width="35%" top="10vh">
      <div class="dialog-title-container" slot="title" ref="pageTitle" />
      <el-form label-width="80px" size="medium" :model="pageForm">
        <el-form-item label="页面名称">
          <el-input style="width:220px" v-model="pageForm.pageName" />
        </el-form-item>
        <el-form-item label="页面标签">
          <el-input style="width:220px" v-model="pageForm.pageLabel" />
        </el-form-item>
        <el-form-item label="页面封面">
          <el-upload
              drag
              multiple
              action="/api/admin/config/images"
              :before-upload="beforeUpload"
              :on-success="uploadCover"
              :show-file-list="false"
              :headers="token"
              class="upload-cover"
          >
            <i class="el-icon-upload" v-if="pageForm.pageCover === ''" />
            <div class="el-upload__text" v-if="pageForm.pageCover === ''">将文件拖到此处，或<em>点击上传</em></div>
            <img v-else :src="pageForm.pageCover" width="360px" height="180px"/>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="addOrEdit = false">取 消</el-button>
        <el-button type="primary" @click="addOrEditPage">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 删除对话框 -->
    <el-dialog :visible.sync="isDelete" width="30%">
      <div class="dialog-title-container" slot="title">
        <i class="el-icon-warning" style="color:#ff9900" />提示
      </div>
      <div style="font-size:1rem">是否删除该页面？</div>
      <div slot="footer">
        <el-button @click="isDelete = false">取 消</el-button>
        <el-button type="primary" @click="deletePage">确 定</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
import * as imageConversion from "image-conversion";
import {deletePageById, getPageList, saveOrUpdatePage} from "@/api/page";
export default {
  data() {
    return {
      loading: true,
      isDelete: false,
      addOrEdit: false,
      pageForm: {
        id: null,
        pageName: "",
        pageLabel: "",
        pageCover: ""
      },
      pageList: [],
      current: 1,
      size: 8,
      count: 0,
      token: {Authorization: this.$store.getters.token}
    }
  },
  created() {
    this.listPages();
  },
  methods: {
    handleCommand(command) {
      let type = command.substring(0, 6)
      let data = command.substring(6)
      switch (type) {
        case 'update':
          this.openDialog(data)
          break;
        case 'delete':
          this.pageForm.id = data
          this.isDelete = true
          break;
      }
    },
    openDialog(data) {
      if (data) {
        this.pageForm = JSON.parse(data);
        this.$refs.pageTitle.innerHTML = "修改页面";
      } else {
        this.pageForm = {
          id: null,
          pageName: "",
          pageLabel: "",
          pageCover: ""
        }
        this.$refs.pageTitle.innerHTML = "新建页面";
      }
      this.addOrEdit = true;
    },
    uploadCover(res) {
      this.pageForm.pageCover = res.data
    },
    beforeUpload(file) {
      return new Promise(resolve => {
        if (file.size / 1024 < 200) {
          resolve(file)
        }
        imageConversion.compressAccurately(file, 200).then(res => resolve(res))
      });
    },
    listPages() {
      getPageList().then(({data}) => {
        this.pageList = data.data
        this.loading = false
      });
    },
    addOrEditPage() {
      if (this.pageForm.pageName.trim() === "") {
        this.$message.error("页面名称不能为空");
        return false;
      }
      if (this.pageForm.pageLabel.trim() === "") {
        this.$message.error("页面标签不能为空");
        return false;
      }
      if (this.pageForm.pageCover === "") {
        this.$message.error("页面封面不能为空");
        return false;
      }
      saveOrUpdatePage(this.pageForm).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listPages();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.addOrEdit = false
      });
    },
    deletePage() {
      deletePageById(this.pageForm.id).then(({ data }) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listPages();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.isDelete = false;
      });
    },
  }
}
</script>

<style scoped>
.page-cover {
  position: relative;
  border-radius: 4px;
  width: 100%;
  height: 170px;
}

.page-name {
  text-align: center;
  margin-top: 0.5rem;
}

.page-item {
  position: relative;
  cursor: pointer;
  margin-bottom: 1rem;
}

.page-operation {
  position: absolute;
  z-index: 1000;
  top: 0.5rem;
  right: 0.8rem;
}
</style>
