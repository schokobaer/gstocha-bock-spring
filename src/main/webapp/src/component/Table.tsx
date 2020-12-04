import React from 'react';
import { TableDto, Position } from '../dto/dtos';
import './Table.css'

export default class Table extends React.Component<Props> {

  componentDidMount() {
    this.joinTable = this.joinTable.bind(this)
  }

  joinTable(pos: Position) {
    let pw: string | undefined = undefined
    if (this.props.table.protected) {
      while (!pw || !pw!.length) {
        pw = prompt('Passwort:') || undefined
      }
    }
    this.props.onJoin(this.props.table, pos, pw)
  }

    getChair(idx: Position) {
        const player = this.props.table.players.find(p => p.position === idx)
        if (player) {
            return player.name
        }
        return <button className="jass-btn" onClick={() => this.joinTable(idx)}>Join</button>
    }

  render() {
    return <div className="table-ct">
      <div className="table-row">
        <div>{this.getChair(0)}</div>
        <div>{this.getChair(1)}</div>
      </div>
      <div className="table-row">
        <div>{this.getChair(3)}</div>
        <div>{this.getChair(2)}</div>
      </div>
    </div>
  }
}

interface Props {
    table: TableDto
    onJoin: (table: TableDto, position: Position, password?: string) => void
}