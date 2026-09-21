# Discussão Final — Shift Left e Shift Right (BancoFácil Digital)

## Controles implementados que são exclusivamente Shift Left

Todos os cinco gates da pipeline (`secret-scan`, `unit-tests`, `sast`, `sca`,
`dockerfile-lint`) atuam **antes do deploy**, sobre artefatos estáticos
(código-fonte, histórico do Git, `pom.xml`, `Dockerfile`), sem exigir a
aplicação em execução. Gitleaks, Semgrep e Hadolint são puramente Shift
Left: analisam texto/AST e nunca observam comportamento em runtime. O mesmo
vale para os testes unitários e para o Branch Protection (item opcional):
são barreiras de processo que impedem que código não revisado ou não
testado avance, mas nada dizem sobre o comportamento do sistema já
implantado.

## Controles que já nascem "duplos" ou que poderiam ser estendidos para Shift Right

O Trivy (SCA) é o caso mais claro de controle que atua nas duas pontas: aqui
ele rodou contra o `pom.xml` (Shift Left), mas o mesmo motor poderia
escanear a imagem já publicada no GHCR periodicamente, pois novas CVEs —
como aconteceu com o próprio Log4Shell — são descobertas depois que a
aplicação já está em produção. Sem um novo scan pós-deploy, uma dependência
aprovada hoje pode se tornar crítica amanhã sem que ninguém perceba.

Além disso, os controles implementados poderiam ser estendidos com:

- **DAST (OWASP ZAP)** contra o ambiente de staging já implantado, testando
  a aplicação em execução (ex.: reconfirmar a ausência de SQL Injection no
  endpoint `/conta` de fora para dentro, como um atacante real faria).
- **Monitoramento de exploração** da mesma CVE do log4j em produção (WAF/IDS
  com assinaturas para o padrão `${jndi:ldap://...}`), como rede de segurança
  caso uma versão vulnerável volte a ser implantada por engano.
- **Feature flags** para liberar gradualmente a correção do cálculo de
  desconto e do endpoint `/conta`, permitindo rollback instantâneo sem novo
  deploy caso o comportamento em produção destoe do esperado.
- **Observabilidade e alertas** (logs estruturados, métricas de erro 5xx,
  alertas de comportamento anômalo) para detectar tentativas de exploração
  ou regressões que nenhum gate estático conseguiria prever.

## O que falta para fechar o ciclo Shift Left + Shift Right

A BancoFácil ainda precisaria: (1) um ambiente de staging automatizado para
rodar DAST antes da promoção a produção; (2) rotação periódica de segredos
de runtime, hoje apenas lidos de variáveis de ambiente, e migração para um
cofre de segredos centralizado (Vault/Secrets Manager) com renovação
automática; (3) reexecução agendada do Trivy contra as imagens já publicadas
no GHCR, não apenas no momento do build; (4) um processo de resposta a
incidentes que conecte os alertas de produção de volta ao pipeline (ex.:
abrir automaticamente uma issue e bloquear novos deploys da mesma versão
vulnerável); e (5) Branch Protection habilitada de fato, transformando os
gates de um relatório informativo em um portão que efetivamente impede o
merge de código inseguro.
