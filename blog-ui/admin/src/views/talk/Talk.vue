<template>
  <el-card class="main-card">
    <div class="title">{{ $route.name }}</div>
    <div class="talk-container">
      <!-- 编辑框 -->
      <Editor ref="editor" v-model="talk.content" class="editor-wrapper" placeholder="说点什么吧"/>
      <!-- 操作菜单 -->
      <div class="operation-wrapper">
        <div class="left-wrapper">
          <!-- 表情 -->
          <el-popover placement="bottom-start" width="460" trigger="click">
            <span @click="addEmoji(key, value)" v-for="(value, key, index) of emojiList" :key="index"
                  class="emoji-item">
              <img :src="value" :title="key" class="emoji" width="24" height="24"/>
            </span>
            <svg-icon icon-class="emoji" slot="reference"/>
          </el-popover>
          <!-- 图片上传 -->
          <el-upload
              action="/api/admin/talk/images"
              multiple
              :headers="token"
              :before-upload="beforeUpload"
              :on-success="upload"
              :show-file-list="false">
            <svg-icon icon-class="image"/>
          </el-upload>
        </div>
        <div>
          <!-- 是否置顶 -->
          <el-switch v-model="talk.isTop" inactive-text="置顶" :active-value="1" :inactive-value="0"
                     style="margin-right:16px"/>
          <!-- 说说状态 -->
          <el-dropdown @command="handleCommand" trigger="click" style="margin-right:16px">
            <span class="talk-status">
              {{ dropdownTitle }}<i class="el-icon-arrow-down el-icon--right"/>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item v-for="(item, index) of statusList" :key="index" :command="item.status">
                {{ item.desc }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <el-button @click="saveOrUpdateTalk" :disabled="talk.content === ''" type="primary" size="small">发布
          </el-button>
        </div>
      </div>
      <!-- 图片上传 -->
      <el-upload
          multiple
          :headers="token"
          v-show="uploadList.length > 0"
          class="talk-image-upload"
          action="/api/admin/talk/images"
          list-type="picture-card"
          :file-list="uploadList"
          :before-upload="beforeUpload"
          :on-success="upload"
          :on-remove="handleRemove">
        <i class="el-icon-plus"/>
      </el-upload>
    </div>
  </el-card>
</template>

<script>
import Editor from "@/components/Editor";
import Emojis from "@/assets/js/emoji";
import * as imageConversion from 'image-conversion'
import {getAdminTalkById, saveOrUpdateAdminTalk} from "@/api/talk";

export default {
  components: {Editor},
  data() {
    return {
      emojiList: Emojis,
      talk: {
        id: null,
        content: "",
        isTop: 0,
        status: 1,
        images: null
      },
      statusList: [
        {status: 1, desc: "公开"},
        {status: 2, desc: "私密"}
      ],
      uploadList: [],
      token: {Authorization: this.$store.getters.token}
    }
  },
  created() {
    if (this.$route.params.talkId) {
      getAdminTalkById(this.$route.params.talkId).then(({data}) => {
        this.talk = data.data
        if (data.data.imageList) {
          data.data.imageList.forEach(item => this.uploadList.push({url: item}))
        }
      });
    }
  },
  methods: {
    handleCommand(command) {
      this.talk.status = command;
    },
    addEmoji(key, value) {
      this.$refs
          .editor
          .addText(`<img src="${value}" alt="${key}" width="24" height="24" style="margin: 0 1px;vertical-align: text-bottom"/>`)
    },
    handleRemove(file) {
      this.uploadList.forEach((item, index) => {
        if (item.url === file.url) {
          this.uploadList.splice(index, 1)
        }
      });
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
      });
    },
    saveOrUpdateTalk() {
      if (this.talk.content.trim() === "") {
        this.$message.error("说说内容不能为空");
        return false;
      }
      if (this.uploadList.length > 0) {
        let imageList = []
        this.uploadList.forEach(item => imageList.push(item.url))
        this.talk.images = JSON.stringify(imageList)
      }
      saveOrUpdateAdminTalk(this.talk).then(({data}) => {
        if (data.flag) {
          this.$refs.editor.clear();
          this.uploadList = [];
          this.$notify.success({
            title: "成功",
            message: data.message
          });
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
      });
    },
  },
  computed: {
    dropdownTitle() {
      let desc = "";
      this.statusList.forEach(item => {
        if (item.status === this.talk.status) {
          desc = item.desc;
        }
      });
      return desc;
    }
  }
}
</script>

<style scoped>
.talk-container {
  margin-top: 40px;
}

.editor-wrapper {
  min-height: 150px;
}

.operation-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
}

.operation-btn {
  cursor: pointer;
  color: #838383;
  font-size: 20px;
  margin-right: 12px;
}

.talk-status {
  cursor: pointer;
  font-size: 12px;
  color: #999;
}

.emoji {
  user-select: none;
  margin: 0.25rem;
  display: inline-block;
  vertical-align: middle;
}

.emoji-item {
  cursor: pointer;
  display: inline-block;
}

.emoji-item:hover {
  transition: all 0.2s;
  border-radius: 0.25rem;
  background: #dddddd;
}

.left-wrapper {
  display: flex;
  width: 50%;
}

.talk-image-upload {
  margin-top: 8px;
}

.svg-icon {
  width: 1.5em;
  height: 1.5em;
  cursor: pointer;
  color: #838383;
  margin-right: 12px;
}
</style>
