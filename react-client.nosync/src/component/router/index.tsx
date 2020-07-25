import { Loader } from 'component/shared'
import React, { Suspense } from 'react'
import { BrowserRouter } from 'react-router-dom'
import Footer from './Footer'
import { Header } from './Header'
import Main from './Main'

const Router = () => {
  return (
    <>
      <Suspense fallback={<Loader/>}>
        <BrowserRouter>
          <Header/>
          <Main/>
        </BrowserRouter>
      </Suspense>
      <Footer/>
    </>
  )
}

export default Router
