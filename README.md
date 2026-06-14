# Inner Council

*A gentle self-care tracker for the different parts of yourself.*

## What is this?

Inner Council is a personal web application inspired by the idea that a single person can have many different needs, interests and ways of experiencing life.

Some parts of us want to learn.

Some want adventure.

Some need beauty.

Some seek movement.

Some simply want to sit under a tree and watch the clouds.

Traditional habit trackers often focus on productivity, consistency and optimization. Inner Council takes a different approach.

Its goal is not to ask:

> "How much did I accomplish today?"

Instead, it asks:

> "Did every important part of me get a chance to live today?"

The application helps track activities associated with five inner characters:

* 🌿 **Maya** — nature, quiet mornings, tea, meditation, creativity and observation.
* 📘 **Elina** — learning, mathematics, programming, languages, books and curiosity.
* 🔥 **Tora** — movement, strength, courage, exploration and adventure.
* ☀️ **Dana** — people, connection, volunteering, spontaneity and fun.
* 🌸 **Naomi** — beauty, comfort, romance, pleasure and self-expression.

Each activity "feeds" one of these characters.

The goal is not to maximize points.

The goal is to notice who has been nourished — and who might need a little more attention.

---

## Philosophy

Inner Council is intentionally designed around compassion rather than productivity.

There are:

* no penalties;
* no streak loss;
* no shame-based mechanics;
* no punishment for missing a day.

The application is built around curiosity and observation.

A low score does not mean failure.

It simply means:

> "Perhaps this part of you has been waiting to be noticed."

Small moments count.

A cup of tea counts.

A walk counts.

Watching a thunderstorm counts.

Calling a friend counts.

Life is not only made of achievements.

---

## Features (MVP)

### Character Dashboard

See the current state of all five characters:

* daily score;
* weekly score;
* nourishment status;
* completed activities.

### Wishes

Create and manage activities ("wishes") for each character.

Examples:

* Walk in nature
* Study mathematics
* Dance
* Call a friend
* Knit
* Watch a sunset

### Activity Tracking

Mark activities as completed and build a picture of how your week has been distributed across different needs and interests.

### Weekly Overview

Review the last seven days and identify:

* which characters received attention;
* which characters may be feeling hungry;
* how balanced your week has been.

---

## Technical Overview

### Backend

* Kotlin
* Ktor
* PostgreSQL
* Exposed ORM
* Koin

### Frontend

* React
* TypeScript
* Vite

### Architecture

The application follows a simple client-server architecture:

Frontend → REST API → PostgreSQL

Main domain entities:

* Character
* Wish
* Completion

The backend calculates daily and weekly scores and exposes dashboard endpoints for the frontend.

---

## Future Ideas

Potential future features:

* bonus conditions ("Tea in nature", "Bath with candles");
* mood tracking;
* character portraits and visual progression;
* seasonal activities;
* custom characters;
* reflection journal;
* recommendation engine ("Who might need attention today?").

---

## Why this project exists

This project started with a simple realization:

Sometimes the problem is not that we are doing too little.

Sometimes the problem is that only one part of us gets to live.

Inner Council is an attempt to create a kinder way of paying attention.

A reminder that productivity is not the same thing as a meaningful day.

And that every part of us deserves a seat at the table.
