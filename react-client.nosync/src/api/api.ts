import { httpGet } from 'api/common'

export const request = async () => {
  return await httpGet('/protected/metrics')
}
