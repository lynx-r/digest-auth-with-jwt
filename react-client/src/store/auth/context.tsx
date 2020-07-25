import React, { createContext, Dispatch, useReducer } from 'react'
import { AuthActions } from './AuthActions'
import authReducer from './authReducer'

export interface AuthState {
  loggedIn: boolean
  authorities: string[]
}

const initialState = {
  loggedIn: false,
  authorities: [],
}
const AuthContext = createContext<{
  state: AuthState
  dispatch: Dispatch<AuthActions>
}>({
  state: initialState,
  dispatch: () => null,
})

const AuthProvider: React.FC = ({children}) => {
  const [state, dispatch] = useReducer(authReducer, initialState)

  return (
    <AuthContext.Provider value={{state, dispatch}}>
      {children}
    </AuthContext.Provider>
  )
}
export { AuthContext, AuthProvider }
