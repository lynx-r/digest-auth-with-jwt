import { AuthActions, LOGIN, LOGOUT } from './AuthActions'
import { AuthState } from './context'

export const loginAction = (payload: AuthState): AuthActions => {
  return {
    type: LOGIN,
    payload,
  }
}

export const logoutAction = (payload: AuthState): AuthActions => {
  return {
    type: LOGOUT,
    payload,
  }
}
