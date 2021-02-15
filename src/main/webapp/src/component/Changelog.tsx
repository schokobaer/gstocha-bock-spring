import React, { Fragment } from 'react';
import './Changelog.css'
import {getLastVersion, setLastVersion, Version} from "../util/GameRepo";

const version = new Version("1.2.18")

const features: Array<Feature> = [
    {icon: '🖖', description: 'V-Style Tischnamen (siehe URL)', version: new Version("1.2.11")},
    {icon: '☎️ ', description: 'Spielbar am Smartphone/Tablet', version: new Version("1.2.12")},
    {icon: '🦊 ', description: 'Firefox bug fixed', version: new Version("1.2.13")},
    {icon: '❕', description: 'Stöcke Button immer sichtbar', version: new Version("1.2.14"), link: 'Stcke_25'},
    {icon: '✏️', description: 'Change Name', version: new Version("1.2.15"), link: 'Change_Name'},
    {icon: '⚫', description: 'Spieler Puck', version: new Version("1.2.16"), link: 'Spieler_Puck_8'},
    {icon: '🎲', description: 'Position Shuffle', version: new Version("1.2.17"), link: 'Neuer_Tisch_8'},

]

export default class Changelog extends React.Component<Props, State> {

    componentWillMount(): void {
        this.setState({lastVersion: getLastVersion()})
        setLastVersion(version)
    }

    render() {
        return <Fragment>
            <div className="news-ct">
                <h2>Changelog</h2>
                {features.reverse().map(f =>
                    <div className={(this.state.lastVersion.compare(f.version) < 0) ? "fresh-feature" : ''}>
                        <div>{f.icon}</div>
                        {f.link ?
                        <div><a target="_blank" href={`howto.html#${f.link}`}>{f.description}</a></div>
                            : <div>{f.description}</div>}
                    </div>
                )}
            </div>
            <div className="build-number">Build: {version.toString()}</div>
        </Fragment>
    }
}

interface Props {
}

interface State {
    lastVersion: Version
}

interface Feature {
    icon: string
    description: string
    version: Version
    link?: string
}
