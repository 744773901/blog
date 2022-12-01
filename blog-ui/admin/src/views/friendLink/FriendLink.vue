<template>
  <el-card class="main-card">
    <div class="title">{{ $route.name }}</div>
    <!-- 操作 -->
    <div class="operation-container">
      <el-button @click="openDialog(null)" type="primary" size="small" icon="el-icon-plus">新增</el-button>
      <el-button @click="deleteFlag = true" :disabled="linkIdList.length === 0" type="danger" size="small" icon="el-icon-delete">批量删除</el-button>
      <!-- 搜索 -->
      <div style="margin-left:auto">
        <el-input clearable @clear="listLinks" @keyup.enter.native="searchLinks" v-model="keyword" prefix-icon="el-icon-search" size="small" placeholder="请输入友链名" style="width:200px"/>
        <el-button @click="searchLinks" type="primary" size="small" icon="el-icon-search" style="margin-left:1rem">搜索</el-button>
      </div>
    </div>
    <!-- 表格展示 -->
    <el-table border :data="linkList" @selection-change="selectionChange" v-loading="loading">
      <!-- 表格列 -->
      <el-table-column type="selection" width="55"/>
      <el-table-column prop="linkAvatar" label="链接头像" align="center" width="180">
        <template slot-scope="scope">
          <img :src="scope.row.linkAvatar" width="40" height="40"/>
        </template>
      </el-table-column>
      <el-table-column prop="linkName" label="链接名" align="center"/>
      <el-table-column prop="linkAddress" label="链接地址" align="center"/>
      <el-table-column prop="linkIntro" label="链接介绍" align="center"/>
      <el-table-column prop="createTime" label="创建时间" width="140" align="center">
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px" />{{ scope.row.createTime | date }}
        </template>
      </el-table-column>
      <!-- 列操作 -->
      <el-table-column label="操作" align="center" width="160">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="openDialog(scope.row)">编辑</el-button>
          <el-popconfirm @confirm="deleteLink(scope.row.id)" title="确定删除吗？" style="margin-left:1rem">
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
        layout="total, sizes, prev, pager, next, jumper"
    />
    <!-- 批量删除对话框 -->
    <el-dialog :visible.sync="deleteFlag" width="30%">
      <div class="dialog-title-container" slot="title">
        <i class="el-icon-warning" style="color:#ff9900" />提示
      </div>
      <div style="font-size:1rem">是否删除选中项？</div>
      <div slot="footer">
        <el-button @click="deleteFlag = false">取 消</el-button>
        <el-button type="primary" @click="deleteLink(null)">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 新增或修改对话框 -->
    <el-dialog :visible.sync="addOrEdit" width="30%">
      <div class="dialog-title-container" slot="title" ref="linkTitle" />
      <el-form label-width="80px" size="medium" :model="linkForm">
        <el-form-item label="链接名">
          <el-input style="width:250px" v-model="linkForm.linkName" />
        </el-form-item>
        <el-form-item label="链接头像">
          <el-input style="width:250px" v-model="linkForm.linkAvatar" />
        </el-form-item>
        <el-form-item label="链接地址">
          <el-input style="width:250px" v-model="linkForm.linkAddress" />
        </el-form-item>
        <el-form-item label="链接介绍">
          <el-input style="width:250px" v-model="linkForm.linkIntro" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="addOrEdit = false">取 消</el-button>
        <el-button type="primary" @click="addOrEditLink">确 定</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
import {deleteFriendLink, listFriendLink, saveOrUpdateFriendLink} from "@/api/friendlink";

export default {
  data() {
    return {
      loading: true,
      deleteFlag: false,
      addOrEdit: false,
      linkIdList: [],
      linkList: [],
      linkForm: {
        id: null,
        linkName: "",
        linkAvatar: "",
        linkIntro: "",
        linkAddress: ""
      },
      keyword: null,
      current: 1,
      size: 10,
      count: 0
    }
  },
  created() {
    this.listLinks()
  },
  methods: {
    selectionChange(linkList) {
      this.linkIdList = []
      linkList.forEach(item => this.linkIdList.push(item.id))
    },
    openDialog(link) {
      if (link) {
        this.linkForm = link;
        this.$refs.linkTitle.innerText = "修改友链"
      } else {
        this.linkForm = {
          id: null,
          linkAvatar: "",
          linkName: "",
          linkIntro: "",
          linkAddress: ""
        }
        this.$refs.linkTitle.innerText = "新增友链"
      }
      this.addOrEdit = true;
    },
    searchLinks() {
      this.current = 1
      this.listLinks()
    },
    listLinks() {
      listFriendLink({
        keyword: this.keyword,
        current: this.current,
        size: this.size
      }).then(({data}) => {
        this.linkList = data.data.records
        this.count = data.data.count
        this.loading = false
      });
    },
    addOrEditLink() {
      if (this.linkForm.linkName.trim() === "") {
        this.$message.error("友链名称不能为空");
        return false;
      }
      if (this.linkForm.linkAvatar.trim() === "") {
        this.$message.error("友链头像不能为空");
        return false;
      }
      if (this.linkForm.linkIntro.trim() === "") {
        this.$message.error("友链介绍不能为空");
        return false;
      }
      if (this.linkForm.linkAddress.trim() === "") {
        this.$message.error("友链地址不能为空");
        return false;
      }
      saveOrUpdateFriendLink(this.linkForm).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listLinks();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.addOrEdit = false
      });
    },
    deleteLink(id) {
      let ids = []
      if (id) {
        ids.push(id);
      } else {
        ids = this.linkIdList
      }
      deleteFriendLink(ids).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listLinks();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.deleteFlag = false
      });
    },
    sizeChange(size) {
      this.size = size;
      this.listLinks();
    },
    currentChange(current) {
      this.current = current;
      this.listLinks();
    }
  }
}
</script>
