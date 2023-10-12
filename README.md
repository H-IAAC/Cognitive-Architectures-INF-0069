# Arquiteturas Cognitivas - Exemplos

Este repositório contêm o necessário para execução de um contêiner Docker com exemplos demonstrativos de arquiteturas cognitivas.

## 🕹️ Simulação

Todos os exemplos executam uma simulação [WS3D.](https://github.com/CST-Group/ws3d) O WS3D é um ambiente virtual para experimento com criaturas artificiais. Neste ambiente virtual, é possível criar um conjunto de criaturas virtuais, que são controladas por seus sensores e atuadores, gerenciados por meio de Sockets.

Todos os exemplos utilizam uma simulação do WS3D, onde uma criatura deve explorar o ambiente e coletar jóias e alimentos. As jóias podem ser de 6 cores diferentes e fornem pontos (recompensa) para o agente quando conjuntos específicos são coletados. Além disso, a criatura possui um nível de energia que decai linearmente ao longo do tempo e quando zerado, impede o movimento da criatura. Para recuperar sua energia a mesma deve coletar alimentos (maçãs e nozes) espalhadas pelo ambiente.

Cada exemplo utiliza uma das arquiteturas apresentadas no curso para controlar a criatura no ambiente WS3D.

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
   cd Executables/
   ```

3. Execute o script `exec.sh`:

   ```bash
   ./exec.sh
   ```

Isso executará o exemplo dentro do contêiner Docker.

Para parar e remover o contêiner Docker quando você terminar de usar os exemplos, digite `Ctrl+d`

É isso! Agora você pode explorar e executar os exemplos do curso dentro do contêiner Docker com facilidade.
