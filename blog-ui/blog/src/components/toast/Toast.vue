<template>
  <v-snackbar v-model="show" top :timeout="2000" :color="color">
    <svg-icon :icon-class="icon" style="margin-right: 10px"/>{{ message }}
  </v-snackbar>
</template>

<script>
export default {
  data() {
    return {
      show: false,
      message: "",
      type: "normal",
      color: "#49b1f5",
      icon: ""
    }
  },
  watch: {
    type(value) {
      switch (value) {
        case "error":
          this.color = "#F56C6C";
          this.icon = "error";
          break;
        case "success":
          this.color = "#52C41A";
          this.icon = "success";
          break;
        case "warning":
          this.color = "#F57C00";
          this.icon = "warning";
      }
    }
  }
}
</script>
