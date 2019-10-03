def call() {
        

        stage('Aguardando aprovação') {
            def v = varsPipeline()

            try {
            if ("${v.mBRANCH_NAME}" == 'master' || "${v.mBRANCH_NAME}" == 'release') {
                        timeout(time:2, unit:'DAYS') {
                            input message:"Aprovar o Deploy em produção?( ${v.mBRANCH_NAME}  )", submitter: 'admin'
                        }

            } else {
                println "Branch:  ${v.mBRANCH_NAME} nao precisa de aprovacao!"
                println 'Seguindo com Deploy'
                println "${v.mPROJETO_NAME}"
                }
            } catch (Exception e) {
                       sh "Erro ao identificar a Branch ${v.mBRANCH_NAME}"
             }    

            }
}