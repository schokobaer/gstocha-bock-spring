import React from 'react';
import './CreateTableDialog.css'
import Dialog from "../Dialog";

export default class CreateTableDialog extends React.Component<Props, State> {

    state: State = {
        passwordValue: ""
    }

    submit() {
        const data: CreateTableData = {
            password: this.state.passwordValue.length > 0 ? this.state.passwordValue : undefined
        }
        this.props.onCreate(data)
    }

    render() {
        let pwCssClasses = "pw-tbx"
        if (this.state.passwordValue.length === 0) {
            pwCssClasses += " pw-tbx-empty"
        }

        return <Dialog>
            <h3>Neuer Tisch</h3>
            <div>
                Password:
                <input type="password" className={pwCssClasses} maxLength={15} value={this.state.passwordValue} onChange={(e) => this.setState({passwordValue: e.target.value}) }/>
            </div>
            <div>
                <button className="jass-btn table-create-btn" style={{marginTop: '15px', fontSize: '18px'}} onClick={() => this.submit()}>Create</button>
                <button className="jass-btn" style={{marginTop: '15px', fontSize: '18px'}} onClick={() => this.props.onCancle()}>Cancel</button>
            </div>
        </Dialog>
    }
}

interface Props {
    onCancle: Function
    onCreate: Function
}

interface State {
    passwordValue: string
}

export interface CreateTableData {
    password?: string
}