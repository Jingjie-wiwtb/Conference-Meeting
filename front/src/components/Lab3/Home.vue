<style src="../../../static/mycss/Lab3/Home.css" lang="css" scoped></style>
<template>
  <div class="HomeDiv">
    <!--菜单-->
    <LeftMenu current-menu-item="Home" />
    <TopToolBar current-menu-item="Home" />

    <!--组件展示-->
    <div class="right-container">
      <div class="LogoContainer"><!--Logo-->
        <svg viewBox="0 0 1000 300">
          <symbol id="line-text">
            <text text-anchor="middle" x="50%" y="45%">Conference</text>
            <text text-anchor="middle" x="50%" y="95%">System</text>
          </symbol>
          <use xlink:href="#line-text" class="text"></use>
          <use xlink:href="#line-text" class="text"></use>
          <use xlink:href="#line-text" class="text"></use>
          <use xlink:href="#line-text" class="text"></use>
        </svg>
      </div>
      <!--海浪背景_start-->
      <div class="wave wave1"></div>
      <div class="wave wave2"></div>
      <div class="wave wave3"></div>
      <div class="wave wave4"></div>
      <!--海浪背景_end-->
    </div>
  </div>
</template>
<script>
  export default {
    name: 'home',
    data(){
      return{

      };
    },
    methods:{

    },
  }
</script>
