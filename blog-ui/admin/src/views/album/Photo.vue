<template>
  <el-card class="main-card">
    <div class="title">{{$route.name}}</div>
    <div class="album-info">
      <el-image :src="albumInfo.albumCover" fit="cover" class="album-cover"/>
      <!-- 相册信息 -->
      <div class="album-detail">
        <div style="margin-bottom:0.6rem">
          <span class="album-name">{{ albumInfo.albumName }}</span>
          <span class="photo-count">{{ albumInfo.photoCount }}张</span>
        </div>
        <div>
          <span v-if="albumInfo.albumDesc" class="album-desc">{{ albumInfo.albumDesc }}</span>
          <el-button @click="uploadPhoto = true" icon="el-icon-picture" type="primary" size="small">上传照片</el-button>
        </div>
      </div>
      <!-- 相册操作 -->
      <div class="operation">
        <div class="all-check">
          <el-checkbox @change="handleCheckAllChange" v-model="checkAll" :indeterminate="isIndeterminate">全选</el-checkbox>
          <div class="check-count">已选择{{ selectPhotoIdList.length }}张</div>
        </div>
        <el-button @click="movePhoto = true" :disabled="selectPhotoIdList.length === 0" type="success" size="small" icon="el-icon-right">移动到</el-button>
        <el-button @click="batchDeletePhoto = true" :disabled="selectPhotoIdList.length === 0" type="danger" size="small" icon="el-icon-delete">批量删除</el-button>
      </div>
    </div>
    <!-- 照片列表 -->
    <el-row class="photo-container" :gutter="10" v-loading="loading">
      <!-- 空状态 -->
      <el-empty v-if="photoList.length === 0" description="暂无照片" />
      <el-checkbox-group v-model="selectPhotoIdList" @change="handleCheckedPhotoChange">
        <el-col :md="4" v-for="item of photoList" :key="item.id">
          <el-checkbox :label="item.id">
            <div class="photo-item">
              <!-- 照片操作 -->
              <div class="photo-operation">
                <el-dropdown @command="handleCommand">
                  <i class="el-icon-more" style="color:#fff" />
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item :command="JSON.stringify(item)">
                      <i class="el-icon-edit" />编辑
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
              <el-image :src="item.photoSrc" :preview-photoSrc-list="photoList" fit="cover" class="photo-img"/>
              <div class="photo-name">{{ item.photoName }}</div>
            </div>
          </el-checkbox>
        </el-col>
      </el-checkbox-group>
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
        layout="prev, pager, next"
    />
    <!-- 上传对话框 -->
    <el-dialog :visible.sync="uploadPhoto" width="70%" top="10vh">
      <div class="dialog-title-container" slot="title">上传照片</div>
      <!-- 上传 -->
      <div class="upload-container">
        <el-upload
            multiple
            v-show="uploadList.length > 0"
            action="/api/admin/photo/album/cover"
            list-type="picture-card"
            :file-list="uploadList"
            :before-upload="beforeUpload"
            :on-success="upload"
            :on-remove="handleRemove"
            :headers="token">
          <i class="el-icon-plus"/>
        </el-upload>
        <div class="upload">
          <el-upload
              drag
              multiple
              v-show="uploadList.length === 0"
              action="/api/admin/photo/album/cover"
              :before-upload="beforeUpload"
              :on-success="upload"
              :show-file-list="false"
              :headers="token">
            <i class="el-icon-upload"/>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <div class="el-upload__tip" slot="tip">支持上传jpg/png文件</div>
          </el-upload>
        </div>
      </div>
      <div slot="footer">
        <div class="upload-footer">
          <div class="upload-count">共上传{{ uploadList.length }}张照片</div>
          <div style="margin-left:auto">
            <el-button @click="uploadPhoto = false">取 消</el-button>
            <el-button @click="savePhotos" :disabled="uploadList.length === 0" type="primary">开始上传</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
    <!-- 编辑对话框 -->
    <el-dialog :visible.sync="editPhoto" width="30%">
      <div class="dialog-title-container" slot="title">修改信息</div>
      <el-form label-width="80px" size="medium" :model="photoForm">
        <el-form-item label="照片名称">
          <el-input style="width:220px" v-model="photoForm.photoName" />
        </el-form-item>
        <el-form-item label="照片描述">
          <el-input style="width:220px" v-model="photoForm.photoDesc" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="editPhoto = false">取 消</el-button>
        <el-button type="primary" @click="updatePhoto">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 移动对话框 -->
    <el-dialog :visible.sync="movePhoto" width="30%">
      <div class="dialog-title-container" slot="title">移动图片</div>
      <el-empty v-if="albumList.length < 2" description="暂无其他相册" />
      <el-form v-else :model="photoForm" label-width="80px" size="medium">
        <el-radio-group v-model="albumId">
          <div class="album-check-item">
            <template v-for="item of albumList">
              <el-radio v-if="item.id !== albumInfo.id" :label="item.id" :key="item.id" style="margin-bottom:1rem">
                <div class="album-check">
                  <el-image :src="item.albumCover" fit="cover" class="album-check-cover"/>
                  <div style="margin-left:0.5rem">{{ item.albumName }}</div>
                </div>
              </el-radio>
            </template>
          </div>
        </el-radio-group>
      </el-form>
      <div slot="footer">
        <el-button @click="movePhoto = false">取 消</el-button>
        <el-button @click="updatePhotoAlbum" :disabled="albumId == null" type="primary">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 批量删除对话框 -->
    <el-dialog :visible.sync="batchDeletePhoto" width="30%">
      <div class="dialog-title-container" slot="title">
        <i class="el-icon-warning" style="color:#ff9900" />提示
      </div>
      <div style="font-size:1rem">是否删除选中照片？</div>
      <div slot="footer">
        <el-button @click="batchDeletePhoto = false">取 消</el-button>
        <el-button type="primary" @click="updatePhotoDelete(null)">确 定</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
import {
  getAllPhotoAlbumInfo,
  getPhotoAlbumById,
  getPhotoList, movePhotos,
  savePhotos,
  updatePhotos,
  updatePhotosIsDelete
} from "@/api/album";
import * as imageConversion from "image-conversion";

export default {
  data() {
    return {
      loading: true,
      checkAll: false,
      isIndeterminate: false,
      uploadPhoto: false,
      editPhoto: false,
      movePhoto: false,
      batchDeletePhoto: false,
      uploadList: [],
      photoList: [],
      photoIdList: [],
      selectPhotoIdList: [],
      albumList: [],
      albumId: null,
      albumInfo: {
        id: null,
        albumName: "",
        albumDesc: "",
        albumCover: "",
        photoCount: 0
      },
      photoForm: {
        id: null,
        photoName: "",
        photoDesc: ""
      },
      current: 1,
      size: 18,
      count: 0,
      token: {Authorization: this.$store.getters.token}
    }
  },
  created() {
    this.listPhotos();
    this.listAlbums();
    this.getAlbumInfo();
  },
  watch: {
    photoList() {
      this.photoIdList = []
      this.photoList.forEach(item => this.photoIdList.push(item.id))
    },
  },
  methods: {
    handleCheckAllChange(val) {
      this.selectPhotoIdList = val ? this.photoIdList : []
      this.isIndeterminate = false
    },
    handleCheckedPhotoChange(value) {
      let checkedCount = value.length;
      this.checkAll = checkedCount === this.photoIdList.length;
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.photoIdList.length;
    },
    handleCommand(command) {
      this.photoForm = JSON.parse(command);
      this.editPhoto = true;
    },
    upload(res) {
      this.uploadList.push({url: res.data})
    },
    beforeUpload(file) {
      return new Promise(resolve => {
        if (file.size / 1024 < 200) {
          resolve(file)
        }
        imageConversion.compressAccurately(file, 200).then(res => resolve(res))
      })
    },
    handleRemove(file) {
      this.uploadList.forEach((item, index) => {
        if (item.url === file.url) {
          this.uploadList.splice(index, 1)
        }
      });
    },
    listPhotos() {
      getPhotoList({
        albumId: this.$route.params.albumId,
        isDelete: 0,
        current: this.current,
        size: this.size
      }).then(({data}) => {
        this.photoList = data.data.records
        this.count = data.data.count
        this.loading = false
      });
    },
    listAlbums() {
      getAllPhotoAlbumInfo().then(({data}) => {
        this.albumList = data.data
      });
    },
    getAlbumInfo() {
      getPhotoAlbumById(this.$route.params.albumId).then(({data}) => {
        this.albumInfo = data.data
      });
    },
    savePhotos() {
      let photoUrlList = []
      this.uploadList.forEach(item => photoUrlList.push(item.url))
      savePhotos({
        albumId: this.$route.params.albumId,
        photoUrlList
      }).then(({ data }) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.uploadList = [];
          this.listPhotos();
          this.getAlbumInfo();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
      });
      this.uploadPhoto = false;
    },
    updatePhoto() {
      if (this.photoForm.photoName.trim() === "") {
        this.$message.error("照片名称不能为空");
        return false;
      }
      updatePhotos(this.photoForm).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listPhotos();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.editPhoto = false;
      });
    },
    updatePhotoDelete() {
      updatePhotosIsDelete({
        ids: this.selectPhotoIdList,
        isDelete: 1
      }).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listPhotos();
          this.getAlbumInfo();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.batchDeletePhoto = false;
      });
    },
    updatePhotoAlbum() {
      movePhotos({
        albumId: this.albumId,
        photoIdList: this.selectPhotoIdList
      }).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listPhotos();
          this.getAlbumInfo();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.movePhoto = false;
      });
    },
    sizeChange(size) {
      this.size = size;
      this.listPhotos();
    },
    currentChange(current) {
      this.current = current;
      this.listPhotos();
    },
  }
}
</script>

<style scoped>
.album-info {
  display: flex;
  margin-top: 2.25rem;
  margin-bottom: 2rem;
}

.album-cover {
  border-radius: 4px;
  width: 5rem;
  height: 5rem;
}

.album-check-cover {
  border-radius: 4px;
  width: 4rem;
  height: 4rem;
}

.album-detail {
  padding-top: 0.4rem;
  margin-left: 0.8rem;
}

.album-desc {
  font-size: 14px;
  margin-right: 0.8rem;
}

.operation {
  padding-top: 1.5rem;
  margin-left: auto;
}

.all-check {
  display: inline-flex;
  align-items: center;
  margin-right: 1rem;
}

.check-count {
  margin-left: 1rem;
  font-size: 12px;
}

.album-name {
  font-size: 1.25rem;
}

.photo-count {
  font-size: 12px;
  margin-left: 0.5rem;
}

.photo-item {
  width: 100%;
  position: relative;
  cursor: pointer;
  margin-bottom: 1rem;
}

.photo-img {
  width: 100%;
  height: 7rem;
  border-radius: 4px;
}

.photo-name {
  font-size: 14px;
  margin-top: 0.3rem;
  text-align: center;
}

.upload-container {
  height: 400px;
}

.upload {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-footer {
  display: flex;
  align-items: center;
}

.photo-operation {
  position: absolute;
  z-index: 1000;
  top: 0.3rem;
  right: 0.5rem;
}

.album-check {
  display: flex;
  align-items: center;
}
</style>
