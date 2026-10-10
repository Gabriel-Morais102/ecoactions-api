# Ambientes de staging e produção

Os ambientes usam o mesmo Docker Compose e a mesma imagem, com configurações e
volumes independentes. O deploy mantém os serviços ativos no host Windows após
o pipeline. O computador e o Docker Desktop precisam continuar ligados.

## Requisitos do host

- Windows x64, Docker Desktop com contêineres Linux e Docker Compose v2;
- PowerShell 7 (`pwsh`) disponível para a conta do runner;
- portas `8081`, `8082`, `27018` e `27019` disponíveis;
- runner com rótulo adicional `ecoactions-deploy`;
- GitHub Actions Runner 2.327.1 ou superior, compatível com as actions do workflow;
- a conta do runner deve conseguir executar `docker info` e `docker compose version`.

Os bancos são publicados somente em `127.0.0.1`. As APIs usam as portas 8081
(staging) e 8082 (produção). Não há URL pública nesta implantação acadêmica.

## Preparar os arquivos de ambiente

Na raiz do código-fonte, abra PowerShell 7 e prepare um diretório persistente
fora de `_work`, usado pelo runner. Neste exemplo, `.deployment` fica na raiz
do projeto e é ignorado pelo Git:

```powershell
$deployRoot = Join-Path (Get-Location) '.deployment'
New-Item -ItemType Directory -Force -Path "$deployRoot/staging", "$deployRoot/production"
Copy-Item deploy/staging.env.example "$deployRoot/staging/.env"
Copy-Item deploy/production.env.example "$deployRoot/production/.env"
$env:ECOACTIONS_DEPLOY_ROOT = $deployRoot
```

Execute as cópias somente na primeira configuração, para preservar credenciais
já definidas. Edite os `.env` e substitua todas as senhas de exemplo. Use
credenciais distintas em staging e produção. Para valores com `$` ou `#`, use
aspas simples. Os `.env` nunca devem ser versionados.

Para disponibilizar o caminho em novas sessões do mesmo usuário:

```powershell
[Environment]::SetEnvironmentVariable('ECOACTIONS_DEPLOY_ROOT', $deployRoot, 'User')
```

Um processo aberto não recebe automaticamente essa alteração. Inicie o runner
em uma sessão que tenha a variável definida ou reinicie-o após configurar.
O caminho deve apontar para a pasta que contém `staging` e `production`.

## Registrar e iniciar o runner

No GitHub do repositório, abra **Settings > Actions > Runners > New self-hosted
runner**, escolha Windows x64 e siga os comandos de download e registro
apresentados. Use uma pasta dedicada, como `.runner`, fora de `.deployment`.
Adicione o rótulo `ecoactions-deploy`; Windows e X64 também são usados pelo
workflow.

Abra o Docker Desktop e inicie o runner pela conta que consegue usar o Docker.
O runner deve aparecer como disponível no GitHub. Os jobs de deploy executam
somente na `main`; pull requests usam os runners hospedados do GitHub.

Com o registro concluído em `.runner`, o script abaixo define os caminhos dos
ambientes e inicia o runner em segundo plano:

```powershell
.\scripts\start-runner.ps1
```

Se PowerShell 7 foi instalado de forma portátil em `.runner/tools/pwsh`, o
script inclui esse diretório no PATH do runner. Também aceita
`-RunnerDirectory` e `-DeployRoot` para caminhos personalizados. Não inicie
outra instância se o runner já estiver em execução.

No host desta entrega, o runner se chama `ecoactions-lab` e usa a versão 2.338.0.
O repositório exige
aprovação para workflows de todos os colaboradores externos, além de manter
os deploys restritos à `main`.

Os `.env` ficam no host, fora do checkout temporário. Essa configuração não usa
GitHub Secrets para transferir senhas dos serviços. Somente a imagem Docker é
baixada dos artefatos do pipeline.

## Deploy manual com os scripts do pipeline

Primeiro, construa a imagem. O deploy usa `--no-build`, portanto ela deve
existir no Docker do host:

```powershell
docker build --tag ecoactions-api:local .
pwsh -File scripts/deploy.ps1 -Environment staging -Image ecoactions-api:local
pwsh -File scripts/deploy.ps1 -Environment production -Image ecoactions-api:local
```

Também é possível informar o caminho diretamente:

```powershell
pwsh -File scripts/deploy.ps1 -Environment staging -Image ecoactions-api:local -DeployRoot $deployRoot
```

O script copia Compose, `docker/` e `scripts/` para a pasta do ambiente sem
substituir seu `.env`. Inicia os serviços, aguarda os health checks e chama o
smoke test. `-Image` tem prioridade sobre `APP_IMAGE` do `.env`.

O pipeline usa `ecoactions-api:<SHA-do-commit>` nos dois ambientes. O último
deploy validado fica registrado em `deployed-image.txt` em cada pasta.
O artefato é compactado para reduzir a transferência. Produção reutiliza a
imagem carregada por staging no mesmo Docker; só baixa o artefato se a tag
do commit não estiver disponível no host.

## Migração de volumes existentes

Volumes novos criam o usuário da API com `readWrite` no banco do ambiente.
Para um volume da versão anterior, configure `MONGO_APP_USERNAME` e
`MONGO_APP_PASSWORD` no `.env`, preservando as credenciais administrativas
já existentes. Alterar somente `MONGO_ROOT_PASSWORD` no arquivo não muda
a senha persistida no MongoDB.

Na raiz do código-fonte, execute para staging, depois de construir a imagem:

```powershell
$stagingEnv = Join-Path $env:ECOACTIONS_DEPLOY_ROOT 'staging/.env'
docker compose --env-file $stagingEnv up --detach --wait --wait-timeout 180 mongo-db
docker compose --env-file $stagingEnv exec -T mongo-db mongosh --quiet /opt/ecoactions/migrate-app-user.js
pwsh -File scripts/deploy.ps1 -Environment staging -Image ecoactions-api:local
```

Repita usando `production/.env` e `-Environment production`. No ambiente local,
use `docker compose up --detach mongo-db` e o mesmo `exec`, sem `--env-file`;
depois execute `docker compose up --detach --build --wait`.

A migração cria ou atualiza somente o usuário da API e suas permissões. Não
apaga collections, documentos ou volumes. Não execute novamente
`01-create-collections-and-crud.js` em um banco com dados que deseja preservar:
essa carga de demonstração é destinada a volumes novos.

## Validação funcional

O health consulta o MongoDB. O smoke test exige HTTP 200 nas consultas
autenticadas de resíduos, pontos de coleta, coletas, destinações e alertas.
Staging também cria um resíduo temporário, lê e remove o registro ao final.
Produção executa apenas leituras.

Para repetir a validação sem refazer o deploy:

```powershell
pwsh -File scripts/smoke-test.ps1 -EnvFile "$env:ECOACTIONS_DEPLOY_ROOT/staging/.env" -CheckCrud
pwsh -File scripts/smoke-test.ps1 -EnvFile "$env:ECOACTIONS_DEPLOY_ROOT/production/.env"
```

Os scripts não imprimem senhas em texto aberto e registram máscaras de
credenciais quando executados pelo GitHub Actions.

## Consultar, parar e reiniciar os ambientes

Use os arquivos do diretório persistente para consultar os serviços:

```powershell
$staging = Join-Path $env:ECOACTIONS_DEPLOY_ROOT 'staging'
docker compose --project-directory $staging --file "$staging/docker-compose.yml" --env-file "$staging/.env" ps
```

Troque `ps` por `stop` para parar sem remover contêineres ou volumes; use
`start` para reiniciar os mesmos contêineres. Repita com a pasta `production`.
O deploy não usa `down --volumes`. A política `restart: unless-stopped` permite
reiniciar os serviços com o Docker, salvo se foram parados explicitamente.
