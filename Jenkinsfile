pipeline {
    agent any

    tools {
        maven 'Maven 3.8.6'
    }

    environment {
        PROJECT_DIR = 'musicapp'
        SONAR_TOKEN = credentials('sonarqube-token')
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

        stage('Start SonarQube (Docker)') {
            steps {
                echo 'Cleaning up any existing sonar container (if exists)...'
                bat '''
                    FOR /F "tokens=*" %%i IN ('docker ps -a -q -f name=sonar') DO (
                        docker stop %%i > nul 2>&1
                        docker rm %%i > nul 2>&1
                    )
                '''

                echo 'Starting SonarQube container...'
                bat '''
                    docker run -d --name sonar ^
                        -p 9000:9000 ^
                        -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true ^
                        sonarqube:latest
                '''

                echo 'Waiting for SonarQube to be ready...'
                bat '''
                    powershell -NoProfile -Command ^
                    "$i=0; ^
                    while ($i -lt 20) { ^
                      try { ^
                        $resp = Invoke-WebRequest http://localhost:9000/api/system/health -UseBasicParsing -TimeoutSec 5; ^
                        if ($resp.StatusCode -eq 200 -and $resp.Content -match '\"status\":\"UP\"') { ^
                          Write-Host 'SonarQube is ready.'; ^
                          exit 0 ^
                        } ^
                      } catch {} ^
                      Write-Host 'SonarQube not ready yet... waiting 5 seconds'; ^
                      Start-Sleep -Seconds 5; ^
                      $i++ ^
                    }; ^
                    exit 1"
                '''
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
                    withSonarQubeEnv('SonarQube') {
                        bat """
                            call mvnw.cmd verify sonar:sonar ^
                                -Dsonar.projectKey=musicapp ^
                                -Dsonar.host.url=http://localhost:9000 ^
                                -Dsonar.token=%SONAR_TOKEN% ^
                                -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml ^
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

            bat '''
                FOR /F "tokens=*" %%i IN ('docker ps -a -q -f name=sonar') DO (
                    docker stop %%i > nul 2>&1
                    docker rm %%i > nul 2>&1
                )
            '''
        }

        success {
            echo 'Spring Boot + Docker + SonarQube pipeline succeeded!'
        }

        failure {
            echo 'Pipeline failed.'
        }
    }
}
