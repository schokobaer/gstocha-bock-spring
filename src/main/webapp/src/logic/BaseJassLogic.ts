export const colors = ["E", "H", "L", "S"]
export const order = ["6", "7", "8", "9", "X", "U", "O", "K", "A"]
export const weisOrder = ["3", "4", "Q", "5", "6", "7", "8", "9"]
export const trumpfOrder = ["E", "L", "H", "S", "G", "B", "KU", "KO"]
export const trumpfRanking = ["6", "7", "8", "X", "O", "K", "A", "9", "U"]

export class JASS {
    public static readonly STOECKE = "S"
    public static readonly QUARTETT = "Q"
    public static readonly BAUER = "U"
    public static readonly NELL = "9"
}

export interface Card {
    color: string
    value: string
}

export function toCard(str: string): Card {
    return {
        color: str[0],
        value: str[1]
    }
}

export type WeisRank = 'S' | '3' | '4' | '5' | '6' |'7' | '8' | '9' | 'Q'
export interface Weis {
    rank: WeisRank
    color?: string
    value?: string
}

export function toWeis(str: string): Weis {
    return {
        rank: str[0] as WeisRank,
        color: str.length === 3 ? str[1] : undefined,
        value: str.length === 3 ? str[2] : str.length === 2 ? str[1] : undefined
    }
}

/**
 * Sorts the cards
 * @param cards
 */
export function sort(cards: Array<string>): Array<string> {
    return cards.sort((a,b) => toCard(a).color === toCard(b).color ? order.indexOf(toCard(a).value) - order.indexOf(toCard(b).value) : toCard(a).color.charCodeAt(0) - toCard(b).color.charCodeAt(0))
}

/**
 * Sums up the points for all weis
 * @param weis weis in weis notation
 */
export function calcWeisPoints(weis: Array<string>): number {
    return weis.reduce((p, w) => {
        if (toWeis(w).rank === "Q") {
            return p + (
                toWeis(w).value === JASS.NELL ? 150 :
                    toWeis(w).value === JASS.BAUER ? 200 : 100
            )
        }
        return p + (
            toWeis(w).rank === "S" || toWeis(w).rank === "3" ? 20 :
                toWeis(w).rank === "4" ? 50 :
                    toWeis(w).rank === "5" ? 100 :
                        toWeis(w).rank === "6" ? 120 :
                            toWeis(w).rank === "7" ? 140 :
                                toWeis(w).rank === "8" ? 160 :
                                    toWeis(w).rank === "9" ? 180 : 0
        )
    }, 0)
}

/**
 * Konverts from cards notation to weis notation:
 * QK -> Quartett King
 * S -> Stoecke
 * 5SK -> Five schell from 9 to King
 * 3E8 -> Three eichel from 6 to 8
 * @param cards
 * @param trumpf
 */
export function cardsToWeis(cards: Array<string>, trumpf: string | null): Array<string> {

    let cards2 = sort([...cards])
    const weis = []

    // Prefer QU and Q9
    const qu = cards2.filter(c => toCard(c).value === JASS.BAUER)
    if (qu.length === 4) {
        weis.push(JASS.QUARTETT + JASS.BAUER)
        qu.forEach(c => cards2.splice(cards2.indexOf(c), 1))
    }

    const q9 = cards2.filter(c => toCard(c).value === JASS.NELL)
    if (q9.length === 4) {
        weis.push(JASS.QUARTETT + JASS.BAUER)
        q9.forEach(c => cards2.splice(cards2.indexOf(c), 1))
    }

    // Weis >= 5
    let colIdx = 0
    while (colIdx < colors.length) {
        const cardsCol = cards2.filter(c => toCard(c).color === colors[colIdx])
        let straight = 1;
        while (straight < cardsCol.length) {
            if (toCard(cardsCol[straight-1]).color !== toCard(cardsCol[straight]).color || order.indexOf(toCard(cardsCol[straight-1]).value) + 1 !== order.indexOf(toCard(cardsCol[straight]).value)) break
            straight++
        }
        if (straight >= 5) {
            weis.push(straight + colors[colIdx] + toCard(cardsCol[straight-1]).value)
            cards2.splice(cards2.indexOf(cardsCol[0]), straight)
        } else {
            colIdx++
        }
    }

    // quartett
    for (const val of order) {
        if (cards2.filter(c => toCard(c).value === val).length === 4) {
            // Filter out 6,7,8 if not trumpf == G || trumpf == KU || trumpf == KO
            if (order.indexOf(val) > 2 || trumpf === "G" || trumpf === "KU" || trumpf === "KO") {
                weis.push(JASS.QUARTETT + val)
                cards2 = cards2.filter(c => toCard(c).value !== val)
            }
        }
    }

    // Weis == 3 || Weis == 4
    colIdx = 0
    while (colIdx < colors.length) {
        const cardsCol = cards2.filter(c => toCard(c).color === colors[colIdx])
        let straight = 1;
        while (straight < cardsCol.length) {
            if (toCard(cardsCol[straight-1]).color !== toCard(cardsCol[straight]).color || order.indexOf(toCard(cardsCol[straight-1]).value) + 1 !== order.indexOf(toCard(cardsCol[straight]).value)) break
            straight++
        }
        if (straight >= 3) {
            weis.push(straight + colors[colIdx] + toCard(cardsCol[straight-1]).value)
            cards2.splice(cards2.indexOf(cardsCol[0]), straight)
        } else {
            colIdx++
        }
    }

    // Stoecke
    /* if (cards.indexOf(trumpf + "O") >= 0 && cards.indexOf(trumpf + "K") >= 0) {
        weis.push(JASS.STOECKE)
    } */

    return weis
}

/**
 * Compares two single weis in weis notation. Returns positive if weisA is better than weisB,
 * 0 if they are equal, otherwise negative.
 * @param weisA weis in weis notation
 * @param weisB weis in weis notation
 * @param trumpf trumpf
 */
function compareSingleWeis(weisA: string, weisB: string, trumpf: string): number {
    if (weisOrder.indexOf(toWeis(weisA).rank) === weisOrder.indexOf(toWeis(weisB).rank)) {
        console.debug('Same rank')
        if (order.indexOf(toWeis(weisA).value!) === order.indexOf(toWeis(weisB).value!)) {
            console.debug('Same value')
            return toWeis(weisA).color === trumpf ? 1 :
                toWeis(weisB).color === trumpf ? -1 : 0
        }
        console.debug('Different value')
        return order.indexOf(toWeis(weisA).value!) - order.indexOf(toWeis(weisB).value!)
    }
    console.debug('Different rank')
    return weisOrder.indexOf(toWeis(weisA).rank) - weisOrder.indexOf(toWeis(weisB).rank)
}

/**
 * Returns positive if weisA is better than weisB, 0 if both are equal, otherwise negative
 * @param weisA array in weis notation
 * @param weisB array in weis notation
 * @param trumpf trumpf
 */
export function compareWeis(weisA: Array<string>, weisB: Array<string>, trumpf: string): number {
    const bestA = weisA.sort((a,b) => compareSingleWeis(a, b, trumpf))[0]
    const bestB = weisB.sort((a,b) => compareSingleWeis(a, b, trumpf))[0]
    console.debug(`Best weisA ${bestA}`)
    console.debug(`Best weisB ${bestB}`)
    return bestA === undefined && bestB === undefined ? 0 :
        bestA === undefined ? -1 :
            bestB === undefined ? 1 :
                compareSingleWeis(bestA, bestB, trumpf)
}


export function cardAllowed(round: Array<string>, card: string, hand: Array<string>, trumpf: string): boolean{
    if (hand.indexOf(card) < 0) {
        console.debug('Card is not on hand')
        return false
    }
    if (round.length === 0) return true
    if (toCard(round[0]).color === toCard(card).color) return true

    // check if card is trumpf
    if (toCard(card).color === trumpf) {
        // if only just trumpfs are left on hand, its ok
        if (hand.filter(c => toCard(c).color === trumpf).length === hand.length) return true
        // else check all previous throws for untertrumpfing
        for (let i = 1; i < round.length; i++) {
            if (toCard(round[i]).color === trumpf && trumpfRanking.indexOf(toCard(card).value) < trumpfRanking.indexOf(toCard(round[i]).value)) {
                // untertrumpft
                console.debug('Tryied to untertrumpf')
                return false
            }
        }
        return true
    }

    // if its a trumpf opening, and the Bauer is the only trumpf on hand, its ok if an other card is palyed
    if (toCard(round[0]).color === trumpf && hand.some(c => c === trumpf + "U") && hand.filter(c => toCard(c).color === trumpf).length === 1) {
        return true
    }

    // not correct collor and not a trumpf -> no cards with correct colors are anymore on hand
    if (hand.filter(c => toCard(c).color === toCard(round[0]).color).length > 0) {
        console.debug('Has still a correct collor card on hand')
        return false
    }

    return true
}

/**
 * True if the home team (first throw) made the round, otherwise false
 * @param round
 * @param trumpf
 */
export function roundWinner(round: Array<string>, trumpf: string): number {
    let best = 0;
    for (let i = 1; i < 4; i++) {
        if (toCard(round[i]).color === toCard(round[best]).color && toCard(round[best]).color !== trumpf) {
            // has played correct collor
            if (trumpf !== "G" && trumpf !== "KU" && order.indexOf(toCard(round[i]).value) > order.indexOf(toCard(round[best]).value)
                || (trumpf === "G" || trumpf === "KU") && order.indexOf(toCard(round[i]).value) < order.indexOf(toCard(round[best]).value)) {
                // has played better card
                best = i
            }
        } else if (round[i][0] === trumpf) {
            if (toCard(round[best]).color !== trumpf || trumpfRanking.indexOf(toCard(round[best]).value) < trumpfRanking.indexOf(toCard(round[i]).value)) {
                // Best was no trumpf or this card was a better trumpf than the best
                best = i
            }
        }
    }
    return best
}

export function calcPoints(round: Array<string>, trumpf: string): number {
    return round.reduce((p,c) => {
        if (toCard(c).color === trumpf && toCard(c).value === JASS.BAUER) {
            return p + 20
        } else if (toCard(c).color === trumpf && toCard(c).value === JASS.NELL) {
            return p + 14
        } else if ((trumpf === "G" || trumpf === "B" || trumpf === "KU" || trumpf === "KO") && toCard(c).value === "8") {
            return p + 8
        } else {
            return p + (toCard(c).value === "X" ? 10 :
                toCard(c).value === "U" ? 2 :
                    toCard(c).value === "O" ? 3 :
                        toCard(c).value === "K" ? 4 :
                            toCard(c).value === "A" ? 11 : 0)
        }
    }, 0)
}