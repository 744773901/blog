<template>
  <el-card class="main-card">
    <div class="title">{{ $route.name }}</div>
    <div class="operation-container">
      <el-button @click="openDialog(null)" type="primary" size="small" icon="el-icon-plus">新增菜单</el-button>
      <!-- 搜索 -->
      <div style="margin-left:auto">
        <el-input clearable @clear="listMenus" v-model="keyword" @keyup.enter.native="listMenus" prefix-icon="el-icon-search" size="small" placeholder="请输入菜单名" style="width:200px"/>
        <el-button @click="listMenus" type="primary" size="small" icon="el-icon-search" style="margin-left:1rem">搜索</el-button>
      </div>
    </div>
    <!-- 菜单列表 -->
    <el-table :data="menuList" :tree-props="{ children: 'children', hasChildren: 'hasChildren' }" v-loading="loading" row-key="id">
      <!-- 菜单名称 -->
      <el-table-column prop="name" label="菜单名称" width="140"/>
      <!-- 菜单icon -->
      <el-table-column prop="icon" align="center" label="图标" width="100">
        <template slot-scope="scope">
          <svg-icon :icon-class="scope.row.icon"/>
        </template>
      </el-table-column>
      <!-- 菜单排序 -->
      <el-table-column prop="orderNum" align="center" label="排序" width="100"/>
      <!-- 访问路径 -->
      <el-table-column prop="path" label="访问路径"/>
      <!-- 组件路径 -->
      <el-table-column prop="component" label="组件路径"/>
      <!-- 是否隐藏 -->
      <el-table-column prop="isHidden" label="隐藏" align="center" width="80">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.isHidden" @change="changeIsHidden(scope.row)" active-color="#13ce66" inactive-color="#F4F4F5" :active-value="1" :inactive-value="0"/>
        </template>
      </el-table-column>
      <!-- 创建时间 -->
      <el-table-column prop="createTime" label="创建时间" align="center" width="150">
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px"/>
          {{ scope.row.createTime | date }}
        </template>
      </el-table-column>
      <!-- 操作 -->
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="scope">
          <el-button v-if="scope.row.children" @click="openDialog(scope.row, 1)" type="text" size="mini">
            <i class="el-icon-plus"/> 新增
          </el-button>
          <el-button @click="openDialog(scope.row, 2)" type="text" size="mini">
            <i class="el-icon-edit" /> 修改
          </el-button>
          <el-popconfirm @confirm="deleteMenu(scope.row.id)" title="确定删除吗？" style="margin-left:10px">
            <el-button size="mini" type="text" slot="reference">
              <i class="el-icon-delete" /> 删除
            </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <!-- 新增或修改对话框 -->
    <el-dialog :visible.sync="addMenu" width="30%" top="12vh">
      <div class="dialog-title-container" slot="title" ref="menuTitle" />
      <el-form label-width="80px" size="medium" :model="menuForm">
        <!-- 菜单类型 -->
        <el-form-item label="菜单类型" v-if="show">
          <el-radio-group v-model="isCatalog">
            <el-radio :label="true">目录</el-radio>
            <el-radio :label="false">一级菜单</el-radio>
          </el-radio-group>
        </el-form-item>
        <!-- 菜单名称 -->
        <el-form-item label="菜单名称">
          <el-input v-model="menuForm.name" style="width:220px"/>
        </el-form-item>
        <!-- 菜单图标 -->
        <el-form-item label="菜单图标">
          <el-popover placement="bottom-start" width="400" trigger="click">
            <el-row>
              <el-col v-for="(item, index) in iconList" :key="index" :md="8" :gutter="10">
                <div class="icon-item" @click="checkIcon(item)">
                  <svg-icon :icon-class="item"/><span style="margin-left: 5px">{{ item }}</span>
                </div>
              </el-col>
            </el-row>
            <el-input v-model="menuForm.icon" :prefix-icon="menuForm.icon" slot="reference" style="width:220px">
              <svg-icon slot="prefix" :icon-class="menuForm.icon"/>
            </el-input>
          </el-popover>
        </el-form-item>
        <!-- 组件路径 -->
        <el-form-item label="组件路径" v-show="!isCatalog">
          <el-input v-model="menuForm.component" style="width:220px" />
        </el-form-item>
        <!-- 路由地址 -->
        <el-form-item label="访问路径">
          <el-input v-model="menuForm.path" style="width:220px" />
        </el-form-item>
        <!-- 显示排序 -->
        <el-form-item label="显示排序">
          <el-input-number v-model="menuForm.orderNum" controls-position="right" :min="1" :max="999"/>
        </el-form-item>
        <!-- 显示状态 -->
        <el-form-item label="显示状态">
          <el-radio-group v-model="menuForm.isHidden">
            <el-radio :label="0">显示</el-radio>
            <el-radio :label="1">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="addMenu = false">取 消</el-button>
        <el-button type="primary" @click="saveOrUpdateMenu">确 定</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
import {deleteMenuById, getMenus, saveOrUpdateMenu} from "@/api/menu";
import icons from "@/assets/js/icons";

export default {
  data() {
    return {
      loading: true,
      show: true,
      isCatalog: true,
      addMenu: false,
      keyword: "",
      menuList: [],
      menuForm: {
        id: null,
        name: "",
        icon: "",
        component: "",
        path: "",
        orderNum: 1,
        parentId: null,
        isHidden: 0
      },
      iconList: icons
    }
  },
  created() {
    this.listMenus()
  },
  methods: {
    openDialog(menu, type) {
      if (menu) {
        this.show = false;
        this.isCatalog = false;
        switch (type) {
          case 1:
            this.menuForm = {
              id: null,
              name: "",
              icon: "",
              component: "",
              path: "",
              orderNum: 1,
              parentId: null,
              isHidden: 0
            };
            this.$refs.menuTitle.innerHTML = "新增菜单";
            this.menuForm.parentId = menu.id;
            break;
          case 2:
            this.$refs.menuTitle.innerHTML = "修改菜单";
            this.menuForm = menu;
            break;
        }
      } else {
        this.$refs.menuTitle.innerHTML = "新增菜单";
        this.show = true;
        this.menuForm = {
          id: null,
          name: "",
          icon: "",
          component: "Layout",
          path: "",
          orderNum: 1,
          parentId: 0,
          isHidden: 0
        };
      }
      this.addMenu = true;
    },
    checkIcon(icon) {
      this.menuForm.icon = icon;
    },
    listMenus() {
      getMenus({keyword: this.keyword}).then(({data}) => {
        this.menuList = data.data
        this.loading = false
      });
    },
    saveOrUpdateMenu() {
      if (this.menuForm.name.trim() === "") {
        this.$message.error("菜单名不能为空");
        return false;
      }
      if (this.menuForm.icon.trim() === "") {
        this.$message.error("菜单icon不能为空");
        return false;
      }
      if (this.menuForm.component.trim() === "") {
        this.$message.error("菜单组件路径不能为空");
        return false;
      }
      if (this.menuForm.path.trim() === "") {
        this.$message.error("菜单访问路径不能为空");
        return false;
      }
      saveOrUpdateMenu(this.menuForm).then(({ data }) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listMenus();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.addMenu = false;
      });
    },
    deleteMenu(id) {
      deleteMenuById(id).then(({ data }) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listMenus();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
      });
    },
    changeIsHidden() {

    },
  }
}
</script>

<style scoped>
.icon-item {
  cursor: pointer;
  padding: 0.5rem 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
