import React from 'react'
import { Button } from 'smart-webcomponents-react/button'

const HomePage = () => {
  console.log('home page')

  return (
    <div>
      <Button onClick={() => alert('hi')}>Normal</Button>
    </div>
  )
}

export default HomePage
