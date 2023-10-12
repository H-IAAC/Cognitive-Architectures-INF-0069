# Arquiteturas Cognitivas - Exemplos

Este repositório contém um contêiner Docker que encapsula vários exemplos de um curso. Os exemplos estão organizados em quatro pastas com os nomes "Lida," "Clarion," "SOAR," e "CST." Para executar cada exemplo, você precisa entrar na pasta específica, navegar até a subpasta "Executáveis" e executar o script `exec.sh`. Aqui estão as instruções para começar.

## Pré-requisitos

Antes de poder executar os exemplos, você precisa garantir que o Docker Engine esteja instalado em seu sistema. Se você ainda não instalou o Docker Engine, siga as instruções de instalação abaixo:

### Instalação do Docker Engine

1. **Linux**: Você pode usar o gerenciador de pacotes de sua distribuição para instalar o Docker. No Ubuntu, você pode usar os seguintes comandos:

   ```bash
   sudo apt-get update
   sudo apt-get install docker.io
   ```

   Para iniciar o Docker e habilitá-lo no boot, execute:

   ```bash
   sudo systemctl start docker
   sudo systemctl enable docker
   ```

2. **Windows**: Instale o [Docker Desktop](https://www.docker.com/products/docker-desktop) para Windows. Siga as instruções de instalação no site do Docker.

3. **macOS**: Instale o [Docker Desktop](https://www.docker.com/products/docker-desktop) para macOS. Siga as instruções de instalação no site do Docker.

## Executando o Contêiner Docker

Depois de ter o Docker Engine instalado, você pode executar o contêiner Docker que contém os exemplos do curso, executando o seguinte comando em seu terminal:

```bash
./docker.sh
```

O script `docker.sh` iniciará o contêiner e configurará o ambiente necessário.

## Acessando os Exemplos do Curso

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

## Limpeza

Para parar e remover o contêiner Docker quando você terminar de usar os exemplos, abra um novo terminal e execute:

```bash
docker stop course-examples
docker rm course-examples
```

É isso! Agora você pode explorar e executar os exemplos do curso dentro do contêiner Docker com facilidade.
