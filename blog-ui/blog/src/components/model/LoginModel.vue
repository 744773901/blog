<template>
  <v-dialog v-model="loginFlag" :fullscreen="isMobile" max-width="460">
    <v-card class="login-container" style="border-radius:4px">
      <v-icon class="float-right" @click="loginFlag = false">mdi-close</v-icon>
      <div class="login-wrapper">
        <!--用户名-->
        <v-text-field
            v-model="username"
            label="邮箱号"
            placeholder="请输入您的邮箱"
            clearable
            @keyup.enter="login"
        />
        <!--密码-->
        <v-text-field
            class="mt-7"
            v-model="password"
            label="密码"
            placeholder="请输入您的密码"
            clearable
            @keyup.enter="login"
            :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
            :type="show ? 'text' : 'password'"
            @click:append="show = !show"
        />
        <!--登录按钮-->
        <v-btn
            class="mt-7"
            block
            color="blue"
            style="color:#fff"
            @click="login"
        >
          登录
        </v-btn>
        <!--注册 找回密码-->
        <div class="mt-10 login-tip">
          <span @click="openRegister" style="color: #4ab1f4">立即注册</span>
          <span @click="openForget" class="float-right" style="color: #4ab1f4">忘记密码?</span>
        </div>
        <!--第三方登录-->
        <div v-if="socialLoginList.length > 0">
          <div class="social-login-title">社交账号登录</div>
          <div class="social-login-wrapper">
            <!--QQ登录-->
            <a v-if="showLogin('qq')" @click="qqLogin" class="mr-3" style="color: #00AAEE">
              <svg-icon icon-class="qq"/>
            </a>
            <!--微信登录-->
            <a v-if="showLogin('wechat')" @click="wechatLogin" class="mr-3" style="color: #00AAEE">
              <svg-icon icon-class="wechat"/>
            </a>
            <!--支付宝登录-->
            <a v-if="showLogin('alipay')" @click="alipayLogin" class="mr-3" style="color: #00AAEE">
              <svg-icon icon-class="alipay"/>
            </a>
            <!--微博登录-->
            <a v-if="showLogin('weibo')" @click="weiboLogin" style="color:#e05244">
              <svg-icon icon-class="weibo"/>
            </a>
          </div>
        </div>
      </div>
    </v-card>
  </v-dialog>
</template>

<script>
import {login} from "../../api/user";

export default {
  data() {
    return {
      username: "",
      password: "",
      show: false
    }
  },
  methods: {
    login() {
      let reg = /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/
      if (!reg.test(this.username)) {
        this.$toast({type: 'error', message: '邮箱格式不正确'})
        return false
      }
      if (this.password.trim().length === 0) {
        this.$toast({ type: 'error', message: '密码不能为空'});
        return false;
      }
      let captcha = new TencentCaptcha(this.config.TENCENT_CAPTCHA_APPID, res => {
        if (res.ret === 0) {
          const data = {username: this.username, password: this.password}
          login(data).then(res => {
            if (res.flag === true) {
              this.username = "";
              this.password = "";
              this.$store.commit("LOGIN", res.data);
              this.$store.commit("CLOSE_MODEL");
              this.$toast({type: 'success', message: '登录成功'})
            } else {
              this.$toast({type: 'error', message: res.message})
            }
          })
        }
      })
      captcha.show()
    },
    qqLogin() {
      // 保存登录时的路径
      this.$store.commit('SAVE_LOGIN_URL', this.$route.path)
      let flag = navigator.userAgent.match(/(iPhone|iPod|Android|ios|iOS|iPad|Backerry|WebOS|Symbian|Windows Phone|Phone)/i)
      if (flag) {
        QC.Login.showPopup({
          appId: this.config.QQ_APP_ID,
          redirectURI: this.config.QQ_REDIRECT_URI
        })
      }
    },
    wechatLogin() {

    },
    alipayLogin() {

    },
    weiboLogin() {
      // 保存登录时的路径
      this.$store.commit('SAVE_LOGIN_URL', this.$route.path)
      window.open(`https://api.weibo.com/oauth2/authorize?client_id=${this.config.WEIBO_APP_KEY}&redirect_uri=${this.config.WEIBO_REDIRECT_URI}&response_type=code`)
    },
    openRegister() {
      this.$store.state.loginFlag = false
      this.$store.state.registerFlag = true
    },
    openForget() {
      this.$store.state.loginFlag = false
      this.$store.state.forgetFlag = true
    },
  },
  computed: {
    loginFlag: {
      get() {
        return this.$store.state.loginFlag
      },
      set(value) {
        this.$store.state.loginFlag = value
      }
    },
    socialLoginList() {
      return this.$store.state.blogInfo.websiteConfig.socialLoginList
    },
    showLogin() {
      return (type) => this.socialLoginList.indexOf(type) !== -1
    },
    isMobile() {
      const clientWidth = document.documentElement.clientWidth;
      if (clientWidth > 960) {
        return false;
      }
      return true;
    }
  }
}
</script>

<style scoped>
.social-login-title {
  margin-top: 1.5rem;
  color: #b5b5b5;
  font-size: 0.75rem;
  text-align: center;
}
.social-login-title::before {
  content: "";
  display: inline-block;
  background-color: #d8d8d8;
  width: 60px;
  height: 1px;
  margin: 0 12px;
  vertical-align: middle;
}
.social-login-title::after {
  content: "";
  display: inline-block;
  background-color: #d8d8d8;
  width: 60px;
  height: 1px;
  margin: 0 12px;
  vertical-align: middle;
}
.social-login-wrapper {
  margin-top: 1rem;
  font-size: 2rem;
  text-align: center;
}
.social-login-wrapper a {
  text-decoration: none;
}
</style>
