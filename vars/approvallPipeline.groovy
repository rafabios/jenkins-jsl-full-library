def call() {
        
        stage('Aguardando aprovação') {
            echo 'aprovacao'
            try {
            if ("${BRANCH_NAME}" == 'master' || "${BRANCH_NAME}" == 'release') {
                        timeout(time:2, unit:'DAYS') {
                            input message:"Aprovar o Deploy em produção?( ${BRANCH_NAME}  )", submitter: 'admin'
                        }

            } else {
                echo 'Seguindo com Deploy'
                }
            } catch (Exception e) {
                       sh "Erro ao identificar a Branch ${BRANCH_NAME}"
             }    

            }
}