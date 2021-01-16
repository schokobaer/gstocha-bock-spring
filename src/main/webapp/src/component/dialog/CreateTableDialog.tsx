import React from 'react';
import './CreateTableDialog.css'
import Dialog from "../Dialog";
import Select from "react-select";

const jassRulesOptions = [
    {value: 'base', label: 'Base'},
    {value: 'dornbirn', label: 'Dornbirn'}
]

export default class CreateTableDialog extends React.Component<Props, State> {

    state: State = {
        passwordValue: "",
        logic: "base"
    }

    submit() {
        const data: CreateTableData = {
            password: this.state.passwordValue.length > 0 ? this.state.passwordValue : undefined,
            logic: this.state.logic
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
            <table style={{width: '100%'}}>
                <tr>
                    <td>Pasword</td>
                    <td>
                        <input
                            type="password"
                            className={pwCssClasses}
                            maxLength={15}
                            value={this.state.passwordValue}
                            onChange={(e) => this.setState({passwordValue: e.target.value}) }/>
                    </td>
                </tr>
                <tr>
                    <td>Jass Rules</td>
                    <td>
                        <Select
                            defaultValue={jassRulesOptions[0]}
                            options={jassRulesOptions}
                            onChange={(v: any) => this.setState({logic: v.value})} />
                    </td>
                </tr>
            </table>
            <div>
                <button className="jass-btn table-create-btn" style={{marginTop: '15px', fontSize: '18px'}} onClick={() => this.submit()}>Create</button>
                <button className="jass-btn" style={{marginTop: '15px', fontSize: '18px'}} onClick={() => this.props.onCancle()}>Cancel</button>
            </div>
        </Dialog>
    }
}

interface Props {
    onCancle: () => void
    onCreate: (data: CreateTableData) => void
}

interface State {
    passwordValue: string
    logic: string
}

export interface CreateTableData {
    password?: string
    logic: string
}