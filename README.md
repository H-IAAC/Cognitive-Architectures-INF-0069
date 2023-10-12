# Arquiteturas Cognitivas - Exemplos

Este repositório contém um contêiner Docker que encapsula vários exemplos de um curso. Os exemplos estão organizados em quatro pastas com os nomes "Lida," "Clarion," "SOAR," e "CST." Para executar cada exemplo, você precisa entrar na pasta específica, navegar até a subpasta "Executáveis" e executar o script `exec.sh`. Aqui estão as instruções para começar.

## 🚀 Pré-requisitos

Antes de poder executar os exemplos, você precisa garantir que o Docker Engine esteja instalado em seu sistema. Se você ainda não instalou o Docker Engine, siga as instruções de instalação abaixo:

### Instalação do Docker Engine

Para execução do contêiner é necessário a instalação do Docker Engine. Siga os passos do site oficial do [Docker](https://docs.docker.com/engine/install/ubuntu/#install-using-the-repository).

> ⚠️ Garanta que esteja seguindo as instrução para instalação do Docker Engine por apt. O Docker Desktop para Linux não irá possibilitar o uso das interfaces gráficas dos exemplos.

## 🏃‍♀️ Executando o Contêiner Docker

Depois de ter o Docker Engine instalado, você pode executar o contêiner Docker que contém os exemplos do curso, executando o seguinte comando em seu terminal:

```bash
./docker.sh
```

O script `docker.sh` iniciará o contêiner e configurará o ambiente necessário.

## 🔍 Acessando os Exemplos do Curso

Após iniciar o contêiner Docker, você encontrará quatro pastas dentro do diretório `/examples`, cada uma correspondendo aos exemplos do curso:

1. `Lida`
2. `Clarion`
3. `SOAR`
4. `CST`

Para executar um exemplo, siga estas etapas:

1. Entre na pasta do exemplo desejado usando o comando `cd`. Por exemplo:

   ```bash
   cd Lida/
   ```

2. Navegue até a subpasta "Executáveis":

   ```bash
   cd Executáveis/
   ```

3. Execute o script `exec.sh`:

   ```bash
   ./exec.sh
   ```

Isso executará o exemplo dentro do contêiner Docker.

Para parar e remover o contêiner Docker quando você terminar de usar os exemplos, digite `Ctrl+d`

É isso! Agora você pode explorar e executar os exemplos do curso dentro do contêiner Docker com facilidade.
