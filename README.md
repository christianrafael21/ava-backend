# Golden Raspberry Awards API

API RESTful para leitura e consulta dos indicados e vencedores da categoria Pior Filme do Golden Raspberry Awards.

## Como executar o projeto

No diretório `goldenraspberry-api`, primeiro compile e instale as dependencias do projeto:

```bash
mvn clean install
```

Depois execute a aplicação:

```bash
mvn spring-boot:run
```

Eu usei a porta 8080, ou seja a aplicação estará disponível em: http://localhost:8080 depois que rodar

## Como executar os testes de integração

No diretório `goldenraspberry-api`, execute:

```bash
mvn test
```

### Base de dados

A base de dados encontra-se na pasta data, podendo ser alterada, seguindo a mesma estrutura.

## Endpoint disponível

### Obter intervalos de prêmios dos produtores

```
GET /api/producers/awards-interval
```

Retorna o produtor com maior intervalo entre dois prêmios consecutivos e o que obteve dois prêmios mais rápido.

Exemplo de resposta:

```json
{
  "min": [
    {
      "producer": "Joel Silver",
      "interval": 1,
      "previousWin": 1990,
      "followingWin": 1991
    }
  ],
  "max": [
    {
      "producer": "Matthew Vaughn",
      "interval": 13,
      "previousWin": 2002,
      "followingWin": 2015
    }
  ]
}
```
