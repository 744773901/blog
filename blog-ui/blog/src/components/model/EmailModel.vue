<template>
  <v-dialog v-model="emailFlag" :fullscreen="isMobile" max-width="460px">
    <v-card class="login-container" style="border-radius:4px">
      <v-icon class="float-right" @click="emailFlag = false">mdi-close</v-icon>
      <div class="login-wrapper">
        <!-- 邮箱 -->
        <v-text-field
            v-model="email"
            label="邮箱号"
            placeholder="请输入您的邮箱号"
            clearable
            @keyup.enter="saveUserEmail"
        />
        <!-- 验证码 -->
        <div class="mt-7 send-wrapper">
          <v-text-field
              maxlength="6"
              clearable
              v-model="code"
              label="验证码"
              placeholder="请输入6位验证码"
              @keyup.enter="saveUserEmail"
          />
          <v-btn text small :loading="loading" :disabled="flag" @click="sendCode">{{ codeMsg }}</v-btn>
        </div>
        <!-- 按钮 -->
        <v-btn
            class="mt-7"
            block
            color="blue"
            style="color:#fff"
            @click="saveUserEmail"
        >
          绑定
        </v-btn>
      </div>
    </v-card>
  </v-dialog>
</template>

<script>
import {saveUserEmail, sendCode} from "../../api/user";

export default {
  data() {
    return {
      email: this.$store.state.email,
      code: '',
      codeMsg: '发送',
      time: 61,
      flag: true,
      show: false,
      loading: false
    }
  },
  watch: {
    email(value) {
      let reg = /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/
      if (reg.test(value)) {
        this.flag = false
      } else {
        this.flag = true
      }
    },
  },
  methods: {
    sendCode() {
      let captcha = new TencentCaptcha(this.config.TENCENT_CAPTCHA_APPID, res => {
        if (res.ret === 0) {
          this.countDown()
          sendCode({username: this.email}).then(res => {
            if (res.flag) {
              this.$toast({type: 'success', message: '发送成功'})
            } else {
              this.$toast({type: 'error', message: res.message})
            }
          });
        }
      });
      captcha.show()
    },
    countDown() {
      this.flag = true
      this.timer = setInterval(() => {
        this.time--
        this.codeMsg = this.time + ' s'
        if (this.time <= 0) {
          clearInterval(this.timer)
          this.codeMsg = '发送'
          this.time = 61
          this.flag = false
        }
      }, 1000);
    },
    saveUserEmail() {
      let reg = /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/
      if (!reg.test(this.email)) {
        this.$toast({type: 'warning', message: '邮箱格式不正确'})
        return false
      }
      if (this.code.trim().length !== 6) {
        this.$toast({type: 'warning', message: '请输入6位验证码'})
        return false
      }
      const user = {
        email: this.email,
        code: this.code
      }
      saveUserEmail(user).then(res => {
        if (res.flag) {
          this.$store.commit('UPDATE_EMAIL', this.email)
          this.email = ''
          this.code = ''
          this.$store.commit('CLOSE_MODEL')
          this.$toast({type: 'success', message: '绑定成功'})
        } else {
          this.$toast({type: 'error', message: res.message})
        }
      });
    },
  },
  computed: {
    emailFlag: {
      get() {
        return this.$store.state.emailFlag
      },
      set(value) {
        this.$store.state.emailFlag = value
      },
    },
    isMobile() {
      const clientWidth = document.documentElement.clientWidth;
      if (clientWidth > 960) {
        return false
      } else {
        return true
      }
    }
  }
}
</script>

<style scoped>
@media (min-width: 760px) {
  .login-container {
    padding: 1rem;
    border-radius: 4px;
    height: 400px;
  }
}
</style>
