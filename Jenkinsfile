pipeline {
    agent any

    tools {
        maven 'Maven 3.8.6'  
    }

    environment {
        PROJECT_DIR = 'musicapp'
        SONAR_TOKEN = credentials('sonarqube-token') // <- your secure SonarQube token ID in Jenkins
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

        stage('Run Unit & Integration Tests (Exclude Selenium)') {
            steps {
                dir("${PROJECT_DIR}") {
                    bat './mvnw test -B -DtrimStackTrace=false -Dtest=!**/*SeleniumTest,!**/selenium/**'
                }
            }
        }

		stage('SonarQube Code Analysis') {
			steps {
				echo "DEBUG: Entering SonarQube Analysis Stage"
				dir("${PROJECT_DIR}") {
					withSonarQubeEnv('SonarQube') {
						bat """
							./mvnw sonar:sonar ^
							-Dsonar.projectKey=musicapp ^
							-Dsonar.host.url=http://localhost:9000 ^
							-Dsonar.login=%SONAR_TOKEN% ^
							-Dsonar.java.binaries=target/classes
						"""

					}
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
