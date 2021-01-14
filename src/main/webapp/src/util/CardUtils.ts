import {within} from "@testing-library/dom";

export function colorToText(col: string): string {
    switch (col) {
        case "E":
            return 'Eichel'
        case "L":
            return 'Laub'
        case "H":
            return 'Herz'
        case "S":
            return 'Schell'
        default:
            return 'Unknown'
    }
}

export function cardValueToText(cardVal: string): string {
    switch (cardVal) {
        case "6":
            return '6'
        case "7":
            return '7'
        case "8":
            return '8'
        case "9":
            return '9'
        case "X":
            return '10'
        case "U":
            return 'Unter'
        case "O":
            return 'Ober'
        case "K":
            return 'König'
        case "A":
            return 'Ass'
        default:
            return 'Unknown'
    }
}

export function weisToText(weis: string): string {
    if (weis.length === 3) {
        return weis[0] + ' Blatt ('  + colorToText(weis[1]) + ' ' + cardValueToText(weis[2]) + ')'
    }
    if (weis.length === 2) {
        return '4 gleiche (' + cardValueToText(weis[1]) + ')'
    }
    return ''
}