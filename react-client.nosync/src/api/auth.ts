import { ClientDigestAuth } from '@mreal/digest-auth'
import { httpHead, httpPost } from 'api/common'
import { API_URLS } from 'config'
import { Token, User } from 'model'

const authObserve = () => {
  const headers = {Authorization: 'Digest try'}
  return httpHead(API_URLS.LOGIN_URL, {headers})
}

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
      uri: API_URLS.LOGIN_URL,
      counter: 1,
    }
  )

  const headers = {Authorization: digest.raw}
  return httpPost<Token>(API_URLS.LOGIN_URL, {}, {headers})
}
