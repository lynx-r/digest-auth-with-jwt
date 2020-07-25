import { AuthState } from 'store/auth/context'
import { AuthActions, LOGIN, LOGOUT } from './AuthActions'

const authReducer = (state: AuthState, action: AuthActions) => {
  switch (action.type) {
    case LOGIN:
      return {
        ...state,
        loggedIn: true,
      }
    case LOGOUT:
      return {
        ...state,
        loggedIn: false,
      }
    default:
      return state
  }
}

export default authReducer
