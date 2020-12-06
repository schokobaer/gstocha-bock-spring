import React, { Fragment } from 'react';
import Karte from './Karte';
import './WeisResolve.css'

export default class WeisResolve extends React.Component<Props, State> {

  weisToCards(weis: string): Array<string> {
      const order = ["6", "7", "8", "9", "X", "U", "O", "K", "A"]
      const cards: Array<string> = []

      if (weis[0] === "Q") {
        cards.push("E" + weis[1])
        cards.push("H" + weis[1])
        cards.push("L" + weis[1])
        cards.push("S" + weis[1])
      } else {
        for (let i = order.indexOf(weis[2]) - Number.parseInt(weis[0]) + 1; i <= order.indexOf(weis[2]); i++) {
            cards.push(weis[1] + order[i])
        }
      }

      return cards
  }

  getPlayerWeis(weis: WeisObj): JSX.Element {
    return <div>
        <h5>{weis.player}</h5>
        <div className="weis-resolve">
            {weis.weis.map(weis => this.weisToCards(weis)).map(w => <div>{w.map(card => <Karte value={card} />)}</div>)}
        </div>
    </div>
  }
  
  render() {
    return <div className="weis-window">
            <h3>Weisen</h3>
            {this.props.weises.map(w => this.getPlayerWeis(w))}
            <button className="jass-btn" style={{marginTop: '15px', fontSize: '18px'}} onClick={this.props.onClose}>Close</button>
        </div>
  }
}

type WeisObj = {
  player: string
  weis: Array<string>
}
interface Props {
  weises: Array<WeisObj>
  onClose: () => void
}

interface State {
  
}
