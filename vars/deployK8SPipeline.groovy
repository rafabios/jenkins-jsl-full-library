def call() {

	stage('Criando arquivos do k8s'){

    ENV_FILE = readFile '.env'
    //ENV_FILE.readLines().grep(/[^#].+/).grep(/.+[=].+/)
    //withEnv(ENV_FILE.readLines().grep(/[^#].+/).grep(/.+[=].+/)) {

    // kube-converter script
	sh("export \$(cat .env | egrep -v '#' | egrep -E '^KUBE') && utils/kube-converter.py")
    //sh("utils/kube-converter.py")

	}

    stage('Deploy to Kubernetes'){

    // Print Nome Correto do ambiente
    utilsPipeline().printDeployEnv()

    // Aplicar k8s config
    sh("python utils/kubectl.py deploy ${KUBE_CFG} ${PROJETO_NAME} ${DOCKER_IMAGE_NAME}")
    
    // ingress
    sh("python utils/kubectl.py ingress ${KUBE_CFG} ${PROJETO_NAME} ${DOCKER_IMAGE_NAME}")


}

    }



}