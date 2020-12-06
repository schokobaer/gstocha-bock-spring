import React from 'react';
import './Dialog.css'
import { GamePlayerDto, WeisPoints } from '../dto/dtos';
import Karte from './Karte';
import {Weis} from "../logic/BaseJassLogic";

/**
 * className={this.props.disabled ? "disabled" : ""}
 */

export default class Dialog extends React.Component<Props> {

    render() {
        return <div className="dialog">
            {this.props.children}
        </div>
    }
}

interface Props {
}