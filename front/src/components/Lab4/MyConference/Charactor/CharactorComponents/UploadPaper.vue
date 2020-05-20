<style src="../../../../../../static/mycss/Lab3/MyConference/Charactor/CharactorComponents/UploadPaper.css" lang="css" scoped></style>
<template>
  <div class="DisplayRow-2">
    <el-form class="mt-6" style="width: 88%; max-width: 800px" :model="submitForm" ref="submitForm" :rules="rules" status-icon>

      <el-form-item prop="paperTitle"><el-input clearable placeholder="论文标题（50字以内）" v-model="submitForm.paperTitle"></el-input></el-form-item>

      <el-form-item prop="paperAbstract"><el-input :autosize="{ minRows: 5, maxRows: 8}" type="textarea" placeholder="论文摘要（800字以内）" v-model="submitForm.paperAbstract"></el-input></el-form-item>
      <!--新增陈列可选的topic，可多选，但是至少选一个_jxw-->
      <!--了解组件详情可以查表https://element.eleme.cn/2.10/#/zh-CN/component/tag-->
      <el-form-item class="d-flex flex-row justify-start align-center flex-wrap" label="选择话题" prop="">
        <!--点击触发把可选话题加入已选话题，如果已选择则从已选中删除_jxw-->
        <el-tag style="margin: 3px;cursor: pointer" title="点击添加话题" effect="plain" type="success" :key="tag" v-for="tag in topicCanBeChosen" @click="addTopic(tag)">
          {{tag}}<!--陈列可选topic-->
        </el-tag>

        <!--这个是effect=light的效果，可删除-->
        <el-tag style="margin: 3px;cursor: pointer" effect="light" title="点击删除话题" type="success" :key="tag" v-for="tag in topicChosen" @click="deleteTopic(tag)">
        {{tag}}<!--陈列可选topic-->
        </el-tag>
      </el-form-item>

      <!--至少有一个作者-->
      <el-form-item label="论文作者(可拖动换序)" prop="">
        <!--可拖拽换序的表格，https://www.cnblogs.com/jin-zhe/p/10181852.html，只用了行拖拽，没用列拖拽-->
        <el-table :data="tableData" border row-key="id" align="left">
          <!--模板，要填数据_jxw-->
          <el-table-column v-for="(item, index) in col" :key="`col_${index}`" :prop="col[index].prop" :label="item.label"></el-table-column>
            <el-table-column><template slot-scope="scope"><el-button round icon="el-icon-delete-solid" @click="removeAuthor(scope.$index)">移除</el-button></template></el-table-column>
        </el-table>

        <el-row class="mt-3">
          <!--点击触发完善添加作者信息的弹窗-->
          <el-button size="medium" plain circle type="success" icon="el-icon-plus" @click="addAuthorDialogFlag = true"></el-button>

          <!--添加自己的按钮只在未添加自己之前显示，并在添加自己后消失_jxw-->
          <el-button size="medium" type="primary" round @click="addMyself">+ 我</el-button>
        </el-row>
      </el-form-item>

      <el-upload class="upload-demo" ref="upload" action="#" accept=".pdf" :on-change="handleProgress" :http-request="uploadSubmit"
        :on-preview="handlePreview" :on-remove="handleRemove" :before-remove="beforeRemove" :file-list="fileList"
        :limit="1" :auto-upload="false" :on-exceed="handleExceed" list-type="text">

        <el-button size="medium" slot="trigger" >选取文件</el-button>

        <el-button size="medium" v-if="(len === 0||topicChosen.length===0||tableData.length===0)&&modify===0" disabled style="margin: 10px" type="primary">提交投稿</el-button>
        <el-button size="medium" v-if="len !== 0&&topicChosen.length!==0&&tableData.length!==0&&modify===0" style="margin: 10px" type="primary" @click="submitUpload()">提交投稿</el-button>

        <el-button size="medium" v-if="(topicChosen.length===0||tableData.length===0)&&modify===1" disabled style="margin: 10px" type="primary">提交修改</el-button>
        <el-button size="medium" v-if="topicChosen.length!==0&&tableData.length!==0&&modify===1" style="margin: 10px" type="primary" @click="submitUpload()">提交修改</el-button>


        <div slot="tip" class="el-upload__tip">只能上传pdf文件，且不超过20Mb</div>
      </el-upload>
    </el-form>


    <!--新增添加其他作者的弹窗，addAuthorDialogFlag===true时显示_jxw-->
    <!--如果想测试这个弹窗可以把addAuthorDialogFlag改成true来看效果-->
    <el-dialog title="添加作者" :visible.sync="addAuthorDialogFlag">

      <!--这个表单是照我临时照搬的Register.vue里面部分作模板，变量名可以改，还需要你加一下表单验证相关,格式不对显示对应提示文字_jxw-->
      <el-form :model="loadAuthorForm" ref="loadAuthorForm" :rules="author_rules" status-icon label-width="auto">
        <el-form-item label="姓名" prop="realname">
          <el-input v-model="loadAuthorForm.realname" placeholder="请输入"></el-input>
        </el-form-item>
        <el-form-item label="单位" prop="organization">
          <el-input v-model="loadAuthorForm.organization" placeholder="请输入"></el-input>
        </el-form-item>
        <el-form-item label="区域" prop="region">
          <el-input v-model="loadAuthorForm.region" placeholder="请输入"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="loadAuthorForm.email" placeholder="请输入"></el-input>
        </el-form-item>
      </el-form>

      <el-row class="mt-4 d-flex justify-end">
        <!--点击触发确认作者信息操作-->
        <el-button type="primary" size="small" @click="chooseAuthorInfo">确认</el-button>
        <el-button size="small" @click="addAuthorDialogFlag = false">取消</el-button>
      </el-row>
    </el-dialog>

  </div>
</template>
<script>
  import Sortable from 'sortablejs'

  export default {
    name: 'uploadPaper',
    props: {
      conference_id: {
        required: true
      },
    },
    data(){
      var validateMail = (rule,value,callback)=>{
        var pattern = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
        if(!pattern.test(value)){
          callback(new Error('邮箱格式不正确!'));
        }
        else {
          callback();
        }
      };
      return{
        // 新增start 临时定的可以改
        topicCanBeChosen: [],// 可选topic
        topicChosen: [],// 已选topic
        modify:0,
        addme:false,
        addAuthorDialogFlag: false,// 添加作者弹窗是否显示的旗
        loadAuthorForm: {// 临时照搬的Register.vue中表单的，变量名可以改
          email:'',
          realname:'',
          organization:'',
          region:'',
        },
        // 作者信息表格相关
        col: [
          {
            label: '姓名',
            prop: 'realname'
          },
          {
            label: '单位',
            prop: 'organization'
          },
          {
            label: '区域',
            prop: 'region'
          },
          {
            label: '邮箱',
            prop: 'email'
          },
        ],
        tableData: [],

        submitForm: {
          paperTitle:'',
          paperAbstract:'',
        },
        len:0,
        fileList:[],

        rules: {
          paperTitle: [
            {min: 1, max: 50, message: '长度必须在 1 到 50 个字符之间', trigger: 'blur'},
            { required: true, message: '请输入论文标题', trigger: 'blur' },
          ],
          paperAbstract:[
            {min: 1, max: 1400, message: '长度必须在 1 到 800 个字之间', trigger: 'blur'},
            { required: true, message: '请输入论文摘要', trigger: 'blur' }
          ],
        },
        author_rules: {
          realname: [
            {min: 1, max: 30, message: '长度必须在 1 到 30 个字符之间', trigger: 'blur'},
            { required: true, message: '请输入该作者的真实姓名', trigger: 'blur' },
          ],
          organization:[
            {min: 1, max: 30, message: '长度必须在 1 到 30 个字之间', trigger: 'blur'},
            { required: true, message: '请输入该作者的单位', trigger: 'blur' }
          ],
          region:[
            {min: 1, max: 30, message: '长度必须在 1 到 30 个字之间', trigger: 'blur'},
            { required: true, message: '请输入该作者的区域', trigger: 'blur' }
          ],
          email:[
            {validator: validateMail, trigger:'blur'},
            { required: true, message: '请输入该作者的邮箱', trigger: 'blur' }
          ],
        }
      };
    },
    mounted() {
      this.rowDrop();
      this.getInfo();
    },
    methods:{
      addTopic(tag){
        this.topicChosen.push(tag);
        this.topicCanBeChosen.splice(this.topicCanBeChosen.indexOf(tag),1);
      },

      deleteTopic(tag){
        this.topicCanBeChosen.push(tag);
        this.topicChosen.splice(this.topicChosen.indexOf(tag),1);
      },

      //行拖拽
      rowDrop() {
        const tbody = document.querySelector('.el-table__body-wrapper tbody');
        const _this = this;
        Sortable.create(tbody, {
          onEnd({ newIndex, oldIndex }) {
            const currRow = _this.tableData.splice(oldIndex, 1)[0];
            _this.tableData.splice(newIndex, 0, currRow);
            const nextRow = _this.tableData.splice(newIndex, 1)[0];
            _this.tableData.splice(oldIndex, 0, nextRow);

          }
        })
      },

      removeAuthor(index){
        if (this.tableData[index].hasOwnProperty('me')) {//true 自身属性
          this.addme=false;
        }
        this.tableData.splice(index,1);
      },

      // 需要实现把自己的信息加进表里,之后添加自己的按钮不显示_jxw
      addMyself(){
        if(this.addme===false) {
          this.$axios.get('/my_information')
            .then(resp => {
              var my_info = resp.data;
              my_info.id = this.tableData.length;
              my_info.me=true;
              this.addme = true;
              this.tableData.push(my_info);
            })
            .catch(error => {
              console.log(error);
            });
        }
      },

      chooseAuthorInfo(){
        this.$refs["loadAuthorForm"].validate((valid) => {
          if (valid) {
            this.tableData.push({
              "id": this.tableData.length,
              "realname": this.loadAuthorForm.realname,
              "organization": this.loadAuthorForm.organization,
              "region": this.loadAuthorForm.region,
              "email": this.loadAuthorForm.email
            });
            this.addAuthorDialogFlag = false;
          }
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
        var vars = search.split("&");
        for (var i=0;i<vars.length;i++) {
          var pair = vars[i].split("=");
          if(pair[0] == variable){return pair[1];}
        }
        return(false);
      },

      getInfo(){
        this.conference_id=this.getQueryVariable("conference_id");
        this.$axios.get('/conference_detail?conference_id='+this.conference_id)
          .then(resp => {
            //resp.data.topics=["aaa","bbb","ccc"];
            this.topicCanBeChosen = JSON.parse(resp.data.topics);
          })
          .catch(error => {
            console.log(error);
          });

        var paper_id=this.getQueryVariable("paper_id");
        if (paper_id) {
          this.modify = 1;
          this.$axios.get('/paper_information?paper_id=' + paper_id)
            .then(resp => {
              this.topicChosen = JSON.parse(resp.data.paper.topics);
              for (var i = 0; i < this.topicChosen.length; i++) {
                this.topicCanBeChosen.splice(this.topicCanBeChosen.indexOf(this.topicChosen[i]), 1);
              }
              this.submitForm.paperTitle = resp.data.paper.title;
              this.submitForm.paperAbstract = resp.data.paper.summary;
              var writers=JSON.parse(resp.data.paper.authors);
              for (i = 0; i < writers.length; i++) {
                writers[i].id = i;
                this.tableData.push(writers[i]);
              }
            })
            .catch(error => {
              console.log(error);
            });
        }
      },

      handleProgress(file, fileList){
        this.len=fileList.length;
      },

      // ele关于文件上传的函数_start
      handleRemove(file, fileList) {
        this.len-=1;
        console.log(this.len);
        console.log(file, fileList);
      },
      handlePreview(file) {
        console.log(file);
      },
      handleExceed(files, fileList) {
        this.$message.warning(`当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
      },
      beforeRemove(file, fileList) {
        return this.$confirm(`确定移除 ${ file.name }？`);
      },
      // ele关于文件上传的函数_end

      //提交标题，摘要和上传的稿件_jxw
      submitUpload(){
        if (this.modify===0||this.len!==0)
          this.$refs.upload.submit();
        else {
          this.submit_edited_paper();
        }
      },

      submit_edited_paper() {
        this.$refs['submitForm'].validate((valid) => {
          if (valid) {
            this.$confirm('确定修改吗？', '确认信息', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
            }).then(() => {
              var form = new FormData();
              var params = JSON.stringify({
                "paper_id": this.getQueryVariable("paper_id"),
                "title": this.submitForm.paperTitle,
                "summary": this.submitForm.paperAbstract,
                "topics": this.topicChosen,
                "authors": this.tableData
              });
              form.append("params", new Blob([params], {type: "application/json"}));
              this.$axios.post('./submit_edited_paper',
                form, {
                  headers: {
                    'Content-Type': 'multipart/form-data;boundary = ' + new Date().getTime()
                  }
                })
                .then(resp => {
                  this.$message({
                    showClose: true,
                    message: '提交成功',
                    type: 'success'
                  });
                  this.tableData = [];
                  this.topicCanBeChosen = this.topicCanBeChosen.concat(this.topicChosen);
                  this.topicChosen = [];
                  this.$refs['submitForm'].resetFields();
                })
                .catch(error => {
                  this.$notify.error({
                    title: '修改失败',
                    message: '您可以再次尝试'
                  });
                  console.log(error);
                });
            }).catch(() => {
              //取消注销消息提示
              this.$message({
                showClose: true,
                message: '已取消修改~',
                type: 'success'
              });
            });
          } else {
            return false;
          }
        });
      },

      uploadSubmit(param){
        this.$refs['submitForm'].validate((valid) => {
          if (valid){
            this.$confirm('确定上传吗？', '确认信息', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
            }).then(() => {
              var fileObj = param.file;
              var form = new FormData();
              var params;
              if (this.modify===0) {
                 params = JSON.stringify({
                   "conference_id": this.conference_id,
                   "title": this.submitForm.paperTitle,
                   "summary": this.submitForm.paperAbstract,
                   "topics": this.topicChosen,
                   "authors": this.tableData
                 });
              }else {
                params=JSON.stringify({
                  "paper_id": this.getQueryVariable("paper_id"),
                  "title": this.submitForm.paperTitle,
                  "summary": this.submitForm.paperAbstract,
                  "topics": this.topicChosen,
                  "authors": this.tableData
                });
              }
              form.append("file", fileObj);
              form.append("params",new Blob([params], {type: "application/json"}));
              if (fileObj.type!=="application/pdf"){
                this.$notify.error({
                  title: '文件类型错误',
                  message: '文件必须为PDF格式'
                });
              }else if (fileObj.size>1000000) {
                this.$notify.error({
                  title: '文件过大',
                  message: '文件大小不能超过1M'
                });
              }else {
                var target;
                if(this.modify===0){
                  target='/submit_paper';
                }else {
                  target='./submit_edited_paper';
                }
                this.$axios.post(target,
                  form,{
                    headers: {
                      'Content-Type': 'multipart/form-data;boundary = ' + new Date().getTime()
                    }
                  })
                  .then(resp => {
                    this.$message({
                      showClose: true,
                      message: '提交成功',
                      type: 'success'
                    });
                    this.tableData=[];
                    this.topicCanBeChosen=(this.topicCanBeChosen).concat(this.topicChosen);
                    this.topicChosen=[];
                    this.$refs['submitForm'].resetFields();
                  })
                  .catch(error => {
                    this.$notify.error({
                      title: '文件上传失败',
                      message: '您可以再次尝试'
                    });
                    console.log(error);

                  });
              }
            }).catch(() => {
              //取消注销消息提示
              this.$message({
                showClose: true,
                message: '已取消上传~',
                type: 'success'
              });
            });
          }else{
            console.log('提交失败!');
            return false;
          }
        });

      },
    },
  }
</script>

