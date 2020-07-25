import { Button, Card, Elevation, FormGroup, InputGroup, } from '@blueprintjs/core'
import { login, request } from 'api'
import { useToaster } from 'hook'
import { User } from 'model'
import React, { useContext, useState } from 'react'
import { AuthContext, loginAction } from 'store'
import { handleStringChange } from 'utils'

const Login = () => {
  const {dispatch} = useContext(AuthContext)
  const {addSuccessToast, addErrorToast} = useToaster()
  const [user, setUser] = useState<User>({
    username: '9999999999',
    password: 'password',
  })
  const onLogin = async () => {
    console.log('login', user)
    // const r = await request()
    // console.log(r)
    const res: any = await login(user)
    console.log('res', res)
    const metrics = await request(res.data.token)
    console.log('metrics', metrics)

    dispatch(loginAction({loggedIn: true, authorities: ['user']}))
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
            <Button icon="person">Register</Button>
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
