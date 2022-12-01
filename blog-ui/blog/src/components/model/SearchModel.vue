<template>
  <v-dialog v-model="searchFlag" :fullscreen="isMobile" max-width="600">
    <v-card class="search-wrapper" style="border-radius:4px">
      <div class="mb-3">
        <span class="search-title">本地搜索</span>
        <!-- 关闭按钮 -->
        <v-icon class="float-right" @click="searchFlag = false">
          mdi-close
        </v-icon>
      </div>
      <!-- 输入框 -->
      <div class="search-input-wrapper">
        <v-icon>mdi-magnify</v-icon>
        <input v-model="keyword" placeholder="输入文章标题或内容..."/>
      </div>
      <!-- 搜索结果 -->
      <div class="search-result-wrapper">
        <hr class="divider"/>
        <ul>
          <li class="search-result" v-for="item of articleList" :key="item.id">
            <!-- 文章标题 -->
            <a @click="goTo(item.id)" v-html="item.articleTitle"/>
            <!-- 文章内容 -->
            <p
                class="search-result-content text-justify"
                v-html="item.articleContent"
            />
          </li>
        </ul>
        <!-- 搜索结果不存在提示 -->
        <div
            v-show="keyword.length && articleList.length === 0"
            style="font-size:0.875rem"
        >
          找不到您查询的内容：{{ keyword }}
        </div>
      </div>
    </v-card>
  </v-dialog>
</template>

<script>
import {searchArticle} from "../../api/article";

export default {
  data() {
    return {
      keyword: "",
      articleList: []
    }
  },
  methods: {
    search(params) {
      searchArticle(params).then(res => {
        if (res.flag) {
          this.articleList = res.data;
        }
      });
    },
    goTo(articleId) {
      this.$store.state.searchFlag = false
      this.$router.push({path: `/articles/${articleId}`})
    },
  },
  watch: {
    keyword(value) {
      const params = {current: 1, keyword: value}
      this.search(params)
    },
  },
  computed: {
    searchFlag: {
      get() {
        return this.$store.state.searchFlag
      },
      set(value) {
        this.$store.state.searchFlag = value
      }
    },
    isMobile() {
      const clientWidth = document.documentElement.clientWidth;
      if (clientWidth > 960) {
        return false;
      }
      return true;
    }
  }
}
</script>

<style scoped>
.search-wrapper {
  padding: 1.25rem;
  height: 100%;
  background: #fff !important;
}
.search-title {
  color: #49b1f5;
  font-size: 1.25rem;
  line-height: 1;
}
.search-input-wrapper {
  display: flex;
  padding: 5px;
  height: 35px;
  width: 100%;
  border: 2px solid #8e8cd8;
  border-radius: 2rem;
}
.search-input-wrapper input {
  width: 100%;
  margin-left: 5px;
  outline: none;
}
@media (min-width: 960px) {
  .search-result-wrapper {
    padding-right: 5px;
    height: 450px;
    overflow: auto;
  }
}
@media (max-width: 959px) {
  .search-result-wrapper {
    height: calc(100vh - 110px);
    overflow: auto;
  }
}
.search-result a {
  color: #555;
  font-weight: bold;
  border-bottom: 1px solid #999;
  text-decoration: none;
}
.search-result-content {
  color: #555;
  cursor: pointer;
  border-bottom: 1px dashed #ccc;
  padding: 5px 0;
  line-height: 2;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}
.divider {
  margin: 20px 0;
  border: 2px dashed #d2ebfd;
}
</style>
