def call() {

 // Template Python
 def p = utilsPipeline()
 def v = varsPipeline()

 // Label 

 def label = "worker-${UUID.randomUUID().toString()}"

 println ">>>> Pod settings:"
 println "Nome da imagem ${v.mJENKINS_DOCKER_BUILD_IMAGE}"

 podTemplate(
  label: label,
  containers: [
   containerTemplate(name: 'python-template', image: "${v.mJENKINS_DOCKER_BUILD_IMAGE}", ttyEnabled: true, command: 'cat')
  ],
  volumes: [
   hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock')
  ]
 ) {


  node(label) {
   stage('Clonando Repositorio') {
    container('python-template') {
     println "Entrando no checkout stage"
     checkout scm
    }
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
   println ">>> Entrando no Deploy stage"
    container('python-template') {
      // This step should not normally be used in your script. Consult the inline help for details.
      //try {
      //withDockerRegistry(credentialsId: 'DOCKERHUB_ACCOUNT_CREDENTIALS', toolName: 'docker') {
      //println v.mDOCKER_HUB_ACCOUNT
        sh("/usr/bin/docker login -u ${v.mDOCKER_HUB_ACCOUNT} -p ${v.mDOCKER_HUB_PASSWORD}")
        sh("/usr/bin/docker build -t ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} .")
        sh("/usr/bin/docker push ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}")

      if ("${v.mBRANCH_NAME}" == 'master' || "${v.mBRANCH_NAME}" == 'release') {

        sh("/usr/bin/docker tag ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:'latest'")
        sh("/usr/bin/docker push ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:'latest'")
      } 
      else {
        sh("/usr/bin/docker tag ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:'development'")
        sh("/usr/bin/docker push ${v.mDOCKER_HUB_ACCOUNT}/${v.mDOCKER_IMAGE_NAME}:'development'")
    }
   }
  }
   //}
   // } catch (Exception e) {
   //      sh 'Erro ao enviar a imagem para o dockerhub'
   // }    
   // Deploy to kubernetes
   stage('Deploy to K8s') {
    container('python-template') {
      deployK8SPipeline()
      }
    }

  
 }
}