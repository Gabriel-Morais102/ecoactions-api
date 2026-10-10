# Ambientes de staging e produção

Os dois ambientes utilizam o mesmo `docker-compose.yml`, mas recebem configurações diferentes por meio de arquivos de ambiente. O nome do projeto Compose faz com que cada ambiente tenha seus próprios containers, rede e volume de dados.

## Staging

```powershell
Copy-Item deploy/staging.env.example deploy/staging.env
docker compose --env-file deploy/staging.env up -d --no-build
```

- API: `http://localhost:8081`
- MongoDB: `localhost:27018`
- Projeto Compose: `ecoactions-staging`

## Produção

```powershell
Copy-Item deploy/production.env.example deploy/production.env
docker compose --env-file deploy/production.env up -d --no-build
```

- API: `http://localhost:8082`
- MongoDB: `localhost:27019`
- Projeto Compose: `ecoactions-production`

## Encerramento

Os ambientes podem ser encerrados sem apagar os volumes:

```powershell
docker compose --env-file deploy/staging.env down
docker compose --env-file deploy/production.env down
```

Os arquivos `staging.env` e `production.env` não devem ser versionados. As senhas dos arquivos `.example` são apenas demonstrativas e precisam ser substituídas antes de um deploy real.
