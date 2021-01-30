import React, { Fragment } from 'react';
import './Runde.css'
import Karte from './Karte';
import { GamePlayerDto } from '../dto/dtos';

export default class Runde extends React.Component<Props, State> {

    state: State = {
        showLast: false
    }

  /**
   * Finds the played card for a specific player. If the player has not played yet or the player is not given,
   * undefined is returned.
   * @param idx index of the player to find out the played card
   */
  getCard(idx: number): string | undefined {
      if (this.props.players[idx] === undefined) return undefined
      const roundStartPos = this.state.showLast && this.props.lastRound ? this.props.lastRound.startPosition : this.props.roundStartPos
      const cards = this.state.showLast && this.props.lastRound ? this.props.lastRound.cards : this.props.cards
      return this.props.players[idx]?.position === roundStartPos ? cards[0] :
        this.props.players[idx]?.position === (roundStartPos! + 4 + 1) % 4 ? cards[1] :
        this.props.players[idx]?.position === (roundStartPos! + 4 + 2) % 4 ? cards[2] :
        this.props.players[idx]?.position === (roundStartPos! + 4 + 3) % 4 ? cards[3] :
        undefined
  }

  getName(idx: number): string {
      if (this.props.players === null || this.props.players[idx] === null) {
          return "Empty"
      }
      return this.props.players[idx]!!.name
  }

  getWeisCall(idx: number) {
      if (this.props.players[idx] !== null && this.props.players[idx]!!.weisCall !== null) {
          const weissCall = this.props.players[idx]!!.weisCall
          const str = weissCall === "Q" ? "4 Gleiche" : weissCall + " Blatt"
          return <label className="runde-weiscall" style={{margin: '0 auto'}}>{str}</label>
      }
      return ""
  }

  getMyCard(): string | undefined {
    const cards = this.state.showLast && this.props.lastRound ? this.props.lastRound.cards : this.props.cards
    return cards.find(c => c !== this.getCard(0) && c !== this.getCard(1) && c !== this.getCard(2))
  }

  render() {
      let actionBtns
      if (this.props.trumpf) {
          actionBtns = <div className="action-ct">
              <div className="trumpf-ct">
                  <img src={`img/trumpf/${this.props.trumpf}.png`} />
              </div>
              <div className="peek-btn"
                   title="Letzter Stich"
                   style={{visibility: this.props.lastRound ? 'visible' : 'hidden'}}
                   onMouseEnter={() => this.setState({showLast: true})}
                   onTouchStart={() => this.setState({showLast: true})}
                   onTouchEnd={() => this.setState({showLast: false})}
                   onTouchCancel={() => this.setState({showLast: false})}
                   onMouseLeave={() => this.setState({showLast: false})}>👀</div>
            </div>
      }
    return <div className="runde-ct">
            {actionBtns}
            <div className="runde-top">
                {this.getWeisCall(1)}
                <label className="runde-playername">{this.getName(1)}</label><br />
                <div style={{transform: 'rotate(180deg)'}}>
                    <Karte value={this.getCard(1)} />
                </div>
            </div>
            <div className="runde-middle">
                <div>
                    <div className="runde-middle-player-ct">
                        <label className="runde-playername" style={{margin: '0 auto'}}>{this.getName(0)}</label>
                        {this.getWeisCall(0)}
                    </div>
                    <div>
                        <div style={{transform: 'rotate(90deg)'}}>
                            <Karte value={this.getCard(0)} />
                        </div>
                    </div>
                </div>
                <div>
                    <div>
                        <div style={{transform: 'rotate(270deg)'}}>
                            <Karte value={this.getCard(2)} />
                        </div>
                    </div>
                    <div className="runde-middle-player-ct">
                        <label className="runde-playername" style={{margin: '0 auto'}}>{this.getName(2)}</label>
                        {this.getWeisCall(2)}
                    </div>
                </div>
            </div>
            <div className="runde-top">
                <Karte value={this.getMyCard()} />
            </div>
        </div>
  }
}

interface Props {
    players: Array<GamePlayerDto | null>
    roundStartPos?: number
    cards: Array<string>
    trumpf?: string
    lastRound?: {
        startPosition: number;
        cards: Array<string>;
    }
}

interface State {
    showLast: boolean
}