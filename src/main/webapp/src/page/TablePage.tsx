import React from 'react';
import RestClient from '../rest/RestClient';
import { TableDto, JoinRequestBody, Position, CreateRequestBody } from 'gstochabock-core';
import Table from '../component/Table';
import './TablePage.css'

class TablePage extends React.Component<Props, State> {

  state: State = {
    loading: true,
    tables: []
  }
  rest: RestClient = new RestClient()
  bcc: BroadcastChannel = new BroadcastChannel('table')

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

  create() {
    const pw = prompt('Passwort', '')
    if (pw === null) {
      return
    }
    const reqBody: CreateRequestBody = {
      playerid: window.localStorage.playerid,
      name: window.localStorage.name,
      password: pw === '' ? undefined : pw
    }
    this.rest.create(reqBody).then(resp => {
      window.location.hash = `#${resp.id}`
    }).catch(err => console.error('Cound not create new table ', err))
    this.setState({loading: true})
  }

  joinTable(table: TableDto, position: Position, password?: string) {
    console.info('Player joins table on position: ' + position)
    const reqBody: JoinRequestBody = {
      playerid: window.localStorage.playerid,
      name: window.localStorage.name,
      position: position,
      password: password
    }
    this.rest.join(table.id, reqBody)
    .then(() => window.location.hash = `#${table.id}`)
    .catch(err => console.error('Could not join ', err))
    this.setState({loading: true})
  }

  render () {
    // No data loaded yet
    if (this.state.loading) {
      return <div>Fetching data ...</div>
    }

    let looser
    if (this.state.tables.length === 0) {
      looser = <div>
        No Tables available 🤓 Create a new one 🔥
      </div>
    }

    return <div className="tablepage">
      <button className="jass-btn" onClick={this.create}>Neuer Tisch</button>

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
}

export default TablePage;
