<template>
  <el-card class="main-card">
    <div class="title">{{$route.name}}</div>
    <div class="operation-container">
      <el-button @click="openDialog(null)" type="primary" size="small" icon="el-icon-plus">新建相册</el-button>
      <div style="margin-left: auto">
        <el-button @click="gotoRecycleBin" type="text" size="small" icon="el-icon-delete" style="margin-right:1rem">回收站</el-button>
        <el-input clearable @clear="listAlbums" @keyup.enter.native="searchAlbums" v-model="keyword" prefix-icon="el-icon-search" size="small" placeholder="请输入相册名" style="width:200px"/>
        <el-button @click="searchAlbums" type="primary" size="small" icon="el-icon-search" style="margin-left:1rem">搜索</el-button>
      </div>
    </div>
    <!-- 相册列表 -->
    <el-row :gutter="12" v-loading="loading">
      <el-empty v-if="albumList == null" description="暂无相册"/>
      <el-col v-for="item of albumList" :key="item.id" :md="6">
        <div class="album-item" @click.stop.prevent="gotoPhotoAlbum(item)">
          <!-- 相册操作 -->
          <div class="album-operation">
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
          <div class="album-photo-count">
            <div>{{ item.photoCount }}</div>
            <svg-icon v-if="item.status === 2" icon-class="password"/>
          </div>
          <el-image fit="cover" class="album-cover" :src="item.albumCover" />
          <div class="album-name">{{ item.albumName }}</div>
        </div>
      </el-col>
    </el-row>
    <!-- 分页 -->
    <el-pagination
        :hide-on-single-page="true"
        class="pagination-container"
        @size-change="sizeChange"
        @current-change="currentChange"
        :current-page="current"
        :page-size="size"
        :total="count"
        layout="prev, pager, next"/>
    <!-- 新增或修改相册对话框 -->
    <el-dialog :visible.sync="addOrEdit" width="35%" top="10vh">
      <div class="dialog-title-container" slot="title" ref="albumTitle" />
      <el-form label-width="80px" size="medium" :model="albumForm">
        <el-form-item label="相册名称">
          <el-input style="width:220px" v-model="albumForm.albumName" />
        </el-form-item>
        <el-form-item label="相册描述">
          <el-input style="width:220px" v-model="albumForm.albumDesc" />
        </el-form-item>
        <el-form-item label="相册封面">
          <el-upload
              drag
              multiple
              class="upload-cover"
              :show-file-list="false"
              :before-upload="beforeUpload"
              :on-success="uploadCover"
              action="/api/admin/photo/album/cover"
              :headers="token"
          >
            <i class="el-icon-upload" v-if="albumForm.albumCover === ''" />
            <div class="el-upload__text" v-if="albumForm.albumCover === ''">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <img
                v-else
                :src="albumForm.albumCover"
                width="360px"
                height="180px"
            />
          </el-upload>
        </el-form-item>
        <el-form-item label="发布形式">
          <el-radio-group v-model="albumForm.status">
            <el-radio :label="1">公开</el-radio>
            <el-radio :label="2">私密</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="addOrEdit = false">取 消</el-button>
        <el-button type="primary" @click="addOrEditAlbum">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 删除对话框 -->
    <el-dialog :visible.sync="isDelete" width="30%">
      <div class="dialog-title-container" slot="title">
        <i class="el-icon-warning" style="color:#ff9900" />提示
      </div>
      <div style="font-size:1rem">是否删除该相册？</div>
      <div slot="footer">
        <el-button @click="isDelete = false">取 消</el-button>
        <el-button type="primary" @click="deleteAlbum">确 定</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
import {deletePhotoAlbum, getPhotoAlbumList, saveOrUpdatePhotoAlbum} from "@/api/album";
import * as imageConversion from 'image-conversion'

export default {
  name: 'Album',
  data() {
    return {
      loading: true,
      isDelete: false,
      addOrEdit: false,
      albumForm: {
        id: null,
        albumName: "",
        albumDesc: "",
        albumCover: "",
        status: 1
      },
      albumList: [],
      keyword: null,
      current: 1,
      size: 8,
      count: 0,
      token: {Authorization: this.$store.getters.token}
    }
  },
  created() {
    this.listAlbums()
  },
  methods: {
    openDialog(data) {
      if (data) {
        this.albumForm = JSON.parse(data);
        this.$refs.albumTitle.innerHTML = "修改相册";
      } else {
        this.albumForm = {
          id: null,
          albumName: "",
          albumDesc: "",
          albumCover: "",
          status: 1
        }
        this.$refs.albumTitle.innerHTML = "新增相册";
      }
      this.addOrEdit = true;
    },
    gotoRecycleBin() {
      this.$router.push({path: '/photos/delete'})
    },
    searchAlbums() {
      this.current = 1
      this.listAlbums()
    },
    gotoPhotoAlbum(item) {
      this.$router.push({path: '/albums/' + item.id})
    },
    handleCommand(command) {
      let type = command.substring(0, 6)
      let data = command.substring(6)
      switch (type) {
        case 'update':
          this.openDialog(data);
          break;
        case 'delete':
          this.albumForm.id = data
          this.isDelete = true;
          break;
      }
    },
    uploadCover(res) {
      this.albumForm.albumCover = res.data
    },
    beforeUpload(file) {
      return new Promise(resolve => {
        if (file.size / 1024 < 200) {
          resolve(file)
        }
        imageConversion.compressAccurately(file, 200).then(res => resolve(res))
      })
    },
    listAlbums() {
      getPhotoAlbumList({
        keyword: this.keyword,
        current: this.current,
        size: this.size
      }).then(({data}) => {
        this.albumList = data.data.records
        this.count = data.data.count
        this.loading = false
      });
    },
    addOrEditAlbum() {
      if (this.albumForm.albumName.trim() === "") {
        this.$message.error("相册名称不能为空");
        return false;
      }
      if (this.albumForm.albumDesc.trim() === "") {
        this.$message.error("相册描述不能为空");
        return false;
      }
      if (this.albumForm.albumCover === "") {
        this.$message.error("相册封面不能为空");
        return false;
      }
      saveOrUpdatePhotoAlbum(this.albumForm).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listAlbums();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.addOrEdit = false
      });
    },
    deleteAlbum() {
      deletePhotoAlbum(this.albumForm.id).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listAlbums();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.isDelete = false
      });
    },
    sizeChange(size) {
      this.current = size
      this.listAlbums()
    },
    currentChange(current) {
      this.current = current
      this.listAlbums()
    },
  },

}
</script>

<style scoped>
.album-cover {
  position: relative;
  border-radius: 4px;
  width: 100%;
  height: 170px;
}

.album-cover::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
}

.album-photo-count {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 1.5rem;
  z-index: 1000;
  position: absolute;
  left: 0;
  right: 0;
  padding: 0 0.5rem;
  bottom: 2.6rem;
  color: #fff;
}

.album-name {
  text-align: center;
  margin-top: 0.5rem;
}

.album-item {
  position: relative;
  cursor: pointer;
  margin-bottom: 1rem;
}

.album-operation {
  position: absolute;
  z-index: 1000;
  top: 0.5rem;
  right: 0.8rem;
}
</style>
