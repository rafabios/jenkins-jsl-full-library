// Funcao para carregar o yml com as variaveis do projeto
// 30-07-2019


def call() {
  Map pipelineCfg = readYaml(file: "${WORKSPACE}/pipeline.yaml")
  return pipelineCfg
}