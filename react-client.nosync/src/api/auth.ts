import { ClientDigestAuth } from '@mreal/digest-auth'
import { httpGet, httpHead, httpPost, setDefaultAuthorizationHeader, setDefaultSessionHeader } from 'api/common'
import { AxiosRequestConfig } from 'axios'
import { API_URLS, CONSTANTS } from 'config'
import jwt from 'jwt-decode'
import { Token, User } from 'model'
import { getCookie, removeCookie, setCookie } from 'utils'

const {
  AUTHENTICATE_HEADER, AUTHORIZATION_HEADER, AUTHORIZATION_SCHEME, ACCESS_TOKEN_COOKIE,
  SESSION_TOKEN_HEADER, REFRESH_TOKEN_COOKIE, SESSION_TOKEN_COOKIE, CSRF_TOKEN_COOKIE
} = CONSTANTS

const {CSRF_URL, REFRESH_TOKEN_URL, LOGIN_URL} = API_URLS

export const getToken = async ({username, password}: User) => {
  await requestCsrfIfNeeded()

  const response = await authObserve()
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
    .then(({data}) => setToken(data))
    .catch(err => {
      const {response} = err
      console.log('??', err.response)
      if (response.status === 403) {
        clearSession()
        return updateToken({})
      }
    })
}

export const updateToken = (req: AxiosRequestConfig) => {
  return requestCsrfIfNeeded()
    .then(() => {
      const refreshToken = getCookie(REFRESH_TOKEN_COOKIE)
      if (!refreshToken) {
        alert('Authorization required')
        throw 'Authorization required'
      }
      const headers = {[AUTHORIZATION_HEADER]: AUTHORIZATION_SCHEME + refreshToken}
      return httpPost<Token>(REFRESH_TOKEN_URL, {}, {headers})
        .then(({data}) => {
          setToken(data)
          'headers' in req && (req.headers[AUTHORIZATION_HEADER] = AUTHORIZATION_SCHEME + data.accessToken)
          return req
        })
    })
}

export const requestCsrfIfNeeded = async () => {
  const isSessionToken = getCookie(SESSION_TOKEN_COOKIE)
  if (isSessionToken) {
    return
  }
  const {data: {token: csrfToken}, headers: {[SESSION_TOKEN_HEADER]: sessionToken}} =
    await httpGet<{ headerName: string, token: string }>(CSRF_URL)
  setSession(csrfToken, sessionToken)
}

export const updateTokenIfNeeded = (req: AxiosRequestConfig) => {
  if (req.url === CSRF_URL || req.url === REFRESH_TOKEN_URL || req.url === LOGIN_URL) {
    return req
  }
  const accessToken = getCookie(ACCESS_TOKEN_COOKIE)
  let diff = -1
  try {
    const token = jwt<any>(accessToken)
    const tokenExp = token.exp
    const now = Math.floor(Date.now() / 1000)
    diff = (tokenExp - now)
  } catch (e) {
    console.log(e)
  }
  if (diff < 0) {
    return updateToken(req)
  }
  return req
}

const authObserve = () => {
  const headers = {[AUTHORIZATION_HEADER]: 'Digest try'}
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

const setSession = (csrfToken: string, sessionToken: string) => {
  setCookie(CSRF_TOKEN_COOKIE, csrfToken)
  setCookie(SESSION_TOKEN_COOKIE, sessionToken)
  setDefaultSessionHeader(csrfToken, sessionToken)
}

const clearSession = () => {
  removeCookie(CSRF_TOKEN_COOKIE)
  removeCookie(SESSION_TOKEN_COOKIE)
}
