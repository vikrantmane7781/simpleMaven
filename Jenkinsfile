pipeline{
 agent any 
  stages{
    stage('maven install'){
      steps{
       withMaven( maven: 'Maven3') {
          sh 'mvn clean install'
        } 
      }
    }
  }
}
