import { AuthState } from './context'

export const LOGIN = 'LOGIN'
export const LOGOUT = 'LOGOUT'

interface LoginAction {
  type: typeof LOGIN
  payload: AuthState
}

interface LogoutAction {
  type: typeof LOGOUT
  payload: AuthState
}

export type AuthActions = LoginAction | LogoutAction
