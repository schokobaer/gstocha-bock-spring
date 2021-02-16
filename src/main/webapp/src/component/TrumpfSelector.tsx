import React from 'react'
import {Trumpf, GamePlayerDto, WriterDto} from '../dto/dtos';
import './TrumpfSelector.css'
import Table from './Table';
import KulmiTable from "./KulmiTable";

class TrumpfSelector extends React.Component<Props, State> {

    state: State = {
        showTable: false
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
                                onClick={() => this.props.onSelected('E')}><img src="img/trumpf/E.svg" alt="E"/>
                        </button>
                        <button className="trumpfselector-trumpf" title="Laub"
                                onClick={() => this.props.onSelected('L')}><img src="img/trumpf/L.svg" alt="L"/>
                        </button>
                        <button className="trumpfselector-trumpf" title="Herz"
                                onClick={() => this.props.onSelected('H')}><img src="img/trumpf/H.svg" alt="H"/>
                        </button>
                        <button className="trumpfselector-trumpf" title="Schell"
                                onClick={() => this.props.onSelected('S')}><img src="img/trumpf/S.svg" alt="S"/>
                        </button>
                    </div>
                    <div className="trumpfselector-flex">
                        <button className="trumpfselector-trumpf" title="Goas"
                                onClick={() => this.props.onSelected('G')}><img src="img/trumpf/G.svg" alt="G"/>
                        </button>
                        <button className="trumpfselector-trumpf" title="Bock"
                                onClick={() => this.props.onSelected('B')}><img src="img/trumpf/B.svg" alt="B"/>
                        </button>
                        <button className="trumpfselector-trumpf" title="Kulmi Unten"
                                onClick={() => this.props.onSelected('KU')}><img src="img/trumpf/KU.svg" alt="KU"/>
                        </button>
                        <button className="trumpfselector-trumpf" title="Kulmi Oben"
                                onClick={() => this.props.onSelected('KO')}><img src="img/trumpf/KO.svg" alt="KO"/>
                        </button>
                    </div>
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
    onSelected: (trumpf: Trumpf) => void
    players: Array<GamePlayerDto>
    puck?: number
    writer?: WriterDto
}
interface State {
    showTable: boolean
}

export default TrumpfSelector;