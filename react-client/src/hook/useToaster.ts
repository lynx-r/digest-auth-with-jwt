import { Intent, Toaster } from '@blueprintjs/core'

const toaster = Toaster.create()
const useToaster = () => {
  const addSuccessToast = (message: string) => {
    toaster.show({message, intent: Intent.SUCCESS})
  }

  const addErrorToast = (message: string) => {
    toaster.show({message, intent: Intent.DANGER})
  }

  return {
    addSuccessToast,
    addErrorToast,
  }
}

export default useToaster
