<style src="" lang="css" scoped></style>
<template>
  <div class="HomeDiv">
    <!--菜单-->
    <LeftMenu />
    <TopToolBar />

    <!--组件展示-->
    <div class="right-container">
      <div class="DisplayBox mt-12 mt-md-0">
        <div class="DisplayRow-1">
          <v-btn class="mr-4" outlined small fab color="teal lighten-1" @click="goBack">
            <v-icon>mdi-arrow-left</v-icon>
          </v-btn>
          <span class="smallTitle">审稿</span>
        </div>
        <v-container class="text-left">
          <!--标题，变量名瞎取得可以改-->
          <v-row class="px-4">
            <Strong class="title" v-html="title"></Strong>
          </v-row>

          <!--摘要，变量名瞎取得可以改-->
          <v-row class="mt-5 px-4">
            <p class="" style="color: #aaa">
              {{ abstract }}
            </p>
          </v-row>


          <div style="border: 3px solid" class="pdf">
            <p class="arrow">

              <span @click="changePdfPage(0)" class="turn" :class="{grey: currentPage==1}">Preview</span>
              {{currentPage}} / {{pageCount}}
              <span @click="changePdfPage(1)" class="turn" :class="{grey: currentPage==pageCount}">Next</span>
            </p>
            <pdf
              :src="src"
            :page="currentPage"
            @num-pages="pageCount=$event"
            @page-loaded="currentPage=$event"
            @loaded="loadPdfHandler">
            </pdf>
          </div>


          <!--ele组件https://element.eleme.cn/2.10/#/zh-CN/component/rate-->
          <v-row class="mt-8 d-flex justify-center">
            <div style="width: 91%" class="d-flex justify-start align-center">
              <span class="px-2 d-block">稿件评分</span>
              <el-rate :max="4" v-model="value1" show-text :colors="colors" :texts="texts1"></el-rate>
            </div>
          </v-row>

          <v-row class="d-flex justify-center mt-5">
            <el-input
              type="textarea"
              placeholder="评语"
              v-model="textarea"
              maxlength="800"
              :rows="6"
              show-word-limit
              style="width: 90%"
             />
          </v-row>

          <!--ele组件https://element.eleme.cn/2.10/#/zh-CN/component/rate-->
          <v-row class="mt-6 d-flex justify-center">
            <div style="width: 91%" class="d-flex justify-start align-center">
              <span class="px-2 d-block">Confidence</span>
              <el-rate :max="4" v-model="value2" show-text :colors="colors" :texts="texts2"></el-rate>
            </div>
          </v-row>

          <v-row class="mt-6 d-flex justify-center">
            <!--点击触发提交_jxw-->
            <el-button v-if="textarea!==''&&value1!=0&&value2!=0" type="primary" @click="submitScorePaper">提 交</el-button>
            <el-button v-else type="primary" disabled>提 交</el-button>
          </v-row>
        </v-container>
      </div>
    </div>
  </div>
</template>
<script>
  import pdf from 'vue-pdf'

  var headers = {

    'Authorization': localStorage.getItem("token")

  };

  var loadingTask = pdf.createLoadingTask({

    url:'http://localhost/api/read_paper?paper_id=53',

    httpHeaders:headers

  });

  export default {
    components: {pdf},
    name: 'ScorePaper',
    data(){
      return{
        // 这些变量名都是乱取的，可以改
        value1: null,
        value2: null,
        paper_id:this.getQueryVariable("paper_id"),
        src: loadingTask,
        texts1: ['reject', 'weak reject', 'weak accept', 'accept'],
        texts2: ['very low' ,'low' , 'high' ,'very high'],
        values1: [-2,-1,1,2],
        values2: [0,1,2,3],
        colors: ['#99A9BF','#99A9BF', '#F7BA2A', '#FF9900'],
        textarea: '',
        title: '',
        abstract: '',
        currentPage: 0, // pdf文件页码
        pageCount: 0, // pdf文件总页数
      };
    },
    mounted() {
      this.getInfo();
    },
    methods:{
      changePdfPage (val) {
        // console.log(val)
        if (val === 0 && this.currentPage > 1) {
          this.currentPage--
          // console.log(this.currentPage)
        }
        if (val === 1 && this.currentPage < this.pageCount) {
          this.currentPage++
          // console.log(this.currentPage)
        }
      },

      // pdf加载时
      loadPdfHandler (e) {
        this.currentPage = 1 // 加载的时候先加载第一页
      },

      getInfo(){
        this.$axios.get('/view_paper?paper_id='+this.paper_id)
          .then(resp => {
            this.title=resp.data.title;
            this.abstract=resp.data.summary;
          })
          .catch(error => {
            console.log(error);
          });

        // this.$axios.get('/read_paper?paper_id='+this.paper_id)
        //   .then(resp => {
        //     const blob = resp.data;
        //     this.src =window.URL.createObjectURL(blob);
        //     console.log(this.src);
        //   })
        //   .catch(error => {
        //     console.log(error);
        //   });
      },

      // 提交_jxw
      submitScorePaper(){
        this.$confirm('确定提交吗？', '确认信息', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
        }).then(() => {
          this.$axios.post('/submit_result', {
            paper_id:this.paper_id,
            score:this.values1[this.value1-1],
            confidence:this.values2[this.value2-1],
            comment:this.textarea
          })
            .then(resp => {
              this.$message({
                showClose: true,
                message: '提交成功',
                type: 'success'
              });
            })
            .catch(error => {
              this.$notify.error({
                title: '提交失败',
                message: '重新提交试试吧！'
              });
              console.log(error);
            })
        }).catch(() => {
          //取消注销消息提示
          this.$message({
            showClose: true,
            message: '已取消提交~',
            type: 'success'
          });
        });
      },

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

      // 返回上一个页面
      goBack(){
        this.$router.go(-1);
      },
    },
  }
</script>

