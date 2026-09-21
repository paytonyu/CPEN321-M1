import express, { type Express } from 'express';

export function createApp(): Express {
  const app = express();

  app.get('/health', (_req, res) => {
    res.json({ status: 'ok' });
  });

  app.get('/name', (_req, res) => {
    res.json({ firstName: 'Payton', lastName: 'Yu' });
  });

  app.get('/server-ip', async (_req, res) => {
    try {
      const r = await fetch('https://api.ipify.org?format=json');
      res.json(await r.json());
    } catch {
      res.status(500).json({ error: 'could not get IP' });
    }
  });

  app.get('/server-time', (_req, res) => {
    const [time, gmt] = new Date().toTimeString().split(' ');
    res.json({ time: `${time} ${gmt.slice(0, 6)}:${gmt.slice(6)}` });
  });

  app.use((_req, res) => {
    res.status(404).json({ error: 'Not Found' });
  });

  return app;
}
