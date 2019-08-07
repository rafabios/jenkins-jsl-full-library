def call() {
  node {
    stage('Clonando Repositorio') {
      println "Entrando no checkout stage"
      checkout scm
    }
    def p = utilsPipeline()

    
      stage('Testando codigo') {
        println "Entrando no Test stage"
        sh 'pip install -r requirements.txt'
        sh 'ls -lah'
        sh p.testCommand
      }
    
      stage('Docker Build & Push Current & Latest Versions') {
        println "Entrando no Deploy stage"
          // This step should not normally be used in your script. Consult the inline help for details.
      //try {
          withDockerRegistry(credentialsId: 'DOCKERHUB_ACCOUNT_CREDENTIALS', toolName: 'docker') {

              sh ("docker build -t ${DOCKER_HUB_ACCOUNT}/${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} .")
              sh ("docker push ${DOCKER_HUB_ACCOUNT}/${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}")
              if ("${BRANCH_NAME}" == 'master' || "${BRANCH_NAME}" == 'release') {

                  sh ("docker tag ${DOCKER_HUB_ACCOUNT}/${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} ${DOCKER_HUB_ACCOUNT}/${DOCKER_IMAGE_NAME}:'latest'")
                  sh ("docker push ${DOCKER_HUB_ACCOUNT}/${DOCKER_IMAGE_NAME}:'latest'")
                } else {
                  sh ("docker tag ${DOCKER_HUB_ACCOUNT}/${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} ${DOCKER_HUB_ACCOUNT}/${DOCKER_IMAGE_NAME}:'development'")
                  sh ("docker push ${DOCKER_HUB_ACCOUNT}/${DOCKER_IMAGE_NAME}:'development'")
                }

              }
     // } catch (Exception e) {
        //      sh 'Erro ao enviar a imagem para o dockerhub'
         // }    
// Deploy to kubernetes
deployK8SPipeline()



      }
    





  }
}