import React, { Fragment } from 'react';
import './App.css';
import TablePage from './page/TablePage';
import GamePage from './page/GamePage';
import WebSocketClient from './rest/WebSocketClient'
import { uuid } from 'uuidv4'
import {getUserName, setUserId, setUserName} from "./util/GameRepo";

class App extends React.Component<Props, State> {

  state: State = {
    tableId: undefined
  }

  ws: WebSocketClient | null = null

  initWebSockets() {

    this.ws = new WebSocketClient()
    this.ws.connect()

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
    while (getUserName() === undefined) {
      let name = prompt("Name eingeben");
      if (name !== null && name.length > 0 ){
        setUserName(name)
        setUserId(uuid())
        this.initWebSockets()
      }
    }

    const howto = <div className="app-logout">
      <a href="howto.html" className="jass-btn">HowTo</a>
    </div>

    if (this.state.tableId) {
      return <div>
        {howto}
        <GamePage websocket={this.ws!} tableId={this.state.tableId} />
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
