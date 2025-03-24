import { createStore } from 'vuex'
import auth from './modules/auth'
import calendar from './modules/calendar'
import employee from './modules/employee'

export default createStore({
  modules: {
    auth,
    calendar,
    employee
  }
})