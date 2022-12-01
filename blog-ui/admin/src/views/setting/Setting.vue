<template>
  <el-card class="main-card">
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="修改信息" name="info">
        <div class="info-container">
          <el-upload
              class="avatar-uploader"
              action="/api/user/avatar"
              :headers="token"
              :show-file-list="false"
              :on-success="updateAvatar">
            <img v-if="avatar" :src="avatar" class="avatar"/>
            <i v-else class="el-icon-plus avatar-uploader-icon"/>
          </el-upload>
          <el-form label-width="70px" :model="infoForm" style="width: 320px; margin-left: 3rem">
            <el-form-item label="昵称">
              <el-input v-model="infoForm.nickname" size="small"/>
            </el-form-item>
            <el-form-item label="个人简介">
              <el-input v-model="infoForm.intro" size="small"/>
            </el-form-item>
            <el-form-item label="个人网站">
              <el-input v-model="infoForm.webSite" size="small"/>
            </el-form-item>
            <el-button @click="updateInfo" size="medium" type="primary" style="margin-left: 125px">修改</el-button>
          </el-form>
        </div>
      </el-tab-pane>
      <el-tab-pane label="修改密码" name="password">
        <el-form label-width="70px" :model="passwordForm" style="width: 320px">
          <el-form-item label="旧密码">
            <el-input
                v-model="passwordForm.oldPassword"
                @keyup.enter.native="updatePassword"
                show-password
                size="small"/>
          </el-form-item>
          <el-form-item label="新密码">
            <el-input
                v-model="passwordForm.newPassword"
                @keyup.enter.native="updatePassword"
                show-password
                size="small"/>
          </el-form-item>
          <el-form-item label="确认密码">
            <el-input
                v-model="passwordForm.confirmPassword"
                @keyup.enter.native="updatePassword"
                show-password
                size="small"/>
          </el-form-item>
          <el-button
              @click="updatePassword"
              size="medium"
              type="primary"
              style="margin-left: 125px">修改
          </el-button>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script>
import {updateAdminPassword, updateUserInfo} from "@/api/user";

export default {
  data() {
    return {
      infoForm: {
        nickname: this.$store.state.userInfo.nickname,
        intro: this.$store.state.userInfo.intro,
        webSite: this.$store.state.userInfo.webSite
      },
      passwordForm: {
        oldPassword: "",
        newPassword: "",
        confirmPassword: ""
      },
      activeName: "info",
      token: {Authorization: this.$store.getters.token}
    }
  },
  computed: {
    avatar() {
      return this.$store.state.userInfo.avatar;
    },
  },
  methods: {
    handleClick(tab) {
    },
    updateAvatar(res) {
      if (res.flag) {
        this.$message.success(res.message);
        this.$store.commit('updateAvatar', res.data)
      } else {
        this.$message.error(res.message);
      }
    },
    updateInfo() {
      if (this.infoForm.nickname.trim() === "") {
        this.$message.error("用户昵称不能为空")
        return false;
      }
      updateUserInfo(this.infoForm).then(({data}) => {
        if (data.flag) {
          this.$message.success(data.message)
          this.$store.commit('updateUserInfo', this.infoForm)
        } else {
          this.$message.error(data.message)
        }
      })
    },
    updatePassword() {
      if (this.passwordForm.oldPassword.trim() === "") {
        this.$message.error("旧密码不能为空")
        return false;
      }
      if (this.passwordForm.newPassword.trim() === "") {
        this.$message.error("新密码不能为空")
        return false;
      }
      if (this.passwordForm.newPassword.length < 6) {
        this.$message.error("新密码不能少于6位")
        return false;
      }
      if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
        this.$message.error("两次密码输入不一致")
        return false;
      }
      updateAdminPassword(this.passwordForm).then(({data}) => {
        if (data.flag) {
          this.passwordForm.oldPassword = "";
          this.passwordForm.newPassword = "";
          this.passwordForm.confirmPassword = "";
          this.$message.success(data.message);
        } else {
          this.$message.error(data.message);
        }
      })
    },
    updateNotice() {

    },
  }
}
</script>

<style scoped>
.avatar-container {
  text-align: center;
}

.el-icon-message-solid {
  color: #f56c6c;
  margin-right: 0.3rem;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
  border-radius: 100%;
}

.info-container {
  display: flex;
  align-items: center;
  margin-left: 20%;
  margin-top: 5rem;
}
</style>
