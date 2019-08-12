def call() {

// Template Python
    def p = utilsPipeline()
    def v = varsPipeline()

podTemplate(containers: [
  containerTemplate(name: 'python-template', image: "${mDOCKER_HUB_ACCOUNT}/${v.mJENKINS_DOCKER_BUILD_IMAGE}", ttyEnabled: true, command: 'cat')
  ]) {

  node(POD_LABEL) {
    stage('Clonando Repositorio') {
      println "Entrando no checkout stage"
      checkout scm
    }


    
      stage('Testando codigo') {
        println "Entrando no Test stage"
        container('python-template') {
          sh 'pip install -r requirements.txt'
          sh 'ls -lah'
          sh p.testCommand
        }
      }
   }
    
      stage('Docker Build & Push Current & Latest Versions') {
        println "Entrando no Deploy stage"
          // This step should not normally be used in your script. Consult the inline help for details.
      //try {
          //withDockerRegistry(credentialsId: 'DOCKERHUB_ACCOUNT_CREDENTIALS', toolName: 'docker') {
              println v.mDOCKER_HUB_ACCOUNT
              sh ("docker build -t ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} .")
              sh ("docker push ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}")
              if ("${v.mBRANCH_NAME}" == 'master' || "${v.mBRANCH_NAME}" == 'release') {

                  sh ("docker tag ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:'latest'")
                  sh ("docker push ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:'latest'")
                } else {
                  sh ("docker tag ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:'development'")
                  sh ("docker push ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:'development'")
                }

              //}
     // } catch (Exception e) {
        //      sh 'Erro ao enviar a imagem para o dockerhub'
         // }    
// Deploy to kubernetes
deployK8SPipeline()



      }
    





  }
}