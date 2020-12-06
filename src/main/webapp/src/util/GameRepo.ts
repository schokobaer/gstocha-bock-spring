
const name = "name"
const id = "id"

export function getUserName(): string | null {
    return window.localStorage.getItem(name)
}

export function setUserName(value: string) {
    window.localStorage.setItem(name, value)
}

export function getUserId(): string | null {
    return window.localStorage.getItem(id)
}

export function setUserId(value: string) {
    window.localStorage.setItem(id, value)
}