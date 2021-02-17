import React from 'react';
import RestClient from '../rest/RestClient';
import { TableDto, JoinRequestBody, Position, CreateRequestBody } from '../dto/dtos';
import Table from '../component/Table';
import './TablePage.css'
import CreateTableDialog, {CreateTableData} from "../component/dialog/CreateTableDialog";
import {getUserId, getUserName} from "../util/GameRepo";
import PasswordDialog from "../component/dialog/PasswordDialog";
import Changelog from "../component/Changelog";

class TablePage extends React.Component<Props, State> {

    state: State = {
        openTables: true,
        tables: [],
        createTableDialog: false
    }

    rest: RestClient = new RestClient()
    bcc: BroadcastChannel = new BroadcastChannel('lounge')

    componentDidMount() {
        this.loadOpenTables = this.loadOpenTables.bind(this)
        this.create = this.create.bind(this)
        this.joinTable = this.joinTable.bind(this)
        this.loadOpenTables()

        this.bcc.onmessage = (msg: MessageEvent) => {
            console.info('TablePage received BCC message')
            if (this.state.openTables) {
                this.loadOpenTables()
            } else {
                this.loadRunningTables()
            }
        }
    }

    componentWillUnmount() {
        this.bcc.close()
    }

    loadOpenTables() {
        this.rest.listOpenTables().then(data => {
            console.info('Loaded tables: ', data)
            this.setState({tables: data})
        })
        .catch(err => {
            console.error('Could not fetch tables', err)
        })
    }

    loadRunningTables() {
        this.rest.listRunningTables(getUserId()!).then(data => {
            console.info('Loaded tables: ', data)
            this.setState({tables: data})
        })
        .catch(err => {
            console.error('Could not fetch running tables', err)
        })
    }

    create(data: CreateTableData) {
        const reqBody: CreateRequestBody = {
            name: getUserName() as string,
            password: data.password,
            logic: data.logic,
            puck: data.starter,
            puckOpens: data.puckOpens,
            writer: data.writer,
            randomizePlayerOrder: data.randomizePlayerPosition
        }
        this.rest.create(getUserId()!, reqBody).then(resp => {
            window.location.hash = `#${resp.id}`
        }).catch(err => console.error('Cound not create new table ', err))
    }

    joinTable(table: TableDto, position: Position) {
        if (table.protected) {
            this.setState({joinRequest: {tableid: table.id, position: position}})
            return
        }

        console.info('Player joins table on position: ' + position)
        const reqBody: JoinRequestBody = {
            name: getUserName() as string,
            position: position
        }
        this.rest.join(getUserId()!, table.id, reqBody)
            .then(() => window.location.hash = `#${table.id}`)
            .catch(err => console.error('Could not join ', err))
        this.setState({joinRequest: undefined})
    }

    joinProtectedTable(pw: string) {
        console.info('Player joins protected table on position: ' + this.state.joinRequest!.position)
        const reqBody: JoinRequestBody = {
            name: getUserName() as string,
            position: this.state.joinRequest!.position,
            password: pw
        }
        this.rest.join(getUserId()!, this.state.joinRequest!.tableid, reqBody)
            .then(() => window.location.hash = `#${this.state.joinRequest!.tableid}`)
            .catch(err => console.error('Could not join ', err))
        this.setState({joinRequest: undefined})
    }

    toggle() {
        const openTables = !this.state.openTables
        this.setState({openTables: openTables})
        if (openTables) {
            this.loadOpenTables()
        } else {
            this.loadRunningTables()
        }
    }

    render () {
        if (this.state.createTableDialog) {
            return <CreateTableDialog onCancle={() => this.setState({createTableDialog: false})} onCreate={(data: CreateTableData) => this.create(data)} />
        }

        if (this.state.joinRequest) {
            return <PasswordDialog onCancle={() => this.setState({joinRequest: undefined})} onSubmit={pw => this.joinProtectedTable(pw)} />
        }

        let looser
        if (this.state.tables.length === 0) {
            looser = <div style={{marginTop: '10px'}}>
                No Tables available 🤓 Create a new one 🔥
            </div>
        }

        return <div className="tablepage">
            <button className="jass-btn" onClick={() => this.setState({createTableDialog: true})}>Neuer Tisch</button>
            <button style={{marginLeft: '10px'}} className="jass-btn" onClick={() => this.toggle()}>{this.state.openTables ? 'Laufende Spiele' : 'Lounge'}</button>

            {looser}
            <div className="tables-ct">
                {this.state.tables.map(t =>
                    <div
                        className="table-ct-ct"
                        style={!this.state.openTables ? {cursor: 'pointer'} : {}}
                        onClick={!this.state.openTables ? (() =>  window.location.hash = `#${t.id}`) : undefined} >
                    <Table
                        table={t}
                        onJoin={this.state.openTables ? this.joinTable : undefined} displayName={true} />
                </div>)}
            </div>
            <Changelog />
        </div>
    }
}

interface Props {
}
interface State {
    tables: Array<TableDto>
    createTableDialog: boolean
    openTables: boolean
    joinRequest?: {
        tableid: string
        position: Position
    }
}

export default TablePage;

