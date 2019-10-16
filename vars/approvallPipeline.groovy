def call(String BRANCH_NAME) {
        
        def BRANCH_NAME_DEF = BRANCH_NAME

        stage('Aguardando aprovação') {
            def v = varsPipeline()
            

            try {
            if ("${BRANCH_NAME_DEF}" == 'master' || "${BRANCH_NAME_DEF}" == 'release') {
                        timeout(time:2, unit:'DAYS') {
                            input message:"Aprovar o Deploy em produção?( ${BRANCH_NAME_DEF}  )", submitter: 'admin'
                        }

            } else {
                println "Branch:  ${BRANCH_NAME_DEF} nao precisa de aprovacao!"
                println 'Seguindo com Deploy'
                println "${v.mPROJETO_NAME_DEF}"
                }
            } catch (Exception e) {
                       sh "Erro ao identificar a Branch ${mBRANCH_NAME_DEF}"
             }    

            }
}