import React, { Fragment } from 'react'
import { GameDto, TrumpfRequestBody, Trumpf, WeisRequestBody, PlayRequestBody, TableDto, Position, JoinRequestBody, GamePlayerDto } from '../dto/dtos'
import '../App.css';
import Hand from '../component/Hand';
import Runde from '../component/Runde';
import TrumpfSelector from '../component/TrumpfSelector';
import WeisBag from '../component/WeisBag';
import { setTimeout } from 'timers';
import WeisResolve from '../component/WeisResolve';
import Table from '../component/Table';
import RestClient from '../rest/RestClient';
import GameResult from '../component/GameResult';
import WebSocketClient from "../rest/WebSocketClient";
import {getUserId, getUserName} from "../util/GameRepo";

class GamePage extends React.Component<Props, State> {

    state: State = {
      loading: true,
      stoecke: false,
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

        this.props.websocket.subscribeToGame(this.props.tableId)

        this.bcc.onmessage = (msg: MessageEvent) => {
          console.info('GamePage: Received a BCC Msg', msg)
          const gameid = msg.data
          if (gameid === this.props.tableId) {
            console.info('Update for my game')
            this.loadTable()
          } else {
              console.info('Update not for my game')
              console.info(gameid.data)
          }
        }
    }

    componentWillUnmount() {
        this.bcc.close()
    }

    loadTable() {
        this.rest.getTable(this.props.tableId, getUserId()!).then(data => {
          console.info('Loaded table: ', data)
          if (data.id !== undefined) {
            console.info('Was a table')
            this.setState({game: undefined, table: data as TableDto, loading: false})
          } else {
            console.info('Was a game')
            const game = data as GameDto
            let weisResult = this.state.weisResult
            if (game.cards.length === 8 /*<- trivial ??? */ && game.players.filter(p => p?.weis?.length || 0 > 0).length > 0) {
              // first round played -> show weis if available
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
            // showLastStich
            let showLastStich = this.state.showLastStich
            if (game.round.length === 0 && game.roundHistory.length > 0) {
              showLastStich = true
            }
            this.setState({game: data as GameDto, weisResult: weisResult, stoecke: false, table: undefined, loading: false, showLastStich: showLastStich})
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
        name: getUserName()!,
        position: position,
        password: password
      }
      this.rest.join(getUserId()!, this.props.tableId, reqBody)
      .catch(err => console.error('Could not join ', err))
      .finally(() => this.loadTable())
      this.setState({loading: true})
    }

    trumpfSelected(trumpf: Trumpf) {
      console.info('Player selected trumpf: ' + trumpf)
      const reqBody: TrumpfRequestBody = {
        trumpf: trumpf
      }
      this.rest.trumpf(getUserId()!, this.props.tableId, reqBody)
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
        cards: this.state.weisCards
      }
      this.rest.weis(getUserId()!, this.props.tableId, reqBody)
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
      table.cards.splice(table.cards.indexOf(card), 1)
      table.currentMove!++
      table.undoable = false
      if (this.state.stoecke) {
        if (card === this.state.game?.trumpf + "O" || card === this.state.game?.trumpf + "K") {
          console.info('Send stocke YESS')
          await this.rest.stoecke(getUserId()!, this.props.tableId)
          .catch(err => console.warn(`Cound not indicate stoecke: ${err}`))
        }
      }
      this.setState({game: table, weisResult: undefined, stoecke: false})
      const reqBody: PlayRequestBody = {
        card: card
      }
      this.rest.play(getUserId()!, this.props.tableId, reqBody)
      .catch(err => console.error('Could not play card ', err))
    }

    undo() {
      this.rest.undo(getUserId()!, this.props.tableId)
      .catch(err => console.error('Could not clean ', err))
      this.setState({loading: true})
    }

    nextGame() {
      this.rest.new(getUserId()!, this.props.tableId)
      .catch(err => console.error('Could not init next game ', err))
      this.setState({loading: true})
    }

    getTablePlayers(): Array<GamePlayerDto|null> {
        // hard coded on 4 players
        if (!this.state.game) {
            return []
        }
        const me = this.state.game.players.find(p => p.name === getUserName())!
        const f = (p: GamePlayerDto) => (p.position + (4 - me.position)) % 4
        const playersCopy = [...this.state.game.players].sort((a, b) => f(a) - f(b))
        const players: Array<GamePlayerDto|null> = []
        var pos = me.position as number
        var i = 0
        while (players.length < 3) {
            if (i >= playersCopy.length) {
                players.push(null)
                continue
            }
            if (playersCopy[i].position === pos) {
                if (playersCopy[i].position !== me.position) {
                    players.push(playersCopy[i])
                }
                i++
            } else {
                players.push(null)
            }
            pos = (pos + 1) % 4
        }
        return players
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

      // Assert thta we have a game
      if (!this.state.game) {
        return <div>Error</div>
      }

      // Not all players joined yet
      if (this.state.game.state === "PENDING") {
        return <Runde players={this.getTablePlayers()} cards={[]} />
      }

      // Undoable
      let undoBtn
      if (this.state.game.undoable) {
        undoBtn = <div style={{position: 'absolute', left: '15px', top: '15px'}}>
          <button onClick={this.undo} className="jass-btn">Undo</button>
        </div>
      }

      // No trumpf set yet
      if (this.state.game.state === "TRUMPF") {
        return <Fragment>
          {undoBtn}
          <TrumpfSelector onSelected={this.trumpfSelected} players={this.state.game.players.map(p => p as GamePlayerDto)} />
          <Hand weisingAllowed={false} cards={this.state.game ? this.state.game.cards : []} />
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
              cards={this.state.game.cards}
              onCardClick={card => this.cardClicked(card)}
              onStartWeising={() => this.setState({weising: true})} />
        </Fragment>
      }

      // All cards in round are set and a weis is available
      let weisResolve
      if (this.state.weisResult) {
        //const weisingPlayers = this.state.game.players.filter(p => p?.weis?.length || 0 > 0).map(p => p!.name) // TODO: What is this? does it work?
        weisResolve = <WeisResolve weises={this.state.weisResult} onClose={() => this.setState({weisResult: undefined})} />
      }
      
      // Game is over
      if(this.state.game.state === "FINISHED") {
          console.info('Should now render GameResult', this.state.game.weisPoints)
        return <GameResult 
                  players={this.state.game.players.map(p => p as GamePlayerDto)}
                  points={this.state.game.points!}
                  roundHistory={this.state.game.roundHistory}
                  weis={this.state.game.weisPoints || []}
                  onNewGame={this.nextGame} />
      }

      // Normal playing state
      // if round is empty and lastRound is set -> lastRound, otherwise round
      let round = this.state.game.round
      let currentMove = this.state.game.currentMove
      if (this.state.game.round.length === 0 && this.state.game.roundHistory.length > 0 && this.state.showLastStich) {
        round = this.state.game.roundHistory[this.state.game.roundHistory.length - 1].cards
        currentMove = this.state.game.roundHistory[this.state.game.roundHistory.length - 1].startPosition
        setTimeout(() => this.setState({showLastStich: false}), 2000)
      }

      // Stoecke Button
      let stoeckeBtn
      if (this.state.stoecke === false && this.state.game.stoeckeable === true &&
        this.state.game.cards.filter(c => c === this.state.game?.trumpf + "O" ||
        c === this.state.game?.trumpf + "K").length === 1 && this.state.game.currentMove === this.state.game.players[0].position) {
          stoeckeBtn = <button className="jass-btn" onClick={() => this.setState({stoecke: true})}>Stöcke</button>
      }


      return <Fragment>
        {undoBtn}
        {weisResolve}
        <Runde players={this.getTablePlayers()}
            roundStartPos={(currentMove! - round!.length + 4) % 4}
            cards={round!}
            lastRound={this.state.game.roundHistory[this.state.game.roundHistory.length - 1]}
            trumpf={this.state.game.trumpf} />
        {stoeckeBtn}
        <Hand weisingAllowed={this.state.game.cards.length === 9}
              cards={this.state.game.cards}
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
    websocket: WebSocketClient
  }
  interface State {
    stoecke: boolean // clicked on stoecke button
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