import WebSocket, { WebSocketServer } from 'ws';
import { Server } from 'http';

const UPSTREAM_URL = 'wss://8.229.22.124';

export function initializeLiveUpdates(httpServer: Server) {
    const wss = new WebSocketServer({ server: httpServer, path: '/live' });

    wss.on('connection', (clientWs) => {
        console.log('Android client connected to /live');
        const upstreamWs = new WebSocket(UPSTREAM_URL, { rejectUnauthorized: false });

        upstreamWs.on('open', () => {
            console.log('Connected to the upstream server');
        });

        upstreamWs.on('message', (data) => {
            if (clientWs.readyState === WebSocket.OPEN) {
                clientWs.send(data.toString());
            }
        });

        clientWs.on('close', () => {
            console.log('Android client disconnected');
            if (upstreamWs.readyState === WebSocket.OPEN) {
                upstreamWs.close();
            }
        });

        upstreamWs.on('error', (error) => {
            console.error('Upstream WS error:', error);
        });

        clientWs.on('error', (error) => {
            console.error('Client WS error:', error);
        });
    });
    
}