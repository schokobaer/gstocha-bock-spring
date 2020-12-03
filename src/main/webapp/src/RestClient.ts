import { TableDto, JoinRequestBody, TrumpfRequestBody, WeisRequestBody, PlayRequestBody, UndoRequestBody, NewRequestBody, CreateRequestBody, CreateResponseBody, StoeckeRequestBody } from 'gstochabock-core'

const api = '/api/table'
export default class RestClient {

    listOpenTables(): Promise<Array<TableDto>>{
        return fetch(`${api}/`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            },
            cache: 'no-cache'
        }).then(resp => {
            if (resp.ok) {
                return resp.json().then(data => {
                    return data as Array<TableDto>
                })
            }
            throw 'Could not load tables'
        })
    }

    getTable(tableId: string, playerId: string): Promise<any> {
        return fetch(`${api}/${tableId}/?playerid=${playerId}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            },
            cache: 'no-cache'
        }).then(resp => {
            if (resp.ok) {
                return resp.json().then(data => {
                    return data
                })
            }
            throw 'Could not load game'
        })
    }

    private sendPost(url: string, req: object): Promise<Response> {
        return fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            cache: 'no-cache',
            body: JSON.stringify(req)
        }).then(resp => {
            if (resp.ok) {
                return resp
            }
            throw 'Could not join table'
        })
    }

    create(req: CreateRequestBody): Promise<CreateResponseBody> {
        return this.sendPost(`${api}/`, req)
                .then(resp => {
                    return resp.json().then(data => {
                        return data as CreateResponseBody
                    })
                })
    }

    join(id: string, req: JoinRequestBody): Promise<any> {
        return this.sendPost(`${api}/${id}/join`, req)
    }

    trumpf(id: string, req: TrumpfRequestBody): Promise<any> {
        return this.sendPost(`${api}/${id}/trumpf`, req)
    }

    weis(id: string, req: WeisRequestBody): Promise<any> {
        return this.sendPost(`${api}/${id}/weis`, req)
    }

    stoecke(id: string, req: StoeckeRequestBody): Promise<any> {
        return this.sendPost(`${api}/${id}/stoecke`, req)
    }

    play(id: string, req: PlayRequestBody): Promise<any> {
        return this.sendPost(`${api}/${id}/play`, req)
    }

    undo(id: string, req: UndoRequestBody): Promise<any> {
        return this.sendPost(`${api}/${id}/undo`, req)
    }

    new(id: string, req: NewRequestBody): Promise<any> {
        return this.sendPost(`${api}/${id}/new`, req)
    }
}