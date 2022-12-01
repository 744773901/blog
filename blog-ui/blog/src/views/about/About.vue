<template>
  <div>
    <!--banner-->
    <div class="banner" :style="cover">
      <h1 class="banner-title">关于我</h1>
    </div>
    <!--关于我内容-->
    <v-card class="blog-container">
      <!--博主头像-->
      <div class="my-wrapper">
        <v-avatar size="110">
          <img class="author-avatar" :src="avatar" />
        </v-avatar>
      </div>
      <!--介绍-->
      <div ref="about" class="about-content markdown-body" v-html="aboutContent"/>
    </v-card>
  </div>
</template>

<script>
import clipboard from 'clipboard'
import {getAbout} from "../../api/about";
export default {
  created() {
    this.getAboutContent()
  },
  destroyed() {
    this.clipboard.destroy()
  },
  data() {
    return {
      aboutContent: '',
      clipboard: null,
      imgList: []
    }
  },
  methods: {
    getAboutContent() {
      getAbout().then(res => {
        if (res.flag) {
          this.aboutContent = res.data
        }
      });
    },
  },
  computed: {
    avatar() {
      return this.$store.state.blogInfo.websiteConfig.websiteAvatar;
    },
    cover() {
      let cover = ''
      this.$store.state.blogInfo.pageList.forEach(item => {
        if (item.pageLabel === 'about') {
          cover = item.pageCover
        }
      })
      return `background: url(${cover}) center center / cover no-repeat`
    },
  }
}
</script>

<style scoped>
.about-content {
  word-break: break-word;
  line-height: 1.8;
}
.my-wrapper {
  text-align: center;
}
.author-avatar {
  transition: all 0.5s;
}
.author-avatar:hover {
  transform: rotate(360deg);
}
</style>
