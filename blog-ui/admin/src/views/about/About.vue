<template>
  <el-card class="main-card">
    <div class="title">{{ $route.name }}</div>
    <mavon-editor v-model="aboutContent" @imgAdd="uploadImage" ref="md" style="height:calc(100vh - 250px);margin-top:2.25rem"/>
    <el-button @click="updateAbout" type="danger" size="medium" style="float: right;margin: 1rem 0;">修改</el-button>
  </el-card>
</template>

<script>

import {uploadArticleImage} from "@/api/article";
import * as imageConversion from "image-conversion";
import {getAboutContent, updateAboutContent} from "@/api/about";

export default {
  data() {
    return {
      aboutContent: ""
    }
  },
  created() {
    this.getAbout()
  },
  methods: {
    getAbout() {
      getAboutContent().then(({data}) => {
        this.aboutContent = data.data
      });
    },
    updateAbout() {
      updateAboutContent({aboutContent: this.aboutContent}).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          // this.getAbout();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
      });
    },
    uploadImage(pos, file) {
      let formData = new FormData()
      if (file.size / 1024 < 200) {
        formData.append("file", file);
        uploadArticleImage(formData).then(({data}) => {
          this.$refs.md.$img2Url(pos, data.data)
        });
      } else {
        imageConversion.compressAccurately(file, 200).then(res => {
          formData.append("file", new window.File([res], file.name, {type: file.type}))
          uploadArticleImage(formData).then(({data}) => {
            this.$refs.md.$img2Url(pos, data.data)
          });
        });
      }
    }
  }
}
</script>
