// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import {router} from './router'
import '../static/font-awesome-4.7.0/css/font-awesome.min.css'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css';

//导入自己的组件
import LeftMenu from "./components/Lab3/LeftMenu";
import MyPaper from "./components/Lab4/MyConference/Charactor/CharactorComponents/MyPaper";
import UploadPaper from "./components/Lab4/MyConference/Charactor/CharactorComponents/UploadPaper";
import CheckPaper from "./components/Lab4/MyConference/Charactor/CharactorComponents/CheckPaper";
import TopToolBar from "./components/Lab4/TopToolBar";

// 引入vuetify
import '@mdi/font/css/materialdesignicons.css' // Ensure you are using css-loader
import Vuetify from 'vuetify'
import 'vuetify/dist/vuetify.min.css'


Vue.use(LeftMenu)//全局引入
Vue.use(MyPaper)
Vue.use(UploadPaper)
Vue.use(CheckPaper)

// 使用vuetify
Vue.use(Vuetify)
Vue.use(ElementUI)

//axios 配置
var axios = require('axios')
// Axios挂载到prototype，全局可以使用this.$axios访问
Vue.prototype.$axios = axios
axios.defaults.timeout = 50000   //设置超时时间
axios.defaults.baseURL = '/api'
axios.defaults.withCredentials = true
//axios.defaults.headers.post['Content-Type'] = "multipart/form-data";

Vue.config.productionTip = false
Vue.config.silent = true

// http request 拦截器
axios.interceptors.request.use(
  config => {
    if(store.state.token) {
      // 判断是否有token，若存在，每个http header加上token
      config.headers.Authorization = localStorage.getItem("token");
      //config.headers['Content-Type'] = "multipart/form-data";
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// http response 拦截器
axios.interceptors.response.use(
  response => {
    return response
  },
  error => {
    console.log(error.response)
    if(error.response.data==='Token Expired') {
      // 清除token 如果不是register/login, 跳转至login
      store.commit('logout');
      router.currentRoute.path !== '/login' &&
      router.currentRoute.path !== '/register' &&
      router.replace({
        path: '/login',
        query: { redirect: router.currentRoute.path }
      })
    }
    return Promise.reject(error.response)
  }
)

Vue.component('LeftMenu', LeftMenu);//初始化组件
Vue.component('MyPaper', MyPaper);
Vue.component('UploadPaper', UploadPaper);
Vue.component('CheckPaper', CheckPaper);
Vue.component('TopToolBar', TopToolBar)

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  //初始化Vuetify
  vuetify: new Vuetify({
    icons: {
      iconfont: 'mdi', // default - only for display purposes
    },
  }),
  components: {
    App,
    LeftMenu,
    MyPaper,
    UploadPaper,
    CheckPaper,
    TopToolBar,
  },
  template: '<App/>'
})
