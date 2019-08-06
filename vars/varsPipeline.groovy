// Funcao para carregar com as variaveis do projeto
// 30-07-2019


def call() {
  Map pipelineCfg = readYaml(text: '''
 

  '''
  )
  return pipelineCfg
}