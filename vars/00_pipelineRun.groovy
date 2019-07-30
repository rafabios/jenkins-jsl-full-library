import jenkins.model.*

def execute() {

  node {

    stage('Initialize') {
      checkout scm
      echo 'Loading pipeline definition'
      //Yaml parser = new Yaml()
      //Map pipelineDefinition = parser.load(new File(pwd() + '/pipeline.yml').text)
       def pipelineDefinition = 50_utilsPipeline()  // Testar
    }

    switch(pipelineDefinition.pipelineType) {
      case 'python':
        // Instantiate and execute a Python pipeline
        new 22_pythonPipeline(pipelineDefinition).executePipeline()
      case 'dotnet':
        // Instantiate and execute a DotNet pipeline
        new 20_dotnetPipeline(pipelineDefinition).executePipeline()
      case 'front':
        // Instantiate and execute a Front pipeline
        new 21_frontPipeline(pipelineDefinition).executePipeline()
      case 'job':
        // Instantiate and execute a Job pipeline
        new 23_jobPipeline(pipelineDefinition).executePipeline()
    }

  }

}