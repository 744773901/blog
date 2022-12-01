<template>
  <div class="right-side" :style="isShow">
    <div @click="check" :class="'setting-container ' + 'right-side-config-hide ' + isOut">
      <svg-icon :icon-class="icon"/>
    </div>
    <div class="setting-container" @click="show">
      <svg-icon class-name="circle" icon-class="setting"/>
    </div>
    <div @click="backTop" class="setting-container">
      <svg-icon icon-class="back-top"/>
    </div>
  </div>
</template>

<script>
export default {
  mounted() {
    window.addEventListener("scroll", this.scrollToTop);
  },
  destroyed() {
    window.removeEventListener("scroll", this.scrollToTop);
  },
  data() {
    return {
      isShow: "",
      isOut: "right-side-out",
      icon: "moon"
    }
  },
  methods: {
    //回到顶部
    backTop() {
      window.scrollTo({
        behavior: "smooth",
        top: 0
      });
    },
    //计算距离顶部的高度，当高度大于100显示回顶部图标，小于100则隐藏
    scrollToTop() {
      this.scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop
      if (this.scrollTop > 20) {
        this.isShow = "opacity: 1;transform: translateX(-38px);"
      } else {
        this.isShow = "";
      }
    },
    show() {
      const flag = this.isOut === "right-side-out";
      this.isOut = flag ? "right-side-in" : "right-side-out";
    },
    check() {
      const flag = this.icon === "moon";
      this.icon = flag ? "sun" : "moon";
      this.$vuetify.theme.dark = !this.$vuetify.theme.dark;
    }
  }
}
</script>

<style scoped>
.right-side {
  z-index: 4;
  position: fixed;
  right: -38px;
  bottom: 85px;
  transition: all 0.5s;
}
.right-side-config-hide {
  transform: translate(35px, 0);
}
.right-side-out {
  animation: rightsideOut 0.3s;
}
.right-side-in {
  transform: translate(0, 0) !important;
  animation: rightsideIn 0.3s;
}
.right-side-icon, .setting-container {
  display: block;
  margin-bottom: 2px;
  width: 30px;
  height: 30px;
  background-color: #49b1f5;
  color: #fff;
  text-align: center;
  font-size: 16px;
  line-height: 30px;
  cursor: pointer;
}
.right-side-icon:hover,
.setting-container:hover {
  background-color: #ff7242;
}
.setting-container .circle {
  animation: turn-around 2s linear infinite;
}
@keyframes turn-around {
  0% {
    transform: rotate(0);
  }
  100% {
    transform: rotate(360deg);
  }
}
@keyframes rightsideOut {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(30px, 0);
  }
}
@keyframes rightsideIn {
  0% {
    transform: translate(30px, 0);
  }
  100% {
    transform: translate(0, 0);
  }
}
</style>
