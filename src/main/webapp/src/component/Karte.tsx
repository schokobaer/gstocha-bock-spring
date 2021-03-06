import React from 'react';
import './Karte.css'

/**
 * className={this.props.disabled ? "disabled" : ""}
 *
 *
 return <div className={`karte ${this.props.disabled ? "disabled" : ""}`} style={zidx || {}}>
 <div>
 <img src={`img/cards/${this.props.value}.jpg`}
 alt={this.props.value}
 onClick={() => this.props.onClick && this.props.disabled !== true ? this.props.onClick(this.props.value!) : undefined } />
 </div>
 </div>
 */

export default class Karte extends React.Component<Props> {
  render() {
    if (!this.props.value) {
      return <div className="karte-empty"><img src="/img/cards/back.png" alt="Unkown Card" /></div>
    }
    let zidx: React.CSSProperties | undefined
    if (this.props.zIndex !== undefined) {
      zidx = {zIndex: this.props.zIndex + 1100}
    }
    return <div className={`karte ${this.props.disabled ? "disabled" : ""}`} style={zidx || {}}>
        <div>
            <div
                style={{backgroundImage: `url("img/cards/${this.props.value}.jpg")`}}
                onClick={() => this.props.onClick && this.props.disabled !== true ? this.props.onClick(this.props.value!) : undefined }>

            </div>
        </div>
    </div>
  }
}

interface Props {
    value?: string
    onClick?: (card: string) => void
    disabled?: boolean
    zIndex?: number
}