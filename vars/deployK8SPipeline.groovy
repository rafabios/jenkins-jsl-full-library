def call() {


	stage('Criando arquivos do k8s'){
    
    def v = varsPipeline()

    ENV_FILE = readFile '.env'
    //ENV_FILE.readLines().grep(/[^#].+/).grep(/.+[=].+/)
    //withEnv(ENV_FILE.readLines().grep(/[^#].+/).grep(/.+[=].+/)) {

    // kube-converter script
	sh("export \$(cat .env | egrep -v '#' | egrep -E '^KUBE') && kube-converter.py")
    //sh("utils/kube-converter.py")

	}

    stage('Deploy to Kubernetes'){

    // Print Nome Correto do ambiente
    utilsPipeline().printDeployEnv()


    // Gerar kubeconfig do google gcp
    //sh("gcloud auth activate-service-account --key-file=${K8S_DEPLOY_ACCOUNT}")
    //sh("gcloud container clusters get-credentials dev-cluster desenvolvimento-250616 --zone us-central1-a")

        // Aplicar k8s config
        sh("env")
        sh("python kubectl.py deploy ${KUBE_CFG} ${PROJETO_NAME} ${DOCKER_IMAGE_NAME}")
        
        // ingress
        sh("python kubectl.py ingress ${KUBE_CFG} ${PROJETO_NAME} ${DOCKER_IMAGE_NAME}")


    }

}