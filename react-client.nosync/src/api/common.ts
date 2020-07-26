import axios, { AxiosRequestConfig } from 'axios'
import { API_URLS } from 'config'

export const axiosInstance = axios.create({
  baseURL: API_URLS.BASE_URL,
  responseType: 'json',
})

export const httpGet = <T>(path: string, config?: AxiosRequestConfig) => {
  return axiosInstance.get<T>(path, config)
    .then(({data}) => data)
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
