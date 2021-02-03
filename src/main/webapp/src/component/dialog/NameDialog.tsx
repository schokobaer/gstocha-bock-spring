import React from 'react';
import './NameDialog.css'
import Dialog from "../Dialog";
import {getUserName} from "../../util/GameRepo";

/**
 * className={this.props.disabled ? "disabled" : ""}
 */

export default class NameDialog extends React.Component<Props, State> {

    state: State = {
        nameValue: ""
    }

    componentDidMount() {
        const name = getUserName() || ""
        this.setState({nameValue: name})
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
                <input type="text" className="name-tbx" maxLength={15} value={this.state.nameValue}
                       onKeyDown={e => e.key === 'Enter' && this.submit()}
                       onChange={e => this.nameChange(e.target.value)} />
            </div>
            <div>
                <button className="jass-btn" style={{marginTop: '15px', fontSize: '18px'}} onClick={() => this.submit()}>Ok</button>
                <button className="jass-btn"
                        style={{marginTop: '15px', marginLeft: '10px', fontSize: '18px', visibility: getUserName() ? "visible" : "hidden"}}
                        onClick={() => this.props.onCancel!()}>Cancel</button>
            </div>
        </Dialog>
    }
}

interface Props {
    onNameSet: (name: string) => void
    onCancel?: () => void
}

interface State {
    nameValue: string
}