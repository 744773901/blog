<template>
  <el-card class="main-card">
    <!-- 标题 -->
    <div class="title">{{ $route.name }}</div>
    <!-- 文章状态分类 -->
    <div class="article-status-menu">
      <span>状态</span>
      <span
          v-for="(item, index) in statusList"
          :key="index"
          @click="changeStatus(item.key)"
          :class="isActive(item.key)">{{ item.name }}</span>
    </div>
    <!-- 文章操作 -->
    <div class="operation-container">
      <el-button
          v-if="isDelete === 0"
          type="danger"
          size="small"
          icon="el-icon-delete"
          :disabled="articleIdList.length === 0"
          @click="updateIsDelete = true">批量删除
      </el-button>
      <el-button
          v-else
          type="danger"
          size="small"
          icon="el-icon-delete"
          :disabled="articleIdList.length === 0"
          @click="remove = true">批量删除
      </el-button>
      <!-- 条件搜索 -->
      <div style="margin-left: auto">
        <!-- 文章类型 -->
        <el-select clearable v-model="type" placeholder="选择文章类型" size="small">
          <el-option v-for="item in typeList" :value="item.value" :label="item.label" :key="item.value"/>
        </el-select>
        <!-- 文章分类 -->
        <el-select clearable filterable v-model="categoryId" placeholder="选择文章分类" size="small">
          <el-option v-for="item in categoryList" :value="item.id" :label="item.categoryName" :key="item.id"/>
        </el-select>
        <!-- 文章标签 -->
        <el-select clearable filterable v-model="tagId" placeholder="选择文章标签" size="small">
          <el-option v-for="item in tagList" :value="item.id" :label="item.tagName" :key="item.id"/>
        </el-select>
        <!-- 文章标题 -->
        <el-input clearable @clear="listArticles" v-model="keyword" @keyup.enter.native="searchArticles" prefix-icon="el-icon-search"
                  size="small" placeholder="请输入文章名" style="width:200px"/>
        <el-button
            type="primary"
            size="small"
            icon="el-icon-search"
            style="margin-left:1rem; width: 80px"
            @click="searchArticles"
        >搜索
        </el-button>
      </div>
    </div>
    <!-- 文章展示 -->
    <el-table border :data="articleList" @selection-change="selectionChange" v-loading="loading">
      <!-- 表格列 -->
      <el-table-column type="selection" width="55"/>
      <!-- 文章封面 -->
      <el-table-column prop="articleCover" label="文章封面" width="180" align="center">
        <template slot-scope="scope">
          <el-image
              class="article-cover"
              :src="scope.row.articleCover ? scope.row.articleCover : 'https://static.talkxj.com/articles/c5cc2b2561bd0e3060a500198a4ad37d.png'"/>
          <svg-icon v-if="scope.row.status === 1" icon-class="public" class-name="article-status-icon"/>
          <svg-icon v-if="scope.row.status === 2" icon-class="private" class-name="article-status-icon"/>
          <svg-icon v-if="scope.row.status === 3" icon-class="draft" class-name="article-status-icon"/>
        </template>
      </el-table-column>
      <!-- 文章标题 -->
      <el-table-column prop="articleTitle" label="标题" align="center"/>
      <!-- 文章分类 -->
      <el-table-column prop="categoryName" label="分类" width="110" align="center"/>
      <!-- 文章标签 -->
      <el-table-column prop="tagDTOList" label="标签" width="170" align="center">
        <template slot-scope="scope">
          <el-tag v-for="item of scope.row.tagDTOList" :key="item.tagId" style="margin-right:0.2rem;margin-top:0.2rem">
            {{ item.tagName }}
          </el-tag>
        </template>
      </el-table-column>
      <!-- 文章浏览量 -->
      <el-table-column prop="viewsCount" label="浏览量" width="70" align="center">
        <template slot-scope="scope">
          <span v-if="scope.row.viewsCount">{{ scope.row.viewsCount }}</span>
          <span v-else>0</span>
        </template>
      </el-table-column>
      <!-- 文章点赞量 -->
      <el-table-column prop="likeCount" label="点赞量" width="70" align="center">
        <template slot-scope="scope">
          <span v-if="scope.row.likeCount">{{ scope.row.likeCount }}</span>
          <span v-else>0</span>
        </template>
      </el-table-column>
      <!-- 文章类型 -->
      <el-table-column prop="type" label="类型" width="80" align="center">
        <template slot-scope="scope">
          <el-tag :type="articleType(scope.row.type).tagType">
            {{ articleType(scope.row.type).name }}
          </el-tag>
        </template>
      </el-table-column>
      <!-- 文章发表时间 -->
      <el-table-column prop="createTime" label="发表时间" width="200" align="center">
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px"/>
          {{ scope.row.createTime | datetime }}
        </template>
      </el-table-column>
      <!-- 文章置顶 -->
      <el-table-column prop="isTop" label="置顶" width="80" align="center">
        <template slot-scope="scope">
          <el-switch
              v-model="scope.row.isTop"
              active-color="#13ce66"
              inactive-color="#F4F4F5"
              :disabled="scope.row.isDelete === 1"
              :active-value="1"
              :inactive-value="0"
              @change="updateTop(scope.row)"/>
        </template>
      </el-table-column>
      <!-- 列操作 -->
      <el-table-column label="操作" align="center" width="150">
        <template slot-scope="scope">
          <el-button
              type="primary"
              size="mini"
              @click="editArticle(scope.row.id)"
              v-if="scope.row.isDelete === 0">
            编辑
          </el-button>
          <el-popconfirm
              title="确定删除吗？"
              style="margin-left:10px"
              @confirm="updateArticleDelete(scope.row.id)"
              v-if="scope.row.isDelete === 0"
          >
            <el-button size="mini" type="danger" slot="reference">
              删除
            </el-button>
          </el-popconfirm>
          <el-popconfirm
              title="确定恢复吗？"
              v-if="scope.row.isDelete === 1"
              @confirm="updateArticleDelete(scope.row.id)"
          >
            <el-button size="mini" type="success" slot="reference">
              恢复
            </el-button>
          </el-popconfirm>
          <el-popconfirm
              style="margin-left:10px"
              v-if="scope.row.isDelete === 1"
              title="确定彻底删除吗？"
              @confirm="deleteArticles(scope.row.id)"
          >
            <el-button size="mini" type="danger" slot="reference">
              删除
            </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <!-- 文章分页 -->
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
    <!-- 批量逻辑删除对话框 -->
    <el-dialog :visible.sync="updateIsDelete" width="30%">
      <div class="dialog-title-container" slot="title">
        <i class="el-icon-warning" style="color:#ff9900"/>提示
      </div>
      <div style="font-size:1rem">是否删除选中项？</div>
      <div slot="footer">
        <el-button @click="updateIsDelete = false">取 消</el-button>
        <el-button type="primary" @click="updateArticleDelete(null)">
          确 定
        </el-button>
      </div>
    </el-dialog>
    <!-- 批量物理删除对话框 -->
    <el-dialog :visible.sync="remove" width="30%">
      <div class="dialog-title-container" slot="title">
        <i class="el-icon-warning" style="color:#ff9900"/>提示
      </div>
      <div style="font-size:1rem">是否彻底删除选中项？</div>
      <div slot="footer">
        <el-button @click="remove = false">取 消</el-button>
        <el-button type="primary" @click="deleteArticles(null)">
          确 定
        </el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
import {deleteArticle, getArticles, updateArticleLogicDelete, updateArticleTop} from "@/api/article";
import {getCategoryByOption} from "@/api/category";
import {getTagByOption} from "@/api/tag";

export default {
  name: "ArticleList",
  data() {
    return {
      loading: true,
      updateIsDelete: false,
      remove: false,
      typeList: [
        {
          value: 1,
          label: "原创"
        },
        {
          value: 2,
          label: "转载"
        },
        {
          value: 3,
          label: "翻译"
        }
      ],
      statusList: [
        {key: 'all', name: '全部'},
        {key: 'public', name: '公开'},
        {key: 'private', name: '私密'},
        {key: 'draft', name: '草稿箱'},
        {key: 'deleted', name: '回收站'}
      ],
      activeStatus: "all",
      articleList: [],
      articleIdList: [],
      categoryList: [],
      tagList: [],
      keyword: null,
      type: null,
      categoryId: null,
      tagId: null,
      isDelete: 0,
      status: null,//文章状态
      current: 1,
      size: 10,
      count: 0
    }
  },
  created() {
    this.listArticles();
    this.listCategories();
    this.listTags();
  },
  methods: {
    changeStatus(status) {
      switch (status) {
        case "all":
          this.isDelete = 0;
          this.status = null;
          break;
        case "public":
          this.isDelete = 0;
          this.status = 1;
          break;
        case "private":
          this.isDelete = 0;
          this.status = 2;
          break;
        case "draft":
          this.isDelete = 0;
          this.status = 3;
          break;
        case "deleted":
          this.isDelete = 1;
          this.status = null;
          break;
      }
      this.current = 1
      this.activeStatus = status
    },
    selectionChange(articleList) {
      this.articleIdList = []
      articleList.forEach(item => this.articleIdList.push(item.id))
    },
    searchArticles() {
      this.current = 1
      this.listArticles()
    },
    editArticle(articleId) {
      this.$router.push({path: `/article/${articleId}`})
    },
    updateArticleDelete(id) {
      let params = {}
      params.isDelete = this.isDelete === 0 ? 1 : 0
      if (id !== null) {
        params.ids = [id];
      } else {
        params.ids = this.articleIdList
      }
      updateArticleLogicDelete(params).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: '成功',
            message: data.message
          });
          this.listArticles();
        } else {
          this.$notify.error({
            title: '失败',
            message: data.message
          });
        }
        this.updateIsDelete = false;
      });
    },
    deleteArticles(id) {
      let ids = []
      if (id !== null) {
        ids.push(id);
      } else {
        ids = this.articleIdList
      }
      deleteArticle(ids).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: '成功',
            message: data.message
          });
          this.listArticles();
        } else {
          this.$notify.error({
            title: '失败',
            message: data.message
          });
        }
        this.remove = false;
      });
    },
    sizeChange(size) {
      this.size = size
      this.listArticles()
    },
    currentChange(current) {
      this.current = current
      this.listArticles()
    },
    updateTop(article) {
      let params = {id: article.id, isTop: article.isTop}
      updateArticleTop(params).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: "操作成功"
          });
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.remove = false
      });
    },
    listArticles() {
      let params = {
        current: this.current,
        size: this.size,
        keyword: this.keyword,
        categoryId: this.categoryId,
        status: this.status,
        tagId: this.tagId,
        type: this.type,
        isDelete: this.isDelete
      }
      getArticles(params).then(({data}) => {
        this.count = data.data.count
        this.articleList = data.data.records
        this.loading = false
      });
    },
    listCategories() {
      getCategoryByOption().then(({data}) => {
        this.categoryList = data.data
      });
    },
    listTags() {
      getTagByOption().then(({data}) => {
        this.tagList = data.data
      });
    },
  },
  computed: {
    isActive() {
      return (status) => {
        return this.activeStatus === status ? "active-status" : "status"
      }
    },
    articleType() {
      return (type) => {
        let tagType = ""
        let name = ""
        switch (Number(type)) {
          case 1:
            tagType = "success"
            name = "原创"
            break;
          case 2:
            tagType = "primary"
            name = "转载"
            break;
          case 3:
            tagType = "warning"
            name = "翻译"
            break;
        }
        return {
          tagType: tagType,
          name: name
        };
      };
    }
  },
  watch: {
    type() {
      this.current = 1;
      this.listArticles();
    },
    categoryId() {
      this.current = 1;
      this.listArticles();
    },
    tagId() {
      this.current = 1;
      this.listArticles();
    },
    status() {
      this.current = 1;
      this.listArticles();
    },
    isDelete() {
      this.current = 1;
      this.listArticles();
    }
  }
}
</script>

<style scoped>
.operation-container {
  margin-top: 1.5rem;
}

.article-status-menu {
  font-size: 14px;
  margin-top: 40px;
  color: #999;
}

.article-status-menu span {
  margin-right: 24px;
}

.status {
  cursor: pointer;
}

.active-status {
  cursor: pointer;
  color: #333;
  font-weight: bold;
}

.article-cover {
  position: relative;
  width: 100%;
  height: 90px;
  border-radius: 4px;
}

.article-cover::after {
  content: "";
  background: rgba(0, 0, 0, 0.3);
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
}

.article-status-icon {
  color: #fff;
  font-size: 1.5rem;
  position: absolute;
  right: 1rem;
  bottom: 1.4rem;
}

.el-select {
  width: 180px;
  margin-right: 1rem;
}
</style>
