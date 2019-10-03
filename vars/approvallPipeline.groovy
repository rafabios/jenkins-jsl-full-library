def call() {
        
         def v = varsPipeline()

        stage('Aguardando aprovação') {
            echo 'aprovacao'
            try {
            if ("${v.mBRANCH_NAME}" == 'master' || "${v.mBRANCH_NAME}" == 'release') {
                        timeout(time:2, unit:'DAYS') {
                            input message:"Aprovar o Deploy em produção?( ${v.mBRANCH_NAME}  )", submitter: 'admin'
                        }

            } else {
                println "Branch:  ${v.mBRANCH_NAME} nao precisa de aprovacao!"
                println 'Seguindo com Deploy'
                }
            } catch (Exception e) {
                       sh "Erro ao identificar a Branch ${v.mBRANCH_NAME}"
             }    

            }
}