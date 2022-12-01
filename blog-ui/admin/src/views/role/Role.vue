<template>
  <el-card class="main-card">
    <div class="title">{{ $route.name }}</div>
    <div class="operation-container">
      <el-button @click="openMenuDialog(null)" type="primary" size="small" icon="el-icon-plus">新增</el-button>
      <el-button @click="isDelete = true" :disabled="roleIdList.length === 0" type="danger" size="small"
                 icon="el-icon-delete">批量删除
      </el-button>
      <!-- 搜索 -->
      <div style="margin-left:auto">
        <el-input clearable @clear="listRoles" v-model="keyword" @keyup.enter.native="searchRoles" prefix-icon="el-icon-search" size="small"
                  placeholder="请输入角色名" style="width:200px"/>
        <el-button @click="searchRoles" type="primary" size="small" icon="el-icon-search" style="margin-left:1rem">搜索
        </el-button>
      </div>
    </div>
    <!-- 表格展示 -->
    <el-table border :data="roleList" @selection-change="selectionChange" v-loading="loading">
      <!-- 表格列 -->
      <el-table-column type="selection" width="55"/>
      <el-table-column prop="roleName" label="角色名" align="center"/>
      <el-table-column prop="roleLabel" label="权限标签" align="center">
        <template slot-scope="scope">
          <el-tag>{{ scope.row.roleLabel }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isDisable" label="禁用" align="center" width="100">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.isDisable" active-color="#13ce66" inactive-color="#F4F4F5" :active-value="1"
                     :inactive-value="0" @change="changeDisable(scope.row)"/>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="150" align="center">
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px"/>
          {{ scope.row.createTime | date }}
        </template>
      </el-table-column>
      <!-- 列操作 -->
      <el-table-column label="操作" align="center" width="220">
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="openMenuDialog(scope.row)">
            <i class="el-icon-edit"/> 菜单权限
          </el-button>
          <el-button @click="openResourceDialog(scope.row)" type="text" size="mini">
            <i class="el-icon-folder-checked"/> 资源权限
          </el-button>
          <el-popconfirm @confirm="deleteRoles(scope.row.id)" title="确定删除吗？" style="margin-left:10px">
            <el-button size="mini" type="text" slot="reference">
              <i class="el-icon-delete"/> 删除
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
    <!-- 菜单对话框 -->
    <el-dialog :visible.sync="roleMenu" width="30%">
      <div class="dialog-title-container" slot="title" ref="roleTitle"/>
      <el-form label-width="80px" size="medium" :model="roleForm">
        <el-form-item label="角色名">
          <el-input v-model="roleForm.roleName" style="width:250px"/>
        </el-form-item>
        <el-form-item label="权限标签">
          <el-input v-model="roleForm.roleLabel" style="width:250px"/>
        </el-form-item>
        <el-form-item label="菜单权限">
          <el-tree :data="menuList" :default-checked-keys="roleForm.menuIdList" check-strictly show-checkbox
                   node-key="id" ref="menuTree"/>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="roleMenu = false">取 消</el-button>
        <el-button type="primary" @click="saveOrUpdateRoleMenu">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 资源对话框 -->
    <el-dialog :visible.sync="roleResource" width="30%" top="9vh">
      <div class="dialog-title-container" slot="title">修改资源权限</div>
      <el-form label-width="80px" size="medium" :model="roleForm">
        <el-form-item label="角色名">
          <el-input v-model="roleForm.roleName" style="width:250px"/>
        </el-form-item>
        <el-form-item label="权限标签">
          <el-input v-model="roleForm.roleLabel" style="width:250px"/>
        </el-form-item>
        <el-form-item label="资源权限">
          <el-tree :data="resourceList" :default-checked-keys="roleForm.resourceIdList" show-checkbox node-key="id"
                   ref="resourceTree"/>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="roleResource = false">取 消</el-button>
        <el-button type="primary" @click="saveOrUpdateRoleResource">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 批量删除对话框 -->
    <el-dialog :visible.sync="isDelete" width="30%">
      <div class="dialog-title-container" slot="title">
        <i class="el-icon-warning" style="color:#ff9900"/>提示
      </div>
      <div style="font-size:1rem">是否删除选中项？</div>
      <div slot="footer">
        <el-button @click="isDelete = false">取 消</el-button>
        <el-button type="primary" @click="deleteRoles(null)">确 定</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
import {deleteRolesById, getRoles, saveOrUpdateRole} from "@/api/role";
import {getMenuOptions} from "@/api/menu";
import {getResourceOptions} from "@/api/resource";

export default {
  data() {
    return {
      loading: true,
      isDelete: false,
      roleList: [],
      roleIdList: [],
      roleMenu: false,
      roleResource: false,
      roleForm: {
        roleName: "",
        roleLabel: "",
        resourceIdList: [],
        menuIdList: []
      },
      resourceList: [],
      menuList: [],
      keyword: null,
      current: 1,
      size: 10,
      count: 0
    }
  },
  created() {
    this.listRoles()
  },
  methods: {
    selectionChange(roleList) {
      this.roleIdList = []
      roleList.forEach(item => this.roleIdList.push(item.id))
    },
    openMenuDialog(role) {
      this.$nextTick(() => {
        this.$refs.menuTree.setCheckedKeys([])
      });
      this.$refs.roleTitle.innerHTML = role ? "修改角色" : "新增角色";
      if (role != null) {
        this.roleForm = role;
      } else {
        this.roleForm = {
          roleName: "",
          roleLabel: "",
          resourceIdList: [],
          menuIdList: []
        };
      }
      this.roleMenu = true
    },
    openResourceDialog(role) {
      this.$nextTick(() => {
        this.$refs.resourceTree.setCheckedKeys([])
      });
      this.roleForm = role
      this.roleResource = true
    },
    searchRoles() {
      this.current = 1
      this.listRoles()
    },
    listRoles() {
      getRoles({
        keyword: this.keyword,
        current: this.current,
        size: this.size
      }).then(({data}) => {
        this.roleList = data.data.records
        this.count = data.data.count
        this.loading = false
      });
      getMenuOptions().then(({data}) => {
        this.menuList = data.data
      });
      getResourceOptions().then(({data}) => {
        this.resourceList = data.data
      });
    },
    deleteRoles(id) {
      let ids = []
      if (id) {
        ids.push(id);
      } else {
        ids = this.roleIdList
      }
      deleteRolesById(ids).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listRoles();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.isDelete = false;
      });
    },
    saveOrUpdateRoleMenu() {
      if (this.roleForm.roleName.trim() === "") {
        this.$message.error("角色名不能为空");
        return false;
      }
      if (this.roleForm.roleLabel.trim() === "") {
        this.$message.error("权限标签不能为空");
        return false;
      }
      this.roleForm.resourceIdList = null;
      this.roleForm.menuIdList = this.$refs.menuTree.getCheckedKeys().concat(this.$refs.menuTree.getHalfCheckedKeys());
      saveOrUpdateRole(this.roleForm).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listRoles();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.roleMenu = false;
      });
    },
    saveOrUpdateRoleResource() {
      this.roleForm.menuIdList = null;
      this.roleForm.resourceIdList = this.$refs.resourceTree.getCheckedKeys();
      saveOrUpdateRole(this.roleForm).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listRoles();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.roleResource = false;
      });
    },
    sizeChange(size) {
      this.size = size;
      this.listRoles();
    },
    currentChange(current) {
      this.current = current;
      this.listRoles();
    }
  }
}
</script>
