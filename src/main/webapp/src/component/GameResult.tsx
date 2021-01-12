import React from 'react';
import './GameResult.css'
import { GamePlayerDto, WeisPoints } from '../dto/dtos';
import Karte from './Karte';

export default class GameResult extends React.Component<Props> {

  getNames(i: number): string {
    const players = this.props.players.filter(p => p.position % 2 === i)
    return `${players[0].name} & ${players[1].name}`
  }

  getTableId = () => document.location.hash.length < 2 ? undefined : document.location.hash.substring(1)

  render() {
    return <div className="result-window">
          <div>
            <h2>Game Results</h2>
            <table>
              <tr>
                <td></td>
                <td>{this.getNames(0) /* TODO: Make generic */}</td>
                <td>{this.getNames(1)}</td>
              </tr>
              <tr>
                <td>Points</td>
                  {this.props.points.map(p=><td>{p}</td>)}
              </tr>
              <tr>
                <td>Weis</td>
                  {this.props.weis.map(w => <td>{w.points}</td>)}
              </tr>
              <tr>
                <td></td>
                  {this.props.weis.map(w=><td>{w.stoecke ? 'Stöcke' : ''}</td>)}
              </tr>
            </table>
          </div>

          <div className="last-stich">
            <h3>Letzter Stich</h3>
            {this.props.lastStich.map(c => <Karte value={c} />)}
          </div>
          <button className="jass-btn" onClick={this.props.onNewGame}>Nächstes Spiel</button>
          <br />
          <p>
            <a href={"/api/table/" + this.getTableId() + "/log"} target="_blank">generate game logs</a>
          </p>
        </div>
  }
}

interface Props {
    players: Array<GamePlayerDto>
    points: Array<number>
    weis: Array<WeisPoints>
    lastStich: Array<string>
    onNewGame: () => void
}