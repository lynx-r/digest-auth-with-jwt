import { Alignment, Button, Navbar } from '@blueprintjs/core'
import React from 'react'
import { useHistory } from 'react-router-dom'

export const Header = () => {
  const {push} = useHistory()
  return (
    <header className="mb-4">
      <Navbar>
        <Navbar.Group align={Alignment.LEFT}>
          <Navbar.Heading>Blueprint</Navbar.Heading>
          <Navbar.Divider/>
          <Button
            className="bp3-minimal"
            icon="home"
            text="Home"
            onClick={() => push('/')}
          />
          <Button
            className="bp3-minimal"
            icon="document"
            text="Employees"
            onClick={() => push('/employees')}
          />
        </Navbar.Group>
        <Navbar.Group align={Alignment.RIGHT}>
          <Button
            className="bp3-minimal"
            icon="home"
            text="Login"
            onClick={() => push('/login')}
          />
        </Navbar.Group>
      </Navbar>
    </header>
  )
}
