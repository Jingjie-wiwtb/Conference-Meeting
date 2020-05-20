import Vue from 'vue'
import Router from 'vue-router'

import Login from "../components/Login";
import Register from "../components/Register";
import NewConference from "../components/Lab4/NewConference";
import Home from "../components/Lab3/Home";
import MyConference from "../components/Lab4/MyConference/MyConference";
import Contribute from "../components/Lab3/UserConference/Contribute";
import Message from "../components/Lab4/Message";
import Paper from "../components/Lab4/UserConference/Paper/Paper";
import OtherConference from "../components/Lab4/UserConference/OtherConference";
import AdminConferenceChecked from "../components/Lab4/AdminConference/AdminConferenceChecked";
import AdminConferenceUnChecked from "../components/Lab3/AdminConference/AdminConferenceUnChecked";
import MyConferenceAttend from "../components/Lab4/MyConference/MyConferenceAttend";
import Chair from "../components/Lab4/MyConference/Charactor/Chair";
import Author from "../components/Lab4/MyConference/Charactor/Author";
import PCMember from "../components/Lab4/MyConference/Charactor/PCMember";
import ScorePaper from "../components/Lab4/MyConference/Charactor/ScorePaper/ScorePaper";
import store from '../store'

Vue.use(Router);

export const router = new Router({
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home,
      meta: {
        requireAuth: true // 需要登录权限
      }
    },
    {
      path: '/NewConference',
      name: 'NewConference',
      component: NewConference,
      meta: {
        requireAuth: true // 需要登录权限
      }
    },
    {
      path: '/Login',
      name: 'Login',
      component: Login
    },
    {
      path: '/Register',
      name: 'Register',
      component: Register
    },
    {
      path: '/MyConference',
      name: 'MyConference',
      component: MyConference,
      meta: {
        requireAuth: true // 需要登录权限
      }
    },
    {
      path: '/Contribute',
      name: 'Contribute',
      component: Contribute,
      meta: {
        requireAuth: true // 需要登录权限
      }
    },
    {
      path: '/Message',
      name: 'Message',
      component: Message,
      meta: {
        requireAuth: true // 需要登录权限
      }
    },
    {
      path: '/Paper',
      name: 'Paper',
      component: Paper,
      meta: {
        requireAuth: true // 需要登录权限
      }
    },
    {
      path: '/OtherConference',
      name: 'OtherConference',
      component: OtherConference,
      meta: {
        requireAuth: true // 需要登录权限
      }
    },
    {
      path: '/AdminConferenceChecked',
      name: 'AdminConferenceChecked',
      component: AdminConferenceChecked,
      meta: {
        requireAuth: true // 需要登录权限
      }
    },
    {
      path: '/AdminConferenceUnChecked',
      name: 'AdminConferenceUnChecked',
      component: AdminConferenceUnChecked,
      meta: {
        requireAuth: true // 需要登录权限
      }
    },
    {
      path: '/MyConferenceAttend',
      name: 'MyConferenceAttend',
      component: MyConferenceAttend,
      meta: {
        requireAuth: true // 需要登录权限
      }
    },
    {
      path: '/Chair',
      name: 'Chair',
      component: Chair,
      meta: {
        requireAuth: true // 需要登录权限
      }
    },
    {
      path: '/Author',
      name: 'Author',
      component: Author,
      meta: {
        requireAuth: true // 需要登录权限
      }
    },
    {
      path: '/PCMember',
      name: 'PCMember',
      component: PCMember,
      meta: {
        requireAuth: true // 需要登录权限
      }
    },
    {
      path: '/ScorePaper',
      name: 'ScorePaper',
      component: ScorePaper,
      meta: {
        requireAuth: true // 需要登录权限
      }
    },
  ]
})

//前端登录拦截
router.beforeEach(function (to, from ,next) {
  if (to.matched.some(record => record.meta.requireAuth)) {
      if (store.state.token) {
        if (store.state.role==="admin") {
          if (to.path==="/Login"||to.path==="/Register"||to.path==="/AdminConferenceChecked"||to.path==="/AdminConferenceUnChecked"||to.path==="/ConferenceDetail") {
            next();
          }else {
            next({
              path: '/AdminConferenceUnChecked'
            })
          }
        }else {
          if (to.path!=="/AdminConferenceChecked"&&to.path!=="/AdminConferenceUnChecked") {
            next();
          }else {
            next({
              path: '/'
            })
          }
        }
      } else {
        next({
          path: '/Login',
          query: {redirect: to.fullPath} // 登录成功之后重新跳转到该路由
        })
      }
    } else {
      next()
    }
})
