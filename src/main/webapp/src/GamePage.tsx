import React, { Fragment } from 'react'
import { TableUpdateMessage, GameDto, TrumpfRequestBody, Trumpf, WeisRequestBody, PlayRequestBody, NewRequestBody, TableDto, Position, JoinRequestBody, UndoRequestBody, PlayerDto, StoeckeRequestBody } from 'gstochabock-core'
import './App.css';
import Hand from './Hand';
import Runde from './Runde';
import TrumpfSelector from './TrumpfSelector';
import WeisBag from './WeisBag';
import { setTimeout } from 'timers';
import WeisResolve from './WeisResolve';
import Table from './Table';
import RestClient from './RestClient';
import GameResult from './GameResult';

class GamePage extends React.Component<Props, State> {

    state: State = {
      loading: true,
      game: undefined,
      table: undefined,
      weising: false,
      weisCards: [],
      showLastStich: false
    }

    rest: RestClient = new RestClient()
    bcc: BroadcastChannel = new BroadcastChannel('game')

    componentDidMount() {
        this.loadTable = this.loadTable.bind(this)
        this.trumpfSelected = this.trumpfSelected.bind(this)
        this.playCard = this.playCard.bind(this)
        this.cardClicked = this.cardClicked.bind(this)
        this.makeWeis = this.makeWeis.bind(this)
        this.removeWeisCard = this.removeWeisCard.bind(this)
        this.nextGame = this.nextGame.bind(this)
        this.joinTable = this.joinTable.bind(this)
        this.undo = this.undo.bind(this)
        this.loadTable()

        this.bcc.onmessage = (msg: MessageEvent) => {
          console.info('GamePage: Received a BCC Msg')
          const updateMsg =  JSON.parse(msg.data) as TableUpdateMessage
          if (updateMsg.id === this.props.tableId) {
            this.loadTable()
          }
        }
    }

    componentWillUnmount() {
        this.bcc.close()
    }

    loadTable() {
        this.rest.getTable(this.props.tableId, window.localStorage.playerid).then(data => {
          console.info('Loaded table: ', data)
          if (data.id !== undefined) {
            console.info('Was a table')
            this.setState({game: undefined, table: data as TableDto, loading: false})
          } else {
            console.info('Was a game')
            const game = data as GameDto
            let weisResult = this.state.weisResult
            if (game.players[0]?.cards?.length === 8 /*<- trivial ??? */ && game.players.filter(p => p?.weis?.length || 0 > 0).length > 0) {
              weisResult = []
              for (let i = 0; i < 4; i++) {
                if (game.players[i]?.weis?.length || 0 > 0) {
                  weisResult.push({
                    player: game.players[i]!.name, 
                    weis: game.players[i]!.weis!
                  })
                }
              }
              console.info('There was a weis: ', weisResult)
            }
            // stoeckable
            let stoecke = this.state.stoecke
            if (game.players[0]?.cards?.length === 9 && game.trumpf) {
              if (game.players[0].cards.indexOf(game.trumpf + "O") >= 0 && game.players[0].cards.indexOf(game.trumpf + "K") >= 0) {
                stoecke = false
              } else {
                stoecke = undefined
              }
            }
            // showLastStich
            let showLastStich = this.state.showLastStich
            if (game.round.length === 0 && game.lastRound) {
              showLastStich = true
            }
            this.setState({game: data as GameDto, weisResult: weisResult, stoecke: stoecke, table: undefined, loading: false, showLastStich: showLastStich})
          }
        })
        .catch(err => {
            this.setState({loading: false})
            console.error('Could not fetch table', err)
        })
    }

    joinTable(table: TableDto, position: Position, password?: string) {
      console.info('Player joins table on position: ' + position)
      const reqBody: JoinRequestBody = {
        playerid: window.localStorage.playerid,
        name: window.localStorage.name,
        position: position,
        password: password
      }
      this.rest.join(this.props.tableId, reqBody)
      .catch(err => console.error('Could not join ', err))
      .finally(() => this.loadTable())
      this.setState({loading: true})
    }

    trumpfSelected(trumpf: Trumpf) {
      console.info('Player selected trumpf: ' + trumpf)
      const reqBody: TrumpfRequestBody = {
        playerid: window.localStorage.playerid,
        trumpf: trumpf
      }
      this.rest.trumpf(this.props.tableId, reqBody)
      .then(() => this.loadTable()).catch(err => console.error('Could not set trumpf ', err))
      this.setState({loading: true})
    }

    removeWeisCard(card: string) {
      const weisCards = [...this.state.weisCards]
      weisCards.splice(weisCards.indexOf(card), 1)
      this.setState({weisCards: weisCards})
    }

    makeWeis() {
      console.info('Player makes the weis ' + this.state.weisCards)
      const reqBody: WeisRequestBody = {
        playerid: window.localStorage.playerid,
        cards: this.state.weisCards
      }
      this.rest.weis(this.props.tableId, reqBody)
      .then(() => this.loadTable()).catch(err => console.error('Could set weis ', err))
      this.setState({weising: false, weisCards: []})
    }

    cardClicked(card: string) {
      if (this.state.weising) {
        if (this.state.weisCards.indexOf(card) < 0) {
          const weisCards = [...this.state.weisCards]
          weisCards.push(card)
          this.setState({weisCards: weisCards})
        }
      } else if (this.state.game?.currentMove === this.state.game?.players[0]?.position) {
        this.playCard(card)
      }
    }

    async playCard(card: string) {
      const table = this.state.game!
      if (table.round.length === 4) {
        table.round = []
      }
      table.round.push(card)
      table.players[0]!.cards!.splice(table.players[0]!.cards!.indexOf(card), 1)
      table.currentMove!++
      table.undoable = false
      let stoecke = this.state.stoecke
      if (stoecke === true) {
        if (card === this.state.game?.trumpf + "O" || card === this.state.game?.trumpf + "K") {
          const stoeckeReqBody: StoeckeRequestBody = {
            playerid: window.localStorage.playerid
          }
          console.info('Send stocke YESS')
          await this.rest.stoecke(this.props.tableId, stoeckeReqBody)
          .catch(err => console.warn(`Cound not indicate stoecke: ${err}`))
        } else {
          stoecke = false
        }
      }
      this.setState({game: table, weisResult: undefined, stoecke: stoecke})
      const reqBody: PlayRequestBody = {
        playerid: window.localStorage.playerid,
        card: card
      }
      this.rest.play(this.props.tableId, reqBody)
      .catch(err => console.error('Could not play card ', err))
    }

    undo() {
      const reqBody: UndoRequestBody = {
        playerid: window.localStorage.playerid
      }
      this.rest.undo(this.props.tableId, reqBody)
      .catch(err => console.error('Could not clean ', err))
      this.setState({loading: true})
    }

    nextGame() {
      const reqBody: NewRequestBody = {
        playerid: window.localStorage.playerid
      }
      this.rest.new(this.props.tableId, reqBody)
      .catch(err => console.error('Could not init next game ', err))
      this.setState({loading: true})
    }

    render () {

      // No data loaded yet
      if (this.state.loading) {
        return <div>Fetching data ...</div>
      }

      // Not joined the table yet
      if (this.state.table) {
        return <div style={{marginTop: '30px'}}>
          <Table table={this.state.table} onJoin={this.joinTable} />
        </div>
      }

      if (!this.state.game) {
        return <div>Error</div>
      }

      // Not all players joined yet
      if (this.state.game.players.filter(k => k !== null).length < 4) {
        return <Runde players={[this.state.game?.players[1] || null, this.state.game?.players[2] || null, 
                  this.state.game?.players[3] || null]} cards={[]} />
      }

      // Undoable
      let undoBtn
      if (this.state.game.undoable) {
        undoBtn = <div style={{position: 'absolute', left: '15px', top: '15px'}}>
          <button onClick={this.undo} className="jass-btn">Undo</button>
        </div>
      }

      // No trumpf set yet
      if (!this.state.game.trumpf) {
        return <Fragment>
          {undoBtn}
          <TrumpfSelector onSelected={this.trumpfSelected} players={this.state.game.players.map(p => p as PlayerDto)} />
          <Hand weisingAllowed={false} cards={this.state.game ? this.state.game.players[0]?.cards! : []} />
        </Fragment>
      }

      // WeisBag is open
      if (this.state.weising) {
        return <Fragment>
          <WeisBag
              cards={this.state.weisCards}
              onCardClick={this.removeWeisCard} 
              onWeis={this.makeWeis} 
              onCancel={() => this.setState({weising: false, weisCards: []})}/>
          <Hand 
              weisingAllowed={false}
              cards={this.state.game.players[0]?.cards!}
              onCardClick={card => this.cardClicked(card)}
              onStartWeising={() => this.setState({weising: true})} />
        </Fragment>
      }

      // All cards in round are set and a weis is available
      let weisResolve
      if (this.state.weisResult) {
        const weisingPlayers = this.state.game.players.filter(p => p?.weis?.length || 0 > 0).map(p => p!.name)
        weisResolve = <WeisResolve weises={this.state.weisResult} onClose={() => this.setState({weisResult: undefined})} />
      }
      
      // Game is over
      if(this.state.game.players[0]?.cards?.length === 0 && this.state.game.round?.length === 0) {
        return <GameResult 
                  players={this.state.game.players.map(p => p as PlayerDto)}
                  points={this.state.game.points!}
                  lastStich={this.state.game.lastRound!.cards}
                  weisPoints={[this.state.game.weisPoints![0].points, this.state.game.weisPoints![1].points]}
                  stoecke={[this.state.game.weisPoints![0].stoecke, this.state.game.weisPoints![1].stoecke]}
                  onNewGame={this.nextGame} />
      }

      // Normal playing state
      // if round is empty and lastRound is set -> lastRound, otherwise round
      let round = this.state.game.round
      let currentMove = this.state.game.currentMove
      if (this.state.game.round.length === 0 && this.state.game.lastRound && this.state.showLastStich) {
        round = this.state.game.lastRound.cards
        currentMove = this.state.game.lastRound.startPosition
        setTimeout(() => this.setState({showLastStich: false}), 2000)
      }
      // Stoecke Button
      let stoeckeBtn
      if (this.state.stoecke === false && 
        this.state.game.players[0]?.cards?.filter(c => c === this.state.game?.trumpf + "O" || 
        c === this.state.game?.trumpf + "K").length === 1 && this.state.game.currentMove === this.state.game.players[0].position) {
          stoeckeBtn = <button className="jass-btn" onClick={() => this.setState({stoecke: true})}>Stöcke</button>
      }
      return <Fragment>
        {undoBtn}
        {weisResolve}
        <Runde players={[this.state.game.players[1], this.state.game.players[2], this.state.game.players[3]]}
            roundStartPos={(currentMove! - round!.length + 4) % 4}
            cards={round!}
            lastRound={this.state.game.lastRound}
            trumpf={this.state.game.trumpf} />
        {stoeckeBtn}
        <Hand weisingAllowed={this.state.game.players[0]!.cards!.length === 9}
              cards={this.state.game.players[0]!.cards!}
              round={this.state.game.round}
              trumpf={this.state.game.trumpf}
              inMove={this.state.game.currentMove === this.state.game.players[0]!.position}
              onCardClick={card => this.cardClicked(card)}
              onStartWeising={() => this.setState({weising: true})} />
      </Fragment>
      
    }
  }
  
  interface Props {
    tableId: string
  }
  interface State {
    stoecke?: boolean // undefined -> not able; false -> able to but not done; true -> indicated stoecke
    loading: boolean
    game?: GameDto
    table?: TableDto
    weising: boolean // If the weis popup should be displayed
    weisCards: Array<string>
    showLastStich: boolean
    weisResult?: Array<{
      player: string
      weis: Array<string>
    }>
  }
  
  export default GamePage;