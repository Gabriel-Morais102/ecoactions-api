# Evidências da atividade DevOps

Esta pasta contém logs de comandos e capturas das páginas do GitHub com os
resultados da aplicação. As capturas de funcionamento mostram a saída real
dos smoke tests após os deploys, preservada nos arquivos de texto correspondentes.

## Execução final

[GitHub Actions — execução #7](https://github.com/Gabriel-Morais102/ecoactions-api/actions/runs/38081156864), commit
`e500df0e9b66cbb88a8895f9aedf5769a1fb4106`. Resultado **Success**, duração **4m 45s**,
com os quatro jobs aprovados. A suíte possui 33 testes sem falhas,
erros ou testes ignorados.

## Índice

| Arquivo | Origem | Resultado |
| --- | --- | --- |
| `01-build-e-testes.txt` | Log do job GitHub Actions | Build e 33 testes aprovados |
| `01-build-e-testes.jpg` | GitHub Actions | Captura do job de build e testes |
| `02-imagem-docker.txt` | Docker no host | Imagem do commit e metadados da execução |
| `02-imagem-docker.jpg` | GitHub Actions | Job de criação e verificação da imagem aprovado |
| `03-deploy-staging-local.txt` | Smoke test e Docker Compose | API, dados e contêineres de staging após deploy |
| `03-deploy-staging-pipeline.jpg` | GitHub Actions | Job de staging aprovado |
| `03-deploy-staging-pipeline.txt` | Log do job GitHub Actions | Etapas e smoke test durante o deploy de staging |
| `04-staging-funcionando.jpg` | Página GitHub do log de staging | Captura da saída real do smoke test |
| `05-deploy-producao-local.txt` | Smoke test e Docker Compose | API, dados e contêineres de produção após deploy |
| `05-deploy-producao-pipeline.jpg` | GitHub Actions | Job de produção aprovado |
| `05-deploy-producao-pipeline.txt` | Log do job GitHub Actions | Etapas e consultas durante o deploy de produção |
| `06-producao-funcionando.jpg` | Página GitHub do log de produção | Captura da saída real do smoke test |
| `07-pipeline-cicd-github.jpg` | GitHub Actions | Visão geral dos quatro jobs aprovados |
| `08-validacao-api.txt` | Requisições HTTP reais | Contrato HTTP, conflitos e regras de negócio |
| `09-health-indisponibilidade.txt` | Interrupção e recuperação do MongoDB | Health 503/DOWN e recuperação 200/UP sem recriar volume |

Os logs de validação local complementam os resultados do pipeline. Os ambientes
usam os bancos e volumes locais do host Windows e não têm URL pública.
