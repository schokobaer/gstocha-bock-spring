import React from 'react'
import {Trumpf, GamePlayerDto, WriterDto} from '../dto/dtos';
import './TrumpfSelector.css'
import Table from './Table';
import KulmiTable from "./KulmiTable";

class TrumpfSelector extends React.Component<Props, State> {

    state: State = {
        showTable: false,
        j7: false,
        j8: false
    }

    trumpfOrder(): Array<string> {
        if (this.props.writer) {
            return this.props.writer.type === "base" ? [
                "E", "L", "H", "S", "G", "B", "7", "8", "K"
            ] : this.props.writer.type === "dornbirn" ? [
                "E", "S", "L", "H", "G", "B", "7", "8", "K"
            ] : []
        }
        return []
    }
    
    select(trumpf: Trumpf) {
        this.props.onSelected(trumpf, this.state.j7 ? "7" : this.state.j8 ? "8" : undefined)
    }

    render () {
        return <div className="trumpfselector-ct">
            <h2>{this.state.showTable ? "Tabelle" : "Trumpf auswählen"}</h2>

            {this.state.showTable ?
                <KulmiTable players={this.props.players} writer={this.props.writer!}/>
                :
                <div>
                    <div className="trumpfselector-flex">
                        <button className="trumpfselector-trumpf" title="Eichel"
                                onClick={() => this.select('E')}><img src="img/trumpf/E.svg" alt="E"/>
                        </button>
                        <button className="trumpfselector-trumpf" title="Laub"
                                onClick={() => this.select('L')}><img src="img/trumpf/L.svg" alt="L"/>
                        </button>
                        <button className="trumpfselector-trumpf" title="Herz"
                                onClick={() => this.select('H')}><img src="img/trumpf/H.svg" alt="H"/>
                        </button>
                        <button className="trumpfselector-trumpf" title="Schell"
                                onClick={() => this.select('S')}><img src="img/trumpf/S.svg" alt="S"/>
                        </button>
                    </div>
                    <div className="trumpfselector-flex">
                        <button className="trumpfselector-trumpf" title="Goas"
                                onClick={() => this.select('G')}><img src="img/trumpf/G.svg" alt="G"/>
                        </button>
                        <button className="trumpfselector-trumpf" title="Bock"
                                onClick={() => this.select('B')}><img src="img/trumpf/B.svg" alt="B"/>
                        </button>
                        <button className="trumpfselector-trumpf" title="Kulmi Unten"
                                onClick={() => this.select('KU')}><img src="img/trumpf/KU.svg" alt="KU"/>
                        </button>
                        <button className="trumpfselector-trumpf" title="Kulmi Oben"
                                onClick={() => this.select('KO')}><img src="img/trumpf/KO.svg" alt="KO"/>
                        </button>
                    </div>
                    {this.props.writer &&
                    <div className="trumpfselector-j-row">
                        <div>
                            <button className={"trumpfselector-trumpf trumpfselector-j" + (this.state.j7 ? " trumpfselector-j-active" : "")} title="7"
                                    onClick={() => this.setState({j7: !this.state.j7, j8: false})}>7
                            </button>
                            <button className={"trumpfselector-trumpf trumpfselector-j" + (this.state.j8 ? " trumpfselector-j-active" : "")} title="8"
                                    onClick={() => this.setState({j7: false, j8: !this.state.j8})}>8
                            </button>
                        </div>
                    </div>
                    }
                </div>
            }

            <div className="table-ct-ct">
                <Table puck={this.props.puck} table={{id: '', protected: false, players: this.props.players, randomOrder: false}} onJoin={() => {}} />
            </div>

            {this.props.writer &&
            <button className="jass-btn"
                    onClick={() => this.setState({showTable: !this.state.showTable})}>{this.state.showTable ? "Trumpf" : "Tabelle"}</button>
            }
        </div>
    }
}

interface Props {
    onSelected: (trumpf: Trumpf, joker?: string) => void
    players: Array<GamePlayerDto>
    puck?: number
    writer?: WriterDto
}
interface State {
    showTable: boolean
    j7: boolean
    j8: boolean
}

export default TrumpfSelector;