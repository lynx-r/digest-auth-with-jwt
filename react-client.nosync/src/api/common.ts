import { updateTokenIfNeeded } from 'api/auth'
import axios, { AxiosRequestConfig } from 'axios'
import { API_URLS, CONSTANTS } from 'config'

const {
  AUTHORIZATION_HEADER, AUTHORIZATION_SCHEME, CSRF_TOKEN_HEADER, SESSION_TOKEN_HEADER, ACCESS_TOKEN_COOKIE,
} = CONSTANTS
export const axiosInstance = axios.create({
  baseURL: API_URLS.BASE_URL,
  responseType: 'json',
})

axiosInstance.interceptors.request.use((req: AxiosRequestConfig) => updateTokenIfNeeded(req))

export const setDefaultAuthorizationHeader = (token: string) => {
  axiosInstance.defaults.headers.common[AUTHORIZATION_HEADER] = AUTHORIZATION_SCHEME + token
}

export const setDefaultSessionHeader = (csrfToken: string, sessionToken: string) => {
  axiosInstance.defaults.headers.common[CSRF_TOKEN_HEADER] = csrfToken
  axiosInstance.defaults.headers.common[SESSION_TOKEN_HEADER] = sessionToken
}

export const httpGet = <T>(path: string, config?: AxiosRequestConfig) => {
  return axiosInstance.get<T>(path, config)
}

export const httpHead = <T>(path: string, config?: AxiosRequestConfig) => {
  return axiosInstance.head<T>(path, config)
}

export const httpPost = <T>(path: string, req: any, config?: AxiosRequestConfig) => {
  return axiosInstance.post<T>(path, req, config)
}

export const httpPut = <T>(path: string, req: T, config?: AxiosRequestConfig) => {
  return axiosInstance.put<T>(path, req, config)
}

export const httpRemove = <T>(path: string, config?: AxiosRequestConfig) => {
  return axiosInstance.delete<T>(path, config)
}
