# 0.2.*
- [X] Adicionado as dependencias básicas do spring
- [X] Adicionado o endpoint para adicionar um customer e criar o idp client
- [X] Adicionado a implementação do identity provider para criar o user
- [X] Adicionado o retry para o refresh do access token do identity provider
- [X] Adicionado o retry, bulkhead e circuit breaker para a criação do IDP user
- [x] Adicionado a compensação para deletar o IDP user caso o customer não seja salvo no database
- [X] Adicionado a atualização do documento do customer
- [X] Adicionado a atualização do telefone do customer
- [X] Adicionado a rota para pegar o customer pelo user id
- [X] Adicionado a rota para pegar o customer autenticado
- [X] Adicionado a criação do address do customer
- [X] Adicionado a atualização do is default do address
- [X] Adicionado a atualização do address (title, zipcode, number, complement, district, city, state)
- [X] Adicionado a rota para pegar o address pelo id
- [X] Adicionado a rota para pegar o default address do customer autenticado
- [X] Adicionado a listagem dos addresses do customer autenticado
- [X] Adicionado a rota para deletar o address pelo id

# 0.1.0
- [X] Adicionado o setup inicial do projeto