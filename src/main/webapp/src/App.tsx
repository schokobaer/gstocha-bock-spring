import React, { Fragment } from 'react';
import './App.css';
import TablePage from './TablePage';
import GamePage from './GamePage';
import { uuid } from 'uuidv4'

class App extends React.Component<Props, State> {

  state: State = {
    tableId: undefined
  }

  ws: WebSocket | null = null

  initWebSockets() {

    this.ws = new WebSocket(`ws://${window.location.hostname}:${window.location.port}/events`)
    this.ws.onopen = (event) => {
      this.ws?.send(window.localStorage.playerid)
    }
    const tableBCC = new BroadcastChannel('table')
    const gameBCC = new BroadcastChannel('game')
    this.ws.onmessage = (msg: MessageEvent) => {
      console.info('Received a WSS message')
      if (msg.data === 'table') {
        tableBCC.postMessage('table')
      }
      else {
        gameBCC.postMessage(msg.data)
      }
    }
  }

  getTableId = () => document.location.hash.length < 2 ? undefined : document.location.hash.substring(1)
  
  componentWillMount() {

    this.setState({tableId: this.getTableId()})
    window.onhashchange = () => {
      this.setState({tableId: this.getTableId()})
    }
    if (window.localStorage.name) {
      this.initWebSockets()
    }
  }

  render () {
    while (window.localStorage.getItem("name") === null) {
      let name = prompt("Name eingeben");
      if (name !== null && name.length > 0 ){
        window.localStorage.name = name
        window.localStorage.playerid = uuid()
        this.initWebSockets()
      }
    }

    const howto = <div className="app-logout">
      <a href="howto.html" className="jass-btn">HowTo</a>
    </div>

    if (this.state.tableId) {
      return <div>
        {howto}
        <GamePage tableId={this.state.tableId} />
      </div>
    }

    return <Fragment>
        {howto}
        <TablePage />    
      </Fragment>
  }
}

interface Props {}
interface State {
  tableId?: string
}

export default App;
