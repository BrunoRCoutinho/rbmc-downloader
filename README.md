RBMC Downloader --- PROJETO FEITO E ATUALIZADO PARA ESTUDOS ---
Aplicação web para download automatizado de arquivos das estações da Rede Brasileira de Monitoramento Contínuo (RBMC), disponibilizados pelo IBGE, com foco no apoio ao planejamento de levantamentos cartográficos com tecnologia GNSS.

Problema - 
O processamento de dados GNSS exige o download das bases de referência das estações RBMC. O portal do IBGE disponibiliza esses arquivos individualmente — 8 arquivos por hora, totalizando 192 arquivos para cada 24 horas de dados.
Em projetos com múltiplos dias de levantamento e mais de uma estação RBMC próxima à área de trabalho, a quantidade de downloads pode se tornar um gargalo operacional significativo.

Solução - 
A aplicação consome a API do IBGE para localizar e baixar os arquivos de forma automatizada. O usuário informa:

Ano -
Data (DOY - Day Of Year) - 
Estação RBMC desejada

O back-end localiza todos os arquivos correspondentes e os disponibiliza em um único arquivo compactado (.zip), cobrindo as 24 horas do dia selecionado.

Funcionalidades - 

 Download automatizado por estação e data
 Compactação das 24 horas em um único arquivo .zip
 Busca de estações em raio de 300 km a partir das coordenadas do levantamento (limite recomendado para controle da Degradação da Acurácia Relativa)
 Conversor de coordenadas entre os datums utilizados no Brasil


Stack - 
CamadaTecnologiaBack-endJava + Spring BootFront-endHTML, CSS, ThymeleafAPI externaIBGE - RBMC


Contribuições
Projeto em desenvolvimento inicial. Sugestões, issues e pull requests são bem-vindos.
