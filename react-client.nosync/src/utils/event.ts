/** Event handler that exposes the target element's value as a boolean. */
export const handleBooleanChange = (handler: (checked: boolean) => void) => {
  return (event: React.FormEvent<HTMLElement>) =>
    handler((event.target as HTMLInputElement).checked)
}

/** Event handler that exposes the target element's value as a string. */
export const handleStringChange = (handler: (value: string) => void) => {
  return (event: React.FormEvent<HTMLElement>) =>
    handler((event.target as HTMLInputElement).value)
}

/** Event handler that exposes the target element's value as a number. */
export const handleNumberChange = (handler: (value: number) => void) => {
  return handleStringChange((value) => handler(+value))
}
