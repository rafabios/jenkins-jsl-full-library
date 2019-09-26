def call() {

 // Template Dotnet
 def p = utilsPipeline()
 def v = varsPipeline()

 // Label 

 def label = "worker-${UUID.randomUUID().toString()}"

 println ">>>> Pod settings:"
 println "Nome da imagem ${v.mJENKINS_DOCKER_BUILD_IMAGE}"

 podTemplate(
  label: label,
  containers: [
   containerTemplate(name: 'dotnet-template', image: "${v.mJENKINS_DOCKER_BUILD_IMAGE}", ttyEnabled: true, command: 'cat')
  ],
  volumes: [
   hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock')
  ]
 ) {


  node(label) {
   stage('Clonando Repositorio') {
    container('dotnet-template') {
     println ">>> Entrando no checkout stage"
     checkout scm
    }
   }

   stage('Testando codigo') {
    println "Entrando no Test stage"
    container('dotnet-template') {

    println "Nenhum teste habilitado!"    

    }
   }
  

  stage('Docker Build & Push Current & Latest Versions') {
   println ">>> Entrando no Deploy stage"
    container('dotnet-template') {
      // This step should not normally be used in your script. Consult the inline help for details.
      //try {
      withDockerRegistry(credentialsId: 'DOCKERHUB_ACCOUNT_CREDENTIALS', toolName: 'docker') {
          //sh("docker build -t ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} .")
          //sh("docker push ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}")
      
      if ("${v.mBRANCH_NAME}" == 'master' || "${v.mBRANCH_NAME}" == 'release') {
        sh("docker build -t ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} .")
        sh("docker tag ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:'latest'")
        sh("docker push ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}")
        sh("docker push ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:'latest'")
        sh("ls -lha")
        sh """ sed -i "s|NOME_DA_IMAGEM|${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}|g" deployment.yaml """
      } 
      else {
        sh("docker build -t ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} .")
        sh("docker tag ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:'development'")
        sh("docker push ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:'development'")
        sh("docker push ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}")
        sh("ls -lha")
        sh """ sed -i "s|NOME_DA_IMAGEM|${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}|g" deployment.yaml """
    }
    }
   }
  }
   //}
   // } catch (Exception e) {
   //      sh 'Erro ao enviar a imagem para o dockerhub'
   // }    
   // Deploy to kubernetes
   stage('Deploy to K8s') {
    println ">>> Entrando na fase de deploy" 
    container('dotnet-template') {
      deployK8SPipeline()
      }
    }

  }
 }
}