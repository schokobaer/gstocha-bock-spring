import React from 'react';
import RestClient from '../rest/RestClient';
import { TableDto, JoinRequestBody, Position, CreateRequestBody } from '../dto/dtos';
import Table from '../component/Table';
import './TablePage.css'
import CreateTableDialog, {CreateTableData} from "../component/dialog/CreateTableDialog";
import {getUserId, getUserName} from "../util/GameRepo";
import PasswordDialog from "../component/dialog/PasswordDialog";

class TablePage extends React.Component<Props, State> {

  state: State = {
    loading: true,
    tables: [],
    createTableDialog: false
  }

  rest: RestClient = new RestClient()
  bcc: BroadcastChannel = new BroadcastChannel('lounge')

  componentDidMount() {
    this.loadTables = this.loadTables.bind(this)
    this.create = this.create.bind(this)
    this.joinTable = this.joinTable.bind(this)
    this.loadTables()

    this.bcc.onmessage = (msg: MessageEvent) => {
      console.info('TablePage received BCC message')
      this.loadTables()
    }
  }

  componentWillUnmount() {
    this.bcc.close()
  }


  loadTables() {
      this.rest.listOpenTables().then(data => {
        console.info('Loaded tables: ', data)
        this.setState({loading: false, tables: data})
      })
      .catch(err => {
          this.setState({loading: false})
          console.error('Could not fetch tables', err)
      })
  }

  create(data: CreateTableData) {
    const reqBody: CreateRequestBody = {
      name: getUserName() as string,
      password: data.password,
      logic: data.logic
    }
    this.rest.create(getUserId()!, reqBody).then(resp => {
      window.location.hash = `#${resp.id}`
    }).catch(err => console.error('Cound not create new table ', err))
    this.setState({loading: true})
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
    this.setState({loading: true, joinRequest: undefined})
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
    this.setState({loading: true, joinRequest: undefined})
  }

  render () {
    // No data loaded yet
    if (this.state.loading) {
      return <div>Fetching data ...</div>
    }

    if (this.state.createTableDialog) {
      return <CreateTableDialog onCancle={() => this.setState({createTableDialog: false})} onCreate={(data: CreateTableData) => this.create(data)} />
    }

    if (this.state.joinRequest) {
      return <PasswordDialog onCancle={() => this.setState({joinRequest: undefined})} onSubmit={pw => this.joinProtectedTable(pw)} />
    }

    let looser
    if (this.state.tables.length === 0) {
      looser = <div>
        No Tables available 🤓 Create a new one 🔥
      </div>
    }

    return <div className="tablepage">
      <button className="jass-btn" onClick={() => this.setState({createTableDialog: true})}>Neuer Tisch</button>

      {looser}
      <div className="tables-ct">
        {this.state.tables.map(t => <div className="table-ct-ct"><Table table={t} onJoin={this.joinTable} /></div>)}
      </div>
    </div>
  }
}

interface Props {
}
interface State {
  loading: boolean
  tables: Array<TableDto>
  createTableDialog: boolean
  joinRequest?: {
    tableid: string
    position: Position
  }
}

export default TablePage;

