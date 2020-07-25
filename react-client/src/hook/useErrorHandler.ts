import { AxiosResponse } from 'axios'
import { useCallback } from 'react'
import { useHistory } from 'react-router-dom'
import { PAGE_URLS } from '../config'
import useToasts from './useToaster'

const useErrorHandler = () => {
  const history = useHistory()
  const {addErrorToast} = useToasts()
  const errorResponseHandler = useCallback(
    (res: AxiosResponse) => {
      if (!res) {
        addErrorToast('Ошибка сети')
        return
      }
      switch (res.status) {
        case 400:
          if (res?.data?.msg) {
            addErrorToast(res.data.msg)
          } else {
            addErrorToast('Ошибка запроса')
          }
          break
        case 401:
        case 403:
          addErrorToast('Доступ закрыт')
          history.push(PAGE_URLS.LOGIN_PAGE_URL)
          break
        case 404:
          addErrorToast('Ресурс не найден')
          break
        case 500:
          addErrorToast('500 Internal server error')
          break
      }
    },
    [addErrorToast, history]
  )

  return {
    errorResponseHandler,
  }
}

export default useErrorHandler
