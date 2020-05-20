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
          <v-tab>审稿</v-tab>
          <v-tab>我的稿件</v-tab>
          <v-tab>投稿</v-tab>

          <v-tabs-items v-model="tab">
            <v-tab-item>
              <CheckPaper v-if="stage==='viewing'" :conference_id="conference_id" />
              <p v-if="stage!=='viewing'">审稿处于关闭状态</p>
            </v-tab-item>
            <v-tab-item>
              <MyPaper />
            </v-tab-item>
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
    name: 'paper',
    data(){
      return{
        tab: null,
        submitForm: {
          paperTitle:'',
          paperAbstract:'',
        },
        conference_id:'',
        fileList:[],
        stage:"",

        rules: {
          paperTitle: [
            {min: 1, max: 50, message: '长度必须在 1 到 50 个字符之间', trigger: 'blur'},
            { required: true, message: '请输入论文标题', trigger: 'blur' },
          ],
          paperAbstract:[
            {min: 1, max: 1400, message: '长度必须在 1 到 800 个字之间', trigger: 'blur'},
            { required: true, message: '请输入论文摘要', trigger: 'blur' }
          ],
        }
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
    },
  }
</script>

