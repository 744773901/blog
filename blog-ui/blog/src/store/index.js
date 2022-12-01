import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    token: null,
    drawer: false,
    searchFlag: false,
    loginFlag: false,
    registerFlag: false,
    forgetFlag: false,
    emailFlag: false,
    userId: null,
    avatar: null,
    nickname: null,
    intro: null,
    webSite: null,
    loginType: null,
    email: null,
    loginUrl: '',
    articleLikeSet: [],
    commentLikeSet: [],
    talkLikeSet: [],
    blogInfo: {}
  },
  mutations: {
    LOGIN(state, data) {
      state.token = data.token
      state.userId = data.userInfo.id
      state.avatar = data.userInfo.avatar
      state.nickname = data.userInfo.nickname
      state.intro = data.userInfo.intro
      state.webSite = data.userInfo.webSite
      state.loginType = data.userInfo.loginType
      state.email = data.userInfo.email
      state.articleLikeSet = data.userInfo.articleLikeSet
      state.commentLikeSet = data.userInfo.commentLikeSet
      state.talkLikeSet = data.userInfo.talkLikeSet
    },
    LOGOUT(state) {
      this.commit('REMOVE_TOKEN')
      state.userId = null
      state.avatar = null
      state.nickname = null
      state.intro = null
      state.webSite = null
      state.loginType = null
      state.email = null
      state.articleLikeSet = []
      state.commentLikeSet = []
      state.talkLikeSet = []
    },
    SAVE_LOGIN_URL(state, url) {
      state.loginUrl = url
    },
    REMOVE_TOKEN(state) {
      state.token = null
    },
    CLOSE_MODEL(state) {
      state.registerFlag = false;
      state.loginFlag = false;
      state.searchFlag = false;
      state.emailFlag = false;
    },
    SET_BLOG_INFO(state, info) {
      state.blogInfo = info
    },
    LIKE_TALK(state, talkId) {
      let talkLikeSet = state.talkLikeSet
      if (talkLikeSet.indexOf(talkId) !== -1) {
        talkLikeSet.splice(talkLikeSet.indexOf(talkId), 1)
      } else {
        talkLikeSet.push(talkId)
      }
    },
    LIKE_COMMENT(state, commentId) {
      let commentLikeSet = state.commentLikeSet
      if (commentLikeSet.indexOf(commentId) !== -1) {
        commentLikeSet.splice(commentLikeSet.indexOf(commentId), 1)
      } else {
        commentLikeSet.push(commentId)
      }
    },
    LIKE_ARTICLE(state, articleId) {
      let articleLikeSet = state.articleLikeSet
      if (articleLikeSet.indexOf(articleId) !== -1) {
        articleLikeSet.splice(articleLikeSet.indexOf(articleId), 1)
      } else {
        articleLikeSet.push(articleId)
      }
    },
    UPDATE_USERINFO(state, userInfo) {
      state.nickname = userInfo.nickname
      state.webSite = userInfo.webSite
      state.intro = userInfo.intro
    },
    UPDATE_AVATAR(state, avatar) {
      state.avatar = avatar
    },
    UPDATE_EMAIL(state, email) {
      state.email = email
    },
  },
  actions: {
  },
  modules: {},
  plugins: [
    createPersistedState({
      storage: window.sessionStorage
    })
  ]
})
