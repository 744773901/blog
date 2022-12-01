<template>
  <el-card class="main-card">
    <div class="title">{{ $route.name }}</div>
    <div class="operation-container">
      <div style="margin-left: auto">
        <el-select clearable @clear="listUser" v-model="loginType" placeholder="选择登录类型" size="small"
                   style="margin-right:1rem">
          <el-option v-for="item in typeList" :label="item.desc" :value="item.type" :key="item.type"/>
        </el-select>
        <el-input v-model="keyword" clearable @clear="listUser" @keyup.enter.native="listUser" placeholder="请输入用户昵称" style="width: 200px" size="small"
                  prefix-icon="el-icon-search"/>
        <el-button @click="searchUser" type="primary" size="small" icon="el-icon-search" style="margin-left: 1rem">搜索
        </el-button>
      </div>
    </div>
    <el-table border :data="userList" v-loading="loading">
      <el-table-column prop="linkAvatar" label="头像" align="center" width="100">
        <template slot-scope="scope">
          <img :src="scope.row.avatar" width="40" height="40"/>
        </template>
      </el-table-column>
      <el-table-column prop="nickname" label="用户昵称" align="center" width="140"/>
      <el-table-column prop="loginType" label="登录方式" align="center" width="80">
        <template slot-scope="scope">
          <el-tag type="success" v-if="scope.row.loginType === 1">邮箱</el-tag>
          <el-tag v-if="scope.row.loginType === 2">QQ</el-tag>
          <el-tag type="danger" v-if="scope.row.loginType === 3">微博</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="roleList" label="用户角色" align="center">
        <template slot-scope="scope">
          <el-tag v-for="(item, index) of scope.row.roleList" :key="index" style="margin-right:4px;margin-top:4px">
            {{ item.roleName }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isDisable" label="禁用" align="center" width="100">
        <template slot-scope="scope">
          <el-switch
              v-model="scope.row.isDisable"
              active-color="#13ce66"
              inactive-color="#F4F4F5"
              :active-value="1"
              :inactive-value="0"
              @change="changeDisable(scope.row)"/>
        </template>
      </el-table-column>
      <el-table-column prop="ipAddress" label="IP地址" align="center" width="140"/>
      <el-table-column prop="ipSource" label="IP来源" align="center" width="140"/>
      <el-table-column prop="createTime" label="创建时间" width="130" align="center">
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px"/>
          {{ scope.row.createTime | date }}
        </template>
      </el-table-column>
      <el-table-column prop="lastLoginTime" label="最近登录时间" width="200" align="center">
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px"/>
          {{ scope.row.lastLoginTime | datetime }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="100">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="openEditDialog(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <el-pagination
        class="pagination-container"
        background
        @size-change="sizeChange"
        @current-change="currentChange"
        :current-page="current"
        :page-size="size"
        :total="count"
        :page-sizes="[10, 20]"
        layout="total, sizes, prev, pager, next, jumper"/>
    <!-- 修改对话框 -->
    <el-dialog :visible.sync="isEdit" width="30%">
      <div class="dialog-title-container" slot="title">修改用户</div>
      <el-form label-width="60px" size="medium" :model="userForm">
        <el-form-item label="昵称">
          <el-input v-model="userForm.nickname" style="width:220px"/>
        </el-form-item>
        <el-form-item label="角色">
          <el-checkbox-group v-model="roleIdList">
            <el-checkbox v-for="item of roles" :key="item.id" :label="item.id">
              {{ item.roleName }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="isEdit = false">取消</el-button>
        <el-button type="primary" @click="editUserRole">确定</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
import {getUserRoles} from "@/api/role";
import {getUsers, updateUserDisable, updateUserRole} from "@/api/user";

export default {
  data() {
    return {
      loading: true,
      isEdit: false,
      userForm: {
        userInfoId: null,
        nickname: ""
      },
      roles: [],
      roleIdList: [],
      userList: [],
      loginType: null,
      typeList: [
        {type: 1, desc: "邮箱"},
        {type: 2, desc: "QQ"},
        {type: 3, desc: "微博"},
      ],
      keyword: null,
      current: 1,
      size: 10,
      count: 0
    }
  },
  created() {
    this.listUser()
    this.listRole()
  },
  watch: {
    loginType() {
      this.current = 1
      this.listUser()
    },
  },
  methods: {
    searchUser() {
      this.current = 1
      this.listUser()
    },
    changeDisable(user) {
      updateUserDisable({
        id: user.userInfoId,
        isDisable: user.isDisable
      }).then(({data}) => {
        if (data.flag) {
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
    editUserRole() {
      this.userForm.roleIdList = this.roleIdList
      updateUserRole(this.userForm).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listUser()
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.isEdit = false
      });
    },
    openEditDialog(user) {
      this.roleIdList = []
      user.roleList.forEach(role => this.roleIdList.push(role.id))
      this.userForm = user
      this.isEdit = true
    },
    listUser() {
      let params = {
        loginType: this.loginType,
        keyword: this.keyword,
        current: this.current,
        size: this.size
      }
      getUsers(params).then(({data}) => {
        this.userList = data.data.records
        this.count = data.data.count
        this.loading = false
      });
    },
    listRole() {
      getUserRoles().then(({data}) => {
        this.roles = data.data
      });
    },
    sizeChange(size) {
      this.size = size;
      this.listUser();
    },
    currentChange(current) {
      this.current = current;
      this.listUser();
    }
  }
}
</script>
