import { CONSTANTS } from 'config'
import { CookieOptions } from 'model'
import { Cookies } from 'react-cookie'

const {COOKIE_PATH} = CONSTANTS

const cookies = new Cookies()

export const getCookie = (name: string) => {
  return cookies.get(name)
}

export const setCookie = (name: string, value: any, options?: CookieOptions) => {
  cookies.set(name, value, {path: COOKIE_PATH, ...options})
}

export const removeCookie = (name: string, options?: CookieOptions) => {
  cookies.remove(name, {path: COOKIE_PATH, ...options})
}
