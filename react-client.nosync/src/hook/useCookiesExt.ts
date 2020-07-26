import { CONSTANTS } from 'config'
import { useCookies } from 'react-cookie'

const {COOKIE_PATH} = CONSTANTS

export interface CookieOptions {
  path: string
  expires: Date
  maxAge: number
  domain: string
  secure: boolean
  httpOnly: boolean
  sameSite: boolean | 'none' | 'lax' | 'strict'
}

const useCookiesExt = (deps?: string[]) => {
  const [cookies, setCookie, removeCookie] = useCookies(deps)
  return {
    cookies,
    setCookie: (name: string, value: any, options?: CookieOptions) => {
      setCookie(name, value, {path: COOKIE_PATH, ...options})
    },
    removeCookie: (name: string, options?: CookieOptions) => {
      removeCookie(name, {path: COOKIE_PATH, ...options})
    }
  }
}

export default useCookiesExt
