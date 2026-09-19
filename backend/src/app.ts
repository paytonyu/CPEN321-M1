import express, { type Express } from 'express';

export function createApp(): Express {
  const app = express();

  app.get('/health', (_req, res) => {
    res.json({ status: 'ok' });
  });

  app.get('/name', (_req, res) => {
    res.json({ Name: 'Payton Yu' });
  });

  app.get('/server-ip', (_req, res) => {
    res.json({ status: 'ok' });
  });

  app.get('/server-time', (_req, res) => {
    res.json({ status: 'hh:mm:ss GMT+hh:mm' });
  });

  app.use((_req, res) => {
    res.status(404).json({ error: 'Not Found' });
  });

  return app;
}
