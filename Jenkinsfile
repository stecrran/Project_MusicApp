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

                echo 'Waiting for Spring Boot app to be ready on port 9091...'
                bat '''
                    echo $i = 0; > waitForApp.ps1
                    echo while ($i -lt 30) { >> waitForApp.ps1
                    echo     try { >> waitForApp.ps1
                    echo         $resp = Invoke-WebRequest http://localhost:9091/api/auth/login -UseBasicParsing -TimeoutSec 5 >> waitForApp.ps1
                    echo         if ($resp.StatusCode -eq 200) { >> waitForApp.ps1
                    echo             Write-Host 'Spring Boot app is ready.'; exit 0 >> waitForApp.ps1
                    echo         } >> waitForApp.ps1
                    echo     } catch {} >> waitForApp.ps1
                    echo     Write-Host 'App not ready yet... waiting 5s'; >> waitForApp.ps1
                    echo     Start-Sleep -Seconds 5 >> waitForApp.ps1
                    echo     $i++ >> waitForApp.ps1
                    echo } >> waitForApp.ps1
                    echo exit 1 >> waitForApp.ps1
                    powershell -ExecutionPolicy Bypass -File waitForApp.ps1
                    del waitForApp.ps1
                '''
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
                    bat 'call mvnw.cmd test -B -DtrimStackTrace=false -Dtest=!**/*SeleniumTest,!**/selenium/**'
                }
            }
        }

        stage('SonarQube Code Analysis') {
            steps {
                dir("${PROJECT_DIR}") {
                    bat '''
                        call mvnw.cmd verify sonar:sonar ^
                            -Dsonar.projectKey=musicapp ^
                            -Dsonar.host.url=http://localhost:9000 ^
                            -Dsonar.token=sqa_00e432d7285ae2a4513c5e6f328b2d9ef4af34ba ^
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml ^
                            -Dsonar.java.binaries=target/classes
                    '''
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

        failure {
            echo '❌ Pipeline failed.'
        }
    }
}