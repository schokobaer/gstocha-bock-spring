import React from 'react';
import './NameDialog.css'
import Dialog from "../Dialog";

/**
 * className={this.props.disabled ? "disabled" : ""}
 */

export default class NameDialog extends React.Component<Props, State> {

    state: State = {
        nameValue: ""
    }

    nameChange(val: string) {
        this.setState({nameValue: val})
    }

    submit() {
        console.info("Submitting name: ", this.state.nameValue)
        this.props.onNameSet(this.state.nameValue)
    }

    render() {
        return <Dialog>
            <div>Name:
            <input type="text" className="name-tbx" maxLength={15} value={this.state.nameValue} onChange={e => this.nameChange(e.target.value)} /></div>
            <div><button className="jass-btn" style={{marginTop: '15px', fontSize: '18px'}} onClick={() => this.submit()}>Ok</button></div>
        </Dialog>
    }
}

interface Props {
    onNameSet: Function
}

interface State {
    nameValue: string
}