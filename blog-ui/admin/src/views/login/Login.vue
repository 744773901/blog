<template>
  <div class="login">
    <el-form status-icon ref="loginForm" :model="loginForm" :rules="loginRules" label-position="left" label-width="0px"
             class="login-form">
      <h3 class="login-title">博客后台管理系统</h3>
      <el-form-item prop="username">
        <el-input v-model="loginForm.username"
                  type="text"
                  auto-complete="off"
                  placeholder="账号"
                  prefix-icon="iconfont icon-zhanghao">
          <svg-icon slot="prefix" icon-class="username" class="el-input__icon input-icon"/>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="loginForm.password"
                  type="password"
                  show-password
                  auto-complete="off"
                  placeholder="密码"
                  prefix-icon="iconfont icon-mima"
                  @keyup.enter.native="handleLogin">
          <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon"/>
        </el-input>
      </el-form-item>
      <el-form-item prop="code">
        <el-input v-model="loginForm.code"
                  auto-complete="off"
                  placeholder="验证码"
                  style="width: 63%"
                  prefix-icon="iconfont icon-yanzhengma"
                  @keyup.enter.native="handleLogin">
          <svg-icon slot="prefix" icon-class="code" class="el-input__icon input-icon"/>
        </el-input>
        <div class="login-code">
          <img :src="codeUrl" @click="getCode">
        </div>
      </el-form-item>
      <!--      <el-checkbox v-model="loginForm.rememberMe" style="margin:0 0 25px 0;">记住密码</el-checkbox>-->
      <el-form-item style="width:100%;">
        <el-button :loading="loading"
                   size="medium"
                   type="primary"
                   style="width:100%;"
                   @click.native.prevent="handleLogin">
          <span v-if="!loading">登 录</span><span v-else>登 录 中...</span>
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {getCodeImg} from "@/api/login";
import {getMenu} from "@/api/menu";

export default {
  name: "Login",
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        code: '',
        uuid: ''
      },
      loginRules: {
        username: [
          {required: true, trigger: "blur", message: "请输入您的账号"}
        ],
        password: [
          {required: true, trigger: "blur", message: "请输入您的密码"}
        ],
        code: [
          {required: true, trigger: "change", message: "请输入验证码"}
        ]
      },
      codeUrl: '',
      loading: false
    };
  },
  created() {
    this.getCode();
  },
  methods: {
    getCode() {
      getCodeImg().then(({data}) => {
        this.codeUrl = data.data.img;
        this.loginForm.uuid = data.data.uuid;
      });
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          this.$store.dispatch('Login', this.loginForm).then(({data}) => {
            getMenu();
            this.$router.push({path: '/'});
          }).catch(() => {
            this.loading = false;
            this.getCode();
          })
        }
      })
    },
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url("../../assets/image/background.png");
  background-size: cover;
}

.login-title {
  margin: 0 auto 30px auto;
  text-align: center;
  color: #707070;
}

.login-form {
  border-radius: 6px;
  background: #ffffff;
  width: 385px;
  padding: 25px 25px 5px 25px;

  .el-input {
    height: 38px;

    input {
      height: 38px;
    }
  }

  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
}

.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}

.login-code {
  width: 33%;
  display: inline-block;
  height: 38px;
  float: right;

  img {
    cursor: pointer;
    vertical-align: middle
  }
}
</style>
