<style src="" lang="css" scoped></style>
<template>
  <v-container>
    <v-row justify="center">
      <v-expansion-panels popout>
        <v-expansion-panel hide-actions>
          <v-expansion-panel-header><!--简要条目陈列-->
            <template v-slot:actions>
              <v-icon>mdi-file-outline</v-icon>
            </template>
            <v-row align="center" class="spacer" no-gutters>
              <v-col class="text-center" cols="4">
                <span>标题</span>
              </v-col>
              <v-col class="text-center" cols="3">
                <span>话题</span>
              </v-col>
              <v-col class="text-center" cols="3">
                <span>稿件状态</span>
              </v-col>
              <v-col class="text-center" cols="2">
                <span>审稿</span>
              </v-col>
            </v-row>
          </v-expansion-panel-header>
        </v-expansion-panel>

        <!--没有稿件显示这个,把你原先搞的那行绿字取消了_jxw-->
        <v-expansion-panel hide-actions v-if="papers.length===0">
          <v-expansion-panel-header>
            <template v-slot:actions>
              <v-icon color="warning">mdi-alert-decagram-outline</v-icon>
            </template>
            <v-row align="center" class="spacer" no-gutters>
              <v-col class="text-center" cols="12">
                <strong>暂时没有稿件~</strong>
              </v-col>
            </v-row>
          </v-expansion-panel-header>
        </v-expansion-panel>

        <!--有稿件显示这个_jxw-->
        <v-expansion-panel v-for="paper in papers" :key="paper" hide-actions>
          <v-expansion-panel-header><!--简要条目陈列-->
            <v-row align="center" class="spacer" no-gutters>
              <!--标题-->
              <v-col class="text-center textOverFlowHidden" cols="4">
                <strong v-html="paper.title" :title="paper.title"></strong>
              </v-col>

              <!--话题-->
              <v-col class="d-flex flex-row justify-start align-center flex-wrap" cols="3">
                <el-tag style="margin: 3px;cursor: pointer;font-size: 8px" effect="light" type="success" :key="tag" v-for="tag in paper.topics">
                  {{tag}}
                </el-tag>
              </v-col>

              <!--审核状态-->
              <v-col class="text-center" cols="3">
                <!--待审核-->
                <el-tag v-if="paper.status==='unchecked'||paper.status==='waiting'" class="tag-default" type="info" effect="dark" key="">待审核</el-tag>
                <!--已审稿,未发布-->
                <el-tag v-if="paper.status==='checked'" class="tag-default" type="danger" effect="dark" key="">已审核未发布</el-tag>
                <!--已审稿,已发布-->
                <el-tag v-if="paper.status==='published'" class="tag-default" type="success" effect="dark" key="">已发布</el-tag>
              </v-col>

              <!--处理-->
              <v-col class="text-center" cols="2">
                <!--待审稿，审稿-->
                <el-button v-if="paper.status==='waiting'" plain style="margin: 0" class="recBtn-medium" size="medium" @click="checkPaper(paper.paper_id)">审稿</el-button>
                <!--已审稿,审稿禁用-->
                <el-button v-else plain style="margin: 0" class="recBtn-medium" size="medium" disabled>审稿</el-button>
              </v-col>
            </v-row>
          </v-expansion-panel-header>
        </v-expansion-panel>
      </v-expansion-panels>
    </v-row>

    <!--页码，直接复制粘贴的AdminConferenceUnChecked.vue里面的_jxw-->
    <div class="DisplayRow-3">
      <el-pagination background @current-change="getInfo" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total.sync='record'></el-pagination>
    </div>
  </v-container>
</template>
<script>
  export default {
    name: 'checkPaper',
    props: {
      conference_id: {
        required: true
      },
    },
    data(){
      return{
        number:-1,
        record:-1,
        currentPage:1,
        papers:[{"paper_id" : "1", "title": "xxx", "topics": ["topic1","topic2"], "status": "waiting"},
          {"paper_id" : "2", "title": "xxx", "topics": ["topic1","topic2"], "status": "checked"},
          {"paper_id" : "2", "title": "xxx", "topics": ["topic1","topic2"], "status": "published"}]
      };
    },
    mounted() {
      // 页码相关
      this.getInfo()
    },
    methods:{
      // 页码相关
      getInfo() {
        this.$axios.get('/view_papers?conference_id='+this.conference_id)
          .then(resp => {
            this.papers = resp.data.papers;
          })
          .catch(error => {
            console.log(error);
          });

      },

      // --------------------跳转--------------------
      // 跳转审稿页面
      checkPaper(paper_id){
        this.$router.push({path: './ScorePaper?paper_id='+paper_id});
      },
    },
  }
</script>

