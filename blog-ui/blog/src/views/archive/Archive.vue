<template>
  <div>
    <!--banner-->
    <div class="banner" :style="cover">
      <h1 class="banner-title">归档</h1>
    </div>
    <!--归档列表-->
    <v-card class="blog-container">
      <timeline>
        <timeline-title> 目前共计{{ count }}篇文章，继续加油</timeline-title>
        <timeline-item v-for="item in archiveList" :key="item.id">
          <!--文章创建时间-->
          <span class="time">{{item.createTime | date}}</span>
          <!--文章标题-->
          <router-link :to="`/articles/${item.id}`" style="color:#666;text-decoration: none">{{item.articleTitle}}</router-link>
        </timeline-item>
      </timeline>
      <!-- 分页按钮 -->
      <v-pagination
          color="#00C4B6"
          v-model="current"
          :length="Math.ceil(count / 10)"
          total-visible="7"
      />
    </v-card>
  </div>
</template>

<script>
import {Timeline, TimelineTitle, TimelineItem} from 'vue-cute-timeline'
import {listArchives} from "../../api/article";
export default {
  components: {Timeline, TimelineTitle, TimelineItem},
  data() {
    return {
      archiveList: [],
      current: 1,
      count: 0
    }
  },
  created() {
    this.getArchives()
  },
  methods: {
    getArchives() {
      listArchives({current: this.current}).then(res => {
        if (res.flag === true) {
          this.archiveList = res.data.records
          this.count = res.data.count
        }
      });
    },
  },
  watch: {
    current(value) {
      listArchives({current: value}).then(res => {
        if (res.flag === true) {
          this.archiveList = res.data.records
          this.count = res.data.count
        }
      });
    },
  },
  computed: {
    cover() {
      let cover = ''
      this.$store.state.blogInfo.pageList.forEach(item => {
        if (item.pageLabel === 'archive') {
          cover = item.pageCover
        }
      });
      return `background: url(${cover}) center center / cover no-repeat`
    },
  }
}
</script>

<style scoped>
.time {
  font-size: 0.75rem;
  color: #555;
  margin-right: 1rem;
}
</style>
