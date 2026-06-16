# Analise de incidente

## Cenario

Durante a criacao de demandas, usuarios relatam erro intermitente ao salvar registros com prazo informado pela tela.

Log observado:

```text
WARN  demanda_validacao_falhou campos=[prazo]
ERROR SQLSTATE 23502 null value in column "prazo" violates not-null constraint
```

## Hipotese principal

O front-end estava enviando `prazo` vazio ou em formato invalido em alguns fluxos de edicao. A API rejeita `prazo` nulo por contrato, mas a mensagem exibida ao usuario era generica, gerando repeticao da tentativa e aumento de erros.

## Correcao sugerida

- Manter validacao no front com `input type="date"` e `min` para bloquear prazo passado.
- Manter validacao no backend com `@NotNull` e `@FutureOrPresent`, porque o backend e a fronteira confiavel.
- Registrar campos invalidos no log sem expor dados sensiveis.
- Retornar erro estruturado com `message` e `fields`, permitindo que a tela destaque o campo correto.

## Prevencao

- Teste de integracao cobrindo payload com `prazo` passado.
- Checklist de Definition of Done: validacao no front, validacao no back e mensagem de erro util.
- Monitorar volume de `demanda_validacao_falhou` por campo para identificar regressao de UX.
- Em producao, criar alerta quando erro 400 por validacao subir acima do padrao historico.

## Trade-offs

Nao foi criado um pipeline complexo de observabilidade para o teste. Logs estruturados e mensagens de erro por campo cobrem o diagnostico minimo pedido sem simular uma arquitetura maior que o problema.
