// Funcao para carregar com as variaveis do projeto
// 30-07-2019

def call(){


  def label = UUID.randomUUID().toString()
  def k8sLabel = "K8NODE-"+label


  // Role para pegar os valor do cofre do Jenkins, nome do repositorio dockerhub
  def DOCKER_HUB_ACCOUNT
  def DOCKER_HUB_PASSWORD
  withCredentials([usernamePassword(credentialsId: 'DOCKERHUB_ACCOUNT_CREDENTIALS', passwordVariable: 'DOCKERHUB_PASSWORD', usernameVariable: 'DOCKERHUB_LOGIN')]) {
      DOCKER_HUB_ACCOUNT = "${env.DOCKERHUB_LOGIN}"
      DOCKER_HUB_PASSWORD = "${env.DOCKERHUB_PASSWORD}"
  }
  // ROOT-SSH : Credenciais do GIT * esta no checkout deploy 
  def scmInfo = checkout scm
  def DOCKER_IMAGE_NAME = "${scmInfo.GIT_URL}".split('/')[-1].replace('.git','')
  def K8S_DEPLOYMENT_NAME = "${scmInfo.GIT_URL}".split('/')[-1].replace('.git','')
  def BRANCH_NAME  = "${env.gitlabBranch}"
  //def TEMPLATES_REPO = "git@spobvokd1001.indusval.com.br:root/templates-utils.git"
  def PROJETO_NAME = 'default'
  def JENKINS_DOCKER_BUILD_IMAGE = "${env.JENKINS_DOCKER_BUILD_IMAGE}"
  def CONSUL_HOST = "${env.CONSUL_HOST}"
  def CONSUL_PORT = "${env.CONSUL_PORT}"

// Kubernetes cluster login

  def K8S_CFG_FILE
withCredentials([string(credentialsId: 'k8_file', variable: 'k8_secret')]) {
         K8S_CFG_FILE = `echo $k8_secret`
         //K8S_CFG_FILE = "${env.k8_secret}"
}


  map = [
    mDOCKER_HUB_ACCOUNT: DOCKER_HUB_ACCOUNT ,
    mDOCKER_HUB_PASSWORD: DOCKER_HUB_PASSWORD,
    mscmInfo: scmInfo,
    mDOCKER_IMAGE_NAME: DOCKER_IMAGE_NAME,
    mK8S_DEPLOYMENT_NAME:K8S_DEPLOYMENT_NAME,
    mBRANCH_NAME: BRANCH_NAME,
    mPROJETO_NAME: PROJETO_NAME,
    mJENKINS_DOCKER_BUILD_IMAGE: JENKINS_DOCKER_BUILD_IMAGE,
    mCONSUL_HOST: CONSUL_HOST,
    mCONSUL_PORT: CONSUL_PORT,
    mK8S_CFG_FILE: K8S_CFG_FILE,
  ]

  return map
}