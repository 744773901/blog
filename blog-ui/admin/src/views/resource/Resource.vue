<template>
  <el-card class="main-card">
    <!-- 标题 -->
    <div class="title">{{ this.$route.name }}</div>
    <div class="operation-container">
      <el-button @click="openDialog(null)" type="primary" size="small" icon="el-icon-plus">新增模块</el-button>
      <!-- 搜索 -->
      <div style="margin-left:auto">
        <el-input clearable @clear="listResources" v-model="keyword" @keyup.enter.native="listResources" prefix-icon="el-icon-search" size="small"
                  placeholder="请输入资源名" style="width:200px"/>
        <el-button @click="listResources" type="primary" size="small" icon="el-icon-search" style="margin-left:1rem">
          搜索
        </el-button>
      </div>
    </div>
    <!-- 权限列表 -->
    <el-table :data="resourceList" :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
              v-loading="loading" row-key="id">
      <el-table-column prop="resourceName" label="资源名" width="220"/>
      <el-table-column prop="url" label="资源路径" width="300"/>
      <el-table-column prop="requestMethod" label="请求方式">
        <template slot-scope="scope" v-if="scope.row.requestMethod">
          <el-tag :type="tagType(scope.row.requestMethod)">{{ scope.row.requestMethod }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isAnonymous" label="匿名访问" align="center">
        <template slot-scope="scope">
          <el-switch
              v-if="scope.row.url"
              v-model="scope.row.isAnonymous"
              @change="changeResource(scope.row)"
              active-color="#13ce66"
              inactive-color="#F4F4F5"
              :active-value="1"
              :inactive-value="0"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" align="center">
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px"/>
          {{ scope.row.createTime | date }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="scope">
          <el-button @click="openAddResourceDialog(scope.row)" type="text" size="mini" v-if="scope.row.children">
            <i class="el-icon-plus"/> 新增
          </el-button>
          <el-button @click="openEditResourceDialog(scope.row)" type="text" size="mini">
            <i class="el-icon-edit"/> 修改
          </el-button>
          <el-popconfirm @confirm="deleteResource(scope.row.id)" title="确定删除吗？" style="margin-left:10px">
            <el-button size="mini" type="text" slot="reference">
              <i class="el-icon-delete"/> 删除
            </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <!-- 新增对话框 -->
    <el-dialog :visible.sync="addModule" width="30%">
      <div class="dialog-title-container" slot="title" ref="moduleTitle"/>
      <el-form label-width="80px" size="medium" :model="resourceForm">
        <el-form-item label="模块名">
          <el-input v-model="resourceForm.resourceName" style="width:220px"/>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addModule = false">取 消</el-button>
        <el-button type="primary" @click="addOrEditResource">确 定</el-button>
      </span>
    </el-dialog>
    <!-- 新增对话框 -->
    <el-dialog :visible.sync="addResource" width="30%">
      <div class="dialog-title-container" slot="title" ref="resourceTitle"/>
      <el-form label-width="80px" size="medium" :model="resourceForm">
        <el-form-item label="资源名">
          <el-input v-model="resourceForm.resourceName" style="width:220px"/>
        </el-form-item>
        <el-form-item label="资源路径">
          <el-input v-model="resourceForm.url" style="width:220px"/>
        </el-form-item>
        <el-form-item label="请求方式">
          <el-radio-group v-model="resourceForm.requestMethod">
            <el-radio :label="'GET'">GET</el-radio>
            <el-radio :label="'POST'">POST</el-radio>
            <el-radio :label="'PUT'">PUT</el-radio>
            <el-radio :label="'DELETE'">DELETE</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addResource = false">取 消</el-button>
        <el-button type="primary" @click="addOrEditResource">确 定</el-button>
      </span>
    </el-dialog>
  </el-card>
</template>

<script>
import {deleteResourceById, getResources, saveOrUpdateResource} from "@/api/resource";

export default {
  data() {
    return {
      loading: true,
      keyword: null,
      addModule: false,
      addResource: false,
      resourceList: [],
      resourceForm: {}
    }
  },
  created() {
    this.listResources()
  },
  methods: {
    openDialog(resource) {
      if (resource) {
        this.resourceForm = resource;
        this.$refs.moduleTitle.innerHTML = "修改模块";
      } else {
        this.resourceForm = {};
        this.$refs.moduleTitle.innerHTML = "添加模块";
      }
      this.addModule = true;
    },
    openEditResourceDialog(resource) {
      if (resource.url) {
        this.openDialog(resource);
        return false;
      }
      this.resourceForm = resource;
      this.$refs.resourceTitle.innerHTML = "修改资源";
      this.addResource = true;
    },
    openAddResourceDialog(resource) {
      this.resourceForm = {};
      this.resourceForm.parentId = resource.id;
      this.$refs.resourceTitle.innerHTML = "添加资源";
      this.addResource = true;
    },
    listResources() {
      getResources({keyword: this.keyword}).then(({ data }) => {
        this.resourceList = data.data;
        this.loading = false;
      })
    },
    changeResource(resource) {
      saveOrUpdateResource(resource).then(({ data }) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listResources();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
      });
    },
    addOrEditResource() {
      if (this.resourceForm.resourceName.trim() === "") {
        this.$message.error("资源名不能为空");
        return false;
      }
      saveOrUpdateResource(this.resourceForm).then(({ data }) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listResources();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.addModule = false;
        this.addResource = false;
      });
    },
    deleteResource(id) {
      deleteResourceById(id).then(({ data }) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listResources();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
      });
    }
  },
  computed: {
    tagType() {
      return (type) => {
        switch (type) {
          case "GET":
            return "";
          case "POST":
            return "success";
          case "PUT":
            return "warning";
          case "DELETE":
            return "danger";
        }
      };
    }
  }
}
</script>
