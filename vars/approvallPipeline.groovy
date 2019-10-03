def call() {
        
        stage('Aguardando aprovação') {
            echo 'aprovacao'
            try {
            if ("${env.BRANCH_NAME}" == 'master' || "${env.BRANCH_NAME}" == 'release') {
                        timeout(time:2, unit:'DAYS') {
                            input message:"Aprovar o Deploy em produção?( ${env.BRANCH_NAME}  )", submitter: 'admin'
                        }

            } else {
                println "Branch:  ${env.BRANCH_NAME} nao precisa de aprovacao!"
                println 'Seguindo com Deploy'
                }
            } catch (Exception e) {
                       sh "Erro ao identificar a Branch ${env.BRANCH_NAME}"
             }    

            }
}