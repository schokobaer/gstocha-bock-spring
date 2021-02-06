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
    setup: true,
    topBanner: true
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
      this.hintBanner()
      this.initWebSockets()
    }

  }

  hintBanner() {
    this.setState({topBanner: true})
    setTimeout(() => this.setState({topBanner: false}), 800)
  }

  setName(name: string) {
    if (name.length > 0 && name.length <= 15) {
      setUserName(name)
      if (getUserId() === null) {
        setUserId(uuid())
      }
      this.setState({setup: false})
      this.hintBanner()
    }
  }

  render () {
    if (this.state.setup === true) {
      return <NameDialog onNameSet={(name: string) => this.setName(name)} onCancel={() => this.setState({setup: false})} />
    }

    const bannerHeight = document.body.clientWidth < 800 ? '-87px' : '-60px'
    const topBanner = <Fragment>
      <div className="top-banner" style={{marginTop: this.state.topBanner ? '0px' : bannerHeight}}>
        <a href="/" className="logo"><img src="/logo192.png" /><span>Gstocha-Bock</span></a>
        <div>
          <span className="jass-btn" onClick={() => this.setState({setup: true})}>Change Name</span>
          <a href="howto.html" target="_blank" className="howto jass-btn">HowTo</a>
        </div>
      </div>
      <div style={{display: 'flex', flexDirection: 'row-reverse'}}>
        <div className="top-banner-down-btn" onClick={() => this.setState({topBanner: !this.state.topBanner})}>💡</div>
      </div>
    </Fragment>

    if (this.state.tableId) {
      return <div>
        {topBanner}
        <GamePage websocket={this.ws!} tableId={this.state.tableId} />
      </div>
    }

    return <Fragment>
        {topBanner}
        <TablePage />    
      </Fragment>
  }
}

interface Props {}
interface State {
  tableId?: string
  setup: boolean
  topBanner: boolean
}

export default App;
