<template>
  <el-submenu
      v-if="!route.hidden && route.children && route.children.length > 0"
      :index="route.path"
      :key="route.path">
    <template slot="title">
      <!--<i :class="route.icon"/>-->
      <svg-icon slot="prefix" :icon-class="route.icon"/>
      <span slot="title">{{ route.name }}</span>
    </template>
    <SideBarItem
        v-for="(item, index) in route.children"
        :route="item"
        :index="item.path"
        :key="index"/>
  </el-submenu>
  <!--<el-menu-item v-else-if="!route.hidden" :index="route.path" :key="route.path">-->
  <!--  <template slot="title">-->
  <!--      <svg-icon slot="prefix" :icon-class="route.icon"/>-->
  <!--      <span slot="title">{{ route.name }}</span>-->
  <!--  </template>-->
  <!--</el-menu-item>-->
  <!--<el-menu-item v-else :index="route.path" :key="route.path">-->
  <!--  <template slot="title">-->
  <!--    <svg-icon slot="prefix" :icon-class="route.icon"/>-->
  <!--    <span slot="title">{{ route.name }}</span>-->
  <!--  </template>-->
  <!--</el-menu-item>-->
  <el-menu-item v-else-if="!route.hidden" :index="route.path" :key="route.path">
    <!--<i :class="route.icon"/>-->
    <template slot="title">
      <svg-icon slot="prefix" :icon-class="route.icon"/>
      <span slot="title">{{ route.name }}</span>
    </template>
  </el-menu-item>
  <el-menu-item v-else :index="route.path" :key="route.path">
    <!--<i :class="route.icon"/>-->
    <template slot="title">
      <svg-icon slot="prefix" :icon-class="route.icon"/>
      <span slot="title">{{ route.name }}</span>
    </template>
  </el-menu-item>
</template>

<script>
export default {
  name: "SideBarItem",
  props: {
    route: {}
  }
}
</script>
