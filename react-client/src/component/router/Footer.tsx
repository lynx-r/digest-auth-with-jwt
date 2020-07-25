import { Colors } from '@blueprintjs/core'
import React from 'react'

const Footer = () => {
  return (
    <footer
      className="mt-auto py-3"
      style={{backgroundColor: Colors.LIGHT_GRAY4}}
    >
      <div className="container">
        <span className="text-muted">© WorkingBit 2020</span>
      </div>
    </footer>
  )
}

export default Footer
