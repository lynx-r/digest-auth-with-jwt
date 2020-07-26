export interface CookieOptions {
  path: string
  expires: Date
  maxAge: number
  domain: string
  secure: boolean
  httpOnly: boolean
  sameSite: boolean | 'none' | 'lax' | 'strict'
}
