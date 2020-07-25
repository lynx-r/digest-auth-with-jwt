import { ClientDigestAuth } from '@mreal/digest-auth'
import { API_URLS } from 'config'
import { User } from 'model'
import { axiosInstance } from '.'

const get = <T>(path: string, config?: any) => {
  return axiosInstance.get<T>(path, config)
}

function post<T>(path: string, req: any, params?: any) {
  return axiosInstance.post<T>(path, req, params).then((res) => res.data)
}

function put<T>(path: string, req: any, params?: any) {
  return axiosInstance.put<T>(path, req, params).then((res) => res.data)
}

function remove<T>(path: string, params?: any) {
  return axiosInstance.delete<T>(path, params).then((res) => res.data)
}

const authObserve = () => {
  const headers = {Authorization: 'Digest try'}
  return get(API_URLS.LOGIN_URL, {headers})
}

const getToken = (
  {username, password}: User,
  reqHeaders: { 'www-authenticate': string }
) => {
  const wwwAuthenticate = reqHeaders['www-authenticate']
  console.log(wwwAuthenticate)

  const incomingDigest = ClientDigestAuth.analyze(wwwAuthenticate)
  const digest = ClientDigestAuth.generateProtectionAuth(
    incomingDigest,
    username,
    password,
    {
      method: 'POST',
      uri: API_URLS.LOGIN_URL,
      counter: 1,
    }
  )

  const dr = digest.raw
  const headers = {Authorization: dr}
  return axiosInstance.post(API_URLS.LOGIN_URL, {}, {headers})
}

export const login = async (user: User) => {
  try {
    console.log('try', user)

    const response = await authObserve()
    const {headers} = response
    return getToken(user, headers)
  } catch (e) {
    const {response} = e
    const {headers} = response
    return getToken(user, headers)
  }
}

export const request = async (token: string) => {
  const apiBasicAuthCredentials = {username: 'user', password: 'password'}
  const config = {auth: {...apiBasicAuthCredentials}, withCredentials: true}
  const req = {
    trafficReport: 0,
    reportType: 0,
    timeSpec: 0,
    aggregation: 0,
    period1: {
      start: '2020-07-20T00:00:00+03:00',
      end: '2020-07-21T15:59:59+03:00',
    },
    period2Start: '2020-07-16T12:47:41+03:00',
    compareType: 0,
  }
  return await get('/protected/metrics', {
    headers: {Authorization: 'Bearer ' + token},
  })
}
// export const request = async () => {
//   const apiBasicAuthCredentials = { username: "user", password: "password" }
//   const config = { auth: { ...apiBasicAuthCredentials }, withCredentials: true }
//   const req = {
//     trafficReport: 0,
//     reportType: 0,
//     timeSpec: 0,
//     aggregation: 0,
//     period1: {
//       start: "2020-07-20T00:00:00+03:00",
//       end: "2020-07-21T15:59:59+03:00",
//     },
//     period2Start: "2020-07-16T12:47:41+03:00",
//     compareType: 0,
//   }
//   return await post("http://localhost:8080/api/report/traffic-report", req, {
//     ...config,
//   })
// }
