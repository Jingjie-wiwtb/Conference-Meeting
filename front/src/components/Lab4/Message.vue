<style lang="css" scoped></style>
<template>
  <div class="HomeDiv">
    <!--菜单-->
    <LeftMenu current-menu-item="Message" />
    <TopToolBar current-menu-item="Message" />

    <!--组件展示-->
    <div class="right-container">
      <div class="DisplayBox mt-12 mt-md-0">
        <!--第1行/抬头-->
        <div class="DisplayRow-1"><span class="smallTitle">消息中心</span></div>
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
                        <span>处理</span>
                      </v-col>
                    </v-row>
                  </v-expansion-panel-header>
                </v-expansion-panel>

                <v-expansion-panel hide-actions v-if="number === 0">
                  <v-expansion-panel-header>
                    <template v-slot:actions>
                      <v-icon color="warning">mdi-bell-alert</v-icon>
                    </template>
                    <v-row align="center" class="spacer" no-gutters>
                      <v-col class="text-center" cols="12">
                        <strong>您没有收到任何主席的程序委员会委员邀请</strong>
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
                      <v-col class="text-center textOverFlowHidden" cols="3">
                        <el-tag v-if="conference.conference_stage==='close'" class="tag-default" type="danger" effect="plain">未开启投稿</el-tag>
                        <el-tag v-if="conference.conference_stage==='submission'" class="tag-default" effect="plain">投稿进行中</el-tag>
                        <el-tag v-if="conference.conference_stage==='submission_end'" class="tag-default" type="warning" effect="plain">投稿已结束</el-tag>

                        <!--新增两个tag_jxw-->
                        <el-tag v-if="conference.conference_stage==='viewing'" class="tag-default" effect="plain">审稿进行中</el-tag>
                        <el-tag v-if="conference.conference_stage==='view_end'" class="tag-default" type="warning" effect="plain">审稿已结束</el-tag>

                        <el-tag v-if="conference.conference_stage==='begin'" class="tag-default" type="success" effect="plain">会议进行中</el-tag>
                        <el-tag v-if="conference.conference_stage==='end'" class="tag-default" type="info" effect="plain">会议已结束</el-tag>
                      </v-col>
                      <v-col class="text-center d-flex justify-space-around" cols="3">
                        <el-button v-if="conference.conference_stage!=='end'&&conference.status==='unprocessed'" type="success" style="margin: 0" class="recBtn-medium" size="medium" @click="process(conference.conference_id)">同意</el-button>
                        <el-button v-if="conference.conference_stage!=='end'&&conference.status==='unprocessed'" type="danger" style="margin: 0" class="recBtn-medium" size="medium" @click="refuse(conference.conference_id)">拒绝</el-button>

                        <el-tag v-if="conference.status==='accept'" class="tag-default" type="info" effect="dark">已同意</el-tag>
                        <el-tag v-if="conference.status==='refuse'" class="tag-default" type="info" effect="dark">已拒绝</el-tag>
                        <el-tag v-if="conference.conference_stage==='end' && conference.status!=='refuse' && conference.status!=='accept'" class="tag-default" type="info" effect="dark">无法处理</el-tag>
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
                        <v-col cols="6" >会议日期：{{conference.start_time}} ~ {{conference.end_time}}</v-col>
                        <v-col cols="6" v-if="conference.submission_begin===undefined">投稿日期：未定 ~ {{conference.submission_ddl}}</v-col>
                        <v-col cols="6" v-if="conference.submission_begin!==undefined">投稿日期：{{conference.submission_begin}} ~ {{conference.submission_ddl}}</v-col>
                      </v-row>
                      <v-row class="text-justify">
                        <v-col cols="12">评审发布日期：{{conference.publish_time}}</v-col>
                      </v-row>
                      <!--新增显示话题_jxw-->
                      <v-row class="text-justify">
                        <v-col cols="12">话题：
                          <el-tag style="margin: 3px" type="success" :key="tag" v-for="tag in JSON.parse(conference.topics)">
                            {{tag}}
                          </el-tag>
                        </v-col>
                      </v-row>
                    </v-card-text>
                  </v-expansion-panel-content>

                </v-expansion-panel>
              </v-expansion-panels>
            </v-row>

            <!--新增接受邀请后选择topic的弹窗，acceptDialogFlag===true时显示_jxw-->
            <!--需要实现确认邀请后弹这个窗_jxw-->
            <!--如果想测试这个弹窗可以把acceptDialogFlag改成true来看效果-->
            <!--这个是选标签的版本-->
            <el-dialog title="选择话题" :visible.sync="acceptDialogFlag">
              <!--此会议相关话题-->
              <!--新增陈列可选的topic-->
              <!--了解组件详情可以查表https://element.eleme.cn/2.10/#/zh-CN/component/tag-->
              <v-container class="d-flex flex-row justify-start align-center flex-wrap">

                <!--需要实现未选择时默认effect=plain，选择之后effect变成light，如果可选的tag在已选中被删除的话effect也要相应变为plain_jxw-->
                <el-tag style="margin: 3px;cursor: pointer" title="点击添加话题" type="success" effect="plain" :key="tag" v-for="tag in topicCanBeChosen" @click="addTopic(tag)">
                  {{tag}}<!--陈列可选topic-->
                </el-tag>

                <!--这个是light的效果，可以删-->
                <el-tag style="margin: 3px;cursor: pointer" title="点击删除话题" type="success" effect="light" :key="tag" v-for="tag in topicChosen" @click="deleteTopic(tag)">
                  {{tag}}<!--陈列可选topic-->
                </el-tag>
              </v-container>
              <el-row class="mt-4 d-flex justify-end">
                <!--至少选一个topic，否则按钮禁用，已实现-->
                <el-button type="primary" size="small" disabled v-if="topicChosen.length === 0">确认</el-button>
                <!--点击触发确认所选Topic操作-->
                <el-button type="primary" size="small" @click="chooseTopic" v-else>确认</el-button>
                <el-button size="small" @click="acceptDialogFlag = false">取消</el-button>
              </el-row>
            </el-dialog>
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
    name: 'message',
    data(){
      return{
        // 新增start
        topic: [],//topic，临时定的名字可以改
        acceptDialogFlag: false,// 接受邀请后选择topic弹窗的旗
        topicCanBeChosen: [],// 可选topic
        topicChosen: [],// 已选topic
        conference_id:"",
        number: -1,
        record:-1,
        currentPage:1,
        conferences:[],
        flag:false
      };
    },
    mounted() {
      this.getInfo()
    },
    methods:{

      // 点击可选topic时触发的函数
      // 需要实现如果此topic已被选择，effect为light的话就把这个topic从已选中删除，且effect改为plain
      // 如果未被选过effect还是plain，那么effect变成light，并加入已选数组_jxw
      addTopic(tag){
        //if (this.topicChosen.indexOf(tag)===-1){
        this.topicChosen.push(tag);
        this.topicCanBeChosen.splice(this.topicCanBeChosen.indexOf(tag),1);
        // }else{
        //   this.$notify.info({
        //     title: '添加话题失败',
        //     message: '已经添加该话题，请勿重复添加~'
        //   });
        //}
      },

      deleteTopic(tag){
        this.topicCanBeChosen.push(tag);
        this.topicChosen.splice(this.topicChosen.indexOf(tag),1);
      },

      // 确认所选Topic
      // 需要实现确认所选Topic，成功弹成功通知，失败弹失败通知，和上次的一样_jxw
      chooseTopic(){
        this.$confirm('确定接受邀请吗？', '确认信息', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
        }).then(() => {
          this.$axios.post('/deal_invitation', {
            conference_id:this.conference_id,
            decision:"accept",
            topics:this.topicChosen
          })
            .then(resp => {
              this.$notify({
                title: '已同意成为该会议审稿人',
                message: '您可以在"我的会议"中查看此会议的详细信息，以及对投递到该会议的稿件进行审核',
                type: 'success',
                position: 'top-left'
              });
              this.acceptDialogFlag=false;
              this.getInfo();
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

      refuse(){
        this.$confirm('确定拒绝邀请吗？', '确认信息', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
        }).then(() => {
          this.$axios.post('/deal_invitation', {
            conference_id:this.conference_id,
            decision:"refuse",
            topics:["null"]
          })
            .then(resp => {
              this.$notify({
                title: '已拒绝成为该会议审稿人',
                type: 'success',
                position: 'top-left'
              });

              this.getInfo();
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



      getInfo() {
        this.$axios.get('/invite_message?page='+this.currentPage)
          .then(resp => {
            this.flag=true;
            this.number = resp.data.number;
            this.record = resp.data.record;
            this.conferences = resp.data.conferences;
          })
          .catch(error => {
            if (error.response.data.number===0){
              this.number=0;
            } else {
              this.record=error.data.record;
              this.currentPage=Math.ceil(this.record/10);
            }
            console.log(error);

          });

        // this.conferences= [
        //   {"conference_id": "11",status:"unprocessed", "chair_name": "xx", "fullname": "xx", "place": "xx", "start_time": "yyyy-mm-dd", "end_time": "yyyy-mm-dd", "conference_stage": "begin","topics":['计算机图形学', '软件质量测试', '机器学习']},// 可选topic},
        //   {"conference_id": "22",status:"unprocessed", "chair_name": "xx", "fullname": "xx", "place": "xx", "start_time": "yyyy-mm-dd", "end_time": "yyyy-mm-dd", "conference_stage": "begin","topics":['信息编码论', '算法', '程序设计理论']},
        //   {"conference_id": "33",status:"unprocessed", "chair_name": "xx", "fullname": "xx", "place": "xx", "start_time": "yyyy-mm-dd", "end_time": "yyyy-mm-dd", "conference_stage": "begin","topics":['数据库', '计算机体系结构', '软件工程','并发，并行和分布式系统']},
        //   {"conference_id": "44",status:"refuse", "chair_name": "xx", "fullname": "xx", "place": "xx", "start_time": "yyyy-mm-dd", "end_time": "yyyy-mm-dd", "conference_stage": "end"}
        // ];
        // this.record=30;
        //---------------test end----------------
      },



      process(conference_id){
        this.acceptDialogFlag=true;
        this.topicChosen=[];
        this.conference_id=conference_id;
        for(var i=0;i<this.conferences.length;i++){
          if(this.conferences[i].conference_id===conference_id){
            this.topicCanBeChosen=JSON.parse(JSON.stringify(JSON.parse(this.conferences[i].topics)));
            break;
          }
        }
      },
    },
  }
</script>

