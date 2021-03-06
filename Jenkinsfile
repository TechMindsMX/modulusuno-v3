pipeline {
  agent any

  environment {
    VERSION = "${UUID.randomUUID().toString().replace('-','')[0..6]}" 
  }

  stages {

    stage('Download Config for Test'){
      steps{
        dir("configFilesTest"){
          sh "mkdir -p /home/jenkins/.modulusuno"
          sh "rm -rf /home/jenkins/.modulusuno/*"
          sh "git clone -b jenkins-new --single-branch git@bitbucket.org:techmindsmx/config-modulusuno-v3.git ."
        }
        sh 'mv configFilesTest/* /home/jenkins/.modulusuno'
      }
    }

    stage('Install plugin') {
      steps{
        dir("workspace"){
          sh "git clone -b master --single-branch git@github.com:makingdevs/aws-sdk-grails3.git ."
          sh "./gradlew install"
        }
      }
    }

    //stage('Run Migration App') {
    //  steps{
    //    dir("web"){
    //      sh './grailsw -Dgrails.env=test clean'
    //      sh './grailsw -Dgrails.env=test dbm-clear-checksums'
    //      sh './grailsw -Dgrails.env=test dbm-update'
    //    }
    //  }
    //}

    //stage('Testing App') {
    //  steps{
    //    dir("m1core"){
    //      sh './grailsw -Dgrails.env=test test-app'
    //    }
    //  }
    //}

    stage('Update Assets') {
      when {
        expression {
          env.BRANCH_NAME in ["master","stage","production"]
        }
      }
      steps{
        dir("web") {
          nodejs(nodeJSInstallationName: 'Node 10.1.0') {
            echo 'Updating bower'
            sh 'bower install'
          }
        }
      }
    }

    stage('Download Config'){
      when {
        expression {
          env.BRANCH_NAME in ["master","stage","production"]
        }
      }
      steps{
        dir("configFiles"){
          sh "git clone -b ${env.BRANCH_NAME}-new --single-branch git@bitbucket.org:techmindsmx/config-modulusuno-v3.git ."
        }
      }
    }

    stage('Preparing build Image Docker'){
      when {
        expression {
          env.BRANCH_NAME in ["master","stage","production"]
        }
      }
      environment {
        NAMEFILE = "${env.BRANCH_NAME == 'master' ? 'test' : 'production'}"
      }
      steps{
        sh "cp configFiles/application-api-${NAMEFILE}.groovy ."
        sh "cp configFiles/application-${NAMEFILE}.groovy ."
        dir("folderDocker"){
          sh "git clone git@github.com:makingdevs/Grails-Docker.git ."
        }
        sh 'cp folderDocker/Dockerfile .'
      }
    }

    stage('Build image docker webservices') {
      when {
        expression {
          env.BRANCH_NAME in ["master","stage","production"]
        }
      }
      environment {
        NAMEFILE = "${env.BRANCH_NAME == 'master' ? 'test' : 'production'}"
      }
      steps{
        script {
          docker.withTool('Docker') {
            docker.withRegistry('https://752822034914.dkr.ecr.us-east-1.amazonaws.com/webservice-modulusuno', 'ecr:us-east-1:techminds-aws') {
              def customImage = docker.build("webservice-modulusuno:${env.VERSION}", "--build-arg FILE_NAME_CONFIGURATION=application-api-${NAMEFILE}.groovy --build-arg PATH_NAME_CONFIGURATION=/root/.modulusuno/ --build-arg APP_NAME=webservices .")
              customImage.push()
            }
          }
        }
      }
    }

    stage('Build image docker web') {
      when {
        expression {
          env.BRANCH_NAME in ["master","stage","production"]
        }
      }
      environment {
        NAMEFILE = "${env.BRANCH_NAME == 'master' ? 'test' : 'production'}"
      }
      steps{
        script {
          docker.withTool('Docker') {
            docker.withRegistry('https://752822034914.dkr.ecr.us-east-1.amazonaws.com/web-modulusuno', 'ecr:us-east-1:techminds-aws') {
              def customImage = docker.build("web-modulusuno:${env.VERSION}", "--build-arg FILE_NAME_CONFIGURATION=application-${NAMEFILE}.groovy --build-arg PATH_NAME_CONFIGURATION=/root/.modulusuno/ --build-arg APP_NAME=web .")
              customImage.push()
            }
          }
        }
      }
    }

    stage('Deploy Docker Machine development web') {
      when {
        expression {
          env.BRANCH_NAME == "master"
        }
      }
      steps{
        sh "ssh ec2-user@34.206.149.172 sh /home/ec2-user/deployApps.sh ${env.VERSION} development web-modulusuno 8093 8080"
      }
    }

    stage('Deploy Docker Machine stage web') {
      when {
        expression {
          env.BRANCH_NAME == "stage"
        }
      }
      steps{
        sh "ssh ec2-user@34.206.149.172 sh /home/ec2-user/deployApps.sh ${env.VERSION} stage web-modulusuno 8094 8080"
      }
    }

    stage('Deploy Docker Machine production web') {
      when {
        expression {
          env.BRANCH_NAME == "production"
        }
      }
      steps{
        sh "ssh ec2-user@34.206.149.172 sh /home/ec2-user/deployApps.sh ${env.VERSION} production web-modulusuno 8095 8080"
      }
    }

    stage('Deploy Docker Machine development webservice') {
      when {
        expression {
          env.BRANCH_NAME == "master"
        }
      }
      steps{
        sh "ssh ec2-user@34.206.149.172 sh /home/ec2-user/deployApps.sh ${env.VERSION} development webservice-modulusuno 8090 8080"
      }
    }

    stage('Deploy Docker Machine stage webservice') {
      when {
        expression {
          env.BRANCH_NAME == "stage"
        }
      }
      steps{
        sh "ssh ec2-user@34.206.149.172 sh /home/ec2-user/deployApps.sh ${env.VERSION} stage webservice-modulusuno 8091 8080"
      }
    }

    stage('Deploy Docker Machine production webservice') {
      when {
        expression {
          env.BRANCH_NAME == "production"
        }
      }
      steps{
        sh "ssh ec2-user@34.206.149.172 sh /home/ec2-user/deployApps.sh ${env.VERSION} production webservice-modulusuno 8092 8080"
      }
    }

  }

  post {
    always {
      cleanWs()
    }
  }
}
