job('EPBYMINW2695/main') {
  parameters {
  activeChoiceParam('Childs') {
        description('Name of the Childs')
        choiceType('CHECKBOX')
        groovyScript {
          script('def list = ["MNTLAB-alubouski-child1-build-job","MNTLAB-alubouski-child2-build-job","MNTLAB-alubouski-child3-build-job","MNTLAB-alubouski-child4-build-job"]; list.each { println "${it}"}')
          fallbackScript('"Error in script"')
        }
   }
      
    activeChoiceParam('BRANCH_NAME') {
        description('Name of Branches')
        choiceType('CHECKBOX')
        groovyScript {
          script('def list = ["master","alubouski"]; list.each { println "${it}"}')
          fallbackScript('"Error in script"')
        }
   }
    
  }
  
  scm {
     git {
            remote {
                
                url('https://github.com/MNT-Lab/mntlab-dsl.git')
            }
       branch('$BRANCH_NAME')
     }
}
  
  steps {
        shell('echo "hello DSL"')
     }
  
 steps {
        conditionalSteps {
            condition {
                alwaysRun()
            }
            
            steps {
                triggerBuilder {
configs {
blockableBuildTriggerConfig {
configs {
predefinedBuildParameters {
properties('BRANCH_NAME=$BRANCH_NAME')
  textParamValueOnNewLine(false)
  }
}  
// A comma separated list of projects to build
projects('$Childs')
// When activating this option, triggered builds will be run synchronously.
block {
buildStepFailureThreshold('UNSTABLE')
unstableThreshold('UNSTABLE')
failureThreshold('never')
}

}
}
                  
                }

            }
        }
    }
    
}



for(def i=1; i<5; i++){
            job("EPBYMINW2695/MNTLAB-alubouski-child${i}-build-job") {
  parameters {
    
 activeChoiceParam('BRANCH_NAME') {
            description('Chose one please')
            choiceType('CHECKBOX')
            groovyScript {
                script('''def gitURL = "https://github.com/MNT-Lab/mntlab-dsl.git";def command = "git ls-remote -h $gitURL";def proc = command.execute();proc.waitFor();if ( proc.exitValue() != 0 ) {println "Error, ${proc.err.text}"System.exit(-1)};def branches = proc.in.text.readLines().collect {it.replaceAll(/[a-z0-9]*\\trefs\\/heads\\//, '')}; branches.each { println "${it}"}''')
                fallbackScript('"Error in script"')
            }
        }

    }
  
  
    scm {
     git {
            remote {
           
                url('https://github.com/MNT-Lab/mntlab-dsl.git')
            }
       branch('$BRANCH_NAME')
     }
}
  
  steps {
        
        shell('chmod a+x script.sh;./script.sh > output.txt;tar -czvf "$BRANCH_NAME"_dsl_script.tar.gz output.txt script.sh dsl.groovy')
    } 
 }
}

