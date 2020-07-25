import axios from 'axios'
import { API_URLS } from 'config'

export const axiosInstance = axios.create({
  baseURL: API_URLS.BASE_URL,
  responseType: 'json',
})

export { login, request } from './auth'
