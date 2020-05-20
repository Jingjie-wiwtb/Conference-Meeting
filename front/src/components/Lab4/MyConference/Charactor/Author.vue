<style lang="css" scoped></style>
<template>
  <div class="HomeDiv">
    <!--菜单-->
    <LeftMenu />
    <TopToolBar />

    <!--组件展示-->
    <div class="right-container">
      <div class="DisplayBox mt-12 mt-md-0">
        <v-tabs v-model="tab" color="#1abc9c" background-color="transparent">
          <v-tab>我的稿件</v-tab>
          <v-tab>投稿</v-tab>

          <v-tabs-items v-model="tab">
            <!--我的稿件-->
            <v-tab-item>
              <MyPaper :conference_id="conference_id" />
            </v-tab-item>

            <!--投稿-->
            <v-tab-item>
              <UploadPaper v-if="stage==='submission'" :conference_id="conference_id" />
              <p v-if="stage!=='submission'">投稿已经关闭</p>
            </v-tab-item>
          </v-tabs-items>
        </v-tabs>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: "author",
    data(){
      return{
        tab: null,
        conference_id:'',
        stage:""

      };
    },
    mounted() {
      this.getInfo();
    },
    methods:{
      getQueryVariable(variable)
      {
        var query = window.location.href;
        var search='';
        for (var i=0;i<query.length;i++) {
          if (query.charAt(i) === '?') {
            search = query.substring(i+1);
            break;
          }
        }
        console.log(search);
        var vars = search.split("&");
        for (var i=0;i<vars.length;i++) {
          var pair = vars[i].split("=");
          if(pair[0] == variable){return pair[1];}
        }
        return(false);
      },

      getInfo(){
        this.conference_id=this.getQueryVariable("conference_id");
        this.stage=this.getQueryVariable("conference_stage");
      },
    }
  }
</script>

<style scoped>

</style>
