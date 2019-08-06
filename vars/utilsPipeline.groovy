// Funcao para carregar o yml com as variaveis do projeto
// 30-07-2019


def call() {
  Map pipelineCfg = readYaml(file: "${WORKSPACE}/pipeline.yaml")
  return pipelineCfg
}


	def printDeployEnv() {

        // Condicação para printar o nome correto em tempo de deploy
        if ("${BRANCH_NAME}" == 'devel') {
                echo "Execudando Deploy para o ambiente de DEV"
        }       
        if ("${BRANCH_NAME}" == 'release' ) {       
                echo "Execudando Deploy para o ambiente de Homologacao"
        }       
        if ("${BRANCH_NAME}" == 'master') {       
                echo "Execudando Deploy para o ambiente de Produção"
        }       
  }