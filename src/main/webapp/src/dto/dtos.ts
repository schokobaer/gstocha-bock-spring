export type Position = 0 | 1 | 2 | 3
export type Trumpf = 'E' | 'L' | 'H' | 'S' | 'G' | 'B' | 'KU' | 'KO'
export type GameState = 'PENDING' | 'TRUMPF' | 'PLAYING' | 'FINISHED'
export type Stoeckability = 'None' | 'Callable' | 'Called'

export interface GamePlayerDto extends TablePlayerDto {
    weis?: Array<string>
    weisCall?: string
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
    roundHistory: Array<GameRoundDto>
    undoable: boolean
    players: Array<GamePlayerDto>
    cards: Array<string>
    stoecke: Stoeckability
    puck?: number
    state?: GameState
}

export interface GameRoundDto {
    startPosition: number
    cards: Array<string>
    teamIndex: number
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
    logic: string
    puck?: string
    randomizePlayerOrder: boolean
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

export interface WeisResponseBody {
    weises: Array<string>
}


export interface PlayRequestBody {
    card: string
}

export interface PlayRequestBody {
    card: string
}

export interface NewGameRequestBody {
    restart: boolean
}



export interface TableUpdateMessage {
    id: string
    game: GameDto | TableDto
}

export type PushUpdateMessage = 'table' | TableUpdateMessage