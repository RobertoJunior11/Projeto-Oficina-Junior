# Exemplo Final - Request e Response Simplificados

## REQUEST (Frontend → Backend):

```json
{
  "dtRevisao": "2024-01-15 10:30:00",
  "descricao": "Revisão completa do veículo",
  "vlServico": 270.0,
  "tiposRevisao": [1, 2],
  "concluida": false,
  "veiculo": {
    "id": 5
  }
}
```

## RESPONSE (Backend → Frontend):

```json
{
  "id": 1,
  "dtRevisao": "2024-01-15 10:30:00",
  "descricao": "Revisão completa do veículo",
  "vlServico": 270.0,
  "tiposRevisao": [
    {
      "codigo": 1,
      "descricao": "Troca de Óleo",
      "valor": 120.0
    },
    {
      "codigo": 2,
      "descricao": "Alinhamento e Balanceamento",
      "valor": 150.0
    }
  ],
  "concluida": false,
  "veiculo": {
    "id": 5,
    "veiculo": "Civic",
    "marca": "Honda",
    "ano": 2020
  }
}
```

## Vantagens da implementação simplificada:

### REQUEST:

- ✅ **Máxima simplicidade**: `"tiposRevisao": [1, 2]`
- ✅ **Menos dados**: Apenas códigos numéricos
- ✅ **Validação automática**: Spring valida os tipos
- ✅ **Performance**: Menor payload na rede

### RESPONSE:

- ✅ **Dados completos**: Código, descrição e valor
- ✅ **Pronto para uso**: Frontend não precisa fazer lookup
- ✅ **Flexível**: Fácil de estender no futuro

## Conversão no Assembler:

```java
// REQUEST → ENTITY
List<Integer> codigos = [1, 2];
List<TipoRevisaoEnum> enums = codigos.stream()
    .map(codigo -> TipoRevisaoEnum.values()[codigo - 1])
    .collect(toList());
// Resultado: [TROCA_OLEO, ALINHAMENTO]

// ENTITY → RESPONSE
List<TipoRevisaoEnum> enums = [TROCA_OLEO, ALINHAMENTO];
List<TipoRevisaoResponse> responses = enums.stream()
    .map(tipoEnum -> TipoRevisaoEnum.getResponse(tipoEnum.getId()))
    .collect(toList());
// Resultado: [{codigo:1, descricao:"Troca de Óleo", valor:120.0}, ...]
```

## Tratamento de Erro:

Se um código inválido for enviado (ex: 99), o sistema lançará uma `OficinaException`:

```json
{
  "httpStatusCode": 400,
  "message": "Tipo de revisão não encontrado",
  "description": "Tipo de revisão não encontrado para o código: 99"
}
```

## Validação de Códigos Válidos:

Os códigos válidos são de 1 a 10:

- 1 = TROCA_OLEO
- 2 = ALINHAMENTO
- 3 = TROCA_PNEU
- 4 = REVISAO_COMPLETA
- 5 = TROCA_FILTRO_AR
- 6 = TROCA_FILTRO_OLEO
- 7 = TROCA_BATERIA
- 8 = TROCA_VELA
- 9 = LIMPADOR_PARABRISA
- 10 = INSPECAO_TECNICA
