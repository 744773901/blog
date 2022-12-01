<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
import {getMenu, report} from "@/api/menu";

export default {
  created() {
    //刷新页面查询用户菜单
    if (this.$store.state.token) {
      getMenu();
    }
    // 上传访客信息
    report()
  }
};
</script>
