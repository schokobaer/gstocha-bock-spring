import React, {Fragment} from 'react'
import {Trumpf, GamePlayerDto, WriterDto, Pair} from '../dto/dtos';
import './KulmiTable.css'

class KulmiTable extends React.Component<Props, State> {

    trumpfOrder(): Array<string> {
        return this.props.writer.type === "base" ? [
            "E", "L", "H", "S", "G", "B", "7", "8", "K"
        ] : this.props.writer.type === "ESLH" ? [
            "E", "S", "L", "H", "G", "B", "7", "8", "K"
        ] : []
    }

    getPlayerName(idx: number): string {
        return this.props.players.find(p => p.position === idx)!.name
    }

    getTeam(idx: number): string {
        return idx === 0 ? this.getPlayerName(0) + " & " + this.getPlayerName(2) :
            this.getPlayerName(1) + " & " + this.getPlayerName(3)
    }



    render () {
        return <div className="kulmi-table">
                <div className="kulmi-table-head">
                    <div>{this.getTeam(0)}</div>
                    <div>{this.getTeam(1)}</div>
                </div>
                {this.trumpfOrder().map((t: string, i: number) =>
                    <div className="trumpf-row">
                        {this.props.writer.table.map((teamData, teamIdx) =>
                            <Fragment>
                                <div className={"kulmi-table-trumpf-col" + (teamData[i].first === 0 ? " trumpf-available" : "") + (this.props.writer.currentTeam === teamIdx && this.props.writer.currentTrumpf === this.trumpfOrder()[i] && this.props.highlight ? ' kulmi-table-active' : '')}>{t}</div>
                                <div className={this.props.writer.currentTeam === teamIdx && this.props.writer.currentTrumpf === this.trumpfOrder()[i] && this.props.highlight ? 'kulmi-table-active' : ''}>{teamData[i].first !== 0 ? teamData[i].first : ''}</div>
                                <div className={this.props.writer.currentTeam === teamIdx && this.props.writer.currentTrumpf === this.trumpfOrder()[i] && this.props.highlight ? 'kulmi-table-active' : ''}>{teamData[i].second !== 0 ? teamData[i].second : ''}</div>
                            </Fragment>
                        )}
                    </div>
                )}
            {this.props.writer.table.filter(teamData =>
                teamData.filter(trumpfData => trumpfData.first === 0).length === 0).length === this.props.writer.table.length && // check if all games are played
                <Fragment>
                    <div className="single-sum-row">
                        {this.props.writer.table.map(teamData =>
                            <Fragment>
                                <div className="kulmi-table-trumpf-col"></div>
                                <div>{teamData.reduce((sum: number, data: Pair<number, number>) => sum + data.first, 0)}</div>
                                <div>{teamData.reduce((sum: number, data: Pair<number, number>) => sum + data.second, 0)}</div>
                            </Fragment>
                        )}
                    </div>
                    <div className="total-sum-row">
                        {this.props.writer.table.map(teamData =>
                            <Fragment>
                                <div className="kulmi-table-trumpf-col"></div>
                                <div>
                                    {
                                        teamData.reduce((sum: number, data: Pair<number, number>) => sum + data.first, 0) +
                                        teamData.reduce((sum: number, data: Pair<number, number>) => sum + data.second, 0)
                                    }
                                </div>
                            </Fragment>
                        )}
                    </div>
                </Fragment>
            }
            </div>
    }
}

interface Props {
    players: Array<GamePlayerDto>
    writer: WriterDto
    highlight?: boolean
}
interface State {
}

export default KulmiTable;