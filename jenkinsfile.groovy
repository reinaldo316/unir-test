pipeline {
    agent {
        label 'docker'
    }
    stages {
        stage('Source') {
            steps {
                git 'https://github.com/reinaldo316/unir-test.git'
            }
        }
        stage('Build') {
            steps {
                echo 'Building stage!'
                sh 'make build'
            }
        }
        stage('Run') {
            steps {
                sh 'make run'            
            }
        }
        stage('Unit tests') {
            steps {
                sh 'make test-unit'
                archiveArtifacts artifacts: 'results/unit-result.xml', fingerprint: true
            }
        }
        stage('API tests') {
            steps {
                sh 'make test-api'
                archiveArtifacts artifacts: 'results/api-result.xml', fingerprint: true
            }
        }
        stage('E2E tests') {
            steps {
                sh 'make test-e2e'
                archiveArtifacts artifacts: 'results/e2e-result.xml', fingerprint: true
            }
        }
    }
    post {
        always {
            junit 'results/*_result.xml'
            cleanWs()
        }
        failure {
            emailext (
                to: 'reinaldo316@gmail.com',
                subject: "Falló el trabajo: \${JOB_NAME} - Build #\${BUILD_NUMBER}",
                body: "El trabajo \${JOB_NAME} ha fallado en la ejecución número \${BUILD_NUMBER}. Por favor, revisa los detalles.",
                attachLog: true
            )
        }
    }
}

