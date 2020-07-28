import { updateTokenIfNeeded } from 'api/auth'
import axios, { AxiosRequestConfig } from 'axios'
import { API_URLS, CONSTANTS } from 'config'

const {AUTHORIZATION_HEADER, AUTHORIZATION_SCHEME, X_CSRF_TOKEN_HEADER, X_SESSION_TOKEN_HEADER} = CONSTANTS
export const axiosInstance = axios.create({
  baseURL: API_URLS.BASE_URL,
  responseType: 'json',
})

// axiosInstance.interceptors.request.use((req: AxiosRequestConfig) => requestCsrfIfNeeded(req))
axiosInstance.interceptors.request.use((req: AxiosRequestConfig) => updateTokenIfNeeded(req))

export const setDefaultAuthorizationHeader = (token: string) => {
  axiosInstance.defaults.headers.common[AUTHORIZATION_HEADER] = AUTHORIZATION_SCHEME + token
}

export const setCsrfTokenHeader = (token: string) => {
  axiosInstance.defaults.headers.common[X_CSRF_TOKEN_HEADER] = token
}

export const setSessionTokenHeader = (token: string) => {
  axiosInstance.defaults.headers.common[X_SESSION_TOKEN_HEADER] = token
}

export const httpGet = <T>(path: string, config?: AxiosRequestConfig) => {
  return axiosInstance.get<T>(path, config)
  // .then(({data}) => data)
  // .catch(console.log)
}

export const httpHead = <T>(path: string, config?: AxiosRequestConfig) => {
  return axiosInstance.head<T>(path, config)
}

export const httpPost = <T>(path: string, req: any, config?: AxiosRequestConfig) => {
  return axiosInstance.post<T>(path, req, config)
    .then(({data}: { data: T }) => data)
}

export const httpPut = <T>(path: string, req: T, config?: AxiosRequestConfig) => {
  return axiosInstance.put<T>(path, req, config)
}

export const httpRemove = <T>(path: string, config?: AxiosRequestConfig) => {
  return axiosInstance.delete<T>(path, config)
}
