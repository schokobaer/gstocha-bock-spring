import React, { Fragment } from 'react';
import './Runde.css'
import Karte from './Karte';
import { PlayerDto } from 'gstochabock-core';

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
                   onMouseLeave={() => this.setState({showLast: false})}>👀</div>
            </div>
      }
    return <div className="runde-ct">
            {actionBtns}
            <div className="runde-top">
                <label className="runde-playername">{this.props.players[1]?.name || "Empty"}</label><br />
                <div style={{transform: 'rotate(180deg)'}}>
                    <Karte value={this.getCard(1)} />
                </div>
            </div>
            <div className="runde-middle">
                <div style={{flexDirection: 'column'}}>
                    <label className="runde-playername" style={{margin: '0 auto'}}>{this.props.players[0]?.name || "Empty"}</label>
                </div>
                <div>
                    <div style={{transform: 'rotate(90deg)'}}>
                        <Karte value={this.getCard(0)} />
                    </div>
                </div>
                <div>
                    <div style={{transform: 'rotate(270deg)'}}>
                        <Karte value={this.getCard(2)} />
                    </div>
                </div>
                <div style={{flexDirection: 'column'}}>
                    <label className="runde-playername" style={{margin: '0 auto'}}>{this.props.players[2]?.name || "Empty"}</label>
                </div>
            </div>
            <div className="runde-top">
                <Karte value={this.getMyCard()} />
            </div>
        </div>
  }
}

interface Props {
    players: Array<PlayerDto | null>
    roundStartPos?: number
    cards: Array<string>
    trumpf?: string
    lastRound?: {
        startPosition: number;
        cards: [string, string, string, string];
    }
}

interface State {
    showLast: boolean
}