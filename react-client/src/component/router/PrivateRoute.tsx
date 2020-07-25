import { PAGE_URLS } from 'config'
import React, { useContext } from 'react'
import { Redirect, Route, useHistory } from 'react-router-dom'
import { AuthContext } from 'store'

const PrivateRoute = ({component: Component, ...rest}: any) => {
  const history = useHistory()
  const {state} = useContext(AuthContext)

  console.log(state)

  if (!state.loggedIn) {
    return (
      <Redirect
        to={{
          pathname: PAGE_URLS.LOGIN_PAGE_URL,
          state: {from: history.location},
        }}
      />
    )
  }

  return <Route {...rest} component={Component}/>
}

export default PrivateRoute
