import React from 'react';
import './GameResult.css'
import { GamePlayerDto } from '../dto/dtos';
import Karte from './Karte';

/**
 * className={this.props.disabled ? "disabled" : ""}
 */

export default class GameResult extends React.Component<Props> {

  getNames(i: number): string {
    const players = this.props.players.filter(p => p.position % 2 === i)
    return `${players[0].name} & ${players[1].name}`
  }

  render() {
    return <div className="result-window">
          <div>
            <h2>Game Results</h2>
            <table>
              <tr>
                <td></td>
                <td>{this.getNames(0)}</td>
                <td>{this.getNames(1)}</td>
              </tr>
              <tr>
                <td>Points</td>
                <td>{this.props.points[0]}</td>
                <td>{this.props.points[1]}</td>
              </tr>
              <tr>
                <td>Weis</td>
                <td>{this.props.weisPoints[0]}</td>
                <td>{this.props.weisPoints[1]}</td>
              </tr>
              <tr>
                <td></td>
                <td>{this.props.stoecke[0] ? 'Stöcke' : ''}</td>
                <td>{this.props.stoecke[1] ? 'Stöcke' : ''}</td>
              </tr>
            </table>
          </div>

          <div className="last-stich">
            <h3>Letzter Stich</h3>
            {this.props.lastStich.map(c => <Karte value={c} />)}
          </div>
          <button className="jass-btn" onClick={this.props.onNewGame}>Nächstes Spiel</button>
        </div>
  }
}

interface Props {
    players: Array<GamePlayerDto>
    points: Array<number>
    weisPoints: Array<number>
    stoecke: Array<boolean>
    lastStich: Array<string>
    onNewGame: () => void
}