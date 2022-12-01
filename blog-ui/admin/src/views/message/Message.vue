<template>
  <el-card class="main-card">
    <div class="title">{{$route.name}}</div>
    <div class="review-menu">
      <span>状态</span>
      <span @click="changeReview(null)" :class="isReview ===null ? 'active-review' : 'review'">全部</span>
      <span @click="changeReview(1)" :class="isReview ===1 ? 'active-review' : 'review'">已审核</span>
      <span @click="changeReview(0)" :class="isReview ===0 ? 'active-review' : 'review'">未审核</span>
    </div>
    <div class="operation-container">
      <el-button @click="deleteFlag = true" :disabled="messageIdList.length === 0" type="danger" size="small" icon="el-icon-delete">批量删除</el-button>
      <el-button @click="updateReview(null)" :disabled="messageIdList.length === 0" type="success" size="small" icon="el-icon-circle-check">批量审核</el-button>
      <div style="margin-left: auto">
        <el-input clearable @clear="listMessage" v-model="keyword" @keyup.enter.native="searchMessage" size="small" placeholder="请输入用户名" style="width: 200px" prefix-icon="el-icon-search"/>
        <el-button @click="searchMessage" type="primary" size="small" style="margin-left: 1rem" prefix-icon="el-icon-search">搜索</el-button>
      </div>
    </div>
    <!-- 表格展示 -->
    <el-table border v-loading="loading" :data="messageList" @selection-change="selectionChange">
      <el-table-column type="selection" width="55"/>
      <el-table-column prop="avatar" label="头像" align="center" width="150">
        <template slot-scope="scope">
          <img :src="scope.row.avatar" width="40" height="40"/>
        </template>
      </el-table-column>
      <el-table-column prop="nickname" label="留言用户" align="center" width="150"/>
      <el-table-column prop="messageContent" label="留言内容" align="center"/>
      <el-table-column prop="ipAddress" label="IP地址" align="center" width="150"/>
      <el-table-column prop="ipSource" label="IP来源" align="center" width="170"/>
      <el-table-column prop="isReview" label="是否审核" align="center" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isReview === 1" type="success">已审核</el-tag>
          <el-tag v-if="scope.row.isReview === 0" type="danger">未审核</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="留言时间" align="center" width="200">
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px"> {{scope.row.createTime | datetime}}</i>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150">
        <template slot-scope="scope">
          <el-button v-if="scope.row.isReview === 0" @click="updateReview(scope.row.id)" slot="reference" type="success" size="mini">通过</el-button>
          <el-popconfirm style="margin-left:10px" title="确定删除吗？" @confirm="removeMessage(scope.row.id)">
            <el-button size="mini" type="danger" slot="reference">删除</el-button>
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
        layout="total, sizes, prev, pager, next, jumper"/>
    <!-- 批量删除对话框 -->
    <el-dialog :visible.sync="deleteFlag" width="30%">
      <div class="dialog-title-container" slot="title">
        <i class="el-icon-warning" style="color:#ff9900" />提示
      </div>
      <div style="font-size:1rem">是否删除选中项？</div>
      <div slot="footer">
        <el-button @click="deleteFlag = false">取 消</el-button>
        <el-button type="primary" @click="removeMessage(null)">确 定</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
import {deleteMessage, getAdminMessage, updateMessageReview} from "@/api/message";

export default {
  data() {
    return {
      loading: true,
      deleteFlag: false,
      isReview: null,
      messageList: [],
      messageIdList: [],
      keyword: null,
      current: 1,
      size: 10,
      count: 0
    }
  },
  created() {
    this.listMessage()
  },
  watch: {
    isReview() {
      this.current = 1
      this.listMessage()
    },
  },
  methods: {
    selectionChange(messageList) {
      this.messageIdList = []
      messageList.forEach(item => this.messageIdList.push(item.id))
    },
    changeReview(review) {
      this.isReview = review;
    },
    updateReview(id) {
      let params = {}
      if (id != null) {
        params.ids = [id]
      } else {
        params.ids = this.messageIdList
      }
      params.isReview = 1
      updateMessageReview(params).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listMessage();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
      });
    },
    searchMessage() {
      this.current = 1
      this.listMessage()
    },
    removeMessage(id) {
      let ids = []
      if (id != null) {
        ids.push(id);
      } else {
        ids = this.messageIdList
      }
      deleteMessage(ids).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listMessage();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
      });
      this.deleteFlag = false;
    },
    listMessage() {
      let params = {
        keyword: this.keyword,
        isReview: this.isReview,
        current: this.current,
        size: this.size
      }
      getAdminMessage(params).then(({data}) => {
        this.messageList = data.data.records
        this.count = data.data.count
        this.loading = false
      });
    },
    sizeChange(size) {
      this.size = size
      this.listMessage()
    },
    currentChange(current) {
      this.current = current
      this.listMessage()
    },
  }
}
</script>

<style scoped>
.operation-container {
  margin-top: 1.5rem;
}
.review-menu {
  font-size: 14px;
  margin-top: 40px;
  color: #999;
}
.review-menu span {
  margin-right: 24px;
}
.review {
  cursor: pointer;
}
.active-review {
  cursor: pointer;
  color: #333;
  font-weight: bold;
}
</style>
