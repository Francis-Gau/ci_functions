def call () {
    pipeline {    
    agent any      
    stages {         
        stage('Build') {             
            steps {                 
                sh 'pip install -r requirements.txt'             
                }         
            }
        stage('Lint Stage') {             
            steps {                 
                sh 'pylint-fail-under --fail_under 5.0 *.py'              
            }     
        }
       stage('Coverage Stage') {             
            steps {                 
                script {
                def files = findFiles(glob: 'test*.py') 
                for ( file in files ){
                    sh "coverage run --omit */*-packages/* ${file}"
                    }
                }
                sh 'coverage report'
            }
                  
        }
        stage('Package'){

                steps{
                    sh 'zip app.zip *.py'
                    archiveArtifacts artifacts: 'app.zip', fingerprint: true    
                }
                post {                 
                always {  

                    script {
                            if ( fileExists( 'api-test-reports' ) ) {
                                junit 'api-test-reports/*.xml' 
                            }
                             if ( fileExists( 'test-reports' ) ) {
                                junit 'test-reports/*.xml'
                            }
                    }
                    
                
                                   
                }             
            } 
        }
       
        }
                  
}

}