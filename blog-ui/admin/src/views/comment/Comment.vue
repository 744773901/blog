<template>
  <el-card class="main-card">
    <div class="title">{{ $route.name }}</div>
    <div class="review-menu">
      <span>状态</span>
      <span @click="changeReview(null)" :class="isReview == null ? 'active-review' : 'review'">全部</span>
      <span @click="changeReview(1)" :class="isReview === 1 ? 'active-review' : 'review'">已审核</span>
      <span @click="changeReview(0)" :class="isReview === 0 ? 'active-review' : 'review'">未审核</span>
    </div>
    <div class="operation-container">
      <el-button @click="remove = true" :disabled="commentIdList.length === 0" type="danger" size="small"
                 icon="el-icon-delete">批量删除
      </el-button>
      <el-button @click="updateCommentReview(null)" :disabled="commentIdList.length === 0" type="success" size="small"
                 icon="el-icon-circle-check">批量审核
      </el-button>
      <div style="margin-left: auto">
        <el-select clearable v-model="type" placeholder="选择评论来源" size="small" style="margin-right:1rem">
          <el-option v-for="item in option" :value="item.key" :label="item.label" :key="item.key"/>
        </el-select>
        <el-input v-model="keyword" @keyup.enter.native="searchComments" prefix-icon="el-icon-search" size="small"
                  placeholder="请输入用户昵称" style="width: 200px"/>
        <el-button @click="searchComments" type="primary" size="small" icon="el-icon-search" style="margin-left:1rem">
          搜索
        </el-button>
      </div>
    </div>
    <el-table border :data="commentList" @selection-change="selectionChange" v-loading="false">
      <el-table-column type="selection" width="55"/>
      <el-table-column prop="avatar" label="头像" align="center" width="120">
        <template slot-scope="scope">
          <img :src="scope.row.avatar" width="40px" height="40px">
        </template>
      </el-table-column>
      <el-table-column prop="nickname" label="评论用户" align="center" width="120"/>
      <el-table-column prop="replyNickname" label="回复用户" align="center" width="120">
        <template slot-scope="scope">
          <span v-if="scope.row.replyNickname">{{scope.row.replyNickname}}</span>
          <span v-else>无</span>
        </template>
      </el-table-column>
      <el-table-column prop="articleTitle" label="文章标题" align="center">
        <template slot-scope="scope">
          <span v-if="scope.row.articleTitle">{{ scope.row.articleTitle }}</span>
          <span v-else>无</span>
        </template>
      </el-table-column>
      <el-table-column prop="commentContent" label="评论内容" align="center">
        <template slot-scope="scope">
          <span v-html="scope.row.commentContent" class="comment-content" />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="评论时间" align="center" width="200">
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px"> {{ scope.row.createTime | datetime }}</i>
        </template>
      </el-table-column>
      <el-table-column prop="isReview" label="是否审核" align="center" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isReview === 0" type="danger">未审核</el-tag>
          <el-tag v-if="scope.row.isReview === 1" type="success">已审核</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="评论来源" align="center" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.type === 1">文章</el-tag>
          <el-tag v-if="scope.row.type === 2" type="warning">友链</el-tag>
          <el-tag v-if="scope.row.type === 3" type="danger">说说</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="160">
        <template slot-scope="scope">
          <el-button @click="updateCommentReview(scope.row.id)" v-if="scope.row.isReview === 0" size="mini" type="success" slot="reference">通过</el-button>
          <el-popconfirm @confirm="removeComments(scope.row.id)" style="margin-left:10px" title="确定删除吗？">
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
    <!-- 批量彻底删除对话框 -->
    <el-dialog :visible.sync="remove" width="30%">
      <div class="dialog-title-container" slot="title">
        <i class="el-icon-warning" style="color:#ff9900"/>提示
      </div>
      <div style="font-size:1rem">是否彻底删除选中项？</div>
      <div slot="footer">
        <el-button @click="remove = false">取 消</el-button>
        <el-button type="primary" @click="removeComments(null)">确 定</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
import {deleteComments, getAdminComment, updateCommentsReview} from "@/api/comment";

export default {
  data() {
    return {
      loading: true,
      remove: false,
      option: [
        {key: 1, label: "文章"},
        {key: 2, label: "友链"},
        {key: 3, label: "说说"}
      ],
      commentIdList: [],
      commentList: [],
      isReview: null,
      type: null,
      keyword: null,
      current: 1,
      size: 10,
      count: 0
    }
  },
  created() {
    this.listComments()
  },
  watch: {
    isReview() {
      this.current = 1
      this.listComments()
    },
    type() {
      this.current = 1;
      this.listComments();
    }
  },
  methods: {
    selectionChange(commentList) {
      this.commentIdList = []
      commentList.forEach(item => this.commentIdList.push(item.id))
    },
    changeReview(isReview) {
      this.isReview = isReview
      this.current = 1
    },
    updateCommentReview(id) {
      let ids = []
      if (id != null) {
        ids.push(id);
      } else {
        ids = this.commentIdList
      }
      let params = {
        ids: ids,
        isReview: 1
      }
      updateCommentsReview(params).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listComments()
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
      });
    },
    searchComments() {
      this.current = 1
      this.listComments()
    },
    removeComments(id) {
      let ids = []
      if (id != null) {
        ids.push(id);
      } else {
        ids = this.commentIdList
      }
      deleteComments(ids).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listComments()
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
      });
    },
    listComments() {
      let params = {
        current: this.current,
        size: this.size,
        keyword: this.keyword,
        type: this.type,
        isReview: this.isReview
      }
      getAdminComment(params).then(({data}) => {
        this.commentList = data.data.records
        this.count = data.data.count
        this.loading = false
      });
    },
    sizeChange(size) {
      this.size = size
      this.listComments()
    },
    currentChange(current) {
      this.current = current
      this.listComments()
    }
  }
}
</script>

<style scoped>
.comment-content {
  display: inline-block;
}

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
