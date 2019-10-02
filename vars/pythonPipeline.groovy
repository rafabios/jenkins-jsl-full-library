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
     println ">>> Entrando no checkout stage"
     checkout scm
    }
   }

  stage('SonarQube analysis') {
    println ">>> Entrando nos testes de Qualidade (SONAR)"
    container('python-template') {
            withSonarQubeEnv('SONAR') {
              withCredentials([string(credentialsId: 'SONAR_SECRET', variable: 'SONAR_SECRET')]) {
            withMaven(maven:'maven3') {
                  def sonarqubeScannerHome = tool name: 'Sonar'
                  sh "echo '172.16.14.231	sonar.dev.apps.indusval.com.br' >> /etc/hosts"
                  sh """ ${sonarqubeScannerHome}/bin/sonar-scanner  -X \
                        -Dsonar.host.url=http://sonar.dev.apps.indusval.com.br:30631 \
                        -Dsonar.projectKey=${v.mDOCKER_IMAGE_NAME} \
                        -Dsonar.login=${env.SONAR_SECRET} \
                        -Dsonar.projectBaseDir=$WORKSPACE \
                        -Dsonar.projectName=${v.mDOCKER_IMAGE_NAME} \
                        -Dsonar.projectVersion=$BUILD_NUMBER \
                        -Dsonar.language=python \
                        -Dsonar.scm.disabled=True \
                        -Dsonar.sourceEncoding=UTF-8 2> error.out || \
                        echo 'Falha ao carregar o SonarQube!: '\$(cat error.out) """
                    }
                }
              }
            }
         }



   stage('Testando codigo') {
    println "Entrando no Test stage"
    container('python-template') {
     sh """ pip install -r requirements.txt 2> error.out || \
     echo 'Falha ao carregar o requiements: '\$(cat error.out) """
     sh """ p.testCommand  || \
     echo 'Falha ao carregar os testes de codigo!' """
    }
   }




  stage('Docker Build & Push Current & Latest Versions') {
   println ">>> Entrando no Deploy stage"
    container('python-template') {
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
    container('python-template') {
      deployK8SPipeline()
      }
    }

  }
 }
}