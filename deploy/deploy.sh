#!/usr/bin/env bash
set -euo pipefail

cd /opt/demo-banco/app
docker compose --env-file /etc/demo-banco/banco.env -p demo-banco \
  up --build -d config-server account-service transaction-service apigateway