import React, {Fragment, ReactFragment} from 'react';
import './CreateTableDialog.css'
import Dialog from "../Dialog";
import Select from "react-select";

const jassRulesOptions = [
    {value: 'base', label: 'Base'},
    {value: 'dornbirn', label: 'Dornbirn'}
]

const jassRuleDescriptions: Record<string, any> = {
    'base':
    <Fragment>
        <ul>
            <li>4 Gleiche 6, 7, 8 erlaubt bei Goas, Bock, Kulmi</li>
            <li>Niedrigere Weis zählt bei Goas</li>
        </ul>
    </Fragment>,
    'dornbirn': <Fragment>
        <ul>
            <li>4 Gleiche 6, 7, 8 sind nie erlaubt</li>
            <li>Es zählt immer der höhere Weis</li>
        </ul>
    </Fragment>
}

const puckCardOptions = [
    {value: 'S6', label: 'Weli'},
    {value: 'SX', label: 'Schell 10'},
    {value: undefined, label: 'Random'},
]

const writerOptions = [
    {value: undefined, label: 'Ohne'},
    {value: 'base', label: 'Richtig'},
    {value: 'ESLH', label: 'Dornbirn'}
]

const writerDescriptions: Record<string, any> = {
    'base':
        <Fragment>Eichel, Laub, Herz, Schell, Geiss, Bock, 7, 8, Kulmi </Fragment>,
    'ESLH': <Fragment>Eichel, Schell, Laub, Herz, Geiss, Bock, 7, 8, Kulmi</Fragment>,
    'undefined': <Fragment>Keine Schrift, offenes Spiel</Fragment>
}

export default class CreateTableDialog extends React.Component<Props, State> {

    state: State = {
        passwordValue: "",
        logic: "base",
        starter: 'S6',
        randomizePlayerPosition: false
    }

    submit() {
        const data: CreateTableData = {
            password: this.state.passwordValue.length > 0 ? this.state.passwordValue : undefined,
            logic: this.state.logic,
            starter: this.state.starter,
            randomizePlayerPosition: this.state.randomizePlayerPosition,
            writer: this.state.writer
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
                            onKeyDown={e => e.key === 'Enter' && this.submit()}
                            onChange={(e) => this.setState({passwordValue: e.target.value}) }/>
                    </td>
                </tr>
                <tr>
                    <td>Shuffle Position</td>
                    <td>
                        <div onClick={() => this.setState({randomizePlayerPosition: !this.state.randomizePlayerPosition})}>
                            <input type="checkbox"
                                   checked={this.state.randomizePlayerPosition}
                                   style={{marginTop: '10px'}} />
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>Puck Starter</td>
                    <td>
                        <Select
                            defaultValue={puckCardOptions[0]}
                            options={puckCardOptions}
                            onChange={(v: any) => this.setState({starter: v.value})} />
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
                <tr>
                    <td></td>
                    <td>
                        {jassRuleDescriptions[this.state.logic]}
                    </td>
                </tr>
                <tr>
                    <td>Schrift</td>
                    <td>
                        <Select
                            defaultValue={writerOptions[0]}
                            options={writerOptions}
                            onChange={(v: any) => this.setState({writer: v.value})} />
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        {writerDescriptions[this.state.writer || 'undefined']}
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
    starter?: string
    randomizePlayerPosition: boolean
    writer?: string
}

export interface CreateTableData {
    password?: string
    logic: string
    starter?: string
    randomizePlayerPosition: boolean
    writer?: string
}