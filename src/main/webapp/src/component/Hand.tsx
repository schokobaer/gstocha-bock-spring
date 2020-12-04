import React, { Fragment } from 'react';
import Karte from './Karte';
import './Hand.css'
import { Trumpf } from '../dto/dtos';
import { cardAllowed } from '../logic/BaseJassLogic'

export default class Hand extends React.Component<Props, State> {

  state: State = {
    weising: false
  }

  componentDidMount() {
    this.cardClicked = this.cardClicked.bind(this)
  }

  isCardDisabled(card: string): boolean {
    if (this.props.inMove === false) {
      return true
    }
    if (this.props.round === undefined || this.props.trumpf === undefined) {
      return false
    }
    if (this.props.round.length === 4 && this.props.inMove) {
      return false
    }
    return !cardAllowed(this.props.round, card, this.props.cards, this.props.trumpf)
  }

  cardClicked(card: string) {
    console.info('Card clicked ' + card)
    if (this.props.onCardClick) {
      this.props.onCardClick(card)
    }
  }

  render() {
    let weisBtn
    if (this.props.weisingAllowed) {
      weisBtn = <div className="jass-btn" style={{marginTop: '30px'}} onClick={() => this.props.onStartWeising!()}>Weisen</div>
    }
    return <Fragment>
      <div className="hand-ct">
        <div className="hand-card-ct">{this.props.cards.map((c, i) => <Karte zIndex={i} disabled={this.isCardDisabled(c)} value={c} key={c} onClick={card => this.cardClicked(card)} />)}</div>
      </div>
      {weisBtn}
    </Fragment>
  }
}

interface Props {
  cards: Array<string>
  weisingAllowed: boolean // if it is ok to show the weising button
  onCardClick?: (card: string) => void
  onStartWeising?: () => void
  round?: Array<string>
  trumpf?: Trumpf
  inMove?: boolean
}

interface State {
  
}
