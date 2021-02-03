import React from 'react';
import './PasswordDialog.css'
import Dialog from "../Dialog";

export default class PasswordDialog extends React.Component<Props, State> {

    state: State = {
        passwordValue: ""
    }

    submit() {
        this.props.onSubmit(this.state.passwordValue)
    }

    render() {
        let pwCssClasses = "pw-tbx"
        if (this.state.passwordValue.length === 0) {
            pwCssClasses += " pw-tbx-empty"
        }

        return <Dialog>
            <div>
                Password:
                <input type="password" className={pwCssClasses} maxLength={15} value={this.state.passwordValue}
                       onKeyDown={e => e.key === 'Enter' && this.submit()}
                       onChange={(e) => this.setState({passwordValue: e.target.value}) }/>
            </div>
            <div>
                <button className="jass-btn ok-btn" style={{marginTop: '15px', fontSize: '18px'}} onClick={() => this.submit()}>Ok</button>
                <button className="jass-btn" style={{marginTop: '15px', fontSize: '18px'}} onClick={() => this.props.onCancle()}>Cancel</button>
            </div>
        </Dialog>
    }
}

interface Props {
    onCancle: Function
    onSubmit: (pw: string) => void
}

interface State {
    passwordValue: string
}
