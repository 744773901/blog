<template>
  <el-card class="main-card">
    <div class="title">{{ $route.name }}</div>
    <div class="operation-container">
      <div style="margin-left: auto">
        <el-input v-model="keyword" clearable @clear="listOnlineUsers" @keyup.enter.native="listOnlineUsers"
                  prefix-icon="el-icon-search" size="small" placeholder="请输入用户昵称" style="width:200px"/>
        <el-button @click="listOnlineUsers" type="primary" size="small" icon="el-icon-search" style="margin-left:1rem">
          搜索
        </el-button>
      </div>
    </div>
    <el-table v-loading="loading" :data="userList">
      <el-table-column prop="avatar" label="头像" align="center" width="100">
        <template slot-scope="scope">
          <img :src="scope.row.avatar" width="40" height="40"/>
        </template>
      </el-table-column>
      <el-table-column prop="nickname" label="用户昵称" align="center"/>
      <el-table-column prop="ipAddress" label="IP地址" align="center"/>
      <el-table-column prop="ipSource" label="IP来源" align="center" width="200"/>
      <el-table-column prop="browser" label="浏览器" align="center" width="160"/>
      <el-table-column prop="os" label="操作系统" align="center"/>
      <el-table-column prop="lastLoginTime" label="最近登录时间" align="center" width="200">
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px"/>
          {{ scope.row.lastLoginTime | datetime }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150">
        <template slot-scope="scope">
          <el-popconfirm @confirm="removeOnlineUser(scope.row)" title="确定下线吗？" style="margin-left:10px">
            <el-button size="mini" type="text" slot="reference">
              <i class="el-icon-delete"/> 下线
            </el-button>
          </el-popconfirm>
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
        layout="total, sizes, prev, pager, next, jumper"
    />
  </el-card>
</template>

<script>
import {getOnlineUsers, offlineUser} from "@/api/user";

export default {
  data() {
    return {
      loading: true,
      userList: [],
      keyword: null,
      current: 1,
      size: 10,
      count: 0,
    }
  },
  created() {
    this.listOnlineUsers()
  },
  methods: {
    listOnlineUsers() {
      getOnlineUsers({
        keyword: this.keyword,
        current: this.current,
        size: this.size
      }).then(({data}) => {
        this.userList = data.data.records
        this.loading = false
      });
    },
    removeOnlineUser(user) {
      offlineUser(user.id).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listOnlineUsers()
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
      });
    },
    sizeChange(size) {
      this.size = size;
      this.listOnlineUsers();
    },
    currentChange(current) {
      this.current = current;
      this.listOnlineUsers();
    }
  }
}
</script>

<style scoped>
label {
  font-weight: bold !important;
}
</style>
