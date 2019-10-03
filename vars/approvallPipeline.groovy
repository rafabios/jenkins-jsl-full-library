def call(String BRANCH_NAME) {
        

        stage('Aguardando aprovação') {
            def v = varsPipeline()

            try {
            if ("${BRANCH_NAME}" == 'master' || "${BRANCH_NAME}" == 'release') {
                        timeout(time:2, unit:'DAYS') {
                            input message:"Aprovar o Deploy em produção?( ${BRANCH_NAME}  )", submitter: 'admin'
                        }

            } else {
                println "Branch:  ${BRANCH_NAME} nao precisa de aprovacao!"
                println 'Seguindo com Deploy'
                println "${v.mPROJETO_NAME}"
                }
            } catch (Exception e) {
                       sh "Erro ao identificar a Branch ${mBRANCH_NAME}"
             }    

            }
}