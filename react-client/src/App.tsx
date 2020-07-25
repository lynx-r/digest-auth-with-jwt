import { Router } from 'component'
import React from 'react'
import { CookiesProvider } from 'react-cookie'
import { AuthProvider } from 'store'
import './App.css'

function App() {
  return (
    <CookiesProvider>
      <AuthProvider>
        <Router/>
      </AuthProvider>
    </CookiesProvider>
  )
}

export default App
