<template>
  <div>
    <el-menu
        class="side-nav-bar"
        router
        :collapse="$store.state.collapse"
        :default-active="$route.path"
        background-color="#304156"
        text-color="#BFCBD9"
        active-text-color="#409EFF">


      <template v-for="(menu, index) in $store.state.userMenuList">
        <!-- 二级菜单 -->
        <el-submenu
            v-if="!menu.hidden && menu.name && menu.children && menu.children.length > 0"
            :index="menu.path"
            :key="index">
          <template slot="title">
            <svg-icon :icon-class="menu.icon"/>
            <span slot="title">{{ menu.name }}</span>
          </template>
          <el-menu-item v-for="(item, index) in menu.children" v-if="!item.hidden" :index="item.path" :key="index">
            <template slot="title">
              <svg-icon :icon-class="item.icon"/>
              <span slot="title">{{ item.name }}</span>
            </template>
          </el-menu-item>
        </el-submenu>
        <!-- 一级菜单 -->
        <el-menu-item v-else-if="!menu.hidden" :index="menu.path" :key="index">
          <svg-icon :icon-class="menu.children[0].icon"/>
          <template slot="title">
            <span slot="title">{{ menu.children[0].name }}</span>
          </template>
        </el-menu-item>
      </template>
      <!--<side-bar-item-->
      <!--    v-for="(item, index) in $store.state.userMenuList"-->
      <!--    :index="index"-->
      <!--    :key="index"-->
      <!--    :route="item"/>-->
    </el-menu>
  </div>
</template>

<script>
import SideBarItem from "@/layout/components/SideBarItem";

export default {
  name: "SideBar",
  components: {SideBarItem}
}
</script>

<style scoped>
.side-nav-bar:not(.el-menu--collapse) {
  width: 210px;
}

.side-nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  overflow-x: hidden;
  overflow-y: auto;
}

.side-nav-bar .svg-icon {
  margin-right: 1rem;
}

*::-webkit-scrollbar {
  width: 0.5rem;
  height: 1px;
}

*::-webkit-scrollbar-thumb {
  border-radius: 0.5rem;
  background-color: rgba(144, 147, 153, 0.3);
}
</style>
