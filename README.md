# Arquiteturas Cognitivas - Exemplos

Este repositório contém o necessário para execução de um contêiner Docker com exemplos demonstrativos de arquiteturas cognitivas.

## 🕹️ Simulação

Todos os exemplos executam uma simulação [WS3D.](https://github.com/CST-Group/ws3d) O WS3D é um ambiente virtual para experimento com criaturas artificiais. Neste ambiente virtual, é possível criar um conjunto de criaturas virtuais, que são controladas por seus sensores e atuadores, gerenciados por meio de Sockets.

Todos os exemplos utilizam uma simulação do WS3D, onde uma criatura deve explorar o ambiente e coletar jóias e alimentos. As jóias podem ser de 6 cores diferentes e fornecem pontos (recompensa) para o agente quando conjuntos específicos são coletados. Além disso, a criatura possui um nível de energia que decai linearmente ao longo do tempo e quando zerado, impede o movimento da criatura. Para recuperar sua energia a mesma deve coletar alimentos (maçãs e nozes) espalhadas pelo ambiente.

Cada exemplo utiliza uma das arquiteturas apresentadas no curso para controlar a criatura no ambiente WS3D.

## 🚀 Pré-requisitos

Antes de poder executar os exemplos, você precisa garantir que o Docker Engine esteja instalado em seu sistema. Se você ainda não instalou o Docker Engine, siga as instruções de instalação abaixo:

### Instalação do Docker Engine

#### **Linux**

Para execução do contêiner é necessário a instalação do Docker Engine. Siga os passos do site oficial do [Docker](https://docs.docker.com/engine/install/ubuntu/#install-using-the-repository).

> ⚠️ Garanta que esteja seguindo as instrução para instalação do Docker Engine por apt. O Docker Desktop para Linux não irá possibilitar o uso das interfaces gráficas dos exemplos.

#### **Windows**

Baixe o instalador e siga os passos de instalação no site do [Docker](https://docs.docker.com/desktop/install/windows-install/).

> ⚠️ Garanta que o WSL 2 esteja instalado conforme as instruções dos [System Requirements](https://docs.docker.com/desktop/install/windows-install/#system-requirements)

Para a utilização no Windows será necessário também a instalação do [VcXsrv](https://sourceforge.net/projects/vcxsrv/)

## 🏃‍♀️ Executando o Contêiner Docker

### **Linux**

Depois de ter o Docker Engine instalado, você pode executar o contêiner Docker que contém os exemplos do curso, executando o script fornecido neste repositório:

```bash
./docker.sh
```

O script `docker.sh` iniciará o contêiner e configurará o ambiente necessário.

### **Windows**

1. Inicie o XServer no Windows, conforme os passos abaixo:
    1. Execute o aplicativo XLaunch.exe
    2. Selecione a opção 'Multiple windows', defina o 'Display number' como 0 e clique 'Avançar'
    3. Selecione a opção 'Start no client' e clique em 'Avançar'
    4. Marque a opção 'Disable access control', clique em 'Avançar' e inicie o XServer clicando em 'Concluir'
3. Inicie o Docker Desktop
4. Encontre o ip de conexão do WSL, conforme os passos abaixo:
    1. Inicie o Prompt de Comando e execute o comando `ipconfig`
    2. Encontre o adaptador de conexão com referência ao WSL. Comumente é o adaptador Ethernet e indicará no título `(WSL)`
    3. Copie o endereço de IPv4
5. Ainda no Prompt de Comando, inicie o container Docker com o comando a seguir, inserindo o ip copiado anteriormente na tag `<MY-WSL-IP>`
```
docker run --rm -it --privileged -e "PULSE_SERVER=/mnt/wslg/PulseServer" -v \\wsl$\Ubuntu\mnt\wslg:/mnt/wslg/ -e DISPLAY=<MY-WSL-IP>:0 brgsil/cog_arq_examples
```

## 🔍 Acessando os Exemplos do Curso

Após iniciar o contêiner Docker, você encontrará quatro pastas dentro do diretório `/examples`, cada uma correspondendo aos exemplos do curso:

1. `SOAR`
2. `Clarion`
3. `Lida`
4. `CST`

Para executar um exemplo, siga estas etapas:

1. Entre na pasta do exemplo desejado usando o comando `cd`. Por exemplo:

   ```bash
   cd Lida/
   ```

2. Navegue até a subpasta "Executable":

   ```bash
   cd Executable/
   ```

3. Execute o script `exec.sh`:

   ```bash
   ./exec.sh
   ```

Isso executará o exemplo dentro do contêiner Docker.

Após finalizar a execução dos exemplos, pode-se parar e remover o contêiner Docker no Prompt de Comando digitando `Ctrl+d`

É isso! Agora você pode explorar e executar os exemplos do curso dentro do contêiner Docker com facilidade.
