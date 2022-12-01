<template>
  <div>
    <div class="comment-title"><svg-icon icon-class="comment"/>评论</div>
    <!--评论框-->
    <div class="comment-wrapper">
      <div style="display: flex;width: 100%">
        <v-avatar size="36">
          <img v-if="$store.state.avatar" :src="$store.state.avatar"/>
          <img v-else :src="$store.state.blogInfo.websiteConfig.touristAvatar"/>
        </v-avatar>
        <div style="width: 100%" class="ml-3">
          <div class="comment-input">
            <textarea v-model="commentContent" class="comment-textarea" placeholder="留下点什么吧..." auto-grow dense/>
          </div>
          <!--操作按钮-->
          <div class="emoji-container">
            <span @click="chooseEmoji = !chooseEmoji" :class="chooseEmoji ? 'emoji-btn-active' : 'emoji-btn'">
              <svg-icon icon-class="emoji"/>
            </span>
            <button @click="insertComment" class="upload-btn v-comment-btn" style="margin-left:auto">提交</button>
          </div>
          <!-- 表情框 -->
          <emoji @addEmoji="addEmoji" :chooseEmoji="chooseEmoji"/>
        </div>
      </div>
    </div>
    <!--评论详情-->
    <div v-if="count > 0 && reFresh">
      <!--评论列表-->
      <div class="comment-wrapper" v-for="(item, index) in commentList" :key="item.id">
        <!--评论头像-->
        <v-avatar class="comment-avatar" size="36">
          <img :src="item.avatar">
        </v-avatar>
        <div class="comment-meta">
          <!--用户名-->
          <div class="comment-user">
            <span v-if="!item.webSite">{{ item.nickname }}</span>
            <a v-else :href="item.webSite" target="_blank">{{ item.nickname }}</a>
            <v-icon style="margin-left: 4px" size="20" color="#ffa51e" v-if="item.userId === 1">
              mdi-check-decagram
            </v-icon>
          </div>
          <!--信息-->
          <div class="comment-info">
            <!--楼层-->
            <span style="margin-right: 10px">{{count - index}}楼</span>
            <!--评论时间-->
            <span style="margin-right: 10px">{{item.createTime | hour}}</span>
            <!--点赞-->
            <span style="margin-right: 4px" :class="isLike(item.id)" @click="like(item)">
              <svg-icon icon-class="like"/>
            </span>
            <!--点赞数-->
            <span v-show="item.likeCount > 0">{{item.likeCount}}</span>
            <!--回复-->
            <span class="reply-btn" @click="replyComment(item, index)">回复</span>
          </div>
          <!--评论内容-->
          <p v-html="item.commentContent" class="comment-content"></p>
          <!--回复人-->
          <div style="display: flex" v-for="reply in item.replyDTOList" :key="reply.id">
            <!--头像-->
            <v-avatar size="36" class="comment-avatar">
              <img :src="reply.avatar"/>
            </v-avatar>
            <div class="reply-meta">
              <!--用户名-->
              <div class="comment-user">
                <span v-if="!reply.webSite">{{ reply.nickname }}</span>
                <a v-else :href="reply.webSite" target="_blank">{{ reply.nickname }}</a>
                <v-icon size="20" color="#ffa51e" v-if="reply.userId === 1">
                  mdi-check-decagram
                </v-icon>
              </div>
              <!--信息-->
              <div class="comment-info">
                <!--评论时间-->
                <span style="margin-right: 10px">{{reply.createTime | hour}}</span>
                <!--点赞-->
                <span style="margin-right: 4px" :class="isLike(reply.id)" @click="like(reply)">
                  <svg-icon icon-class="like"/>
                </span>
                <!--点赞数-->
                <span v-show="reply.likeCount > 0">{{reply.likeCount}}</span>
                <!--回复-->
                <span class="reply-btn" @click="replyComment(reply, index)">回复</span>
              </div>
              <!--回复内容-->
              <p class="comment-content">
                <!--回复用户名-->
                <template v-if="reply.replyUserId !== item.userId">
                  <span v-if="!reply.replyWebSite" class="ml-1">@{{reply.replyNickname}}</span>
                  <a v-else :href="reply.replyWebSite" target="_blank" class="comment-nickname ml-1">
                    @{{ reply.replyNickname }}
                  </a> ，
                </template>
                <span v-html="reply.commentContent"/>
              </p>
            </div>
          </div>
          <!--回复数量-->
          <div ref="check" v-show="item.replyCount > 3" class="mb-3" style="font-size:0.75rem;color:#6d757a">
            共 <b>{{item.replyCount}}</b> 条回复，
            <span @click="checkReplies(item, index)" style="color:#00a1d6;cursor:pointer">点击查看</span>
          </div>
          <!--回复分页-->
          <div ref="paging" class="mb-3" style="font-size:0.75rem;color:#222;display:none">
            <span style="padding-right:10px">
              共{{ Math.ceil(item.replyCount / 5) }}页
            </span>
            <Paging
                ref="page"
                :total-page="Math.ceil(item.replyCount / 5)"
                :comment-id="item.id"
                :index="index"
                @changeReplyCurrent="changeReplyCurrent"
            />
          </div>
          <!--回复框-->
          <Reply :type="type" ref="reply" @reloadReply="reloadReply"/>
        </div>
      </div>
      <!--加载按钮-->
      <div class="load-wrapper">
        <v-btn outlined v-if="count > commentList.length" @click="getComments">加载更多...</v-btn>
      </div>
    </div>
    <!--没有评论提示-->
    <div v-else style="padding:1.25rem;text-align:center">来发评论吧~</div>
  </div>
</template>

<script>
import Emoji from '../components/Emoji'
import Paging from "../components/Paging";
import Reply from "../components/Reply";
import EmojiList from '../assets/js/emoji'
import {listComments, listRepliesById, saveComment, saveCommentLike} from "../api/comment";
export default {
  components: {Emoji, Paging, Reply},
  props: {
    type: {
      type: Number
    }
  },
  created() {
    this.getComments()
  },
  data() {
    return {
      reFresh: true,
      commentContent: '',
      commentList: [],
      chooseEmoji: false,
      current: 1,
      count: 0
    }
  },
  methods: {
    getComments() {
      const arr = this.$route.path.split('/')
      let params = {
        current: this.current,
        type: this.type
      }
      if (this.type === 3) {
        params.topicId = arr[2]
      }
      listComments(params).then(res => {
        if (res.flag === true) {
          if (this.current === 1) {
            this.commentList = res.data.records
          } else {
            this.commentList.push(...res.data.records)
          }
          this.current++
          this.count = res.data.count
          this.$emit('getCommentCount', this.count)
        }
      });
    },
    insertComment() {
      if (!this.$store.state.userId) {
        this.$store.state.loginFlag = true
        return false
      }
      if (this.commentContent.trim().length === 0) {
        this.$toast({type: 'warning', message: '评论内容不能为空'})
        return false
      }
      let reg = /\[.+?\]/g
      this.commentContent = this.commentContent.replace(reg, (str) => {
        return `<img src="${EmojiList[str]}" width="22px" height="20px" style="padding: 0 1px"/>`
      });
      let arr = this.$route.path.split('/')
      let comment = {
        commentContent: this.commentContent,
        type: this.type
      }
      if (this.type === 3) {
        comment.topicId = arr[2]
      }
      this.commentContent = ''
      saveComment(comment).then(res => {
        if (res.flag === true) {
          this.current = 1
          this.getComments()
          const isReview = this.$store.state.blogInfo.websiteConfig.isCommentReview
          // 是否开启评论审核(0 开启 1 关闭)
          if (isReview) {
            this.$toast({type: 'success', message: '评论成功，正在审核中'})
          } else {
            this.$toast({type: 'success', message: '评论成功'})
          }
        } else {
          this.$toast({type: 'error', message: res.message})
        }
      })
    },
    replyComment(item, index) {
      this.$refs.reply.forEach(item => {
        item.$el.style.display = 'none'
      })
      //传值给回复框
      this.$refs.reply[index].commentContent = ''
      this.$refs.reply[index].nickname = item.nickname
      this.$refs.reply[index].replyUserId = item.userId
      this.$refs.reply[index].parentId = this.commentList[index].id
      this.$refs.reply[index].chooseEmoji = false
      this.$refs.reply[index].index = index
      this.$refs.reply[index].$el.style.display = 'block'
    },
    checkReplies(item, index) {
      listRepliesById(item.id, {current: 1, size: 5}).then(res => {
        if (res.flag === true) {
          this.$refs.check[index].style.display = 'none'
          item.replyDTOList = res.data
          // 超过1页才显示分页
          if (Math.ceil(item.replyCount / 5) > 1) {
            this.$refs.paging[index].style.display = 'flex'
          }
        }
      })
    },
    like(comment) {
      if (!this.$store.state.userId) {
        this.$store.state.loginFlag = true
        return false
      }
      saveCommentLike(comment.id).then(res => {
        if (res.flag === true) {
          if (this.$store.state.commentLikeSet.indexOf(comment.id) !== -1) {
            this.$set(comment, 'likeCount', comment.likeCount - 1)
          } else {
            this.$set(comment, 'likeCount', comment.likeCount + 1)
          }
          this.$store.commit('LIKE_COMMENT', comment.id)
        }
      });
    },
    addEmoji(key) {
      this.commentContent += key
    },
    changeReplyCurrent(current, index, commentId) {
      listRepliesById(commentId, {current: current, size: 5}).then(res => {
        if (res.flag === true) {
          this.commentList[index].replyDTOList = res.data
        }
      });
    },
    reloadReply(index) {
      listRepliesById(this.commentList[index].id, {current: this.$refs.page[index].current}).then(res => {
        if (res.flag) {
          this.commentList[index].replyCount++;
          //回复大于5条展示分页
          if (this.commentList[index].replyCount > 5) {
            this.$refs.paging[index].style.display = 'flex'
          }
          this.$refs.check[index].style.display = 'none'
          this.$refs.reply[index].$el.style.display = 'none'
          this.commentList[index].replyDTOList = res.data
        }
      });
    },
  },
  watch: {
    commentList() {
      this.reFresh = false;
      this.$nextTick(() => {
        this.reFresh = true;
      });
    }
  },
  computed: {
    isLike() {
      return (id) => {
        let commentLikeSet = this.$store.state.commentLikeSet
        return commentLikeSet.indexOf(id) !== -1 ? 'like-active' : 'like'
      }
    },
  }
}
</script>

<style scoped>
.comment-wrapper {
  margin-top: 20px;
  display: flex;
  padding: 16px 20px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.1);
  box-shadow: 0 3px 8px 6px rgb(7 17 27 / 6%);
  transition: all 0.3s ease 0s;
}
.comment-wrapper:hover {
  box-shadow: 0 5px 10px 8px rgb(7 17 27 / 16%);
  transform: translateY(-3px);
}
.comment-title {
  display: flex;
  align-items: center;
  font-size: 1.25rem;
  font-weight: bold;
  line-height: 40px;
  margin-top: 20px;
}
.comment-title .svg-icon {
  font-size: 1.5rem;
  margin-right: 5px;
}
.count {
  padding: 5px;
  line-height: 1.75;
  font-size: 1.25rem;
  font-weight: bold;
}
.comment-meta {
  margin-left: 0.8rem;
  width: 100%;
  border-bottom: 1px dashed #f5f5f5;
}
.reply-meta {
  margin-left: 0.8rem;
  width: 100%;
}
.comment-user {
  font-size: 15px;
  line-height: 1.75;
}
.comment-user a {
  color: #1abc9c !important;
  font-weight: 500;
  transition: 0.3s all;
}
.comment-nickname {
  text-decoration: none;
  color: #1abc9c !important;
  font-weight: 500;
}
.comment-info {
  line-height: 1.75;
  font-size: 0.75rem;
  color: #b3b3b3;
}
.reply-btn {
  cursor: pointer;
  float: right;
  color: #ef2f11;
}
.comment-content {
  font-size: 0.875rem;
  line-height: 1.75;
  padding-top: 0.625rem;
  white-space: pre-line;
  word-wrap: break-word;
  word-break: break-all;
}
.comment-avatar {
  transition: all 0.5s;
}
.comment-avatar:hover {
  transform: rotate(360deg);
}
.like {
  cursor: pointer;
  font-size: 0.875rem;
}
.like:hover {
  color: #eb5055;
}
.like-active {
  cursor: pointer;
  font-size: 0.875rem;
  color: #eb5055;
}
.load-wrapper {
  margin-top: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.load-wrapper button {
  background-color: #49b1f5;
  color: #fff;
}
</style>
