<style src="" lang="css" scoped></style>
<template>
  <v-container-fluid>
    <v-app-bar dark class="d-md-none d-block" dense absolute color="#2c3e50" elevate-on-scroll scroll-target="#scrolling-techniques-7">

      <!--主页-->
      <v-tooltip bottom>
        <template v-slot:activator="{ on }">
          <v-btn icon v-on="on" @click="goToHome">
            <v-icon>mdi-home</v-icon>
          </v-btn>
        </template>
        <span>主页</span>
      </v-tooltip>

      <v-toolbar-title class="d-none d-sm-flex ">Conference System</v-toolbar-title>
      <v-toolbar-title class="d-xs-flex d-sm-none">CS</v-toolbar-title>
      <v-spacer></v-spacer>

      <!--创建会议-->
      <v-tooltip bottom>
        <template v-slot:activator="{ on }">
          <v-btn icon v-on="on" @click="goToNewConference">
            <v-icon>mdi-playlist-plus</v-icon>
          </v-btn>
        </template>
        <span>创建会议</span>
      </v-tooltip>

      <!--所有会议-->
      <v-tooltip bottom>
        <template v-slot:activator="{ on }">
          <v-btn icon v-on="on" @click="goToAllConference">
            <v-icon>mdi-account-group</v-icon>
          </v-btn>
        </template>
        <span>所有会议</span>
      </v-tooltip>

      <!--我的会议-->
      <v-tooltip bottom>
        <template v-slot:activator="{ on }">
          <v-btn icon v-on="on" @click="goToMyConference">
            <v-icon>mdi-account</v-icon>
          </v-btn>
        </template>
        <span>我的会议</span>
      </v-tooltip>

      <!--消息中心-->
      <v-tooltip bottom>
        <template v-slot:activator="{ on }">
          <v-btn icon v-on="on" @click="goToMessage">
            <v-icon>mdi-bell</v-icon>
          </v-btn>
        </template>
        <span>消息中心</span>
      </v-tooltip>

      <el-dropdown @command="handleCommand" :hide-on-click="false">
        <div class="AvatarBox"><!--头像外框-->
          <v-avatar class="mx-2" color="indigo" size="36">
            <img :src="url" :alt="myname" v-if="myname !== 'admin'" />
            <img :src="adminUrl" alt="管理员" v-else />
          </v-avatar>
        </div>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="a">查看大图</el-dropdown-item>
          <el-dropdown-item command="b">更换头像</el-dropdown-item>
          <el-dropdown-item command="c">注销</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </v-app-bar>

    <el-dialog :visible.sync="dialogVisible">
      <img width="100%" :src="url" alt="" v-if="myname !== 'admin'">
      <img width="100%" :src="adminUrl" alt="" v-else>
    </el-dialog>
  </v-container-fluid>

</template>


<script>
  export default {
    name: 'topToolBar',
    data(){
      return{
        avatarItems: [
          { title: '注销' },
        ],

        record:0,
        myname:localStorage.getItem("username"),
        url:"../../../static/images/默认头像.jpg",
        adminUrl:"../../../static/images/管理员头像.PNG",

        dialogVisible: false,
      };
    },
    props:{
      currentMenuItem:{
        type: String,
        default: '',
      }
    },
    mounted() {
      this.getInfo()
    },
    methods: {
      getInfo() {
        this.$axios.get('/message_number')
          .then(resp => {
            this.record = resp.data.number;
          })
          .catch(error => {
            console.log(error);
          });
      },
      // handleRemove(file, fileList) {
      //   console.log(file, fileList);
      // },
      // handlePictureCardPreview(file) {
      //   this.dialogImageUrl = file.url;
      //   this.dialogVisible = true;
      // },
      // handleExceed(files, fileList) {
      //   this.$message.warning(`限制选择 1 个文件`);
      // },
      //
      // submitUpload() {
      //   this.$confirm('确定上传吗？', '确认信息', {
      //     confirmButtonText: '确定',
      //     cancelButtonText: '取消',
      //   }).then(() => {
      //     this.$refs.upload.submit();
      //   }).catch(() => {
      //     //取消注销消息提示
      //     this.$message({
      //       showClose: true,
      //       message: '已取消上传~',
      //       type: 'success'
      //     });
      //   });
      // },
      //
      // uploadSubmit(param){
      //   var fileObj = param.file;
      //   console.log(fileObj);
      //   if (fileObj.type.substring(0,6)!=="image/"){
      //     this.$notify.error({
      //       title: '文件类型错误',
      //       message: "必须为图片"
      //     });
      //   }else if (fileObj.size>21000000) {
      //     this.$notify.error({
      //       title: '图片过大',
      //       message: '图片大小不能超过20M'
      //     });
      //   }else {
      //     //todo
      //     this.$message({
      //       showClose: true,
      //       message: '上传成功！',
      //       type: 'success'
      //     });
      //     this.dialogFormVisible = false;
      //   }
      // },
      //跳转消息中心
      goToMessage() {
        if (this.$store.state.role==="admin"){
          this.$notify.error({
            title: '错误',
            message: '管理员没有此权限'
          });
        } else {
          if(this.currentMenuItem === 'Message'){
            this.refresh();
          }
          else{
            this.$router.push({path: './Message'});
          }
        }
      },

      // 注销弹窗，确认取消
      logoutCheck() {
        this.$confirm('确定要注销吗？', '确认信息', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
        }).then(() => {
          this.$store.commit("logout");

          //注销成功通知
          this.$notify({
            title: '注销成功！',
            type: 'success'
          });
          //跳转登录
          this.$router.push({path: './login'});
        }).catch(() => {
          //取消注销消息提示
          this.$message({
            showClose: true,
            message: '已取消注销~',
            type: 'success'
          });
        });
      },

      handleCommand(command) {
        if (command==="c"){
          this.logoutCheck();
        }else if(command==="b"){
          this.$notify.info({
            title: '消息',
            message: '抱歉，此功能暂未开发',
            position: 'top-right'
          });
        }else {
          this.dialogVisible=true;
        }
      },

      //跳转我的会议
      goToMyConference() {
        if (this.$store.state.role==="admin"){
          this.$notify.error({
            title: '错误',
            message: '管理员没有此权限'
          });
        } else {
          if(this.currentMenuItem === 'MyConference'){
            this.refresh();
          }
          else{
            this.$router.push({path: './MyConference'});
          }
        }
      },

      //功能未开发通知
      alertNo() {
        this.$notify.info({
          title: '消息',
          message: '抱歉，此功能暂未开发',
          position: 'top-right'
        });
      },

      //跳转主页
      goToHome() {
        if (this.$store.state.role==="admin"){
          this.$notify.error({
            title: '错误',
            message: '管理员没有此权限'
          });
        } else {
          if(this.currentMenuItem === 'Home'){
            this.refresh();
          }
          else{
            this.$router.push({path: './'});
          }
        }
      },

      //跳转创建会议
      goToNewConference() {
        if (this.$store.state.role==="admin"){
          this.$notify.error({
            title: '错误',
            message: '管理员没有此权限'
          });
        } else {
          if(this.currentMenuItem === 'NewConference'){
            this.refresh();
          }
          else{
            this.$router.push({path: './NewConference'});
          }
        }
      },

      //跳转投稿
      goToAllConference(){
        if (this.$store.state.role==="admin"){
          this.$notify.error({
            title: '错误',
            message: '管理员没有此权限'
          });
        } else {
          if(this.currentMenuItem === 'AllConference'){
            this.refresh();
          }
          else{
            this.$router.push({path:'./Contribute'});
          }
        }
      },

      //刷新页面
      refresh() {
        this.$router.go(0);
      },
    },
  }
</script>


