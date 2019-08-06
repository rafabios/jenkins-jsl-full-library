import jenkins.model.*

// Variaveis

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

// Fim das variaveis Globais

// Chama aprovação antes de iniciar o build
approvallPipeline()

def call() {

  node {

    stage('Initialize') {
      checkout scm
      echo 'Loading pipeline definition'
      Map pipelineDefinition = utilsPipeline()  // Testar
      println pipelineDefinition.type
    

    switch(pipelineDefinition.type) {
      case 'python':
        // Instantiate and execute a Python pipeline
        //new pythonPipeline(pipelineDefinition)
        println "Switch to python Pipeline"
        new pythonPipeline().call()
      //case 'dotnet':
        // Instantiate and execute a DotNet pipeline
        //new dotnetPipeline(pipelineDefinition).executePipeline()
      //case 'front':
        // Instantiate and execute a Front pipeline
        //new frontPipeline(pipelineDefinition).executePipeline()
      //case 'job':
        // Instantiate and execute a Job pipeline
        //new jobPipeline(pipelineDefinition).executePipeline()
     }
   }
  }
}