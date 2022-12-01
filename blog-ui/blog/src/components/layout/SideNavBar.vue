<template>
  <v-navigation-drawer
      v-model="drawer"
      app
      right
      disable-resize-watcher
      overlay-opacity="0.8"
      width="250px">

    <!--头像-->
    <div class="blogger-info">
      <v-avatar size="110" style="margin-bottom: 0.5rem">
        <img :src="this.$store.state.blogInfo.websiteConfig.websiteAvatar"/>
      </v-avatar>
    </div>

    <!--博客信息-->
    <div class="blog-info-wrapper">
      <div class="blog-info-data">
        <router-link to="/archives">
          <div style="font-size: 0.875rem">文章</div>
          <div style="font-size: 1.125rem">{{this.$store.state.blogInfo.articleCount}}</div>
        </router-link>
      </div>
      <div class="blog-info-data">
        <router-link to="/categories">
          <div style="font-size: 0.875rem">分类</div>
          <div style="font-size: 1.125rem">{{this.$store.state.blogInfo.categoryCount}}</div>
        </router-link>
      </div>
      <div class="blog-info-data">
        <router-link to="/tags">
          <div style="font-size: 0.875rem">标签</div>
          <div style="font-size: 1.125rem">{{this.$store.state.blogInfo.tagCount}}</div>
        </router-link>
      </div>
    </div>

    <!--分割线-->
    <hr>

    <!--页面导航-->
    <div class="menu-container">
      <div class="menus-item">
        <router-link to="/">
          <svg-icon icon-class="home"/> 首页
        </router-link>
      </div>
      <div class="menus-item">
        <router-link to="/archives">
          <svg-icon icon-class="archive"/> 归档
        </router-link>
      </div>
      <div class="menus-item">
        <router-link to="/albums">
          <svg-icon icon-class="photo-album"/> 相册
        </router-link>
      </div>
      <div class="menus-item">
        <router-link to="/talks">
          <svg-icon icon-class="talk"/> 说说
        </router-link>
      </div>
      <div class="menus-item">
        <router-link to="/categories">
          <svg-icon icon-class="category"/> 分类
        </router-link>
      </div>
      <div class="menus-item">
        <router-link to="/tags">
          <svg-icon icon-class="tag"/> 标签
        </router-link>
      </div>
      <div class="menus-item">
        <router-link to="/links">
          <svg-icon icon-class="friend-link"/> 友链
        </router-link>
      </div>
      <div class="menus-item">
        <router-link to="/about">
          <svg-icon icon-class="about"/> 关于
        </router-link>
      </div>
      <div class="menus-item">
        <router-link to="/message">
          <svg-icon icon-class="message"/> 留言
        </router-link>
      </div>
      <div class="menus-item" v-if="!this.$store.state.avatar">
        <a @click="openLogin"><svg-icon icon-class="login"/> 登录</a>
      </div>
      <div v-else>
        <div class="menus-item">
          <router-link to="/user">
            <svg-icon icon-class="login"/> 个人中心
          </router-link>
        </div>
        <div class="menus-item">
          <a @click="logout">
            <svg-icon icon-class="login"/> 退出
          </a>
        </div>
      </div>
    </div>

  </v-navigation-drawer>
</template>

<script>
import {logout} from "../../api/user";

export default {
  methods: {
    openLogin() {
      this.$store.state.loginFlag = true
    },
    logout() {
      if (this.$route.path === '/user') {
        this.$router.go(-1)
      }
      logout().then(res => {
        if (res.flag) {
          this.$store.commit('LOGOUT');
          this.$toast({type: 'success', message: '注销成功'});
        } else {
          this.$toast({type: 'error', message: res.message});
        }
      });
    }
  },
  computed: {
    drawer: {
      get() {
        return this.$store.state.drawer
      },
      set(value) {
        this.$store.state.drawer = value
      }
    }
  }
}
</script>

<style scoped>
.blogger-info {
  padding: 30px 30px 0;
  text-align: center;
}
.blog-info-wrapper {
  display: flex;
  align-items: center;
  padding: 10px 10px 0;
}
.blog-info-data {
  flex: 1;
  line-height: 2;
  text-align: center;
}
hr {
  border: 2px dashed #d2ebfd;
  margin: 20px 0;
}
.menu-container {
  padding: 0 10px 40px;
  animation: 0.8s ease 0s 1 normal none running sidebarItem;
}
.menus-item a {
  padding: 6px 30px;
  display: block;
  line-height: 2;
}
.menus-item .svg-icon {
  margin-right: 2rem;
}
@keyframes sidebarItem {
  0% {
    transform: translateX(200px);
  }
  100% {
    transform: translateX(0);
  }
}
</style>
