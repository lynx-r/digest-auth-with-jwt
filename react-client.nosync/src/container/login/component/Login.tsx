import { Button, Card, Elevation, FormGroup, InputGroup, } from '@blueprintjs/core'
import { request } from 'api'
import { useAuth, useToaster } from 'hook'
import { User } from 'model'
import React, { useContext, useState } from 'react'
import { AuthContext, loginAction } from 'store'
import { handleStringChange } from 'utils'

const Login = () => {
  const {dispatch} = useContext(AuthContext)
  const {login} = useAuth()
  const {addSuccessToast, addErrorToast} = useToaster()
  const [user, setUser] = useState<User>({
    username: '9999999999',
    password: 'password',
  })
  const onLogin = async () => {
    console.log('???')
    await login(user)
    dispatch(loginAction({loggedIn: true, authorities: ['user']}))
  }

  const onRegister = async () => {
    const metrics = await request()
    console.log('metrics', metrics)
  }

  const handleLoginChange = handleStringChange((username) =>
    setUser((s) => ({...s, username}))
  )
  const handlePasswordChange = handleStringChange((password) =>
    setUser((s) => ({...s, password}))
  )

  return (
    <div className="row">
      <Card className="col-6 offset-3" elevation={Elevation.TWO}>
        <FormGroup
          helperText="Login…"
          label="Login"
          labelFor="login-input"
          labelInfo="(required)"
        >
          <InputGroup
            id="login-input"
            placeholder="Login"
            onChange={handleLoginChange}
          />
        </FormGroup>
        <FormGroup
          helperText="Password…"
          label="Password"
          labelFor="password-input"
          labelInfo="(required)"
        >
          <InputGroup
            id="password-input"
            placeholder="Password"
            type="password"
            onChange={handlePasswordChange}
          />
        </FormGroup>
        <div className="row justify-content-end">
          <div className="col-auto">
            <Button icon="person" onClick={onRegister}>Register</Button>
          </div>
          <div className="col-auto">
            <Button icon="log-in" onClick={onLogin}>
              Login
            </Button>
          </div>
        </div>
      </Card>
    </div>
  )
}

export default Login
