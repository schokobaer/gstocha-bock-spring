import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

export default class WebSocketClient {
    private socket: any
    private client: any
    private subscriptions = new Map<string, any>()

    private loungeBCC = new BroadcastChannel('lounge')
    private gameBCC = new BroadcastChannel('game')

    connect() {
        this.socket = new SockJS('/event/websocket');
        this.client = Stomp.over(this.socket);
        const that = this;
        console.info("Trying to connect to websocket endpoint")
        this.client.connect({}, function(frame: any) {
            console.info("Connect fallback")
            console.info(frame)
            that.subscribe('/event/lounge', (msg: any) => {
                console.info("Received a websocket message for lounge")
                that.loungeBCC.postMessage("update")
            });
        });
    }

    subscribeToTable(tableid: string) {
        console.info('WebSocketClient subscribes to ' + tableid)
    }


    subscribe(path: string, msgHandler: Function) {
        let sub = this.client.subscribe(path, msgHandler);
        console.info("Subscribing for topic " + path)
        this.subscriptions.set(path, sub);
    }

}