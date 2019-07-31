def call() {
  node {
    stage('Checkout') {
      checkout scm
      println "Entrando no checkout stage"
    }
    def p = utilsPipeline()

    
      stage('Test') {
        println "Entrando no Test stage"
        sh 'pip install -r requirements.txt'
        sh 'ls -lah'
        sh p.testCommand
      }
    
    println "Var branch:"
    println env.BRANCH_NAME
    
    if (env.BRANCH_NAME == 'master' && p.deployUponTestSuccess == true) {
      
        stage('Deploy') {
          println "Entrando no deploy stage"
          sh "echo ${p.deployCommand} ${p.deployEnvironment}"
      
      }
    }
  }
}