import { getToken } from 'api'
import { CONSTANTS } from 'config'
import { useCookies } from 'hook'
import { User } from 'model'

const {ACCESS_TOKEN_COOKIE, REFRESH_TOKEN_COOKIE} = CONSTANTS

const COOKIE_DEPS = [ACCESS_TOKEN_COOKIE, REFRESH_TOKEN_COOKIE]

const useAuth = () => {
  const {cookies, setCookie, removeCookie} = useCookies(COOKIE_DEPS)
  const login = async (user: User) => {
    await getToken(user)
    // setCookie(ACCESS_TOKEN_COOKIE, token.accessToken)
    // setCookie(REFRESH_TOKEN_COOKIE, token.refreshToken)
    // setDefaultAuthorizationHeader(token.accessToken)
  }

  // const accessToken = useMemo(() => cookies[ACCESS_TOKEN_COOKIE], [cookies])
  // if (!!accessToken) {
  //   setDefaultAuthorizationHeader(accessToken)
  // }

  return {
    login
  }
}

export default useAuth
