export type Position = 0 | 1 | 2 | 3
export type Trumpf = 'E' | 'L' | 'H' | 'S' | 'G' | 'B' | 'KU' | 'KO'
export type GameState = 'PENDING' | 'TRUMPF' | 'PLAYING' | 'FINISHED'

export interface GamePlayerDto extends TablePlayerDto {
    weis?: Array<string>
}

export interface WeisPoints {
    points: number
    stoecke: boolean
}

export interface GameDto {
    currentMove?: number
    trumpf?: Trumpf
    points?: Array<number>
    weisPoints?: Array<WeisPoints>
    round: Array<string>
    lastRound?: {
        startPosition: number
        cards: Array<string>
    }
    undoable: boolean
    players: Array<GamePlayerDto>
    cards: Array<string>
    stoeckeable: boolean | null
    state?: GameState
}

export interface TablePlayerDto {
    name: string
    position: Position
}

export interface TableDto {
    id: string
    protected: boolean
    players: Array<TablePlayerDto>
}

export interface CreateRequestBody {
    name: string
    password?: string
}

export interface CreateResponseBody {
    id: string
}

export interface JoinRequestBody {
    name: string
    position: Position
    password?: string
}

export interface TrumpfRequestBody {
    trumpf: Trumpf
}

export interface WeisRequestBody {
    cards: Array<string>
}


export interface PlayRequestBody {
    card: string
}

export interface PlayRequestBody {
    card: string
}




export interface TableUpdateMessage {
    id: string
    game: GameDto | TableDto
}

export type PushUpdateMessage = 'table' | TableUpdateMessage