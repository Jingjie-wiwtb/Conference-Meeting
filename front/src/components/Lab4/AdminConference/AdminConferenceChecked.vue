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
            <div class="MenuBox-x-item" @click="goToAdminConferenceUnChecked">未审核</div>
            <div class="MenuBox-x-item" @click="refresh">已审核</div>
            <div class="MenuBox-xAnimation start-2"></div>
          </div>
        </div>
        <!--第2行/内容框-->
        <div class="DisplayRow-2">
          <v-container>
            <v-row justify="center">
              <v-expansion-panels popout>
                <v-expansion-panel hide-actions>
                  <v-expansion-panel-header>
                    <template v-slot:actions>
                      <v-icon>mdi-card-bulleted</v-icon>
                    </template>
                    <v-row align="center" class="spacer" no-gutters>
                      <v-col class="text-center" cols="1">
                        <span>主席</span>
                      </v-col>
                      <v-col class="text-center" cols="2">
                        <span>全称</span>
                      </v-col>
                      <v-col class="text-center" cols="3">
                        <span>开始日期</span>
                      </v-col>
                      <v-col class="text-center" cols="3">
                        <span>会议状态</span>
                      </v-col>
                      <v-col class="text-center" cols="3">
                        <span>审核结果</span>
                      </v-col>
                    </v-row>
                  </v-expansion-panel-header>
                </v-expansion-panel>

                <v-expansion-panel hide-actions v-if="number === 0">
                  <v-expansion-panel-header><!--简要条目陈列-->
                    <template v-slot:actions>
                      <v-icon color="warning">mdi-alert-decagram-outline</v-icon>
                    </template>
                    <v-row align="center" class="spacer" no-gutters>
                      <v-col class="text-center" cols="12">
                        <strong>没有已审核的会议</strong>
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
                      <v-col class="text-center textOverFlowHidden" cols="2">
                        <strong title="点击显示会议详情" v-html="conference.fullname"></strong>
                      </v-col>
                      <v-col class="text-center textOverFlowHidden" cols="3">
                        <strong title="点击显示会议详情" v-html="conference.start_time"></strong>
                      </v-col>
                      <v-col class="text-center" cols="3">
                        <el-tag v-if="conference.conference_stage==='close'" class="tag-default" type="danger" effect="plain">未开启投稿</el-tag>
                        <el-tag v-if="conference.conference_stage==='submission'" class="tag-default" effect="plain">投稿进行中</el-tag>
                        <el-tag v-if="conference.conference_stage==='submission_end'" class="tag-default" type="warning" effect="plain">投稿已结束</el-tag>

                        <!--新增两个tag_jxw-->
                        <el-tag v-if="conference.conference_stage==='viewing'" class="tag-default" effect="plain">审稿进行中</el-tag>
                        <el-tag v-if="conference.conference_stage==='view_end'" class="tag-default" type="warning" effect="plain">审稿已结束</el-tag>


                        <el-tag v-if="conference.conference_stage==='begin'" class="tag-default" type="success" effect="plain">会议进行中</el-tag>
                        <el-tag v-if="conference.conference_stage==='end'" class="tag-default" type="info" effect="plain">会议已结束</el-tag>
                      </v-col>
                      <v-col class="text-center" cols="3">
                        <el-tag v-if="conference.audit_status==='fail'" class="tag-default" key="" type="danger" effect="dark">未通过</el-tag>
                        <el-tag v-if="conference.audit_status==='pass'" class="tag-default" key="" type="success" effect="dark">已通过</el-tag>
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
                        <v-col cols="12">主题：
                          <el-tag type="success" style="margin: 3px" :key="tag" v-for="tag in JSON.parse(conference.topics)">
                          {{tag}}<!--陈列已选topic-->
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
          <el-pagination background @current-change="getInfo" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total.sync='record'></el-pagination>        </div>
      </div>
    </div>
  </div>
</template>
<script>
  export default {
    name: 'AdminConferenceChecked',

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
        this.$axios.get('/get_conference?type=checked&page='+this.currentPage)
          .then(resp => {
            this.number=resp.data.number;
            this.record = resp.data.record;
            this.conferences = resp.data.conferences;
          })
          .catch(error => {
              if (error.response.data.record===0){
                this.number=0;
              } else {
                this.record=error.data.record;
                this.currentPage=Math.ceil(this.record/6);
              }
              console.log(error);

          });

        //---------------test start----------------
        // this.conferences=[
        //   {"conference_id" : "1", "chair_name": "xxx", "fullname": "xxx", "place": "xxx", "start_time": "yyyy-mm-dd", "end_time": "yyyy-mm-dd", "audit_status": "pass"},
        //   {"conference_id" : "2", "chair_name": "xxx", "fullname": "xxx", "place": "xxx", "start_time": "yyyy-mm-dd", "end_time": "yyyy-mm-dd", "audit_status": "fail"},
        //   {"conference_id" : "3", "chair_name": "xxx", "fullname": "xxx", "place": "xxx", "start_time": "yyyy-mm-dd", "end_time": "yyyy-mm-dd", "audit_status": "pass"}
        // ];
        // this.record=30;
        //---------------test end----------------
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

