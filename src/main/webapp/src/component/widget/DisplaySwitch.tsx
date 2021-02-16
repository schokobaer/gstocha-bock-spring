import React from 'react';
import './DisplaySwitch.css'

/**
 * className={this.props.disabled ? "disabled" : ""}
 */

export default class DisplaySwitch<T> extends React.Component<Props<T>, State> {

    render() {
        return <div className="display-switch">
            {this.props.items.map(i =>
                <div className={'display-switch-item' + (this.props.value === i.value ? ' display-switch-item-active' : '')}
                    onClick={() => this.props.value !== i.value && this.props.onChange(i.value)}>{i.title}</div>
            )}
        </div>
    }
}

export interface SwitchItem<T> {
    value: T
    title: string
}

interface Props<T> {
    onChange: (value: T) => void
    items: Array<SwitchItem<T>>
    value: T
}

interface State {

}