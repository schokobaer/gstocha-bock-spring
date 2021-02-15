
const name = "name"
const id = "id"
const lastversion = "lastversion"

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

export class Version {
    private readonly value: string
    constructor(str: string) {
        this.value = str
    }

    compare(v: Version): number {
        const that = v.value.split(".")
        const me = this.value.split(".")
        const n = Math.min(that.length, me.length)
        for (let i = 0; i < n; i++) {
            const x = parseInt(me[i])
            const y = parseInt(that[i])
            if (x - y !== 0) {
                return x - y
            }
        }
        return me.length - that.length
    }

    toString(): string {
        return this.value
    }
}

export function getLastVersion(): Version {
    if (window.localStorage.getItem(lastversion) === null) {
        return new Version("1.2.16")
    }
    return new Version(window.localStorage.getItem(lastversion) as string)
}

export function setLastVersion(version: Version) {
    window.localStorage.setItem(lastversion, version.toString())
}