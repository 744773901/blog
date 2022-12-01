<template>
  <v-dialog v-model="forgetFlag" :fullscreen="isMobile" max-width="460">
    <v-card class="login-container" style="border-radius:4px">
      <v-icon class="float-right" @click="forgetFlag = false">mdi-close</v-icon>
      <div class="login-wrapper">
        <!--用户名-->
        <v-text-field
            v-model="username"
            label="邮箱号"
            placeholder="请输入您的邮箱号"
            clearable
            @keyup.enter="forget"
        />
        <!--密码-->
        <v-text-field
            v-model="password"
            clearable
            class="mt-7"
            label="新密码"
            placeholder="请输入您的新密码"
            @keyup.enter="forget"
            :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
            :type="show ? 'text' : 'password'"
            @click:append="show = !show"
        />
        <!--确认密码-->
        <v-text-field
            v-model="confirmPassword"
            clearable
            class="mt-7"
            label="确认密码"
            placeholder="请确认您的密码"
            @keyup.enter="forget"
            :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
            :type="show ? 'text' : 'password'"
            @click:append="show = !show"
        />
        <!--验证码-->
        <div class="mt-7 send-wrapper">
          <v-text-field
              v-model="code"
              maxlength="6"
              label="验证码"
              placeholder="请输入6位验证码"
              clearable
              @keyup.enter="forget"
          />
          <v-btn text small :loading="loading" :disabled="flag" @click="sendCode">{{ message }}</v-btn>
        </div>
        <!--忘记密码按钮-->
        <v-btn block class="mt-7" @click="forget" color="red" style="color:#fff">确认修改</v-btn>
        <!--登录-->
        <div class="mt-10 login-tip">
          已有账号？<span @click="openLogin" style="color: #4ab1f4">登录</span>
        </div>
      </div>
    </v-card>
  </v-dialog>
</template>

<script>
import {sendCode, updatePassword} from "../../api/user";
export default {
  data() {
    return {
      username: "",
      password: "",
      confirmPassword: "",
      code: "",
      message: "发送",
      time: 61,
      flag: false,
      show: false,
      loading: false
    }
  },
  methods: {
    sendCode() {
      let captcha = new TencentCaptcha(this.config.TENCENT_CAPTCHA_APPID, (res) => {
        if (res.ret === 0) {
          sendCode({username: this.username}).then(res => {
            if (res.flag) {
              this.$toast({type: 'success', message: '发送成功'})
            } else {
              this.$toast({type: 'error', message: res.message})
            }
          });
          this.countdown();
        }
      });
      captcha.show()
    },
    countdown() {
      this.flag = true
      this.loading = true
      this.timer = setInterval(() => {
        this.loading = false
        this.time--
        this.message = this.time + " s"
        if (this.time <= 0) {
          clearInterval(this.timer)
          this.message = "发送"
          this.time = 61
          this.flag  =false
        }
      }, 1000);
    },
    forget() {
      let reg = /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/
      if (!reg.test(this.username)) {
        this.$toast({
          type: "error",
          message: "邮箱格式不正确"
        })
        return false
      }
      if (this.password.trim().length < 6) {
        this.$toast({
          type: "error",
          message: "密码不能少于6位"
        })
        return false
      }
      if (this.password !== this.confirmPassword) {
        this.$toast({
          type: "error",
          message: "两次密码输入不一致"
        })
        return false
      }
      if (this.code.trim().length < 6) {
        this.$toast({
          type: "error",
          message: "请输入6位验证码"
        })
        return false
      }
      const user = {
        username: this.username,
        password: this.password,
        code: this.code
      }
      updatePassword(user).then(res => {
        if (res.flag) {
          this.$toast({type: 'success', message: '修改成功!'})
          this.forgetFlag = false
          this.$store.state.loginFlag = true
        } else {
          this.$toast({type: 'error', message: res.message})
        }
      });
    },
    openLogin() {
      this.$store.state.forgetFlag = false
      this.$store.state.loginFlag = true
    },
  },
  computed: {
    forgetFlag: {
      get() {
        return this.$store.state.forgetFlag
      },
      set(value) {
        this.$store.state.forgetFlag  = value
      },
    },
    isMobile() {
      const clientWidth = document.documentElement.clientWidth;
      if (clientWidth > 960) {
        return false;
      }
      return true;
    },
  },
  watch: {
    username(value) {
      let reg = /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
      if (reg.test(value)) {
        this.flag = false;
      } else {
        this.flag = true;
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  padding: 1rem;
  border-radius: 4px;
  height: 580px;
}
</style>
