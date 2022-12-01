<template>
  <v-app id="app">
    <!--导航栏-->
    <TopNavBar/>
    <!--侧边导航栏-->
    <SideNavBar/>
    <!--内容-->
    <v-main>
      <router-view :key="$route.fullPath"/>
    </v-main>
    <!--页脚-->
    <Footer/>
    <!--返回顶部-->
    <BackTop/>
    <!--搜索框-->
    <SearchModel/>
    <!--登录框-->
    <LoginModel/>
    <!--注册框-->
    <RegisterModel/>
    <!--忘记密码框-->
    <ForgetModel/>
    <!--绑定邮箱框-->
    <EmailModel/>
    <!--聊天室-->
    <ChatRoom v-if="blogInfo.websiteConfig.isChatRoom === 1"/>
  </v-app>
</template>

<script>

import TopNavBar from "./components/layout/TopNavBar";
import SideNavBar from "./components/layout/SideNavBar";
import Footer from "./components/layout/Footer";
import BackTop from "./components/BackTop";
import SearchModel from "./components/model/SearchModel"
import LoginModel from "./components/model/LoginModel";
import RegisterModel from "./components/model/RegisterModel";
import ForgetModel from "./components/model/ForgetModel";
import EmailModel from "./components/model/EmailModel";
import ChatRoom from "./components/ChatRoom";
import {getInfo, report} from "./api/user";

export default {
  components: {Footer, SideNavBar, TopNavBar, BackTop, SearchModel, LoginModel, RegisterModel, ForgetModel, EmailModel, ChatRoom},
  mounted() {
    console.log(
        '\n' + ` %c 博客 v1.0.0` + ` %c ${process.env.VITE_SITE_URL || 'http://127.0.0.1'} ` + '\n',
        'color: #fadfa3; background: #030307; padding:5px 0;', 'background: rgb(127,200,248); padding:5px 0;'
    )
  },
  created() {
    this.getBlogInfo()
    report()
  },
  methods: {
    getBlogInfo() {
      getInfo().then(res => {
        if (res.flag) {
          this.$store.commit('SET_BLOG_INFO', res.data)
        }
      });
    },
  },
  computed: {
    blogInfo() {
      return this.$store.state.blogInfo
    },
    isMobile() {
      return navigator.userAgent.match(
          /(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i
      );
    }
  }
};
</script>
