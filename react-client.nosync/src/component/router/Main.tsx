import React, { lazy } from 'react'
import { Route, Switch } from 'react-router-dom'
import PrivateRoute from './PrivateRoute'

const HomePage = lazy(() =>
  import('container/home').then(
    (res: any) => new Promise((resolve) => setTimeout(() => resolve(res), 1000))
  )
)
const LoginPage = lazy(() => import('container/login'))
const EmployeePage = lazy(() => import('container/employee'))

const Main = () => {
  return (
    <main className="flex-shrink-0">
      <div className="container">
        <Switch>
          <Route exact path="/">
            <HomePage/>
          </Route>
          <Route path="/login">
            <LoginPage/>
          </Route>
          <PrivateRoute path="/employees">
            <EmployeePage/>
          </PrivateRoute>
        </Switch>
      </div>
    </main>
  )
}

export default Main
