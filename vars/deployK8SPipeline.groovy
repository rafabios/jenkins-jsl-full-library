def call() {

    def v = varsPipeline()

	stage('Criando arquivos do k8s'){
    
    

    ENV_FILE = readFile '.env'
    //ENV_FILE.readLines().grep(/[^#].+/).grep(/.+[=].+/)
    //withEnv(ENV_FILE.readLines().grep(/[^#].+/).grep(/.+[=].+/)) {
    print("Variaveis de ambiente: ")
    sh("env")
    // kube-converter script
	sh("export \$(cat .env | egrep -v '#' | egrep -E '^KUBE') && kube-converter.py")
    //sh("utils/kube-converter.py")

	}

    stage('Deploy to Kubernetes'){

    // Print Nome Correto do ambiente
    //utilsPipeline().printDeployEnv()


    // Gerar kubeconfig do google gcp
    //sh("gcloud auth activate-service-account --key-file=${K8S_DEPLOY_ACCOUNT}")
    //sh("gcloud container clusters get-credentials dev-cluster desenvolvimento-250616 --zone us-central1-a")

withCredentials([file(credentialsId: 'k8_file', variable: 'k8_secret')]) {
         K8S_CFG_FILE = "${env.k8_secret}"


        // Aplicar k8s config
        print(">>> Aplicando configuracao: ")
        //sh "kubectl.py deploy  $(echo  $v.mK8S_CFG_FILE > .x.cfg | echo $PWD/.x.cfg) ${v.mPROJETO_NAME} ${v.mDOCKER_IMAGE_NAME}"
        sh("kubectl.py deploy  ${K8S_CFG_FILE} ${v.mPROJETO_NAME} ${v.mDOCKER_IMAGE_NAME}")
        
        //status
        print(">>> Checando status do rollout: ")
        sh("kubectl.py status  ${K8S_CFG_FILE} ${v.mPROJETO_NAME} ${v.mDOCKER_IMAGE_NAME}")

        // ingress
        print(">>> Endpoint para acesso: ")
        //sh "kubectl.py ingress $(echo  $v.mK8S_CFG_FILE > .x.cfg | echo $PWD/.x.cfg) ${v.mPROJETO_NAME} ${v.mDOCKER_IMAGE_NAME")
        sh("kubectl.py ingress ${K8S_CFG_FILE} ${v.mPROJETO_NAME} ${v.mDOCKER_IMAGE_NAME}")
        
        }


    }

}