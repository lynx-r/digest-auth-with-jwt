import { ClientDigestAuth } from '@mreal/digest-auth'
import {
  httpGet,
  httpHead,
  httpPost,
  setCsrfTokenHeader,
  setDefaultAuthorizationHeader,
  setSessionTokenHeader
} from 'api/common'
import { AxiosRequestConfig } from 'axios'
import { API_URLS, CONSTANTS } from 'config'
import jwt from 'jwt-decode'
import { Token, User } from 'model'
import { getCookie, setCookie } from 'utils'

const {
  AUTHENTICATE_HEADER, AUTHORIZATION_HEADER, AUTHORIZATION_SCHEME, ACCESS_TOKEN_COOKIE,
  REFRESH_TOKEN_COOKIE, X_CSRF_TOKEN_HEADER, X_SESSION_TOKEN_HEADER
} = CONSTANTS

const {CSRF_URL, REFRESH_TOKEN_URL, LOGIN_URL} = API_URLS

export const getToken = async ({username, password}: User) => {
  const res = await httpGet<{ headerName: string, token: string }>(CSRF_URL)
  const {data: {token: xCsrfToken}, headers: {[X_SESSION_TOKEN_HEADER]: xAuthToken}} = res
  console.log(xAuthToken)
  setCsrfTokenHeader(xCsrfToken)
  setSessionTokenHeader(xAuthToken)
  const csrfHeader = {[X_CSRF_TOKEN_HEADER]: xCsrfToken}

  console.log(csrfHeader)
  const response = await authObserve(csrfHeader)
  const {headers: tryHeaders} = response
  const wwwAuthenticate = tryHeaders[AUTHENTICATE_HEADER]
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

  const headers = {[AUTHORIZATION_HEADER]: digest.raw}
  return httpPost<Token>(LOGIN_URL, {}, {headers})
    .then(setToken)
}

export const updateToken = (req: AxiosRequestConfig) => {
  const refreshToken = getCookie(REFRESH_TOKEN_COOKIE)
  const headers = {[AUTHORIZATION_HEADER]: AUTHORIZATION_SCHEME + refreshToken}
  return httpPost<Token>(REFRESH_TOKEN_URL, {}, {headers})
    .then(token => {
      setToken(token)
      req.headers[AUTHORIZATION_HEADER] = AUTHORIZATION_SCHEME + token.accessToken
      return req
    })
}

export const requestCsrfIfNeeded = (req: AxiosRequestConfig) => {
  if (req.url === CSRF_URL || req.url === REFRESH_TOKEN_URL || req.url === LOGIN_URL) {
    return req
  }
  const res = httpGet(CSRF_URL)
  res.then(res => console.log(res))
  return req
}

export const updateTokenIfNeeded = (req: AxiosRequestConfig) => {
  if (req.url === CSRF_URL || req.url === REFRESH_TOKEN_URL || req.url === LOGIN_URL) {
    return req
  }
  const accessToken = getCookie(ACCESS_TOKEN_COOKIE)
  try {
    const token = jwt<any>(accessToken)
    const tokenExp = token.exp
    const now = Math.floor(Date.now() / 1000)
    const diff = (tokenExp - now)
    if (diff < 60) {
      return updateToken(req)
    }
  } catch (e) {
    console.log(e)
  }
  return req
}

const authObserve = (csrfHeader: { [key: string]: string }) => {
  const headers = {[AUTHORIZATION_HEADER]: 'Digest try', ...csrfHeader}
  return httpHead(LOGIN_URL, {headers})
    .catch(err => {
      console.log(err.response)
      if (err.response.status === 401) {
        return err.response
      }
      throw err
    })
}

const setToken = ({accessToken, refreshToken}: Token) => {
  setCookie(ACCESS_TOKEN_COOKIE, accessToken)
  setCookie(REFRESH_TOKEN_COOKIE, refreshToken)
  setDefaultAuthorizationHeader(accessToken)
}
