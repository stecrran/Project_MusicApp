pipeline {
    agent any

    tools {
        maven 'Maven 3.8.6'
    }

    environment {
        PROJECT_DIR = 'musicapp'
    }

    stages {

        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('Checkout Source') {
            steps {
                checkout scm
            }
        }

        stage('Start MySQL & MusicApp via Docker Compose') {
            steps {
                dir("${PROJECT_DIR}") {
                    bat 'docker-compose up -d mysql musicapp'
                }
                echo 'Waiting for MySQL + App to stabilize...'
                sleep 30
            }
        }

        stage('Build & Package Spring Boot App') {
            steps {
                dir("${PROJECT_DIR}") {
                    bat 'call mvnw.cmd clean package -DskipTests'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                dir("${PROJECT_DIR}") {
                    bat 'docker build -t musicapp:latest -f Dockerfile .'
                }
            }
        }

        stage('Run Unit & Integration Tests (Exclude Selenium)') {
            steps {
                dir("${PROJECT_DIR}") {
                    echo 'Waiting for app to be ready on port 9091...'
                    bat '''
                        for /L %%i in (1,1,30) do (
                            powershell -Command "try { Invoke-WebRequest http://localhost:9091/api/auth/login -UseBasicParsing } catch { Start-Sleep -Seconds 1 }"
                        )
                    '''
                    bat 'call mvnw.cmd test -B -DtrimStackTrace=false -Dtest=!**/*SeleniumTest,!**/selenium/**'
                }
            }
        }

        stage('SonarQube Code Analysis') {
            steps {
                dir("${PROJECT_DIR}") {
                    bat 'IF EXIST .scannerwork (rd /s /q .scannerwork)'
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        withSonarQubeEnv('SonarQube') {
                            bat """
                                call mvnw.cmd clean test jacoco:report sonar:sonar ^
                                    -Dsonar.projectKey=musicapp ^
                                    -Dsonar.login=%SONAR_TOKEN% ^
                                    -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml ^
                                    -Dsonar.java.binaries=target/classes ^
                                    -Dsonar.exclusions=**/config/**,**/model/**,**/filter/**,**/util/**,**/MusicApplication.java ^
                                    -Dsonar.coverage.exclusions=src/test/**,**/*.js
                            """
                        }
                    }
                }
            }
        }

		stage('SonarQube Quality Gate') {
			steps {
				script {
					timeout(time: 15, unit: 'MINUTES') {
						def qualityGate = waitForQualityGate()
						echo "SonarQube Quality Gate: ${qualityGate.status}"
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
        always {
            echo 'Cleaning up containers...'
            dir("${PROJECT_DIR}") {
                bat '''
                    IF EXIST docker-compose.yml (
                        docker-compose down --remove-orphans
                    ) ELSE (
                        echo docker-compose.yml not found. Skipping docker-compose down.
                    )
                '''
            }
        }

        success {
            echo '✅ Spring Boot + Docker + SonarQube pipeline succeeded!'
        }

        unstable {
            echo '⚠️ Pipeline marked as UNSTABLE due to SonarQube quality gate.'
        }

        failure {
            echo '❌ Pipeline failed.'
        }
    }
}
