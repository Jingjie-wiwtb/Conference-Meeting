<style src="../../../static/mycss/NewConference.css" lang="css" scoped></style>
<template>
  <div class="HomeDiv">
    <!--菜单-->
    <LeftMenu current-menu-item="NewConference" />
    <TopToolBar current-menu-item="NewConference" />

    <!--组件展示-->
    <div class="right-container">
      <div class="DisplayBox mt-12 mt-md-0">
        <!--第1行/抬头-->
        <div class="DisplayRow-1"><span class="smallTitle">创建会议</span></div>
        <!--第2行/注册表单-->
        <div class="DisplayRow-2">
          <el-form class="ConstructConfBox" cl :model="newConferenceForm" ref="newConferenceForm" :rules="rules" status-icon label-width="auto">  <!--相当于v-bind:model的缩写-->
            <el-form-item  label="会议简称" prop="abbr">
              <el-input style="width: 100%" autofocus v-model="newConferenceForm.abbr" placeholder="请输入"></el-input>
            </el-form-item>

            <el-form-item label="会议全称" prop="fullName">
              <el-input style="width: 100%" v-model="newConferenceForm.fullName" placeholder="请输入"></el-input>
            </el-form-item>

            <el-form-item label="会议时间" prop="jxwTest">
              <el-date-picker style="width: 100%" v-model="newConferenceForm.jxwTest" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" :picker-options="pickerOptions0"></el-date-picker>
            </el-form-item>

            <el-form-item  label="会议举办地点" prop="place" >
              <el-input style="width: 100%" v-model="newConferenceForm.place" placeholder="请输入"></el-input>
            </el-form-item>

            <el-form-item label="投稿截止日期" prop="submissionDdl" placeholder="请输入投稿截止日期">
              <el-date-picker style="width: 100%" type="date" v-model="newConferenceForm.submissionDdl" placeholder="请选择" :picker-options="pickerOptions0"></el-date-picker>
            </el-form-item>

            <!--修改逻辑，评审发布日期在会议开始之前_jxw-->
            <el-form-item label="评审结果发布日期" prop="publishTime">
              <el-date-picker style="width: 100%" type="date" v-model="newConferenceForm.publishTime" placeholder="请选择" :picker-options="pickerOptions0"></el-date-picker>
            </el-form-item>

            <!--新增陈列可选的topic-->
            <!--了解组件详情可以查表https://element.eleme.cn/2.10/#/zh-CN/component/tag-->
            <el-form-item label="可选会议话题" prop="">
              <v-container-fluid class="d-flex flex-row justify-start align-center flex-wrap">
                <!--点击触发把可选话题加入已选话题_jxw-->
                <!--需要实现未选择时默认effect=plain，选择之后effect变成light，如果可选的tag在已选中被删除的话effect也要相应变为plain_jxw-->
                <el-tag style="margin: 3px;cursor: pointer" title="点击添加话题" type="success" effect="plain" :key="tag" v-for="tag in topicCanBeChosen" @click="addTopic(tag)">
                  {{tag}}<!--陈列可选topic-->
                </el-tag>

                <el-tag style="margin: 3px;cursor: pointer" title="点击删除话题" type="success" effect="light" :key="tag" v-for="tag in topicChosen" @click="deleteTopic(tag)">
                  {{tag}}<!--陈列可选topic-->
                </el-tag>
              </v-container-fluid>
            </el-form-item>

            <!--新增添加topic，加一下星号，至少有一个话题_jxw-->
            <!--topic的中文名称，暂定为话题_待商榷-->
            <!--了解组件详情可以查表https://element.eleme.cn/2.10/#/zh-CN/component/tag-->
            <el-form-item label="添加更多话题" prop="">
              <v-container-fluid class="d-flex flex-row justify-start align-center flex-wrap">
                <el-input style="width: 90px" v-if="inputVisible" v-model="inputValue" ref="saveTagInput" size="small" @keyup.enter.native="handleInputConfirm" @blur="handleInputConfirm"></el-input>
                <!--点击添加话题按钮↓后，会触发显示这个topic输入框↑_jxw-->
                <el-button type="success" plain style="margin: 3px" v-else size="small" @click="showInput">+ 添加话题</el-button>
              </v-container-fluid>
            </el-form-item>

            <el-form-item>
              <el-button @click="submitForm('newConferenceForm')" type="primary">提 交</el-button>
              <el-button @click="resetForm('newConferenceForm')">重 置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
  export default {
    name: 'test',
    data(){
      var validateAddr = (rule,value,callback)=>{
        var pattern = /^[a-zA-Z0-9]+$/;
        if(value === ''){
          callback(new Error('会议简称不能为空'));
        }
        else if(!pattern.test(value)){
          callback(new Error('会议简称只能包含数字和字母'));
        }
        else{
          callback();
        }
      };

      // var validateConferenceEndTime = (rule,value,callback)=> {
      //   if (value < this.newConferenceForm.startTime) {
      //     callback(new Error('会议结束时间必须在开始时间之后!'));
      //   } else {
      //     callback();
      //   }
      // };

      var validDate = (rule,value,callback) => {
        callback(new Error(value))
        if((value[0]==="") || (value[1]==="")){
          callback(new Error("请选择时间"))
        }else{
          callback()
        }
      };

      var validateContributeDeadline = (rule,value,callback)=> {
        if (value >= this.newConferenceForm.jxwTest[0]) {
          callback(new Error('投稿截止日期必须在会议开始时间之前!'));
        }else if (value<new Date()){
          callback(new Error('投稿截止日期必须在未来!'));
        } else {
          callback();
        }
      };


      var validatePublishTime = (rule,value,callback)=> {
        if (value >= this.newConferenceForm.jxwTest[0]||value<=this.newConferenceForm.submissionDdl) {
          callback(new Error('评审发布时间必须在会议开始之前和投稿截止之后!'));
        } else {
          callback();
        }
      };


      return{
        // 新增start 临时定的可以改
        topicCanBeChosen: ['计算机图形学', '软件质量测试', '机器学习','信息编码论', '算法', '程序设计理论','数据库', '计算机体系结构', '软件工程','并发，并行和分布式系统'],// 可选topic
        topicChosen: [],// 已选topic
        inputVisible: false,// 控制topic输入框是否出现的旗
        inputValue: '',// topic输入框绑定的值
        // 新增end

        //时间选择禁用今天（不包括今天）以前的日期
        pickerOptions0: {
          disabledDate(time) {
            return time.getTime() < Date.now();
          },
        },

        dialogFormVisible : false,

        newConferenceForm: {
          abbr:'',
          fullName:'',
          place:'',
          submissionDdl:'',
          publishTime:'',
          jxwTest: [],
        },

        rules: {
          abbr: [
            { required: true, message: '会议简称不能为空', trigger: 'blur' },
            { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' },
            {validator: validateAddr, trigger: 'blur'}
    ],
          fullName:[
            { required: true, message: '请输入会议全称', trigger: 'blur' },
          ],
          jxwTest: [
            {required: true, message: '请输入会议时间', trigger: 'blur' },
          ],
          place: [
            {required: true, message: '请输入会议举办地点', trigger: 'blur' }
          ],
          submissionDdl: [
            {required: true, message: '请输入投稿截止日期', trigger: 'blur' },
            {validator: validateContributeDeadline, trigger: 'blur'}
          ],
          publishTime: [
            {required: true, message: '请输入评审结果发布日期', trigger: 'blur' },
            {validator: validatePublishTime, trigger: 'blur'}
          ]
        }
      };
    },
    methods: {
      // 新增topic相关函数start

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


      // 点击添加话题按钮后显示topic输入框
      showInput() {
        this.inputVisible = true;
        this.$nextTick(_ => {
          this.$refs.saveTagInput.$refs.input.focus();
        });
      },

      // 应该是修改这个吧，我也不太清楚你自己研究一下
      // 需要实现如果输入的topic是已选数组中有了的，则不加进已选数组，即已选数组中不能出现两个一样的topic_jxw
      // 需要实现如果输入的topic是可选数组中有了的，则同时从可选数组中消失_jxw
      handleInputConfirm() {
        // 原本ele的东西
        let inputValue = this.inputValue;
        if (inputValue) {
          if (this.topicChosen.indexOf(inputValue) === -1) {
            this.topicChosen.push(inputValue);
          } else {
            this.$notify.info({
              title: '添加话题失败',
              message: '已经添加该话题，请勿重复添加~'
            });
          }
        }

        this.inputVisible = false;
        this.inputValue = '';
      },
      // 新增topic相关函数end

      submitForm(formName) {
        if (this.topicChosen.length>=1) {
          this.$refs[formName].validate((valid) => {
            if (valid) {
              this.$confirm('确定创建此会议吗？', '确认信息', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
              }).then(() => {
                this.$axios.post('/newconference', {
                  abbr: this.newConferenceForm.abbr,
                  fullName: this.newConferenceForm.fullName,
                  startTime: this.newConferenceForm.jxwTest[0],
                  endTime: this.newConferenceForm.jxwTest[1],
                  place: this.newConferenceForm.place,
                  submissionDdl: this.newConferenceForm.submissionDdl,
                  publishTime: this.newConferenceForm.publishTime,
                  topics: this.topicChosen
                })
                  .then(resp => {
                    if (resp.status === 200) {
                      this.$notify({
                        title: '提交成功，请耐心等待管理员批准。',
                        type: 'success'
                      });
                      this.$refs[formName].resetFields();
                    }
                  })
                  .catch(error => {
                    this.$notify.error({
                      title: '创建失败',
                      message: '数据不符合要求！'
                    });
                    console.log(error)

                  })
              }).catch(() => {
                //取消注销消息提示
                this.$message({
                  showClose: true,
                  message: '已取消创建~',
                  type: 'success'
                });
              });

            }
          });
        }else {
          this.$notify.info({
            title: '无法创建',
            message: '话题数目必须不少于1~'
          });
        }
      },

      resetForm(formName) {
        this.$refs[formName].resetFields();
      },

    },
  }
</script>

