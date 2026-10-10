# Evidências da atividade DevOps

Esta pasta contém somente registros obtidos de execuções reais do projeto. Os
arquivos `.txt` preservam a saída original dos comandos, enquanto os arquivos
`.png` apresentam essas mesmas informações em um formato mais legível para o
README e para a documentação técnica.

## Evidências concluídas

| Arquivo | Origem | Resultado comprovado |
| --- | --- | --- |
| `01-build-e-testes.txt` | Maven Wrapper | Build concluído e 9 testes aprovados |
| `01-build-e-testes.png` | Log Maven acima | Resumo visual do build e dos testes |
| `02-imagem-docker.txt` | `docker image inspect` | Imagem, arquitetura, tamanho e usuário |
| `02-imagem-docker.png` | Log Docker acima | Resumo visual da imagem criada |
| `03-deploy-staging-local.txt` | Docker Compose e `/api/health` | Serviços de staging saudáveis |
| `04-staging-funcionando.png` | Log de staging acima | API e MongoDB de staging funcionando |
| `05-deploy-producao-local.txt` | Docker Compose e `/api/health` | Serviços de produção saudáveis |
| `06-producao-funcionando.png` | Log de produção acima | API e MongoDB de produção funcionando |

## Evidências pendentes

Os prints do workflow no GitHub Actions dependem da publicação do projeto em um
repositório GitHub e de uma execução real do arquivo `.github/workflows/ci-cd.yml`:

- `03-deploy-staging-pipeline.png`;
- `05-deploy-producao-pipeline.png`.

Esses arquivos não devem ser substituídos por imagens simuladas.
