import React from 'react';
import { TableDto, Position } from '../dto/dtos';
import './Table.css'

export default class Table extends React.Component<Props> {

  componentDidMount() {
    this.joinTable = this.joinTable.bind(this)
  }

  joinTable(pos: Position) {
    this.props.onJoin!(this.props.table, pos)
  }

    getChair(idx: Position) {
        const player = this.props.table.players.find(p => p.position === idx)
        if (player) {
            return player.name
        } else if (this.props.onJoin === undefined) {
            return '?'
        }
        return <button className="jass-btn" onClick={() => this.joinTable(idx)}>Join</button>
    }

  render() {
    return <div className="table-ct">
      <div className="table-row">
        <div>{this.props.puck === 0 && "⚫ "} {this.getChair(0)}</div>
        <div>{this.props.puck === 1 && "⚫ "}{this.getChair(1)}</div>
      </div>
      <div className="table-row">
        <div>{this.props.puck === 3 && "⚫ "}{this.getChair(3)}</div>
        <div>{this.props.puck === 2 && "⚫ "}{this.getChair(2)}</div>
      </div>
    </div>
  }
}

interface Props {
    table: TableDto
    onJoin?: (table: TableDto, position: Position) => void
    puck?: number
}