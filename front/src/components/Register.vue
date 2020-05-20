<style src="../../static/mycss/Register.css" lang="css" scoped></style>
<template>
  <div id="registerDialog">
    <div class="reg-title-box">
      <h1 class="reg-title">Register</h1>
    </div>

    <div class="reg-container">
      <div class="title"><span class="underline-yellow">注册</span></div>

      <el-form class="regFormBox" :model="registerForm" ref="registerForm" :rules="rules" status-icon label-width="auto">  <!--相当于v-bind:model的缩写-->

        <el-form-item label="用户名" prop="username">
          <el-input autofocus v-model="registerForm.username" placeholder="请输入"></el-input>
        </el-form-item>

        <el-form-item label="真实姓名" prop="realname">
          <el-input v-model="registerForm.realname" placeholder="请输入"></el-input>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入"></el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input show-password type="password" v-model="registerForm.password" placeholder="请输入"></el-input>
        </el-form-item>

        <el-form-item label="确认密码" prop="checkpass">
          <el-input show-password type="password" v-model="registerForm.checkpass" placeholder="请输入"></el-input>
        </el-form-item>

        <el-form-item label="区域" prop="region">
          <el-input v-model="registerForm.region" placeholder="请输入"></el-input>
        </el-form-item>

        <el-form-item label="单位" prop="organization">
          <el-input v-model="registerForm.organization" placeholder="请输入"></el-input>
          <p id="prompt1" style="display: none;color: green"><i class="fa fa-exclamation-circle" aria-hidden="true"></i>注册成功，现在就去登录吧！</p>
          <p id="prompt2" style="display: none;color: red"><i class="fa fa-exclamation-circle" aria-hidden="true"></i>该用户名已被注册！</p>
        </el-form-item>

        <div class="last-row">
          <div class="message">
            <router-link class="green-small" style="float: left" to="login">已经注册？去登录</router-link>
          </div>
          <div class="btnBox">
            <div class="reg-btn-green" @click="submitForm('registerForm')">提交</div>
            <div class="reg-btn-white" @click="resetForm('registerForm')">重置</div>
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>


<script>
  export default{
    name:'Register',

    data(){

      var validateName = (rule,value,callback)=>{
        var pattern = /^[A-Za-z0-9-_]+$/;
        var pattern2=/^[a-zA-Z-]/;
        if(value === ''){
          callback(new Error('用户名不能为空'));
        }else if(!(pattern.test(value)&&pattern2.test(value))){
          callback(new Error('账号只能包含数字、字母及两种特殊字符（-_），且首位必须为字母或-'));
        }
        else if(!pattern.test(value)){
          callback(new Error('不低于6位，且不能为纯数字或纯字母！  如：a1b2c33'));
        }
        else{
          callback();
        }
      };


      var validatePass = (rule, value, callback) => {
        var pattern = /(?!^(\d+|[a-zA-Z]+|[-_]+)$)^[\w-_]/;

        if (value === '') {
          callback(new Error('请输入密码'));
        }else if (!(pattern.test(value))) {
          callback(new Error('密码需要包含字母、数字、特殊符号（-_）中至少两种'));
        }else if(value.search(this.registerForm.username)!==-1){
          callback(new Error('密码不能包含账号'));
        }
        else {
          if (this.registerForm.checkPass !== '') {
            this.$refs.registerForm.validateField('checkpass');
          }
          callback();
        }
      };

      var validatePass2 = (rule,value,callback)=>{
        if(value === ''){
          callback(new Error('请再次输入密码'));
        }
        else {
          setTimeout(() => {
            if (value !== this.registerForm.password) {
              callback(new Error('再次输入密码不一致!'));
            } else {
              callback();
            }
          }, 1000)
        }
      };

      var validateMail = (rule,value,callback)=>{
        var pattern = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
        if(value === ''){
          callback(new Error('邮箱不能为空'));
        }
        else if(!pattern.test(value)){
          callback(new Error('邮箱格式不正确!'));
        }
        else {
          callback();
        }
      };

      return{
        dialogFormVisible : false,

        registerForm: {
          username:'',
          email:'',
          realname:'',
          password:'',
          checkpass:'',
          organization:'',
          region:'',
        },

        rules: {
          username: [
            {validator: validateName, trigger: 'blur'},
            {min: 5, max: 32, message: '长度在 5 到 32 个字符', trigger: 'blur'},
            { required: true, message: '请输入用户名', trigger: 'blur' },
          ],
          realname: [
            {min: 2, max: 30, message: '长度在 2 到 30 个字符', trigger: 'blur'},
            { required: true, message: '请输入你的真实姓名', trigger: 'blur' },
          ],
          email:[
            { required: true, message: '请输入邮箱地址', trigger: 'blur' },
            {validator: validateMail, trigger:'blur'},
          ],
          password: [
            {min: 6, max: 32, message: '长度在 6 到 32 个字符', trigger: 'blur'},
            {validator: validatePass, trigger: 'blur'},
            { required: true, message: '请设置密码', trigger: 'blur' },
          ],
          checkpass: [
            {validator: validatePass2, trigger: 'blur'},
            { required: true, message: '请在输入一次密码', trigger: 'blur' },
          ],
          region: [
            {required: true, message: '请填写所属区域', trigger: 'blur' }
          ],
          organization: [
            {required: true, message: '请填写所属单位', trigger: 'blur' }
          ],
        }
      };
    },

    methods: {

      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if(valid){
            console.log(this.registerForm.username);
            this.$axios.post('/register', {
              username:this.registerForm.username,
              realname:this.registerForm.realname,
              email:this.registerForm.email,
              password:this.registerForm.password,
              organization:this.registerForm.organization,
              region:this.registerForm.region,
            })
              .then(resp => {
                document.getElementById("prompt1").style.display="inline";
                document.getElementById("prompt2").style.display="none";
                this.$confirm('注册成功，现在去登录吗？', '确认信息', {
                  confirmButtonText: '确定',
                  cancelButtonText: '取消',
                }).then(() => {
                  this.$router.push({path: './login'});
                }).catch(() => {
                  this.resetForm("registerForm");
                });
              })
              .catch(error => {
                  document.getElementById("prompt2").style.display="inline";
                  document.getElementById("prompt1").style.display="none";

              })
          }
          else{
            console.log('提交失败!');
            return false;
          }
        });
      },

      resetForm(formName) {
        this.$refs[formName].resetFields();
      }

    }
  }
</script>

