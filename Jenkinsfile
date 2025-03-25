pipeline {
    agent any

    tools {
        maven 'Maven 3.8.6'  
    }

    environment {
        PROJECT_DIR = 'musicapp'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Package Spring Boot App') {
            steps {
                dir("${PROJECT_DIR}") {
                    bat './mvnw clean package -DskipTests'
                }
            }
        }

        stage('Run Tests') {
            steps {
                dir("${PROJECT_DIR}") {
                    bat './mvnw test'
                }
            }
        }

        stage('Archive JAR Artifact') {
            steps {
                archiveArtifacts artifacts: "${PROJECT_DIR}/target/*.jar", fingerprint: true
            }
        }
    }

    post {
        success {
            echo 'Spring Boot + Vue (static) build succeeded!'
        }
        failure {
            echo 'Build failed.'
        }
    }
}
