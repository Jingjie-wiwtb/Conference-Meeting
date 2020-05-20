<style src="../../../../static/mycss/Lab3/AdminConference/AdminConference.css" lang="css" scoped></style>
<template>
  <div class="HomeDiv">
    <!--菜单-->
    <LeftMenu />
    <TopToolBar />

    <!--组件展示-->
    <div class="right-container">
      <div class="DisplayBox mt-12 mt-md-0">
        <!--第1行/抬头-->
        <div class="DisplayRow-1">
          <div class="MenuBox-x">
            <div class="MenuBox-x-item" @click="refresh">未审核</div>
            <div class="MenuBox-x-item" @click="goToAdminConferenceChecked">已审核</div>
            <div class="MenuBox-xAnimation start-1"></div>
          </div>
        </div>
        <!--第2行/内容框-->
        <div class="DisplayRow-2">
          <v-container>
            <v-row justify="center">
              <v-expansion-panels popout>
                <v-expansion-panel hide-actions>
                  <v-expansion-panel-header><!--简要条目陈列-->
                    <template v-slot:actions>
                      <v-icon>mdi-card-bulleted</v-icon>
                    </template>
                    <v-row align="center" class="spacer" no-gutters>
                      <v-col class="text-center" cols="1">
                        <span>主席</span>
                      </v-col>
                      <v-col class="text-center" cols="3">
                        <span>全称</span>
                      </v-col>
                      <v-col class="text-center hidden-sm-and-down" cols="2">
                        <span>地点</span>
                      </v-col>
                      <v-col class="text-center hidden-sm-and-down" cols="3">
                        <span>开始日期</span>
                      </v-col>
                      <v-col class="text-center" md="3" xs="8">
                        <span>是否通过</span>
                      </v-col>
                    </v-row>
                  </v-expansion-panel-header>
                </v-expansion-panel>

                <v-expansion-panel hide-actions v-if="number === 0">
                  <v-expansion-panel-header>
                    <template v-slot:actions>
                      <v-icon color="warning">mdi-alert-decagram-outline</v-icon>
                    </template>
                    <v-row align="center" class="spacer" no-gutters>
                      <v-col class="text-center" cols="12">
                        <strong>没有未审核的会议</strong>
                      </v-col>
                    </v-row>
                  </v-expansion-panel-header>
                </v-expansion-panel>

                <v-expansion-panel v-for="conference in conferences" :key="conference" hide-actions><!--会议条目循环-->
                  <v-expansion-panel-header><!--简要条目陈列-->
                    <v-row align="center" class="spacer" no-gutters>
                      <v-col class="text-center textOverFlowHidden" cols="1">
                        <strong title="点击显示会议详情" v-html="conference.chair_name"></strong>
                      </v-col>
                      <v-col class="text-center textOverFlowHidden" cols="3">
                        <strong title="点击显示会议详情" v-html="conference.fullname"></strong>
                      </v-col>
                      <v-col class="text-center textOverFlowHidden hidden-sm-and-down" cols="2">
                        <strong title="点击显示会议详情" v-html="conference.place"></strong>
                      </v-col>
                      <v-col class="text-center textOverFlowHidden hidden-sm-and-down" cols="3">
                        <strong title="点击显示会议详情" v-html="conference.start_time"></strong>
                      </v-col>
                      <v-col class="text-center d-flex justify-space-around"  md="3" xs="8">
                        <el-button class="recBtn-medium" type="success" size="medium" style="margin: 0" @click="deal(conference.conference_id,'pass')">通过</el-button>
                        <el-button class="recBtn-medium" type="danger" size="medium" style="margin: 0" @click="deal(conference.conference_id,'fail')">驳回</el-button>
                      </v-col>
                    </v-row>
                  </v-expansion-panel-header>
                  <v-expansion-panel-content><!--展开文本-->
                    <v-divider></v-divider><!--分割线-->
                    <v-card-text>
                      <v-row class="text-justify">
                        <v-col cols="6">主席：{{conference.chair_name}}</v-col>
                        <v-col cols="6">举办地点：{{conference.place}}</v-col>
                      </v-row>
                      <v-row class="text-justify">
                        <v-col cols="6">简称：{{conference.abbr}}</v-col>
                        <v-col cols="6">全称：{{conference.fullname}}</v-col>
                      </v-row>
                      <v-row class="text-justify">
                        <v-col cols="12">会议日期：{{conference.start_time}} ~ {{conference.end_time}}</v-col>
                      </v-row>
                      <v-row class="text-justify">
                        <v-col cols="12" v-if="conference.submission_begin===undefined">投稿日期：未定 ~ {{conference.submission_ddl}}</v-col>
                        <v-col cols="12" v-if="conference.submission_begin!==undefined">投稿日期：{{conference.submission_begin}} ~ {{conference.submission_ddl}}</v-col>
                      </v-row>
                      <v-row class="text-justify">
                        <v-col cols="6">评审发布日期：{{conference.publish_time}}</v-col>
                      </v-row>
                      <v-row class="text-justify">
                        <v-col cols="12">话题：
                          <el-tag type="success" style="margin: 3px" :key="tag" v-for="tag in JSON.parse(conference.topics)">
                            {{tag}}
                          </el-tag></v-col>
                      </v-row>
                    </v-card-text>
                  </v-expansion-panel-content>
                </v-expansion-panel>

              </v-expansion-panels>
            </v-row>
          </v-container>
        </div>
        <!--第3行/页码-->
        <div class="DisplayRow-3">
          <el-pagination background @current-change="getInfo" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total.sync='record'></el-pagination>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
  export default {
    name: 'adminConferenceUnChecked',
    data(){
      return{
        number:-1,
        record:-1,
        currentPage:1,
        conferences:[],
      };
    },
    mounted() {
      this.getInfo()
    },
    methods:{
      getInfo() {
        this.$axios.get('/get_conference?type=unchecked&page='+this.currentPage)
          .then(resp => {
            this.record = resp.data.record;
            this.conferences = resp.data.conferences;
            this.number=resp.data.number;
          })
          .catch(error => {
              if (error.response.data.record===0){
                this.number=0;
                this.conferences=[];
              } else {
                this.record=error.data.record;
                this.currentPage=Math.ceil(this.record/6);
              }
              console.log(error);

          });

      },

      deal(conference_id,choose){
        this.$confirm('确定提交吗？', '确认信息', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
        }).then(() => {
          this.$axios.post('/audit', {
            conference_id:conference_id,
            decision:choose
          })
            .then(resp => {
              if (choose==='pass') {
                this.$notify({
                  title: '已批准该会议',
                  message: '您可以在"已审核"中查看此会议的详细信息',
                  type: 'success',
                  position: 'top-left'
                });
                this.getInfo();
              }else {
                this.$notify({
                  title: '已拒绝通过该会议',
                  type: 'success',
                  position: 'top-left'
                });
                this.getInfo();
              }
            })
            .catch(error => {
              this.$notify.error({
                title: '处理失败',
                message: '您可以再次尝试'
              });
              console.log(error);

            });
        }).catch(() => {
          //取消注销消息提示
          this.$message({
            showClose: true,
            message: '已取消处理~',
            type: 'success'
          });
        });


      },


      //刷新页面
      refresh() {
        this.$router.go(0);
      },

      // --------------------跳转--------------------
      //跳转已审核
      goToAdminConferenceChecked(){
        this.$router.push({path:'./AdminConferenceChecked'});
      },

      //跳转未审核
      goToAdminConferenceUnChecked(){
        this.$router.push({path:'./AdminConferenceUnChecked'});
      },

      //跳转会议详情
      goToConferenceDetail(conference_id){
        this.$router.push({path:'./ConferenceDetail?conference_id='+conference_id});
      },
    },
  }
</script>
