<template>
  <el-card class="main-card">
    <div class="title">{{ $route.name }}</div>
    <!-- 表格操作 -->
    <div class="operation-container">
      <el-button @click="openDialog(null)" type="primary" size="small" icon="el-icon-plus">新增</el-button>
      <el-button @click="isDelete = true" :disabled="this.categoryIdList.length === 0" type="danger" size="small"
                 icon="el-icon-delete">批量删除
      </el-button>
      <div style="margin-left: auto">
        <el-input v-model="keyword" @keyup.enter.native="searchCategories" @clear="listCategories"
                  prefix-icon="el-icon-search" size="small"
                  placeholder="请输入分类名" style="width:200px" clearable/>
        <el-button style="margin-left:1rem" @click="searchCategories" type="primary" icon="el-icon-search" size="small">
          搜索
        </el-button>
      </div>
    </div>
    <!-- 表格展示 -->
    <el-table border :data="categoryList" @selection-change="selectionChange" v-loading="loading">
      <el-table-column type="selection" width="55"/>
      <el-table-column prop="categoryName" label="分类名称" align="center"/>
      <el-table-column prop="articleCount" label="文章数量" align="center"/>
      <el-table-column prop="createTime" label="创建时间" align="center">
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px"/>
          {{ scope.row.createTime | datetime }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160px" align="center">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="openDialog(scope.row)">编辑</el-button>
          <el-popconfirm title="确定删除吗？" style="margin-left:1rem" @confirm="removeCategory(scope.row.id)">
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
    <el-dialog :visible.sync="isDelete" width="30%">
      <div class="dialog-title-container" slot="title">
        <i class="el-icon-warning" style="color:#ff9900"/>提示
      </div>
      <div style="font-size:1rem">是否删除选中项？</div>
      <div slot="footer">
        <el-button @click="isDelete = false">取 消</el-button>
        <el-button type="primary" @click="removeCategory(null)">
          确 定
        </el-button>
      </div>
    </el-dialog>
    <!-- 新增和编辑分类对话框 -->
    <el-dialog :visible.sync="addOrEdit" width="30%">
      <div class="dialog-title-container" slot="title" ref="categoryTitle"/>
      <el-form label-width="80px" size="medium" :model="categoryForm">
        <el-form-item label="分类名">
          <el-input v-model="categoryForm.categoryName" style="width:220px"/>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="addOrEdit = false">取 消</el-button>
        <el-button type="primary" @click="addOrEditCategory">确 定</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
import {deleteCategory, getAdminCategories, saveOrUpdateCategory} from "@/api/category";

export default {
  name: 'Category',
  data() {
    return {
      loading: true,
      isDelete: false,
      addOrEdit: false,
      keyword: null,
      categoryIdList: [],
      categoryList: [],
      categoryForm: {
        id: null,
        categoryName: ""
      },
      current: 1,
      size: 10,
      count: 0
    };
  },
  created() {
    this.listCategories()
  },
  methods: {
    selectionChange(categoryList) {
      this.categoryIdList = []
      categoryList.forEach(item => this.categoryIdList.push(item.id))
    },
    searchCategories() {
      this.current = 1
      this.listCategories()
    },
    openDialog(category) {
      if (category != null) {
        this.categoryForm.id = category.id
        this.categoryForm.categoryName = category.categoryName
        this.$refs.categoryTitle.innerHTML = "修改分类";
      } else {
        this.categoryForm.id = null
        this.categoryForm.categoryName = ""
        this.$refs.categoryTitle.innerHTML = "添加分类";
      }
      this.addOrEdit = true;
    },
    removeCategory(id) {
      let ids = []
      if (id != null) {
        ids.push(id);
      } else {
        ids = this.categoryIdList
      }
      deleteCategory(ids).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "删除成功",
            message: data.message
          });
          this.listCategories();
        } else {
          this.$notify.error({
            title: "删除失败",
            message: data.message
          });
        }
        this.isDelete = false
      });
    },
    addOrEditCategory() {
      if (this.categoryForm.categoryName.trim() === "") {
        this.$message.error("分类名不能为空");
        return false;
      }
      saveOrUpdateCategory(this.categoryForm).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listCategories();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.addOrEdit = false
      });
    },
    listCategories() {
      getAdminCategories({
        current: this.current,
        size: this.size,
        keyword: this.keyword
      }).then(({data}) => {
        //如果删除完最后一页数据则返回上一页数据
        if (data.data.records.length === 0) {
          getAdminCategories({
            current: --this.current,
            size: this.size,
            keyword: this.keyword
          }).then(({data}) => {
            this.categoryList = data.data.records;
            this.count = data.data.count
            this.loading = false
          });
        } else {
          this.categoryList = data.data.records;
          this.count = data.data.count
          this.loading = false
        }
      });
    },
    sizeChange(size) {
      this.size = size
      this.listCategories()
    },
    currentChange(current) {
      this.current = current
      this.listCategories()
    }
  }
}
</script>
