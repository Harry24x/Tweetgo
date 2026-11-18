#!/usr/bin/env bash
# Builds and starts TweetGo in production. Run from anywhere inside the repo.
set -euo pipefail
cd "$(dirname "$0")"

if [ ! -f .env ]; then
    read -rp "Your domain (e.g. tweetgo-harman.duckdns.org): " domain
    {
        echo "DOMAIN=${domain}"
        echo "DB_PASSWORD=$(openssl rand -hex 16)"
        echo "JWT_SECRET=$(openssl rand -hex 32)"
    } > .env
    chmod 600 .env
    echo "Created deploy/.env with random passwords (keep this file private)."
fi

git -C .. pull --ff-only || true
docker compose -f docker-compose.prod.yml --env-file .env up -d --build
docker compose -f docker-compose.prod.yml --env-file .env ps
echo
echo "Done. Open https://$(grep '^DOMAIN=' .env | cut -d= -f2) (HTTPS can take a minute the first time)."
