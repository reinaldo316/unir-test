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
        stage('Unit tests') {
            steps {
                sh 'make test-unit'
                archiveArtifacts artifacts: 'results/unit_result.xml'
            }
        }
        stage('API tests') {
            steps {
                sh 'make test-api'
                archiveArtifacts artifacts: 'results/api_result.xml'
            }
        }
        stage('E2E tests') {
            steps {
                sh 'make test-e2e'
                archiveArtifacts artifacts: 'results/cypress_result.xml'
            }
        }
    }
    post {
        always {
            junit 'results/*_result.xml'
            cleanWs()
        }
        success {
            emailext (
                to: 'reinaldo316@gmail.com',
                subject: "Trabajo exitoso: \${JOB_NAME} - Build #\${BUILD_NUMBER}",
                body: "El trabajo \${JOB_NAME} se ejecutó correctamente en la ejecución número \${BUILD_NUMBER}."
            )
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
