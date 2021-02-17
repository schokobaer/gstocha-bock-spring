import React from 'react';
import './GameResult.css'
import {GamePlayerDto, GameRoundDto, WeisPoints, WriterDto} from '../dto/dtos';
import Karte from './Karte';
import DisplaySwitch, {SwitchItem} from "./widget/DisplaySwitch";
import KulmiTable from "./KulmiTable";

export default class GameResult extends React.Component<Props, State> {

    state: State = {
        display: 'RESULT',
        puckRestart: false
    }

    getNames(i: number): string {
        const players = this.props.players.filter(p => p.position % 2 === i)
        return `${players[0].name} & ${players[1].name}`
    }

    getTableId = () => document.location.hash.length < 2 ? undefined : document.location.hash.substring(1)

    getStichTable() {
        const idxArr: Array<number> = []
        const resultArr: Array<Array<Array<string>>> = []
        let i = 0

        // adding the indizes for the teams
        this.props.points.forEach(p => idxArr.push(0))

        const history = this.props.roundHistory
        while (i < this.props.roundHistory.length) {
            const teamIdx = history[i].teamIndex
            if (idxArr[teamIdx] >= resultArr.length) {
                // adding a new row
                const row: Array<any> = []
                this.props.points.forEach(p => row.push(null))
                row[teamIdx] = history[i].cards
                resultArr.push(row)
            } else {
                // looking for the row
                resultArr[idxArr[teamIdx]][teamIdx] = history[i].cards
            }

            // increasing counter
            idxArr[history[i].teamIndex] = idxArr[history[i].teamIndex] + 1
            i++
        }
        return resultArr
    }

    render() {
        let gameResult = <React.Fragment>
            <div>
                <table style={{width: '100%'}}>
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
                {this.props.roundHistory[this.props.roundHistory.length - 1].cards.map(c => <Karte value={c} />)}
            </div>
        </React.Fragment>

        let stiche = <React.Fragment>
            <div>
                <table style={{width: '100%'}}>
                    <tr>
                        <td>{this.getNames(0) /* TODO: Make generic */}</td>
                        <td>{this.getNames(1)}</td>
                    </tr>
                    {this.getStichTable().map(row =>
                        <tr>
                            {row.map(cell =>
                            <td>
                                {cell !== null ? <div className="history-stich">
                                    {cell.map(c => <Karte value={c} />)}
                                </div> : ''}
                            </td>
                            )}
                        </tr>)
                    }
                </table>
            </div>
        </React.Fragment>

        const displayItems: Array<SwitchItem<DisplayValue>> = [
            {title: 'Ergebnis', value: 'RESULT'},
            {title: 'Stiche', value: 'STICHE'}
        ]
        if (this.props.writer) {
            displayItems.push({title: 'Tabelle', value: 'TABLE'})
        }

        return <React.Fragment>
            <div className="result-window">

                <DisplaySwitch onChange={(value: DisplayValue) => this.setState({display: value})}
                               items={displayItems}
                               value={this.state.display} />

                {this.state.display === 'STICHE' && stiche}
                {this.state.display === 'RESULT' && gameResult}
                {this.state.display === 'TABLE' && <KulmiTable players={this.props.players} writer={this.props.writer!} highlight={true} /> }

                {this.props.writer == null &&
                <div onClick={() => this.setState({puckRestart: !this.state.puckRestart})}>
                    <input type="checkbox"
                           checked={this.state.puckRestart}
                           style={{marginTop: '10px'}}/> Puck neu vergeben
                </div>
                }
                <button className="jass-btn" onClick={() => this.props.onNewGame(this.state.puckRestart)}>Nächstes Spiel</button>
                <br />

                <p>
                    <a href={"/api/table/" + this.getTableId() + "/log"} target="_blank">generate game logs</a>
                </p>
            </div>
        </React.Fragment>
    }
}

interface Props {
    players: Array<GamePlayerDto>
    points: Array<number>
    weis: Array<WeisPoints>
    roundHistory: Array<GameRoundDto>
    onNewGame: (puckRestart: boolean) => void
    writer?: WriterDto
}

type DisplayValue = 'RESULT' | 'STICHE' | 'TABLE'

interface State {
    display: DisplayValue
    puckRestart: boolean
}