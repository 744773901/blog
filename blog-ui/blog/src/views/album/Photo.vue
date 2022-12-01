<template>
  <div>
    <div class="banner" :style="cover">
      <h1 class="banner-title">{{photoAlbumName}}</h1>
    </div>
    <v-card class="blog-container">
      <div class="photo-wrap">
        <img @click="preview(index)" class="photo" :src="item" v-for="(item, index) in photoList" :key="index"/>
      </div>
      <!-- 无限加载 -->
      <infinite-loading @infinite="infiniteHandler">
        <div slot="no-more"/>
        <div slot="no-results"/>
      </infinite-loading>
    </v-card>
  </div>
</template>

<script>
import {listPhotosByAlbumId} from "../../api/photo";

export default {
  data() {
    return {
      photoAlbumName: '',
      photoAlbumCover: '',
      photoList: [],
      current: 1,
      size: 10
    }
  },
  methods: {
    infiniteHandler($state) {
      let params = {
        current: this.current,
        size: this.size
      }
      listPhotosByAlbumId(this.$route.params.albumId, params).then(res => {
        if (res.flag === true) {
          this.photoAlbumName = res.data.photoAlbumName
          this.photoAlbumCover = res.data.photoAlbumCover
          if (res.data.photoList.length) {
            this.current++
            this.photoList.push(...res.data.photoList)
            $state.loaded()
          } else {
            $state.complete()
          }
        }
      });
    },
    preview(index) {
      this.$imagePreview({
        index: index,
        images: this.photoList
      })
    }
  },
  computed: {
    cover() {
     return `background: url(${this.photoAlbumCover}) center center / cover no-repeat`
    }
  }
}
</script>

<style scoped>
.photo-wrap {
  display: flex;
  flex-wrap: wrap;
}

.photo {
  margin: 3px;
  cursor: pointer;
  flex-grow: 1;
  object-fit: cover;
  height: 200px;
}

.photo-wrap::after {
  content: "";
  display: block;
  flex-grow: 9999;
}

@media (max-width: 759px) {
  .photo {
    width: 100%;
  }
}
</style>
