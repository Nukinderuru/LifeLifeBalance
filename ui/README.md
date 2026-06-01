# Inner Council Frontend

React + TypeScript + Vite frontend for the Inner Council Ktor API.

## Stack

- React
- TypeScript
- Vite
- React Router
- CSS Modules
- Small typed API gateway layer

## Features

- Today dashboard with watercolor-style character cards
- Wishes page with create, edit, deactivate, complete, and undo completion
- Character detail page with date picker
- Weekly overview with summary cards and 7-day grid
- Responsive journal-inspired UI

## Setup

Install dependencies:

```bash
npm install
```

Create environment file:

```bash
cp .env.example .env
```

Default backend URL:

```env
VITE_API_BASE_URL=http://localhost:8080
```

## Run

Start the frontend:

```bash
npm run dev
```

The app runs on `http://localhost:5173` by default.

## Run In Docker

From the repository root:

```bash
docker compose up --build
```

This serves the frontend on `http://localhost:5173` and proxies API calls to the backend container.

## Build

```bash
npm run build
```

## Notes

- The frontend expects the Ktor backend to be running locally.
- In Docker, the frontend uses same-origin `/api/...` requests through Nginx proxying.
- The wishes page fetches both wishes and today’s completions so it can show `Feed today` and `Already fed · Undo` states.
- No mock data is used in the main implementation.
