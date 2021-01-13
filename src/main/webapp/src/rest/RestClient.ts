import { TableDto, JoinRequestBody, TrumpfRequestBody, WeisRequestBody, PlayRequestBody, CreateRequestBody, CreateResponseBody } from '../dto/dtos'

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

    getTable(tableId: string, playerid: string): Promise<any> {
        return fetch(`${api}/${tableId}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'playerid': playerid
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

    private sendPost(url: string, req: object, playerid: string): Promise<Response> {
        return fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                'playerid': playerid
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

    create(playerid: string, req: CreateRequestBody): Promise<CreateResponseBody> {
        return this.sendPost(`${api}/`, req, playerid)
                .then(resp => {
                    return resp.json().then(data => {
                        return data as CreateResponseBody
                    })
                })
    }

    join(playerid: string, id: string, req: JoinRequestBody): Promise<any> {
        return this.sendPost(`${api}/${id}/join`, req, playerid)
    }

    trumpf(playerid: string, id: string, req: TrumpfRequestBody): Promise<any> {
        return this.sendPost(`${api}/${id}/trumpf`, req, playerid)
    }

    weis(playerid: string, id: string, req: WeisRequestBody): Promise<any> {
        return this.sendPost(`${api}/${id}/weis`, req, playerid)
    }

    stoecke(playerid: string, id: string): Promise<any> {
        return this.sendPost(`${api}/${id}/stoecke`, {}, playerid)
    }

    play(playerid: string, id: string, req: PlayRequestBody): Promise<any> {
        return this.sendPost(`${api}/${id}/play`, req, playerid)
    }

    lay(playerid: string, id: string): Promise<any> {
        return this.sendPost(`${api}/${id}/lay`, {}, playerid)
    }

    undo(playerid: string, id: string): Promise<any> {
        return this.sendPost(`${api}/${id}/undo`, {}, playerid)
    }

    new(playerid: string, id: string): Promise<any> {
        return this.sendPost(`${api}/${id}/new`, {}, playerid)
    }
}