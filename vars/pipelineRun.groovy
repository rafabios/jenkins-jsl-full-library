import jenkins.model.*

def call() {

  node {

    stage('Initialize') {
      checkout scm
      echo 'Loading pipeline definition'
      //Yaml parser = new Yaml()
       //Map pipelineDefinition = parser.load(new File(pwd() + '/pipeline.yml').text)
       def pipelineDefinition
       Map pipelineDefinition = utilsPipeline()  // Testar
       println pipelineDefinition.type
    

    switch(pipelineDefinition.type) {
      case 'python':
        // Instantiate and execute a Python pipeline
        new pythonPipeline(pipelineDefinition)
      //case 'dotnet':
        // Instantiate and execute a DotNet pipeline
        //new dotnetPipeline(pipelineDefinition).executePipeline()
      //case 'front':
        // Instantiate and execute a Front pipeline
        //new frontPipeline(pipelineDefinition).executePipeline()
      //case 'job':
        // Instantiate and execute a Job pipeline
        //new jobPipeline(pipelineDefinition).executePipeline()
     }
   }
  }
}