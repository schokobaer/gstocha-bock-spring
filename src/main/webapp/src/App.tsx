import React, { Fragment } from 'react';
import './App.css';
import TablePage from './page/TablePage';
import GamePage from './page/GamePage';
import WebSocketClient from './rest/WebSocketClient'
import { uuid } from 'uuidv4'
import {getUserId, getUserName, setUserId, setUserName} from "./util/GameRepo";
import NameDialog from "./component/dialog/NameDialog";

class App extends React.Component<Props, State> {

  state: State = {
    tableId: undefined,
    setup: true
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

    if (getUserName() && getUserId()) {
      this.setState({setup: false})
      this.initWebSockets()
    }
  }

  setName(name: string) {
    if (name.length > 0 && name.length <= 15) {
      setUserName(name)
      setUserId(uuid())
      this.setState({setup: false})
    }
  }

  render () {
    if (this.state.setup === true) {
      return <NameDialog onNameSet={(name: string) => this.setName(name)} />
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
  setup: boolean
}

export default App;
