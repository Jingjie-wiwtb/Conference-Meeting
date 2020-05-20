import Vue from 'vue'
import Vuex from 'vuex'
Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    token: localStorage.getItem('token') || null,
    userDetails: localStorage.getItem('userDetails') || null,
    role:localStorage.getItem('role') || null,
  },
  mutations: {
    login(state, data){
      localStorage.setItem('token', data.token)
      localStorage.setItem('userDetails', data.userDetails)
      state.user = data.userDetails
      state.token = data.token
      if (localStorage.getItem("username")==="admin"){
        localStorage.setItem('role', "admin")
      } else {
        localStorage.setItem('role', "ordinary")
      }
      state.role = localStorage.getItem('role');
    },
    logout(state) {
      // 移除token
      localStorage.removeItem('token');
      localStorage.removeItem('userDetails');
      localStorage.removeItem('username');
      localStorage.removeItem('role');
      state.userDetails = null;
      state.token = null;
      state.role=null;
    }
  },
  actions: {
  }
})
