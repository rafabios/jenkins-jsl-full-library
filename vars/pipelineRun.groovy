import jenkins.model.*

def execute() {

  node {

    stage('Initialize') {
      checkout scm
      echo 'Loading pipeline definition'
      //Yaml parser = new Yaml()
      //Map pipelineDefinition = parser.load(new File(pwd() + '/pipeline.yml').text)
       def pipelineDefinition = utilsPipeline()  // Testar
       //println pipelineDefinition.pipelineType
    }

    switch(pipelineDefinition.pipelineType) {
      case 'python':
        // Instantiate and execute a Python pipeline
        new pythonPipeline(pipelineDefinition).executePipeline()
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