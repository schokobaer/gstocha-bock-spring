import React, { Fragment } from 'react'
import Karte from './Karte'
import './WeisBag.css'

export default class WeisBag extends React.Component<Props, State> {

  state: State = {
    weising: false
  }

  componentDidMount() {
    this.cardClicked = this.cardClicked.bind(this)
  }

  cardClicked(card: string) {
    console.info('Card clicked ' + card)
    if (this.props.onCardClick) {
      this.props.onCardClick(card)
    }
  }

  render() {
    return <Fragment>
        <div>
            <h2>Weis</h2>
            <div className="weis-bag">
                {this.props.cards.map(c => <Karte value={c} onClick={this.props.onCardClick} />)}
            </div>
            <div>
                <div className="jass-btn" style={{marginRight: '5px'}} onClick={this.props.onWeis}>Weis it!</div>
                <div className="jass-btn" onClick={this.props.onCancel}>Abbruch</div>
            </div>
        </div>
    </Fragment>
  }
}

interface Props {
  cards: Array<string>
  onCardClick: (card: string) => void
  onWeis: () => void
  onCancel: () => void
}

interface State {
  
}
