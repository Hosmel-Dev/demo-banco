#!/usr/bin/env bash
set -euo pipefail

cd /opt/demo-banco/app
docker compose -f docker-compose.yml -f docker-compose.aws.yml \
  --env-file /etc/demo-banco/banco.env -p demo-banco \
  up --build -d config-server account-service transaction-service apigateway

docker compose -f docker-compose.yml -f docker-compose.aws.yml \
  --env-file /etc/demo-banco/banco.env -p demo-banco \
  up -d --no-deps grafana
  