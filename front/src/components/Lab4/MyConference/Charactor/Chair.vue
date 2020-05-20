<style src="../../../../../static/mycss/Lab3/MyConference/Charactor/Chair.css" lang="css" scoped></style>
<template>
  <div class="HomeDiv">
    <!--菜单-->
    <LeftMenu />
    <TopToolBar />

    <!--组件展示-->
    <div class="right-container">
      <div class="DisplayBox mt-12 mt-md-0">
        <v-tabs v-model="tab" color="#1abc9c" background-color="transparent">
          <v-tab>邀请委员</v-tab>
          <v-tab>程序委员会</v-tab>
          <v-tab>审稿</v-tab>
          <v-tab>开启投稿</v-tab>
          <v-tab>开启审稿</v-tab>
          <v-tab>发布评审结果</v-tab>

          <v-tabs-items v-model="tab">

            <!--开启审稿后显示这个_jxw-->
<!--            <v-tab-item>-->
<!--              <v-row class="d-flex justify-center mt-8">-->
<!--                <p>袜，审稿已经开启了！</p>-->
<!--              </v-row>-->
<!--            </v-tab-item>-->

            <!--开启审稿前显示这个，可以邀请程序委员会委员人_jxw-->
            <v-tab-item>
              <v-row class="d-flex justify-center my-8">
                <el-input class="pcNameText" clearable placeholder="委员的真实姓名" prefix-icon="el-icon-search" v-model.lazy="pcName"></el-input>
                <el-button class="pcNameBtn" type="primary" @click="getUsers">搜索</el-button>
              </v-row>
              <v-container>
                <v-row justify="center">
                  <v-expansion-panels popout>
                    <v-expansion-panel hide-actions>
                      <v-expansion-panel-header><!--简要条目陈列-->
                        <template v-slot:actions>
                          <v-icon>mdi-card-account-details-outline</v-icon>
                        </template>
                        <v-row align="center" class="spacer" no-gutters>
                          <v-col class="text-center" cols="1">
                            <span>姓名</span>
                          </v-col>
                          <v-col class="text-center" cols="4">
                            <span>邮箱</span>
                          </v-col>
                          <v-col class="text-center" cols="2">
                            <span>区域</span>
                          </v-col>
                          <v-col class="text-center" cols="2">
                            <span>单位</span>
                          </v-col>
                          <v-col class="text-center" cols="3">
                            <span>选择</span>
                          </v-col>
                        </v-row>
                      </v-expansion-panel-header>
                    </v-expansion-panel>

                    <v-expansion-panel v-for="use in user" :key="use" hide-actions><!--会议条目循环-->
                      <v-expansion-panel-header><!--简要条目陈列-->
                        <v-row align="center" class="spacer" no-gutters>
                          <v-col class="text-center textOverFlowHidden" cols="1">
                            <strong v-html="lastSearch" :title="lastSearch"></strong>
                          </v-col>
                          <v-col class="text-center textOverFlowHidden" cols="4">
                            <strong v-html="use.email" :title="use.email"></strong>
                          </v-col>
                          <v-col class="text-center textOverFlowHidden" cols="2">
                            <strong v-html="use.region" :title="use.region"></strong>
                          </v-col>
                          <v-col class="text-center textOverFlowHidden" cols="2">
                            <strong v-html="use.organization" :title="use.organization"></strong>
                          </v-col>
                          <v-col class="text-center d-flex justify-center" cols="3">
                            <el-button type="primary" style="margin: 0" class="recBtn-medium" size="medium" @click="invitePCMember(use.username)">邀请</el-button>
                          </v-col>
                        </v-row>
                      </v-expansion-panel-header>
                    </v-expansion-panel>

                    <v-expansion-panel v-if="flag===false">
                      <v-expansion-panel-header><!--简要条目陈列-->
                        <template v-slot:actions>
                          <v-icon color="warning">mdi-account-alert</v-icon>
                        </template>
                        <v-row align="center" class="spacer" no-gutters>
                          <v-col class="text-center" cols="12">
                            <strong>抱歉，未找到任何名为"{{lastSearch}}"的用户</strong>
                          </v-col>
                        </v-row>
                      </v-expansion-panel-header>
                    </v-expansion-panel>
                  </v-expansion-panels>
                </v-row>
              </v-container>
            </v-tab-item>

            <!--新增陈列所有PcMember的信息-->
            <v-tab-item>
              <v-container>
                <v-row justify="center">
                  <v-expansion-panels popout>


                    <v-expansion-panel hide-actions>
                      <v-expansion-panel-header><!--简要条目陈列-->
                        <template v-slot:actions>
                          <v-icon>mdi-card-account-details-outline</v-icon>
                        </template>
                        <v-row align="center" class="spacer" no-gutters>
                          <v-col class="text-center" cols="2">
                            <span>用户名</span>
                          </v-col>
                          <v-col class="text-center" cols="2">
                            <span>姓名</span>
                          </v-col>
                          <v-col class="text-center" cols="2">
                            <span>邮箱</span>
                          </v-col>
                          <v-col class="text-center" cols="1">
                            <span>区域</span>
                          </v-col>
                          <v-col class="text-center" cols="2">
                            <span>单位</span>
                          </v-col>
                          <v-col class="text-center" cols="2">
                            <span>负责话题</span>
                          </v-col>
                          <v-col class="text-center" cols="1">
                            <span>邀请状态</span>
                          </v-col>
                        </v-row>
                      </v-expansion-panel-header>
                    </v-expansion-panel>

                    <!--没有PCMember时显示这个-->
                    <v-expansion-panel hide-actions v-if="users.length===0">
                      <v-expansion-panel-header>
                        <template v-slot:actions>
                          <v-icon color="warning">mdi-alert-decagram-outline</v-icon>
                        </template>
                        <v-row align="center" class="spacer" no-gutters>
                          <v-col class="text-center" cols="12">
                            <strong>此会议还没有程序委员会委员，快去邀请吧~</strong>
                          </v-col>
                        </v-row>
                      </v-expansion-panel-header>
                    </v-expansion-panel>

                    <!--有PCMember显示这个，里面的数据不对的，是我复制粘贴邀请PCMember那个里面的，需要改动_jxw-->
                    <v-expansion-panel v-for="user in users" :key="user" hide-actions><!--会议条目循环-->
                      <v-expansion-panel-header><!--简要条目陈列-->
                        <v-row align="center" class="spacer" no-gutters>
                          <v-col class="text-center textOverFlowHidden" cols="2">
                            <strong v-html="user.username" :title="user.username"></strong>
                          </v-col>
                          <v-col class="text-center textOverFlowHidden" cols="2">
                            <strong v-html="user.real_name" :title="user.real_name"></strong>
                          </v-col>
                          <v-col class="text-center textOverFlowHidden" cols="2">
                            <strong v-html="user.email" :title="user.email"></strong>
                          </v-col>
                          <v-col class="text-center textOverFlowHidden" cols="1">
                            <strong v-html="user.region" :title="user.region"></strong>
                          </v-col>
                          <v-col class="text-center textOverFlowHidden" cols="2">
                            <strong v-html="user.organization" :title="user.organization"></strong>
                          </v-col>
                          <v-col class="text-center textOverFlowHidden" cols="2">
                            <el-tag v-if="user.hasOwnProperty('topics')" style="margin: 3px" type="success" :key="tag" v-for="tag in user.topics">
                              {{tag}}
                            </el-tag>
                            <p v-if="!user.hasOwnProperty('topics')">-</p>
                          </v-col>
                          <v-col class="text-center d-flex justify-center" cols="1">
                            <!--已同意-->
                            <el-tag v-if="user.status==='accept'" class="tag-default" type="success" effect="dark">已同意</el-tag>
                            <!--已拒绝-->
                            <el-tag v-if="user.status==='refuse'" class="tag-default" type="danger" effect="dark">已拒绝</el-tag>
                            <!--未处理-->
                            <el-tag v-if="user.status==='unprocessed'" class="tag-default" type="info" effect="dark">未处理</el-tag>
                          </v-col>
                        </v-row>
                      </v-expansion-panel-header>
                    </v-expansion-panel>

                    <div class="DisplayRow-3" id="pag">
                      <el-pagination background @current-change="getInfo" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total.sync='record'></el-pagination>
                    </div>
                  </v-expansion-panels>
                </v-row>
              </v-container>
            </v-tab-item>

            <!--审稿-->
            <v-tab-item>
              <!--审稿未开始前显示这个-->
              <v-row v-if="status!=='viewing'" class="d-flex justify-center mt-8">
                <p>抱歉，审稿暂未开启TT</p>
              </v-row>

              <!--审稿开始后显示这个-->
              <CheckPaper v-if="status==='viewing'" :conference_id="conference_id" />
            </v-tab-item>

            <!--开启投稿-->
            <v-tab-item>
              <v-row class="d-flex justify-center mt-8">
                <el-button v-if="status==='close'" class="recBtn-medium" type="success" plain @click="open">开启投稿</el-button>
                <p v-if="status!=='close'">袜，投稿已经开启了！</p>
              </v-row>
            </v-tab-item>

            <!--新增开启审稿-->
            <v-tab-item>
              <!--未开启时显示这个-->
              <v-row class="d-flex justify-center mt-8" v-if="status==='submission'||status==='submission_end'">
                <el-radio-group v-model="strategy">
                  <el-radio label="1">基于话题相关度</el-radio>
                  <el-radio label="2">基于审稿平均负担</el-radio>
                </el-radio-group>
              </v-row>

              <!--未开启时也显示这个-->
              <v-row class="d-flex justify-center mt-8" v-if="status==='submission'||status==='submission_end'">
                <!--点击触发开启投稿，必须要选择一个才能提交-->
                <el-button class="recBtn-medium" type="primary" plain @click="openCheckPaper">开启审稿</el-button>
              </v-row>

              <!--已经开启审稿时显示这个-->
              <v-row class="d-flex justify-center mt-8" v-if="status==='viewing'||status==='view_end'||status==='begin'||status==='end'">
                <p>袜，审稿已经开启了！</p>
              </v-row>
              <v-row class="d-flex justify-center mt-8" v-if="status==='close'">
                <p>还是先开启投稿吧~</p>
              </v-row>

            </v-tab-item>

            <!--新增发布评审结果-->
            <v-tab-item>
              <!--未发布时显示这个-->
              <v-row class="d-flex justify-center mt-8" v-if="status==='viewing'">
                <!--点击触发发布评审结果，必须所有PCMember都审完才能发布，否则弹通知提示-->
                <el-button class="recBtn-medium" type="warning" plain @click="announceResult">发布评审结果</el-button>
              </v-row>

              <v-row class="d-flex justify-center mt-8" v-if="status==='close'||status==='submission'||status==='submission_end'">
                <!--点击触发发布评审结果，必须所有PCMember都审完才能发布，否则弹通知提示-->
                <el-button disabled class="recBtn-medium" type="warning" plain>发布评审结果</el-button>
              </v-row>

              <!--已经发布时显示这个-->
              <v-row class="d-flex justify-center mt-8" v-if="status==='view_end'||status==='begin'||status==='end'">
                <p>袜，评审结果已经发布了！</p>
              </v-row>
            </v-tab-item>
          </v-tabs-items>
        </v-tabs>
      </div>
    </div>
  </div>
</template>
<script>
  export default {
    name: 'chair',
    data(){
      return{
        record:-1,
        currentPage:1,
        users:[],
        strategy: '1',
        choose: false,
        pcName: '',
        lastSearch:'',
        state:[],
        user:[],
        tab: null,
        status:'',
        conference_id:'',
        flag:true
      };
    },
    mounted() {
      this.getStatus();
      this.getInfo();
    },
    methods:{
      // 开启审稿，只有PCMember>=2时才可开启，如果小于2弹通知框提示_jxw
      openCheckPaper(){
        this.$confirm('确定开启审稿吗？', '确认信息', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
        }).then(() => {
          this.$axios.get("/start_view?conference_id="+this.conference_id+"&strategy="+this.strategy)
            .then(resp => {
              this.$message({
                showClose: true,
                message: '已成功开启审稿~',
                type: 'success'
              });
              this.status="viewing";
            })
            .catch(error => {
              if (error.data.message==="no one contributed"){
                this.$notify.info({
                  title: '开启失败',
                  message: '还没有人投稿，再等等吧~'
                });
              } else {
                this.$notify.info({
                  title: '开启失败',
                  message: '目前参与此会议的程序委员会委员数目少于2人，请再邀请新的程序委员会委员~'
                });
              }
              console.log(error);
            });
        }).catch(() => {
          //取消注销消息提示
          this.$message({
            showClose: true,
            message: '已取消开启~',
            type: 'success'
          });
        });
      },

      // 发布审稿结果,如果_jxw
      announceResult(){
        this.$confirm('确定发布评审结果吗？', '确认信息', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
        }).then(() => {
          this.$axios.get("/end_view?conference_id="+this.conference_id)
            .then(resp => {
              this.$message({
                showClose: true,
                message: '已成功发布评审结果~',
                type: 'success'
              });
              this.status="view_end";
            })
            .catch(error => {
                this.$notify.info({
                  title: '开启失败',
                  message: '尚有评审人员未完成审稿，无法开启~'
                });
              console.log(error);
            });
        }).catch(() => {
          //取消注销消息提示
          this.$message({
            showClose: true,
            message: '已取消发布~',
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
      getStatus(){
        this.conference_id=this.getQueryVariable("conference_id");
        this.$axios.get('/conference_detail?conference_id='+this.conference_id)
          .then(resp => {
            this.status=resp.data.conference_stage;
          })
          .catch(error => {
            console.log(error);
          });
      },
      getInfo(){
        this.$axios.get('/invite_status?conference_id='+this.conference_id+'&page='+this.currentPage)
          .then(resp => {
            this.record=resp.data.record;
            this.users=resp.data.conferences;
            for (var i=0;i<this.users.length;i++){
              if (this.users[i].hasOwnProperty("topics"))
                this.users[i].topics=JSON.parse(this.users[i].topics)
            }
          })
          .catch(error => {
            if (error.data.record===0){
              this.number=0;
            } else {
              this.record=error.data.record;
              this.currentPage=Math.ceil(this.record/10);
            }
            console.log(error);
          });
      },

      getUsers() {
        this.$axios.get('/find_user?realname='+this.pcName)
          .then(resp => {
            this.lastSearch=this.pcName;
            this.state.length=resp.data.user.length;
            this.user=resp.data.user;
            this.flag=true;
          })
          .catch(error => {
            this.lastSearch = this.pcName;
            this.flag = false;
            this.user = [];
            console.log(error);
          });

        //---------------test start----------------
        // this.user=[
        //   {"username" : "1", "organization": "xxx", "region": "xxx","email":"xxx"},
        //   {"username" : "2", "organization": "xxx", "region": "xxx","email":"xxx"},
        //   {"username" : "3", "organization": "xxx", "region": "xxx","email":"xxx"}
        // ];
        // document.getElementById("noFound").style.display="none";
        // document.getElementById("found").style.display="inline";
        //---------------test end----------------
      },

      //邀请该用户成为审稿人
      invitePCMember(username){
        this.$confirm('确定邀请吗？', '确认信息', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
        }).then(() => {
          this.$axios.post('/invite_pc', {
            conference_id: this.conference_id,
            username: username
          })
            .then(resp => {
              this.$message({
                showClose: true,
                message: '邀请成功',
                type: 'success'
              });
              this.getInfo();
            })
            .catch(error => {
              this.$notify.error({
                title: '邀请失败',
                message: '可能是因为你已经邀请过你刚刚选中的人，重新选择吧！'
              });
              console.log(error);
            });
        }).catch(() => {
          //取消注销消息提示
          this.$message({
            showClose: true,
            message: '已取消邀请~',
            type: 'success'
          });
        });

      },

      //开启投稿
      open(){
        this.$confirm('确定开启投稿吗？', '确认信息', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
        }).then(() => {
          this.$axios.get("/start_submission?conference_id="+this.conference_id)
            .then(resp => {
              this.$message({
                showClose: true,
                message: '已成功开启投稿~',
                type: 'success'
              });
              this.status="submission";
            })
            .catch(error => {
              this.$notify.error({
                title: '开启失败',
                message: '您可以再次尝试'
              });
              console.log(error);
            });
        }).catch(() => {
          //取消注销消息提示
          this.$message({
            showClose: true,
            message: '已取消开启~',
            type: 'success'
          });
        });
      },
    },
  }
</script>

