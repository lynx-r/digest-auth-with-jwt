import { ClientDigestAuth } from '@mreal/digest-auth'
import { httpHead, httpPost, setDefaultAuthorizationHeader } from 'api/common'
import { AxiosRequestConfig } from 'axios'
import { API_URLS, CONSTANTS } from 'config'
import jwt from 'jwt-decode'
import { Token, User } from 'model'
import { getCookie, setCookie } from 'utils'

const {ACCESS_TOKEN_COOKIE, REFRESH_TOKEN_COOKIE} = CONSTANTS
const {REFRESH_TOKEN_URL, LOGIN_URL} = API_URLS

export const getToken = async ({username, password}: User) => {
  const response = await authObserve()
  const {headers: tryHeaders} = response
  const wwwAuthenticate = tryHeaders['www-authenticate']
  const incomingDigest = ClientDigestAuth.analyze(wwwAuthenticate)
  const digest = ClientDigestAuth.generateProtectionAuth(
    incomingDigest,
    username,
    password,
    {
      method: 'POST',
      uri: LOGIN_URL,
      counter: 1,
    }
  )

  const headers = {Authorization: digest.raw}
  return httpPost<Token>(LOGIN_URL, {}, {headers})
    .then(setToken)
}

export const updateToken = (req: AxiosRequestConfig) => {
  const refreshToken = getCookie(REFRESH_TOKEN_COOKIE)
  const headers = {Authorization: 'Bearer ' + refreshToken}
  return httpPost<Token>(REFRESH_TOKEN_URL, {}, {headers})
    .then(token => {
      setToken(token)
      req.headers['Authorization'] = 'Bearer ' + token.accessToken
      return req
    })
}


export const updateTokenIfNeeded = (req: AxiosRequestConfig) => {
  if (req.url === REFRESH_TOKEN_URL || req.url === LOGIN_URL) {
    return req
  }
  const accessToken = getCookie(ACCESS_TOKEN_COOKIE)
  const token = jwt<any>(accessToken)
  const tokenExp = token.exp
  const now = Math.floor(Date.now() / 1000)
  const diff = (tokenExp - now)
  if (diff < 60) {
    return updateToken(req)
  }
  return req
}

const authObserve = () => {
  const headers = {Authorization: 'Digest try'}
  return httpHead(LOGIN_URL, {headers})
}

const setToken = ({accessToken, refreshToken}: Token) => {
  setCookie(ACCESS_TOKEN_COOKIE, accessToken)
  setCookie(REFRESH_TOKEN_COOKIE, refreshToken)
  setDefaultAuthorizationHeader(accessToken)
}
