def call(x) {
  node {
    stage('Checkout') {
      checkout scm
    }
    def p = x

    
      stage('Test') {
        sh 'pip install -r requirements.txt'
        sh 'ls -lah'
        sh p.testCommand
      }
    

    if (env.BRANCH_NAME == 'master' && p.deployUponTestSuccess == true) {
      
        stage('Deploy') {
          sh "echo ${p.deployCommand} ${p.deployEnvironment}"
      
      }
    }
  }
}